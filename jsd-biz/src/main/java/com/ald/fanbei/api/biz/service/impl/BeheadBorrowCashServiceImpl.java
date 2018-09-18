package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.util.Date;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.ald.fanbei.api.biz.bo.jsd.TrialBeforeBorrowBo;
import com.ald.fanbei.api.biz.bo.jsd.ApplyBorrowCashBo.ApplyBorrowCashReq;
import com.ald.fanbei.api.biz.bo.jsd.ApplyBorrowCashBo.JsdGoodsInfoBo;
import com.ald.fanbei.api.biz.bo.jsd.TrialBeforeBorrowBo.TrialBeforeBorrowReq;
import com.ald.fanbei.api.biz.bo.jsd.TrialBeforeBorrowBo.TrialBeforeBorrowResp;
import com.ald.fanbei.api.biz.bo.ups.UpsDelegatePayRespBo;
import com.ald.fanbei.api.biz.bo.xgxy.XgxyBorrowNoticeBo;
import com.ald.fanbei.api.biz.service.BeheadBorrowCashService;
import com.ald.fanbei.api.biz.service.JsdResourceService;
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
import com.ald.fanbei.api.common.enums.JsdNoticeType;
import com.ald.fanbei.api.common.enums.OrderType;
import com.ald.fanbei.api.common.exception.BizException;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.JsdBorrowCashDao;
import com.ald.fanbei.api.dal.dao.JsdBorrowLegalOrderCashDao;
import com.ald.fanbei.api.dal.dao.JsdBorrowLegalOrderDao;
import com.ald.fanbei.api.dal.dao.JsdNoticeRecordDao;
import com.ald.fanbei.api.dal.dao.JsdUserDao;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderDo;
import com.ald.fanbei.api.dal.domain.JsdNoticeRecordDo;
import com.ald.fanbei.api.dal.domain.JsdResourceDo;
import com.ald.fanbei.api.dal.domain.JsdUserBankcardDo;
import com.ald.fanbei.api.dal.domain.JsdUserDo;
import com.alibaba.fastjson.JSON;


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
    JsdBorrowLegalOrderDao jsdBorrowLegalOrderDao;
    @Resource
    JsdBorrowLegalOrderCashDao jsdBorrowLegalOrderCashDao;
    @Resource
    JsdNoticeRecordDao jsdNoticeRecordDao;
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
		
		JsdResourceDo review = jsdResourceService.getByTypeAngSecType(Constants.JSD_CONFIG, Constants.JSD_CONFIG_REVIEW_MODE);
		String reviewSwitch = review.getValue(); // 审批开关 
		BigDecimal allAmount = new BigDecimal(review.getValue1());	// 放款总额
		
		// 极速贷审批模式
		if(StringUtil.equals(JsdBorrowCashReviewSwitch.AUTO.name(), reviewSwitch)){		// 自动
			jsdBorrowCashDao.updateReviewStatus(JsdBorrowCashReviewStatus.PASS.name(), cashDo.getRid());
			upsUtil.autoJsdDelegatePay(cashDo, orderDo, mainCard);
			
		}else if(StringUtil.equals(JsdBorrowCashReviewSwitch.MANUAL.name(), reviewSwitch)){	// 手动
			
			
		}else if(StringUtil.equals(JsdBorrowCashReviewSwitch.SEMI_AUTO.name(), reviewSwitch)){	// 半自动
			// 当天放款总额
			BigDecimal currDayAllamount = jsdBorrowCashDao.getCurrDayAllamount();
			// 剩余款额
			BigDecimal remainAmount = BigDecimalUtil.subtract(allAmount, currDayAllamount.add(cashDo.getAmount()));
			if(remainAmount.compareTo(BigDecimal.ZERO)>0){
				jsdBorrowCashDao.updateReviewStatus(JsdBorrowCashReviewStatus.PASS.name(), cashDo.getRid());
				upsUtil.autoJsdDelegatePay(cashDo, orderDo, mainCard);
			}
		}
		
		
	}
	
	/**
     * 构建 主借款
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
        afBorrowCashDo.setOverdueRate(new BigDecimal(trialResp.overdueRate));
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
     * 构建 商品订单
     * @return
     */
    private JsdBorrowLegalOrderDo buildBorrowLegalOrder(ApplyBorrowCashReq cashReq, Long userId) {
    	JsdGoodsInfoBo goodsBo = cashReq.goodsInfo;
    	
        JsdBorrowLegalOrderDo afBorrowLegalOrderDo = new JsdBorrowLegalOrderDo();
        afBorrowLegalOrderDo.setUserId(userId);
        afBorrowLegalOrderDo.setPriceAmount(new BigDecimal(goodsBo.goodsPrice));
        afBorrowLegalOrderDo.setGoodsName(goodsBo.goodsName);
        afBorrowLegalOrderDo.setStatus(JsdBorrowLegalOrderStatus.UNPAID.getCode());
        String orderCashNo = generatorClusterNo.getOrderNo(OrderType.LEGAL);
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
        
        jsdNoticeRecord(cashDo,"", XgxyBorrowNotifyStatus.SUCCESS.name());
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
        cashDo.setStatus(JsdBorrowCashStatus.CLOSED.name());
        cashDo.setRemark(failMsg);
        cashDo.setGmtClose(new Date());
        
        orderDo.setStatus(JsdBorrowLegalOrderStatus.CLOSED.name());
        this.transUpdate(cashDo, orderDo);

        jsdNoticeRecord(cashDo, failMsg,  XgxyBorrowNotifyStatus.FAILED.name());
	}
	
	private void jsdNoticeRecord(JsdBorrowCashDo cashDo,String msg, String status) {
        try {
        	XgxyBorrowNoticeBo xgxyPayBo = buildXgxyPay(cashDo, msg, status);
            JsdNoticeRecordDo noticeRecordDo = buildJsdNoticeRecord(cashDo, xgxyPayBo);
            jsdNoticeRecordDao.addNoticeRecord(noticeRecordDo);
            if(xgxyUtil.borrowNoticeRequest(xgxyPayBo)){
                noticeRecordDo.setRid(noticeRecordDo.getRid());
                noticeRecordDo.setGmtModified(new Date());
                jsdNoticeRecordDao.updateNoticeRecordStatus(noticeRecordDo);
            }
        } catch (Exception e) {
            logger.error("dsedNoticeRecord, notify user occur error!", e); //通知过程抛出任何异常捕获，不影响主流程
        }
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

    private JsdNoticeRecordDo buildJsdNoticeRecord(JsdBorrowCashDo cashDo, XgxyBorrowNoticeBo xgxyPayBo) {
    	JsdNoticeRecordDo noticeRecordDo = new JsdNoticeRecordDo();
        noticeRecordDo.setUserId(cashDo.getUserId());
        noticeRecordDo.setRefId(String.valueOf(cashDo.getRid()));
        noticeRecordDo.setType(JsdNoticeType.DELEGATEPAY.code);
        noticeRecordDo.setTimes(Constants.NOTICE_FAIL_COUNT);
        noticeRecordDo.setParams(JSON.toJSONString(xgxyPayBo));
        return noticeRecordDo;
    }

}