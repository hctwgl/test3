package com.ald.fanbei.api.web.h5.api.loan;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfLoanPeriodsService;
import com.ald.fanbei.api.biz.service.AfLoanService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.service.AfUserBankcardService;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.dao.AfLoanDao;
import com.ald.fanbei.api.dal.dao.AfLoanPeriodsDao;
import com.ald.fanbei.api.dal.domain.AfLoanDo;
import com.ald.fanbei.api.dal.domain.AfLoanPeriodsDo;
import com.ald.fanbei.api.web.common.H5Handle;
import com.ald.fanbei.api.web.common.H5HandleResponse;
import com.ald.fanbei.api.web.vo.AfLoanVo;
import com.alibaba.fastjson.JSON;

/**
 * @Description: 白领贷借款信息
 * @Copyright (c) 浙江阿拉丁电子商务股份有限公司 All Rights Reserved.
 * @author yanghailong
 * @date 2018年1月22日
 */
@Component("loanInfoApi")
public class LoanInfoApi implements H5Handle {

	@Resource
	AfResourceService afResourceService;
	@Resource
	AfUserAuthService afUserAuthService;
	@Resource
	AfUserBankcardService afUserBankcardService;
	@Resource
	AfUserAccountService afUserAccountService;
	@Resource
	AfLoanService afLoanService;
	@Resource
	AfLoanPeriodsService afLoanPeriodsService;
	
	@Resource
	BizCacheUtil bizCacheUtil;
	@Resource
	RiskUtil riskUtil;
	
	@Override
	public H5HandleResponse process(Context context) {
		
		H5HandleResponse resp = new H5HandleResponse(context.getId(), FanbeiExceptionCode.SUCCESS);
		try {
			
			Long loanId = (Long) context.getData("loanId");
			if(loanId == null || loanId <= 0){
				throw new FanbeiException(FanbeiExceptionCode.PARAM_ERROR);
			}
			
			// 借款信息
			AfLoanDo loanDo = afLoanService.getById(loanId);
			if(loanDo == null){
				throw new FanbeiException(FanbeiExceptionCode.BORROW_DETAIL_NOT_EXIST_ERROR);
			}
			// 当前待还分期信息
			AfLoanPeriodsDo afLoanPeriod = afLoanPeriodsService.getLastActivePeriodByLoanId(loanId);
			if(afLoanPeriod == null){
				throw new FanbeiException(FanbeiExceptionCode.LOAN_PERIOD_NOT_EXIST_ERROR);
			}
			
			// 每期待还金额
			BigDecimal perPeriodAmount = BigDecimalUtil.add(afLoanPeriod.getAmount(),afLoanPeriod.getServiceFee(),afLoanPeriod.getInterestFee());
			
			AfLoanVo loanVo = new AfLoanVo();
			loanVo.setLoanPeriodsId(afLoanPeriod.getRid());		// 当前借款期数id
			loanVo.setNper(afLoanPeriod.getNper());		// 当前期数
			loanVo.setCurrentPeriodAmount(BigDecimalUtil.add(perPeriodAmount,afLoanPeriod.getOverdueAmount()));	// 本月待还金额
			loanVo.setGmtPlanRepay(afLoanPeriod.getGmtPlanRepay());		// 本月还款时间
			loanVo.setStatus(afLoanPeriod.getOverdueStatus());		// 逾期状态
			loanVo.setPerPeriodAmount(perPeriodAmount);		// 每期还款金额
			loanVo.setRepayDay(DateUtil.getDay(afLoanPeriod.getGmtPlanRepay()));	// 每月还款时间
			
			loanVo.setAmount(loanDo.getAmount());		// 借款金额
			loanVo.setTotalServiceFee(loanDo.getTotalServiceFee());	// 总手续费
			loanVo.setTotalInterestFee(loanDo.getTotalInterestFee());	// 总利息
			loanVo.setPeriods(loanDo.getPeriods());		// 还款期限
			loanVo.setLoanProduct(loanDo.getPrdType());	// 借钱产品
			loanVo.setCardNumber(loanDo.getCardNo());	// 银行卡号
			loanVo.setCardName(loanDo.getCardName());	// 银行卡名称
			loanVo.setGmtCreate(loanDo.getGmtCreate());		// 申请时间
			loanVo.setGmtArrival(loanDo.getGmtArrival());	// 打款时间
			
			resp.setResponseData(JSON.toJSONString(loanVo));
			
		} catch (Exception e) {
			logger.error("/loanInfoApi error = {}", e.getStackTrace());
			resp.setResponseData("获取借款信息失败");
		}
		
		return resp;
	}

}
