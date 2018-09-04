package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.bo.jsd.TrialBeforeBorrowBo;
import com.ald.fanbei.api.biz.bo.jsd.TrialBeforeBorrowBo.TrialBeforeBorrowReq;
import com.ald.fanbei.api.biz.bo.jsd.TrialBeforeBorrowBo.TrialBeforeBorrowResp;
import com.ald.fanbei.api.biz.service.JsdBorrowCashService;
import com.ald.fanbei.api.biz.service.JsdResourceService;
import com.ald.fanbei.api.biz.service.impl.JsdResourceServiceImpl.ResourceRateInfoBo;
import com.ald.fanbei.api.biz.third.util.OriRateUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.JsdBorrowCashStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.JsdBorrowCashDao;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashDo;


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
    OriRateUtil oriRateUtil;

	@Override
	public BaseDao<JsdBorrowCashDo, Long> getDao() {
		return jsdBorrowCashDao;
	}

	@Override
	public JsdBorrowCashDo getByBorrowNo(String borrowNo) {
		return jsdBorrowCashDao.getByBorrowNo(borrowNo);
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

	
	/**
     * 获取风控分层利率
     * @param openId
     * @return
     */
	public BigDecimal getRiskOriRate(String openId) {
        BigDecimal oriRate = BigDecimal.valueOf(0.003);
        try {
            String poundageRate = oriRateUtil.getOriRateNoticeRequest(openId);
            if(StringUtils.isBlank(poundageRate)) {
                poundageRate = "0.003";
            }
            oriRate = new BigDecimal(poundageRate);
        } catch (Exception e) {
            logger.info(openId + "从西瓜信用获取分层用户额度失败：" + e);
        }
        return oriRate;
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
		
		BigDecimal oriRateDaily = this.getRiskOriRate(req.openId);
		BigDecimal borrowAmount = new BigDecimal(req.amount);
		BigDecimal borrowDay = new BigDecimal(req.term);

		ResourceRateInfoBo rateInfo = jsdResourceService.getRateInfo(req.term);
		
        BigDecimal legalInterestRate = rateInfo.interestRate;
        BigDecimal legalServiceRate = rateInfo.serviceRate;
        BigDecimal overdueRate = rateInfo.overdueRate;
        
        BigDecimal interestAmount = legalInterestRate.divide(BigDecimal.valueOf(Constants.ONE_YEAY_DAYS), 6, RoundingMode.HALF_UP).multiply(borrowAmount).multiply(borrowDay).setScale(2);
        BigDecimal serviceAmount = legalServiceRate.divide(BigDecimal.valueOf(Constants.ONE_YEAY_DAYS), 6, RoundingMode.HALF_UP).multiply(borrowAmount).multiply(borrowDay).setScale(2);
        
        BigDecimal totalProfit = oriRateDaily.multiply(borrowAmount).multiply(borrowDay).setScale(2);
        BigDecimal legalProfit = interestAmount.add(serviceAmount);
        
        BigDecimal orderCashService = BigDecimal.valueOf(10);
        BigDecimal finalDiffProfit = totalProfit.subtract(legalProfit).subtract(orderCashService);
        if (finalDiffProfit.compareTo(BigDecimal.ZERO) <= 0) {
        	finalDiffProfit = BigDecimal.ZERO;
        }
        
        TrialBeforeBorrowResp resp = new TrialBeforeBorrowResp();
        resp.totalAmount = borrowAmount.add(totalProfit).toString();
        resp.arrivalAmount = borrowAmount.toString();
        resp.interestRate = legalInterestRate.toString();
        resp.interestAmount = interestAmount.toString();
        resp.serviceRate = legalServiceRate.toString();
        resp.serviceAmount = serviceAmount.toString();
        resp.overdueRate = overdueRate.toString();
        resp.billAmount = new BigDecimal[]{borrowAmount};
        resp.remark = "本金" + borrowAmount + "元,总利息" + legalProfit + "元";
        resp.totalDiffFee = finalDiffProfit.toString();
        resp.sellInterestFee = BigDecimal.ZERO.toString();
        resp.sellServiceFee = orderCashService.toString();
        
        bo.resp = resp;
    }
	
	
	@Override
	public void dealBorrowSucc(Long cashId, String outTradeNo) {
		final JsdBorrowCashDo cashDo = jsdBorrowCashDao.getById(cashId);
		
		Date currDate = new Date(System.currentTimeMillis());
		cashDo.setGmtArrival(currDate);
		
        Date arrivalEnd = DateUtil.getEndOfDatePrecisionSecond(currDate);
        Date repaymentDay = DateUtil.addDays(arrivalEnd, Integer.valueOf(cashDo.getType()) - 1);
        cashDo.setGmtPlanRepayment(repaymentDay);
        
        // TODO 事务更新 order待发货，orderCash待还
        jsdBorrowCashDao.updateById(cashDo);
	}

	@Override
	public void dealBorrowFail(Long cashId, String outTradeNo, String failMsg) {
		final JsdBorrowCashDo cashDo = jsdBorrowCashDao.getById(cashId);
		
        Date cur = new Date();
        cashDo.setStatus(JsdBorrowCashStatus.CLOSED.name());
        cashDo.setRemark("UPS异步打款失败，" + failMsg);
        cashDo.setGmtModified(cur);
        cashDo.setGmtClose(cur);
        
        // TODO 事务关闭 order 和 orderCash
        jsdBorrowCashDao.updateById(cashDo);
	}

}