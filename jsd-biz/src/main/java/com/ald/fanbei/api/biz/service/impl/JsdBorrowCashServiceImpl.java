package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.ald.fanbei.api.biz.bo.jsd.TrialBeforeBorrowBo;
import com.ald.fanbei.api.biz.bo.jsd.TrialBeforeBorrowBo.TrialBeforeBorrowReq;
import com.ald.fanbei.api.biz.bo.jsd.TrialBeforeBorrowBo.TrialBeforeBorrowResp;
import com.ald.fanbei.api.biz.bo.xgxy.XgxyBorrowNoticeBo;
import com.ald.fanbei.api.biz.service.JsdBorrowCashService;
import com.ald.fanbei.api.biz.service.JsdResourceService;
import com.ald.fanbei.api.biz.service.impl.JsdResourceServiceImpl.ResourceRateInfoBo;
import com.ald.fanbei.api.biz.third.enums.XgxyBorrowNotifyStatus;
import com.ald.fanbei.api.biz.third.util.OriRateUtil;
import com.ald.fanbei.api.biz.third.util.XgxyUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.JsdBorrowCashStatus;
import com.ald.fanbei.api.common.enums.JsdBorrowLegalOrderCashStatus;
import com.ald.fanbei.api.common.enums.JsdBorrowLegalOrderStatus;
import com.ald.fanbei.api.common.enums.JsdNoticeType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.JsdBorrowCashDao;
import com.ald.fanbei.api.dal.dao.JsdBorrowLegalOrderCashDao;
import com.ald.fanbei.api.dal.dao.JsdBorrowLegalOrderDao;
import com.ald.fanbei.api.dal.dao.JsdNoticeRecordDao;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderCashDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderDo;
import com.ald.fanbei.api.dal.domain.JsdNoticeRecordDo;
import com.alibaba.fastjson.JSON;


/**
 * 极速贷ServiceImpl
 * 
 * @author yanghailong
 * @version 1.0.0 初始化
 * @date 2018-08-22 16:18:06
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("jsdBorrowCashService")
public class JsdBorrowCashServiceImpl extends ParentServiceImpl<JsdBorrowCashDo, Long> implements JsdBorrowCashService {
    
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
    XgxyUtil xgxyUtil;
    @Resource
    OriRateUtil oriRateUtil;
    @Resource
    TransactionTemplate transactionTemplate;
    
	@Override
	public BaseDao<JsdBorrowCashDo, Long> getDao() {
		return jsdBorrowCashDao;
	}

	@Override
	public JsdBorrowCashDo getByBorrowNo(String borrowNo) {
		return jsdBorrowCashDao.getByBorrowNo(borrowNo);
	}
	@Override
	public JsdBorrowCashDo getByTradeNoXgxy(String tradeNoXgxy) {
		return jsdBorrowCashDao.getByTradeNoXgxy(tradeNoXgxy);
	}

	@Override
	public void checkCanBorrow(Long userId) {
		List<JsdBorrowCashDo> notFinishBorrowList = jsdBorrowCashDao.getBorrowCashByStatusNotInFinshAndClosed(userId);
		if(!notFinishBorrowList.isEmpty()) {
			throw new FanbeiException(FanbeiExceptionCode.JSD_BORROW_CASH_STATUS_ERROR);
		}
	}

	@Override
	public String getCurrentLastBorrowNo(String orderNoPre) {
		return jsdBorrowCashDao.getCurrentLastBorrowNo(orderNoPre);
	}
	
	@Override
	public int getBorrowCashOverdueCount() {
		Date date = new Date(System.currentTimeMillis());
		Date bengin = DateUtil.getStartOfDate(date);
		return jsdBorrowCashDao.getBorrowCashOverdueCount(bengin);
	}

	@Override
	public List<JsdBorrowCashDo> getBorrowCashOverdue(int nowPage, int pageSize) {
		Date date = new Date(System.currentTimeMillis());
		Date bengin = DateUtil.getStartOfDate(date);
		return jsdBorrowCashDao.getBorrowCashOverduePaging(bengin, nowPage, pageSize);
	}

	@Override
	public List<JsdBorrowCashDo> getBorrowCashOverdueByUserIds(String userIds) {
		Date date = new Date(System.currentTimeMillis());
		Date bengin = DateUtil.getStartOfDate(date);
		return jsdBorrowCashDao.getBorrowCashOverdueByUserIds(bengin, userIds);
	}
	
	
	public void transUpdate(final JsdBorrowCashDo cashDo, final JsdBorrowLegalOrderDo orderDo, final JsdBorrowLegalOrderCashDo orderCashDo) {
    	transactionTemplate.execute(new TransactionCallback<String>() {
            @Override
            public String doInTransaction(TransactionStatus ts) {
            	jsdBorrowCashDao.updateById(cashDo);
            	jsdBorrowLegalOrderCashDao.updateById(orderCashDo);
            	jsdBorrowLegalOrderDao.updateById(orderDo);
                return "success";
            }
        });
    }

	
	/**
     * 获取风控分层利率
     * @param openId
     * @return
     */
	@Override
	public BigDecimal getRiskDailyRate(String openId) {
        BigDecimal riskRateDaily = BigDecimal.valueOf(0.005); // TODO 数据库配置利率
        try {
            String riskRate = oriRateUtil.getOriRateNoticeRequest(openId); //风控返回的数据为日利率，并除以1000
            if( StringUtils.isNotBlank(riskRate) ) {
            	if(BigDecimal.ZERO.compareTo(new BigDecimal(riskRate)) == 0) {
            		logger.error("openId=" + openId + ", riskRate from xgxy is 0.00 !");
            	}else {
            		riskRateDaily = new BigDecimal(riskRate).divide(BigDecimal.valueOf(1000), 6, RoundingMode.HALF_UP);
            	}
            }else {
            	logger.error("openId=" + openId + ", riskRate from xgxy is null!");
            }
        } catch (Exception e) {
            logger.error("openId=" + openId + ", occur exception when getRiskDailyRate, msg=" + e.getMessage(), e);
        }
        return riskRateDaily;
    }
	
	@Override
	public BigDecimal calcuTotalAmount(JsdBorrowCashDo cashDo, JsdBorrowLegalOrderCashDo orderCashDo) {
		BigDecimal cashTotalAmount = BigDecimalUtil.add(cashDo.getAmount(), cashDo.getInterestAmount(), cashDo.getPoundageAmount(), cashDo.getOverdueAmount(),
				  	cashDo.getSumRepaidInterest(), cashDo.getSumRepaidPoundage(), cashDo.getSumRepaidOverdue());
		BigDecimal orderTotalAmount = BigDecimal.ZERO;
		if(orderCashDo != null) {
			orderTotalAmount = BigDecimalUtil.add(orderCashDo.getAmount(), orderCashDo.getInterestAmount(), orderCashDo.getPoundageAmount(), orderCashDo.getOverdueAmount(),
					orderCashDo.getSumRepaidInterest(), orderCashDo.getSumRepaidPoundage(), orderCashDo.getSumRepaidOverdue());
		}
		return cashTotalAmount.add(orderTotalAmount);
	}
	
	@Override
	public BigDecimal calcuUnrepayAmount(JsdBorrowCashDo cashDo, JsdBorrowLegalOrderCashDo orderCashDo) {
		BigDecimal totalAmount = this.calcuTotalAmount(cashDo, orderCashDo);
		return totalAmount.subtract(cashDo.getRepayAmount()).subtract(orderCashDo.getRepaidAmount());
	}
	
	/**
	 * 解析各项利息费用
	 * @param borrowAmount
	 * @param borrowType
	 * @param oriRate
	 * @return
	 */
	public void resolve(TrialBeforeBorrowBo bo) {
		TrialBeforeBorrowReq req = bo.req;
		
		BigDecimal titularBorrowAmount = req.amount; // 并非真实借款额
		BigDecimal borrowDay = new BigDecimal(req.term);

		ResourceRateInfoBo borrowRateInfo = jsdResourceService.getRateInfo(req.term);
		
		BigDecimal borrowISRate = borrowRateInfo.interestRate.add(borrowRateInfo.serviceRate);
        BigDecimal borrowInterestRate = borrowRateInfo.interestRate;
        BigDecimal borrowOverdueRate = borrowRateInfo.overdueRate;
        
        // 借款 利息 与 服务费 乘法表达式可复用左侧
        BigDecimal borrowinterestLeft = borrowInterestRate.divide(BigDecimal.valueOf(Constants.ONE_YEAY_DAYS), 6, RoundingMode.HALF_UP).multiply(borrowDay);
        BigDecimal borrowISLeft = borrowISRate.divide(BigDecimal.valueOf(Constants.ONE_YEAY_DAYS), 6, RoundingMode.HALF_UP).multiply(borrowDay);
        
        BigDecimal titularInterestAmount = borrowinterestLeft.multiply(titularBorrowAmount);
        BigDecimal titularServiceAmount = borrowISLeft.multiply(titularBorrowAmount).subtract(titularInterestAmount);
        BigDecimal totalProfit = bo.riskDailyRate.multiply(titularBorrowAmount).multiply(borrowDay);
        
        BigDecimal finalDiffProfit = totalProfit.subtract(titularInterestAmount).subtract(titularServiceAmount);
        logger.info("resolve borrow amount, openId=" + req.openId + ", actualDiffProfit=" + finalDiffProfit);
        if (finalDiffProfit.compareTo(BigDecimal.ZERO) <= 0) {
        	finalDiffProfit = BigDecimal.ZERO;
        }
        
        BigDecimal actualOrderAmount = finalDiffProfit.setScale(-1, RoundingMode.UP);	//最终商品价格,以10取整
        BigDecimal actualBorrowAmount = titularBorrowAmount.subtract(actualOrderAmount);//真实借款额
        TrialBeforeBorrowResp resp = new TrialBeforeBorrowResp();
        
        //处理借款相关利息
        BigDecimal actualBorrowInterestAmount = borrowinterestLeft.multiply(actualBorrowAmount).setScale(2, RoundingMode.HALF_UP);
        BigDecimal actualBorrowServiceAmount = borrowISLeft.multiply(actualBorrowAmount).setScale(2, RoundingMode.DOWN).subtract(actualBorrowInterestAmount);
        resp.borrowAmount = actualBorrowAmount.setScale(2, RoundingMode.HALF_UP).toString();
        resp.arrivalAmount = actualBorrowAmount.setScale(2, RoundingMode.HALF_UP).toString();
        resp.interestRate = borrowInterestRate.setScale(2, RoundingMode.HALF_UP).toString();
        resp.interestAmount = actualBorrowInterestAmount.toString();
        resp.serviceRate = borrowRateInfo.serviceRate.setScale(2, RoundingMode.HALF_UP).toString();
        resp.serviceAmount = actualBorrowServiceAmount.toString();
        resp.overdueRate = borrowOverdueRate.setScale(2, RoundingMode.HALF_UP).toString();
        
        //处理搭售商品相关利息
        ResourceRateInfoBo orderRateInfo = jsdResourceService.getOrderRateInfo(req.term);
        BigDecimal orderInterestRate = orderRateInfo.interestRate;
        BigDecimal orderISRate = orderRateInfo.interestRate.add(orderRateInfo.serviceRate);
        BigDecimal orderOverdueRate = orderRateInfo.overdueRate;
        BigDecimal actualOrderInterestAmount = orderInterestRate.divide(BigDecimal.valueOf(Constants.ONE_YEAY_DAYS), 6, RoundingMode.HALF_UP)
        			.multiply(borrowDay).multiply(actualOrderAmount).setScale(2, RoundingMode.HALF_UP);
        BigDecimal actualOrderServiceAmount = orderISRate.divide(BigDecimal.valueOf(Constants.ONE_YEAY_DAYS), 6, RoundingMode.HALF_UP)
        			.multiply(borrowDay).multiply(actualOrderAmount).setScale(2, RoundingMode.DOWN).subtract(actualOrderInterestAmount);
        resp.totalDiffFee = actualOrderAmount.toPlainString();
        resp.sellInterestFee = actualOrderInterestAmount.toString();
        resp.sellInterestRate = orderInterestRate.setScale(2, RoundingMode.HALF_UP);
        resp.sellServiceFee = actualOrderServiceAmount.toString();
        resp.sellServiceRate = orderRateInfo.serviceRate.setScale(2, RoundingMode.HALF_UP);
        resp.sellOverdueRate = orderOverdueRate.setScale(2, RoundingMode.HALF_UP);
        
        BigDecimal totalAmount = BigDecimalUtil.add(actualBorrowAmount, actualBorrowInterestAmount, actualBorrowServiceAmount, 
    			actualOrderAmount, actualOrderInterestAmount, actualOrderServiceAmount);
        resp.totalAmount = totalAmount.toString();
        resp.billAmount = new BigDecimal[]{totalAmount.setScale(2, RoundingMode.HALF_UP)};
        
        // 1、借款费用【借款利息金额+借款服务费金额】元，其中借款利息【借款利息金额】元，借款服务费【借款服务费金额】元。2、商品费用【分期利息金额+分期服务费金额】元，其中分期利息【分期利息金额】元，分期服务费【分期服务费金额】元。
        resp.remark = "1、借款费用" + BigDecimalUtil.add(actualBorrowInterestAmount, actualBorrowServiceAmount) + "元，"
        			+ "其中借款利息" + actualBorrowInterestAmount + "元，借款服务费" + actualBorrowServiceAmount + "元。"
        			+ "2、商品费用" + BigDecimalUtil.add(actualOrderInterestAmount, actualOrderServiceAmount) + "元，"
					+ "其中分期利息" + actualOrderInterestAmount + "元，分期服务费"+ actualOrderServiceAmount +"元。";
        
        bo.resp = resp;
    }
	
	
	@Override
	public void dealBorrowSucc(Long cashId, String outTradeNo) {
		JsdBorrowCashDo cashDo = jsdBorrowCashDao.getById(cashId);
		if(cashDo == null) {
			throw new FanbeiException("dealBorrowSucc, can't find refer borrowCash by id=" + cashId);
		}
		logger.info("dealBorrowSucc, borrowCashId="+ cashId + ", borrowNo=" + cashDo.getBorrowNo()
			+ ", tradeNoXgxy=" + cashDo.getTradeNoXgxy() + ", tradeNoUps=" + cashDo.getTradeNoUps());
		
		JsdBorrowLegalOrderDo orderDo = jsdBorrowLegalOrderDao.getLastOrderByBorrowId(cashId);
		JsdBorrowLegalOrderCashDo orderCashDo = jsdBorrowLegalOrderCashDao.getLastOrderCashByBorrowId(cashId);
		
		Date currDate = new Date(System.currentTimeMillis());
		Date arrivalEnd = DateUtil.getEndOfDatePrecisionSecond(currDate);
        Date repaymentDate = DateUtil.addDays(arrivalEnd, Integer.valueOf(cashDo.getType()) - 1);
		cashDo.setGmtArrival(currDate);
        cashDo.setGmtPlanRepayment(repaymentDate);
        cashDo.setStatus(JsdBorrowCashStatus.TRANSFERRED.name());
        cashDo.setTradeNoUps(outTradeNo);
        
        orderDo.setStatus(JsdBorrowLegalOrderStatus.AWAIT_DELIVER.name());
        orderCashDo.setStatus(JsdBorrowLegalOrderCashStatus.AWAIT_REPAY.name());
        orderCashDo.setGmtPlanRepay(repaymentDate);
        this.transUpdate(cashDo, orderDo, orderCashDo);
        
        jsdNoticeRecord(cashDo,"", XgxyBorrowNotifyStatus.SUCCESS.name());
	}

	@Override
	public void dealBorrowFail(Long cashId, String outTradeNo, String failMsg) {
		JsdBorrowCashDo cashDo = jsdBorrowCashDao.getById(cashId);
		if(cashDo == null) {
			throw new FanbeiException("dealBorrowFail, can't find refer borrowCash by id=" + cashId);
		}
		logger.info("dealBorrowFail, borrowCashId="+ cashId + ", borrowNo=" + cashDo.getBorrowNo()
			+ ", tradeNoXgxy=" + cashDo.getTradeNoXgxy() + ", tradeNoUps=" + cashDo.getTradeNoUps());
		
		JsdBorrowLegalOrderDo orderDo = jsdBorrowLegalOrderDao.getLastOrderByBorrowId(cashId);
		JsdBorrowLegalOrderCashDo orderCashDo = jsdBorrowLegalOrderCashDao.getLastOrderCashByBorrowId(cashId);
		cashDo.setTradeNoUps(outTradeNo);
		
		dealBorrowFail(cashDo, orderDo, orderCashDo, failMsg);
	}
	
	@Override
	public void dealBorrowFail(JsdBorrowCashDo cashDo, JsdBorrowLegalOrderDo orderDo, JsdBorrowLegalOrderCashDo orderCashDo, String failMsg) {
        cashDo.setStatus(JsdBorrowCashStatus.CLOSED.name());
        cashDo.setRemark(failMsg);
        cashDo.setGmtClose(new Date());
        
        orderDo.setStatus(JsdBorrowLegalOrderStatus.CLOSED.name());
        orderCashDo.setStatus(JsdBorrowLegalOrderCashStatus.CLOSED.name());
        this.transUpdate(cashDo, orderDo, orderCashDo);


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