package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.util.Date;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfLoanService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.AfLoanHomeRejectType;
import com.ald.fanbei.api.dal.dao.AfLoanDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountDao;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.domain.AfLoanDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;



/**
 * 贷款业务ServiceImpl
 * 
 * @author Jiang Rongbo
 * @version 1.0.0 初始化
 * @date 2018-01-19 16:50:31
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afLoanService")
public class AfLoanServiceImpl extends ParentServiceImpl<AfLoanDo, Long> implements AfLoanService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfLoanServiceImpl.class);
    
    @Resource
	AfResourceService afResourceService;
	@Resource
	AfUserAuthService afUserAuthService;
	
	@Resource
	RiskUtil riskUtil;
	
	@Resource
	private AfUserAccountDao afUserAccountDao;
	@Resource
    private AfLoanDao afLoanDao;
	
	@Override
	public LoanHomeInfoBo getHomeInfo(Long userId){
		AfUserAccountDo userAccount = afUserAccountDao.getUserAccountInfoByUserId(userId);
		if(userAccount != null) {
			return processLogin(userAccount);
		}else{
			return processUnlogin();
		}
	}
	
	private LoanHomeInfoBo processLogin(AfUserAccountDo userAccount) {
		LoanHomeInfoBo bo = new LoanHomeInfoBo();
		bo.isLogin = true;
		bo.rejectCode = AfLoanHomeRejectType.PASS.name();
		
		this.dealResource(bo, userAccount); 	// 处理 额度 信息
		this.dealLoan(bo, userAccount);  		// 处理 贷款 信息
		
		this.dealFinal(bo, userAccount);		// 汇总处理

		return bo;
	}
	
	private void dealResource(LoanHomeInfoBo bo, AfUserAccountDo userAccount) {
	}
	
	private void dealLoan(LoanHomeInfoBo bo, AfUserAccountDo userAccount) {
		Long userId = userAccount.getUserId();
		
		AfLoanDo loanDo = afLoanDao.fetchLastByUserId(userId);
		if(loanDo == null) {
			bo.hasLoan = false;
			return;
		}
		
		String status = loanDo.getStatus();
		if(false) { // TODO
			bo.hasLoan = false;
			return;
		}
		
		bo.hasLoan = true;
		bo.loanId = loanDo.getRid();
		bo.loanStatus = status;
		bo.loanAmount = loanDo.getAmount();
		bo.loanArrivalAmount = loanDo.getArrivalAmount();
		bo.loanRestAmount = null; // TODO
		bo.loanGmtApply = loanDo.getGmtCreate();
		bo.loanGmtPlanRepayment = loanDo.getGmtPlanRepayment();
		if(loanDo.getOverdueAmount().compareTo(BigDecimal.ZERO) > 0) { 
			bo.isBorrowOverdue = true;
		}else {
			bo.isBorrowOverdue = false;
		}
	}
	
	private void dealFinal(LoanHomeInfoBo bo, AfUserAccountDo userAccount) {
		// 借款总开关
		if() { // TODO
			bo.rejectCode = AfLoanHomeRejectType.SWITCH_OFF.name();
			return;
		}
		
		AfUserAuthDo afUserAuthDo = afUserAuthService.getUserAuthInfoByUserId(userAccount.getUserId());
		//检查是否认证过
		
		//检查强风控
		// TODO
		
		//检查弱风控
		
		//检查额度
		
	}
	
	private LoanHomeInfoBo processUnlogin(){
		LoanHomeInfoBo bo = new LoanHomeInfoBo();
		
		bo.isLogin = false;

		bo.interestRate = null;	// TODO
		bo.poundageRate = null; // TODO 
		bo.overdueRate = null; 	// TODO
		bo.companyName = null; 	// TODO
		bo.loanCashDay = null;// TODO
		bo.lender = null;		// TODO

		// 获取后台配置的最大金额和最小金额
		AfResourceDo rateInfoDo = afResourceService.getConfigByTypesAndSecType(Constants.BORROW_RATE, Constants.BORROW_CASH_INFO_LEGAL);
		bo.maxAmount = new BigDecimal(rateInfoDo.getValue4());
		bo.minAmount = new BigDecimal(rateInfoDo.getValue1());
		
		// TODO 借款总开关配置判断
		
		return bo;
	}
	
	public final static class LoanHomeInfoBo{
		public String rejectCode; //拒绝码，通过则为 "PASS"
		
		public boolean isLogin;
		public String companyName;
		public String interestRate;
		public String poundageRate;
		public String overdueRate;
		public String lender;
		public String loanCashDay;
		
		public BigDecimal maxAmount;
		public BigDecimal minAmount;
		
		public boolean hasLoan;
		public Long loanId;
		public String loanStatus;
		public BigDecimal loanAmount;
		public BigDecimal loanArrivalAmount;
		public BigDecimal loanRestAmount;
		public Date loanGmtApply;
		public Date loanGmtPlanRepayment;
		public boolean isBorrowOverdue;
	}
    
    
	@Override
	public BaseDao<AfLoanDo, Long> getDao() {
		return afLoanDao;
	}

}