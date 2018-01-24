package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.bo.loan.ApplyLoanBo;
import com.ald.fanbei.api.biz.service.AfLoanPeriodsService;
import com.ald.fanbei.api.biz.service.AfLoanService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.common.DBResource;
import com.ald.fanbei.api.common.enums.AfLoanRejectType;
import com.ald.fanbei.api.common.enums.AfLoanStatus;
import com.ald.fanbei.api.common.enums.RiskStatus;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.dal.dao.AfLoanDao;
import com.ald.fanbei.api.dal.dao.AfLoanPeriodsDao;
import com.ald.fanbei.api.dal.dao.AfLoanProductDao;
import com.ald.fanbei.api.dal.dao.AfResourceDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountDao;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.domain.AfLoanDo;
import com.ald.fanbei.api.dal.domain.AfLoanPeriodsDo;
import com.ald.fanbei.api.dal.domain.AfLoanProductDo;
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
    @Resource
	AfResourceService afResourceService;
	@Resource
	AfUserAuthService afUserAuthService;
	@Resource
	AfLoanPeriodsService afLoanPeriodsService;
	
	@Resource
	RiskUtil riskUtil;
	
	@Resource
    private AfLoanDao afLoanDao;
	@Resource
    private AfLoanPeriodsDao afLoanPeriodsDao;
	@Resource
    private AfLoanProductDao afLoanProductDao;
	@Resource
	private AfUserAccountDao afUserAccountDao;
	@Resource
	private AfResourceDao afResourceDao;
	
	@Override
	public LoanHomeInfoBo getHomeInfo(Long userId){
		LoanDBCfgBo loanCfg = this.getDBCfg();
		AfUserAccountDo userAccount = afUserAccountDao.getUserAccountInfoByUserId(userId);
		if(userAccount != null) {
			return processLogin(userAccount, loanCfg);
		}else{
			return processUnlogin(loanCfg);
		}
	}
	
	@Override
	public void doLoan(ApplyLoanBo bo) {
		
	}
	
	public LoanDBCfgBo getDBCfg() {
		LoanDBCfgBo bo = new LoanDBCfgBo();
		
		List<AfResourceDo> loanCfgs = afResourceDao.getConfigByTypes(DBResource.TYPE_LOAN);
		for(AfResourceDo resDo : loanCfgs) {
			if(DBResource.SEC_TYPE_LOAN_SWITCH.equals(resDo.getSecType())) {
				bo.SWITCH = resDo.getValue();
			}
			if(DBResource.SEC_TYPE_LOAN_MAX_QUOTA.equals(resDo.getSecType())) {
				bo.MAX_QUOTA = new BigDecimal(resDo.getValue());
			}
			if(DBResource.SEC_TYPE_LOAN_MIN_QUOTA.equals(resDo.getSecType())) {
				bo.MIN_QUOTA = new BigDecimal(resDo.getValue());
			}
			if(DBResource.SEC_TYPE_LOAN_INTEREST_RATE.equals(resDo.getSecType())) {
				bo.INTEREST_RATE = resDo.getValue();
			}
			if(DBResource.SEC_TYPE_LOAN_POUNDAGE_RATE.equals(resDo.getSecType())) {
				bo.POUNDAGE_RATE = resDo.getValue();
			}
			if(DBResource.SEC_TYPE_LOAN_OVERDUE_RATE.equals(resDo.getSecType())) {
				bo.OVERDUE_RATE = resDo.getValue();
			}
		}
		
		return bo;
	}
	
	
	private LoanHomeInfoBo processLogin(AfUserAccountDo userAccount, LoanDBCfgBo loanCfg) {
		LoanHomeInfoBo bo = new LoanHomeInfoBo();
		bo.isLogin = true;
		
		this.dealResource(bo, userAccount, loanCfg);// 处理 配置 信息
		this.dealLoan(bo, userAccount, loanCfg);  	// 处理 贷款 信息
		bo.rejectCode = rejectCheck(userAccount, loanCfg).name();

		return bo;
	}
	
	private void dealResource(LoanHomeInfoBo bo, AfUserAccountDo userAccount, LoanDBCfgBo loanCfg) {
		BigDecimal maxAuota = loanCfg.MAX_QUOTA;
		BigDecimal usableAmount = userAccount.getAuAmount().subtract(userAccount.getUsedAmount());
		maxAuota = maxAuota.compareTo(usableAmount) < 0 ? maxAuota : usableAmount;
		bo.maxQuota = maxAuota;
		bo.minQuota = loanCfg.MIN_QUOTA;
		
		bo.interestRate = loanCfg.INTEREST_RATE;
		bo.poundageRate = loanCfg.POUNDAGE_RATE;
		bo.overdueRate = loanCfg.OVERDUE_RATE;
		AfLoanProductDo prdDo = afLoanProductDao.getLast();
		bo.periods = prdDo.getPeriods();
		bo.prdName = prdDo.getName();
		bo.prdType = prdDo.getPrdType();
	}
	
	private void dealLoan(LoanHomeInfoBo bo, AfUserAccountDo userAccount, LoanDBCfgBo loanCfg) {
		Long userId = userAccount.getUserId();
		
		AfLoanDo loanDo = afLoanDao.fetchLastByUserId(userId);
		if(loanDo == null) {
			bo.hasLoan = false;
			return;
		}
		
		String status = loanDo.getStatus();
		if(AfLoanStatus.FINISHED.name().equals(status)
			|| AfLoanStatus.TRANSFER_FAIL.name().equals(status)
			|| AfLoanStatus.CLOSED.name().equals(status)) {
			bo.hasLoan = false;
			return;
		}
		
		bo.hasLoan = true;
		bo.loanId = loanDo.getRid();
		bo.loanStatus = status;
		bo.loanAmount = loanDo.getAmount();
		bo.loanArrivalAmount = loanDo.getArrivalAmount();
		bo.loanGmtApply = loanDo.getGmtCreate();
		
		// 处理贷款当期
		AfLoanPeriodsDo activePeriod = afLoanPeriodsDao.getLastActivePeriodByLoanId(loanDo.getRid());
		bo.curPeriodId = activePeriod.getRid();
		bo.curPeriodAmount = activePeriod.getAmount();
		bo.curPeriodRestAmount = afLoanPeriodsService.calcuRestAmount(activePeriod);
		bo.curPeriodGmtPlanRepay = activePeriod.getGmtPlanRepay();
		if(activePeriod.getOverdueAmount().compareTo(BigDecimal.ZERO) > 0) { 
			bo.isOverdue = true;
		}else {
			bo.isOverdue = false;
		}
	}
	
	private AfLoanRejectType rejectCheck(AfUserAccountDo userAccount, LoanDBCfgBo loanCfg) {
		//贷款总开关
		if(YesNoStatus.YES.getCode().equals(loanCfg.SWITCH)) {
			return AfLoanRejectType.SWITCH_OFF;
		}
		
		// TODO 检查是否认证过
		
		//检查强风控
		AfUserAuthDo afUserAuthDo = afUserAuthService.getUserAuthInfoByUserId(userAccount.getUserId());
		if(RiskStatus.NO.getCode().equals(afUserAuthDo.getRiskStatus())) { 
			return AfLoanRejectType.NO_PASS_STRO_RISK;
		}
		
		// TODO 检查弱风控
		
		// 检查额度
		if(loanCfg.MIN_QUOTA.compareTo(userAccount.getAuAmount()) > 0) {
			return AfLoanRejectType.QUOTA_TOO_SMALL;
		}
		
		return AfLoanRejectType.PASS;
	}
	
	private LoanHomeInfoBo processUnlogin(LoanDBCfgBo loanCfg){
		LoanHomeInfoBo bo = new LoanHomeInfoBo();
		bo.rejectCode = AfLoanRejectType.PASS.name();
		bo.isLogin = false;

		bo.interestRate = loanCfg.INTEREST_RATE;
		bo.poundageRate = loanCfg.POUNDAGE_RATE;
		bo.overdueRate = loanCfg.OVERDUE_RATE;
		bo.maxQuota = loanCfg.MAX_QUOTA;
		bo.minQuota = loanCfg.MIN_QUOTA;
		
		//贷款总开关
		if(YesNoStatus.YES.getCode().equals(loanCfg.SWITCH)) {
			bo.rejectCode = AfLoanRejectType.SWITCH_OFF.name();
		}
		return bo;
	}
	
	public final static class LoanHomeInfoBo{
		public String rejectCode; //拒绝码，通过则为 "PASS"
		
		public boolean isLogin;
		public BigDecimal maxQuota;
		public BigDecimal minQuota;
		public String interestRate;
		public String poundageRate;
		public String overdueRate;
		public int periods;
		public String prdType;
		public String prdName;
		
		public boolean hasLoan;
		public Long loanId;
		public String loanStatus;
		public BigDecimal loanAmount;
		public BigDecimal loanArrivalAmount;
		public Date loanGmtApply;
		
		public Long curPeriodId;
		public BigDecimal curPeriodAmount;
		public BigDecimal curPeriodRestAmount;
		public Date curPeriodGmtPlanRepay;
		public boolean isOverdue;
	}
	
	public final static class LoanDBCfgBo{
		public String SWITCH;
		
		public BigDecimal MAX_QUOTA;
		public BigDecimal MIN_QUOTA;
		public String INTEREST_RATE;
		public String POUNDAGE_RATE;
		public String OVERDUE_RATE;
	}
    
	@Override
	public BaseDao<AfLoanDo, Long> getDao() {
		return afLoanDao;
	}

}