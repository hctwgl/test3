package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.ald.fanbei.api.biz.bo.jsd.ApplyBorrowCashBo.ApplyBorrowCashReq;
import com.ald.fanbei.api.biz.bo.jsd.ApplyBorrowCashBo.JsdGoodsInfoBo;
import com.ald.fanbei.api.biz.bo.jsd.TrialBeforeBorrowBo;
import com.ald.fanbei.api.biz.bo.jsd.TrialBeforeBorrowBo.TrialBeforeBorrowReq;
import com.ald.fanbei.api.biz.bo.jsd.TrialBeforeBorrowBo.TrialBeforeBorrowResp;
import com.ald.fanbei.api.biz.bo.xgxy.XgxyBorrowNoticeBo;
import com.ald.fanbei.api.biz.service.BeheadBorrowCashService;
import com.ald.fanbei.api.biz.service.JsdNoticeRecordService;
import com.ald.fanbei.api.biz.service.JsdResourceService;
import com.ald.fanbei.api.biz.service.impl.JsdResourceServiceImpl.ResourceRateInfoBo;
import com.ald.fanbei.api.biz.third.enums.XgxyBorrowNotifyStatus;
import com.ald.fanbei.api.biz.third.util.UpsUtil;
import com.ald.fanbei.api.biz.third.util.XgxyUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.biz.util.GeneratorClusterNo;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.BorrowVersionType;
import com.ald.fanbei.api.common.enums.JsdBorrowCashReviewStatus;
import com.ald.fanbei.api.common.enums.JsdBorrowCashReviewSwitch;
import com.ald.fanbei.api.common.enums.JsdBorrowCashStatus;
import com.ald.fanbei.api.common.enums.JsdBorrowLegalOrderStatus;
import com.ald.fanbei.api.common.exception.BizException;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.JsdBorrowCashDao;
import com.ald.fanbei.api.dal.dao.JsdBorrowLegalOrderCashDao;
import com.ald.fanbei.api.dal.dao.JsdBorrowLegalOrderDao;
import com.ald.fanbei.api.dal.dao.JsdBorrowLegalOrderInfoDao;
import com.ald.fanbei.api.dal.dao.JsdUserDao;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderInfoDo;
import com.ald.fanbei.api.dal.domain.JsdResourceDo;
import com.ald.fanbei.api.dal.domain.JsdUserBankcardDo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;


/**
 * 极速贷砍头ServiceImpl
 * 
 * @author yanghailong
 * @version 1.0.0 初始化
 * @date 2018-09-17 16:18:06
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("beheadBorrowCashService")
public class BeheadBorrowCashServiceImpl extends ParentServiceImpl<JsdBorrowCashDo, Long> implements BeheadBorrowCashService {
    
	@Resource
    JsdBorrowCashDao jsdBorrowCashDao;
    @Resource
    JsdResourceService jsdResourceService;
    @Resource
    JsdNoticeRecordService jsdNoticeRecordService;
    @Resource
    JsdBorrowLegalOrderDao jsdBorrowLegalOrderDao;
    @Resource
    JsdBorrowLegalOrderCashDao jsdBorrowLegalOrderCashDao;
    @Resource
    JsdBorrowLegalOrderInfoDao jsdBorrowLegalOrderInfoDao;
    @Resource
    JsdUserDao jsdUserDao;

    @Resource
    XgxyUtil xgxyUtil;
    @Resource
    BizCacheUtil bizCacheUtil;
    @Resource
    TransactionTemplate transactionTemplate;
    @Resource
    GeneratorClusterNo generatorClusterNo;
    @Resource
    UpsUtil upsUtil;
    
	@Override
	public BaseDao<JsdBorrowCashDo, Long> getDao() {
		return jsdBorrowCashDao;
	}
	
	@Override
	public void applyBeheadBorrowCash(ApplyBorrowCashReq cashReq, final JsdUserBankcardDo mainCard, TrialBeforeBorrowBo trialBo) {
		
		this.resolve(trialBo);
		final JsdBorrowCashDo cashDo = buildBorrowCashDo(cashReq, mainCard, trialBo);
		final JsdBorrowLegalOrderDo orderDo = buildBorrowLegalOrder(cashReq, trialBo.userId);
		
		transactionTemplate.execute(new TransactionCallback<Long>() {
            @Override
            public Long doInTransaction(TransactionStatus arg0) {
            	
                jsdBorrowCashDao.saveRecord(cashDo);
                
                Long borrowId = cashDo.getRid();
                orderDo.setBorrowId(borrowId);
                jsdBorrowLegalOrderDao.saveRecord(orderDo);
                
                return borrowId;
            }
        });
		
		// 从西瓜获取订单信息
		this.saveOrderInfo(cashDo);
		
		JsdResourceDo review = jsdResourceService.getByTypeAngSecType(Constants.JSD_CONFIG, Constants.JSD_CONFIG_REVIEW_MODE);
		String reviewSwitch = review.getValue(); // 审批开关 
		BigDecimal allAmount = new BigDecimal(review.getValue1());	// 放款总额
		logger.info("applyBeheadBorrowCash borrowNo="+cashDo.getBorrowNo()+", reviewSwitch="+reviewSwitch);
		
		/*极速贷审批模式*/
		// 1.自动
		if(StringUtil.equals(JsdBorrowCashReviewSwitch.AUTO.name(), reviewSwitch)) {
			jsdBorrowCashDao.updateReviewStatus(JsdBorrowCashReviewStatus.PASS.name(), cashDo.getRid());
			cashDo.setReviewStatus(JsdBorrowCashReviewStatus.PASS.name());
			upsUtil.autoJsdDelegatePay(cashDo, orderDo, mainCard);
		}
		
		// 2.半自动
		else if(StringUtil.equals(JsdBorrowCashReviewSwitch.SEMI_AUTO.name(), reviewSwitch)) {
			// 当天放款总额
			long currDayAllamount = bizCacheUtil.incr(Constants.CACHEKEY_BORROW_CURRDAY_ALLAMOUNT, 0L, DateUtil.getTodayLast());
			logger.info("currday borrow allamount cache : borrowNo="+cashDo.getBorrowNo()+", currAllamount="+currDayAllamount);
			// 剩余款额
			BigDecimal remainAmount = BigDecimalUtil.subtract(allAmount, new BigDecimal(currDayAllamount));
			
			if(remainAmount.compareTo(cashDo.getAmount()) >= 0){
				jsdBorrowCashDao.updateReviewStatus(JsdBorrowCashReviewStatus.PASS.name(), cashDo.getRid());
				cashDo.setReviewStatus(JsdBorrowCashReviewStatus.PASS.name());
				upsUtil.autoJsdDelegatePay(cashDo, orderDo, mainCard);
			}
		}
		
		// 3.手动
		/*else if(StringUtil.equals(JsdBorrowCashReviewSwitch.MANUAL.name(), reviewSwitch)) {	
		}*/
		
	}

	/**
	 * 从西瓜获取订单信息
	 */
	private void saveOrderInfo(final JsdBorrowCashDo cashDo) {
		try {
			Map<String,String> data = Maps.newHashMap();
			data.put("borrowNo", cashDo.getTradeNoXgxy());
			HashMap<String, String> orderInfoMap = xgxyUtil.borrowNoticeRequest(data);
			if(!orderInfoMap.isEmpty()){
				JsdBorrowLegalOrderInfoDo orderInfoDo = JSONObject.parseObject(JSON.toJSONString(orderInfoMap), JsdBorrowLegalOrderInfoDo.class);
				orderInfoDo.setUserId(cashDo.getUserId());
				orderInfoDo.setBorrowId(cashDo.getRid());
				jsdBorrowLegalOrderInfoDao.saveRecord(orderInfoDo); 
			}else {
				logger.error("beheadBorrowCashService saveOrderInfo error, xgxy return orderInfoMap is empty");
			}
		} catch (Exception e) {
			logger.error("beheadBorrowCashService saveOrderInfo error",e);
		}
	}
	
	/**
	 * 解析各项利息费用
	 * @param borrowAmount
	 * @param borrowType
	 * @param oriRate
	 * @return
	 */
	@Override
	public void resolve(TrialBeforeBorrowBo bo) {
		TrialBeforeBorrowReq req = bo.req;
		
		BigDecimal titularBorrowAmount = req.amount; // 借款额
		BigDecimal borrowDay = new BigDecimal(req.term);

		ResourceRateInfoBo borrowRateInfo = jsdResourceService.getRateInfo(req.term);
		
		BigDecimal borrowServiceRate = borrowRateInfo.serviceRate;
        BigDecimal borrowInterestRate = borrowRateInfo.interestRate;
        BigDecimal borrowOverdueRate = borrowRateInfo.overdueRate;
        
        // 借款 利息 与 服务费 乘法表达式可复用左侧
        BigDecimal borrowinterestLeft = borrowInterestRate.divide(BigDecimal.valueOf(Constants.ONE_YEAY_DAYS), 12, RoundingMode.HALF_UP).multiply(borrowDay);
        BigDecimal borrowServiceRateLeft = borrowServiceRate.divide(BigDecimal.valueOf(Constants.ONE_YEAY_DAYS), 12, RoundingMode.HALF_UP).multiply(borrowDay);
        
        BigDecimal interestAmount = borrowinterestLeft.multiply(titularBorrowAmount).setScale(2, RoundingMode.HALF_UP);
        BigDecimal serviceAmount = borrowServiceRateLeft.multiply(titularBorrowAmount).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalProfit = bo.riskDailyRate.multiply(titularBorrowAmount).multiply(borrowDay);
        
        BigDecimal finalDiffProfit = totalProfit.subtract(interestAmount).subtract(serviceAmount);
        logger.info("resolve borrow amount, openId=" + req.openId + ", actualDiffProfit=" + finalDiffProfit);
        if (finalDiffProfit.compareTo(BigDecimal.ZERO) <= 0) {
        	finalDiffProfit = BigDecimal.ZERO;
        }
        
        BigDecimal actualOrderAmount = finalDiffProfit.setScale(-1, RoundingMode.UP);	//最终商品价格,以10取整
        BigDecimal actualBorrowAmount = titularBorrowAmount.subtract(actualOrderAmount);//到账金额
        TrialBeforeBorrowResp resp = new TrialBeforeBorrowResp();
        
        //处理借款相关利息
        resp.borrowAmount = titularBorrowAmount.setScale(2, RoundingMode.HALF_UP).toString();
        resp.arrivalAmount = actualBorrowAmount.add(actualOrderAmount).setScale(2, RoundingMode.HALF_UP).toString();
        resp.interestRate = borrowInterestRate.setScale(4, RoundingMode.HALF_UP).toString();
        resp.interestAmount = interestAmount.toString();
        resp.serviceRate = borrowRateInfo.serviceRate.setScale(4, RoundingMode.HALF_UP).toString();
        resp.serviceAmount = serviceAmount.toString();
        resp.overdueRate = borrowOverdueRate.divide(new BigDecimal(360)).setScale(4, RoundingMode.HALF_UP).toString();
        
        //商品价格
        resp.totalDiffFee = actualOrderAmount.toPlainString();
        
        BigDecimal totalAmount = BigDecimalUtil.add(titularBorrowAmount, interestAmount, serviceAmount);
        resp.totalAmount = totalAmount.toString();
        resp.billAmount = new BigDecimal[]{totalAmount.setScale(2, RoundingMode.HALF_UP)};
        
        // 1、借款费用【借款利息金额+借款服务费金额】元，其中借款利息【借款利息金额】元，借款服务费【借款服务费金额】元。
        resp.remark = "1、借款费用" + BigDecimalUtil.add(interestAmount, serviceAmount) + "元，其中借款利息" + interestAmount + "元，借款服务费" + serviceAmount + "元。";
        
        bo.resp = resp;
    }
	
	/**
     * 构建 主借款(砍头)
     * @return
     */
    private JsdBorrowCashDo buildBorrowCashDo(ApplyBorrowCashReq cashReq, JsdUserBankcardDo mainCard, TrialBeforeBorrowBo trialBo) {
    	TrialBeforeBorrowReq trialReq = trialBo.req;
    	TrialBeforeBorrowResp trialResp = trialBo.resp;
    	
        JsdBorrowCashDo afBorrowCashDo = new JsdBorrowCashDo();
        afBorrowCashDo.setAmount(new BigDecimal( trialResp.borrowAmount) );
        afBorrowCashDo.setArrivalAmount(new BigDecimal( trialResp.arrivalAmount ));// 到账金额和借款金额相同
        afBorrowCashDo.setCardName(mainCard.getBankName());
        afBorrowCashDo.setCardNumber(mainCard.getBankCardNumber());
        afBorrowCashDo.setType(trialReq.term);
        afBorrowCashDo.setStatus(JsdBorrowCashStatus.APPLY.name());
        afBorrowCashDo.setUserId(trialBo.userId);
        afBorrowCashDo.setInterestAmount(new BigDecimal(trialResp.interestAmount));
        afBorrowCashDo.setPoundageAmount(new BigDecimal(trialResp.serviceAmount));
        afBorrowCashDo.setPoundageRate(new BigDecimal(trialResp.serviceRate));
        afBorrowCashDo.setInterestRate(new BigDecimal(trialResp.interestRate));
        afBorrowCashDo.setOverdueRate(new BigDecimal(trialResp.overdueRate).multiply(new BigDecimal(360)));
        afBorrowCashDo.setRiskDailyRate(trialBo.riskDailyRate);
        afBorrowCashDo.setProductNo(trialReq.productNo);
        afBorrowCashDo.setTradeNoXgxy(cashReq.borrowNo);
        afBorrowCashDo.setBorrowNo(generatorClusterNo.getLoanNo(new Date()));
        afBorrowCashDo.setRepayPrinciple(BigDecimal.ZERO);
        afBorrowCashDo.setVersion(BorrowVersionType.BEHEAD.name());
        afBorrowCashDo.setBorrowRemark(cashReq.loanRemark);
        afBorrowCashDo.setRepayRemark(cashReq.repayRemark);
        afBorrowCashDo.setReviewStatus(JsdBorrowCashReviewStatus.WAIT.name()); // 待审批
        return afBorrowCashDo;
    }
    
    /**
     * 构建 商品订单(砍头)
     * @return
     */
    private JsdBorrowLegalOrderDo buildBorrowLegalOrder(ApplyBorrowCashReq cashReq, Long userId) {
    	JsdGoodsInfoBo goodsBo = cashReq.goodsInfo;
    	
        JsdBorrowLegalOrderDo afBorrowLegalOrderDo = new JsdBorrowLegalOrderDo();
        afBorrowLegalOrderDo.setUserId(userId);
        afBorrowLegalOrderDo.setPriceAmount(new BigDecimal(goodsBo.goodsPrice));
        afBorrowLegalOrderDo.setGoodsName(goodsBo.goodsName);
        afBorrowLegalOrderDo.setStatus(JsdBorrowLegalOrderStatus.UNPAID.getCode());
        String orderCashNo = generatorClusterNo.getOrderNo(new Date());
        afBorrowLegalOrderDo.setOrderNo(orderCashNo);
        return afBorrowLegalOrderDo;
    }
    
	
	private void transUpdate(final JsdBorrowCashDo cashDo, final JsdBorrowLegalOrderDo orderDo) {
    	transactionTemplate.execute(new TransactionCallback<String>() {
            @Override
            public String doInTransaction(TransactionStatus ts) {
            	jsdBorrowCashDao.updateById(cashDo);
            	jsdBorrowLegalOrderDao.updateById(orderDo);
                return "success";
            }
        });
    }

	
	@Override
	public void dealBorrowSucc(Long cashId, String outTradeNo) {
		JsdBorrowCashDo cashDo = jsdBorrowCashDao.getById(cashId);
		if(cashDo == null) {
			throw new BizException("behead dealBorrowSucc, can't find refer borrowCash by id=" + cashId);
		}
		
		logger.info("behead dealBorrowSucc, borrowCashId="+ cashId + ", borrowNo=" + cashDo.getBorrowNo()
			+ ", tradeNoXgxy=" + cashDo.getTradeNoXgxy() + ", tradeNoUps=" + cashDo.getTradeNoUps());
		
		if(JsdBorrowCashStatus.FINISHED.name().equals(cashDo.getStatus())) {
			logger.warn("behead cur borrowNo " + cashDo.getBorrowNo() + "have FINISHED! repeat UPS callback!");
			return;
		}
		
		JsdBorrowLegalOrderDo orderDo = jsdBorrowLegalOrderDao.getLastOrderByBorrowId(cashId);
		
		Date currDate = new Date(System.currentTimeMillis());
		Date arrivalEnd = DateUtil.getEndOfDatePrecisionSecond(currDate);
        Date repaymentDate = DateUtil.addDays(arrivalEnd, Integer.valueOf(cashDo.getType()) - 1);
		cashDo.setGmtArrival(currDate);
        cashDo.setGmtPlanRepayment(repaymentDate);
        cashDo.setStatus(JsdBorrowCashStatus.TRANSFERRED.name());
        cashDo.setTradeNoUps(outTradeNo);
        
        orderDo.setStatus(JsdBorrowLegalOrderStatus.AWAIT_DELIVER.name());
        this.transUpdate(cashDo, orderDo);
        
        jsdNoticeRecordService.dealBorrowNoticed(cashDo, this.buildXgxyPay(cashDo, "放款成功", XgxyBorrowNotifyStatus.SUCCESS.name()));
	}

	@Override
	public void dealBorrowFail(Long cashId, String outTradeNo, String failMsg) {
		JsdBorrowCashDo cashDo = jsdBorrowCashDao.getById(cashId);
		if(cashDo == null) {
			throw new BizException("behead dealBorrowFail, can't find refer borrowCash by id=" + cashId);
		}
		logger.info("behead dealBorrowFail, borrowCashId="+ cashId + ", borrowNo=" + cashDo.getBorrowNo()
			+ ", tradeNoXgxy=" + cashDo.getTradeNoXgxy() + ", tradeNoUps=" + cashDo.getTradeNoUps());
		
		JsdBorrowLegalOrderDo orderDo = jsdBorrowLegalOrderDao.getLastOrderByBorrowId(cashId);
		cashDo.setTradeNoUps(outTradeNo);
		
		dealBorrowFail(cashDo, orderDo, failMsg);
	}
	
	@Override
	public void dealBorrowFail(JsdBorrowCashDo cashDo, JsdBorrowLegalOrderDo orderDo, String failMsg) {
		
		// 缓存减去失败金额
		long currAllamount = bizCacheUtil.incr(Constants.CACHEKEY_BORROW_CURRDAY_ALLAMOUNT, -cashDo.getAmount().longValue(), DateUtil.getTodayLast());
		logger.info("currday borrow allamount cache : borrowNo="+cashDo.getBorrowNo()+", currAllamount-"+cashDo.getAmount().longValue()+", currAllamount="+currAllamount);

		cashDo.setStatus(JsdBorrowCashStatus.CLOSED.name());
        cashDo.setRemark(failMsg);
        cashDo.setGmtClose(new Date());
        
        orderDo.setStatus(JsdBorrowLegalOrderStatus.CLOSED.name());
        this.transUpdate(cashDo, orderDo);

        jsdNoticeRecordService.dealBorrowNoticed(cashDo, this.buildXgxyPay(cashDo, failMsg, XgxyBorrowNotifyStatus.FAILED.name()));
	}
	
    private XgxyBorrowNoticeBo buildXgxyPay(JsdBorrowCashDo cashDo, String msg,String status) {
    	XgxyBorrowNoticeBo  xgxyPayBo = new XgxyBorrowNoticeBo();
        xgxyPayBo.setTradeNo(cashDo.getTradeNoUps());
        xgxyPayBo.setBorrowNo(cashDo.getTradeNoXgxy());
        xgxyPayBo.setReason(msg);
        xgxyPayBo.setStatus(status);
        xgxyPayBo.setGmtArrival(cashDo.getGmtArrival());
        xgxyPayBo.setTimestamp(System.currentTimeMillis());
        return xgxyPayBo;
    }
    
}
