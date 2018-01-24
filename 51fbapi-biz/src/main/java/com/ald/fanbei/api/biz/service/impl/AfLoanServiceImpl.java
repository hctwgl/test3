package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.bo.loan.ApplyLoanBo;
import com.ald.fanbei.api.biz.bo.loan.LoanDBCfgBo;
import com.ald.fanbei.api.biz.bo.loan.LoanHomeInfoBo;
import com.ald.fanbei.api.biz.service.AfLoanPeriodsService;
import com.ald.fanbei.api.biz.service.AfLoanService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.DBResource;
import com.ald.fanbei.api.common.enums.AfLoanRejectType;
import com.ald.fanbei.api.common.enums.AfLoanStatus;
import com.ald.fanbei.api.common.enums.RiskStatus;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
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
 * @author ZJF
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
	BizCacheUtil bizCacheUtil;
	
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
			return dealHomeLogin(userAccount, loanCfg);
		}else{
			return dealHomeUnlogin(loanCfg);
		}
	}
	
	@Override
	public void doLoan(ApplyLoanBo bo) {
		// TODO 询问 风控 是否放行
		
		// TODO 检查我方前置条件
		
		// TODO 处理期数信息，并全部入库
		
		// TODO 调用UPS打款
	}
	
	@Override
	public void dealLoanSucc(Long loanId, String loanNo, String tradeNoOut) {
		// TODO 修改我方记录
		
		// TODO 通知用户
		
		// TODO 通知风控/催收
	}
	
	@Override
	public void dealLoanFail(Long loanId, String loanNo, String tradeNoOut) {
		// TODO 查询出loan 和 loanPeriods记录
		dealLoanFail(null, null); // TODO
	}
	
	@Override
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
	
	@Override
	public BigDecimal getUserLayRate(Long userId) {
		try {
			// TODO FROM cache
			
			// TODO ASK risk
			
			// TODO SAVE cache
			return BigDecimal.ZERO;
		} catch (Exception e) {
			throw new FanbeiException("getUserLayRate error!", e);
		}
	}
	
	/**
	 * 处理登陆场景下首页信息
	 */
	private LoanHomeInfoBo dealHomeLogin(AfUserAccountDo userAccount, LoanDBCfgBo loanCfg) {
		LoanHomeInfoBo bo = new LoanHomeInfoBo();
		bo.isLogin = true;
		
		// 处理 配置 信息
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
		
		this.dealHomeLoginLoan(bo, userAccount, loanCfg);  	// 处理 贷款 信息
		bo.rejectCode = rejectCheck(userAccount, loanCfg).name();

		return bo;
	}
	private void dealHomeLoginLoan(LoanHomeInfoBo bo, AfUserAccountDo userAccount, LoanDBCfgBo loanCfg) {
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
	/**
	 * 处理非登陆场景下首页信息
	 */
	private LoanHomeInfoBo dealHomeUnlogin(LoanDBCfgBo loanCfg){
		LoanHomeInfoBo bo = new LoanHomeInfoBo();
		bo.rejectCode = AfLoanRejectType.PASS.name();
		bo.isLogin = false;

		bo.interestRate = loanCfg.INTEREST_RATE;
		bo.poundageRate = loanCfg.POUNDAGE_RATE;
		bo.overdueRate = loanCfg.OVERDUE_RATE;
		bo.maxQuota = loanCfg.MAX_QUOTA;
		bo.minQuota = loanCfg.MIN_QUOTA;
		
		if(YesNoStatus.YES.getCode().equals(loanCfg.SWITCH)) {//贷款总开关
			bo.rejectCode = AfLoanRejectType.SWITCH_OFF.name();
		}
		return bo;
	}
	
	private void dealLoanFail(AfLoanDo loanDo, List<AfLoanPeriodsDo> periodDos) {
		// TODO 修改我方记录
		
		// TODO 通知用户
	}
	
	/**
	 * 检查用户是否可以进行贷款行为，只有返回PASS才可以放行
	 * @param userAccount
	 * @param loanCfg
	 * @return
	 */
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
	
	@Override
	public BaseDao<AfLoanDo, Long> getDao() {
		return afLoanDao;
	}

}