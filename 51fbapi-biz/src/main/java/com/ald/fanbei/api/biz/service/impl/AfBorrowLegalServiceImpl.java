package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.util.Date;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfBorrowCashService;
import com.ald.fanbei.api.biz.service.AfBorrowLegalService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.service.impl.AfResourceServiceImpl.BorrowLegalCfgBean;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.AfBorrowCashRejectType;
import com.ald.fanbei.api.common.enums.AfBorrowCashReviewStatus;
import com.ald.fanbei.api.common.enums.AfBorrowCashStatus;
import com.ald.fanbei.api.common.enums.AfCounponStatus;
import com.ald.fanbei.api.common.enums.AfResourceSecType;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.RiskStatus;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.dao.AfBorrowCashDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountDao;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;

/**
 * 参考 {@link GetBorrowCashHomeInfoApi}
 * @author ZJF
 * @version 1.0.0 初始化
 * @date 2017-12-10 10:14:21
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afBorrowLegalService")
public class AfBorrowLegalServiceImpl extends ParentServiceImpl<AfBorrowCashDo, Long> implements AfBorrowLegalService {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Resource
	AfResourceService afResourceService;
	@Resource
	AfBorrowCashService afBorrowCashService;
	@Resource
	AfUserAuthService afUserAuthService;
	
	@Resource
	BizCacheUtil bizCacheUtil;
	@Resource
	RiskUtil riskUtil;
	
	@Resource
	private AfUserAccountDao afUserAccountDao;
	@Resource
	private AfBorrowCashDao afBorrowCashDao;
	
	@Override
	public BorrowLegalHomeInfoBo getHomeInfo(Long userId){
		AfUserAccountDo userAccount = afUserAccountDao.getUserAccountInfoByUserId(userId);
		if(userAccount != null) {
			return processLogin(userAccount);
		}else{
			return processUnlogin();
		}
	}
	
	private BorrowLegalHomeInfoBo processLogin(AfUserAccountDo userAccount) {
		BorrowLegalCfgBean cfgBean = afResourceService.getBorrowLegalCfgInfo();
		
		BorrowLegalHomeInfoBo bo = new BorrowLegalHomeInfoBo();
		bo.rejectCode = AfBorrowCashRejectType.PASS.name();
		bo.isLogin = true;
		
		bo.minQuota = cfgBean.minAmount;
		bo.borrowCashDay = cfgBean.borrowCashDay;
		
		this.dealBorrow(bo, userAccount);  		// 处理 借款/续期 信息
		AfBorrowCashRejectType rejectType = this.rejectCheck(bo, cfgBean, userAccount);
		bo.rejectCode = rejectType.name();
		
		BigDecimal maxCfgAmount = cfgBean.maxAmount;
		BigDecimal maxAmount = BigDecimal.ZERO;
		if(!AfBorrowCashRejectType.PASS.equals(rejectType)) {
			maxAmount = maxCfgAmount;
		} else if(userAccount != null) {
			BigDecimal usableAmount = userAccount.getAuAmount().subtract(userAccount.getUsedAmount());
			maxAmount = maxCfgAmount.compareTo(usableAmount) < 0 ? maxCfgAmount : usableAmount;
		}
		bo.maxQuota = maxAmount;

		return bo;
	}
	
	private void dealBorrow(BorrowLegalHomeInfoBo bo, AfUserAccountDo userAccount) {
		Long userId = userAccount.getUserId();
		
		AfBorrowCashDo cashDo = afBorrowCashDao.fetchLastByUserId(userId);
		if(cashDo == null) {
			bo.hasBorrow = false;
			return;
		}
		
		String status = cashDo.getStatus();
		if( AfBorrowCashStatus.finsh.getCode().equals(status) 
				|| AfBorrowCashStatus.transedfail.getCode().equals(status)
				|| AfBorrowCashStatus.closed.getCode().equals(status)) {
			bo.hasBorrow = false;
			return;
		}
		
		bo.hasBorrow = true;
		bo.borrowId = cashDo.getRid();
		bo.borrowStatus = status;
		bo.borrowAmount = cashDo.getAmount();
		bo.borrowArrivalAmount = cashDo.getArrivalAmount();
		bo.borrowRestAmount = afBorrowCashService.calculateLegalRestAmount(cashDo);
		bo.borrowGmtApply = cashDo.getGmtCreate();
		bo.borrowGmtPlanRepayment = cashDo.getGmtPlanRepayment();
		if(cashDo.getOverdueAmount().compareTo(BigDecimal.ZERO) > 0) { 
			bo.isBorrowOverdue = true;
		}else {
			bo.isBorrowOverdue = false;
		}
	}
	private AfBorrowCashRejectType rejectCheck(BorrowLegalHomeInfoBo bo, BorrowLegalCfgBean cfgBean, AfUserAccountDo userAccount) {
		// 借款总开关
		if (YesNoStatus.NO.getCode().equals(cfgBean.supuerSwitch) ) {
			return AfBorrowCashRejectType.SWITCH_OFF;
		}
		
		AfUserAuthDo afUserAuthDo = afUserAuthService.getUserAuthInfoByUserId(userAccount.getUserId());
		//检查是否认证过，是否通过强风控
		if (StringUtils.equals(RiskStatus.NO.getCode(), afUserAuthDo.getRiskStatus())) {
			return AfBorrowCashRejectType.NO_AUTHZ;
		}
		
		// 检查上笔贷款
		if ( AfBorrowCashStatus.closed.getCode().equals(bo.borrowStatus) 
					&& AfBorrowCashReviewStatus.refuse.getCode().equals(bo.borrowStatus) ) {
			AfResourceDo afResourceDo = afResourceService.getConfigByTypesAndSecType(AfResourceType.RiskManagementBorrowcashLimit.getCode(), AfResourceSecType.RejectTimePeriod.getCode());
			if (afResourceDo != null && AfCounponStatus.O.getCode().equals(afResourceDo.getValue4())) {
				Integer rejectTimePeriod = NumberUtil.objToIntDefault(afResourceDo.getValue1(), 0);
				Date desTime = DateUtil.addDays(bo.borrowGmtApply, rejectTimePeriod);
				if (DateUtil.getNumberOfDatesBetween(DateUtil.formatDateToYYYYMMdd(desTime), DateUtil.getToday()) < 0) { // 风控拒绝日期内
					return AfBorrowCashRejectType.NO_PASS_WEAK_RISK;
				}
			}
		}
		
		//检查额度
		if(bo.minQuota.compareTo(userAccount.getAuAmount()) > 0) {
			return AfBorrowCashRejectType.QUOTA_TOO_SMALL;
		}
		
		return AfBorrowCashRejectType.PASS;
	}
	
	private BorrowLegalHomeInfoBo processUnlogin(){
		BorrowLegalCfgBean cfgBean = afResourceService.getBorrowLegalCfgInfo();
		
		BorrowLegalHomeInfoBo bo = new BorrowLegalHomeInfoBo();
		bo.rejectCode = AfBorrowCashRejectType.PASS.name();
		bo.isLogin = false;
		
		AfResourceDo legalBorrowCfg = afResourceService.getConfigByTypesAndSecType(Constants.BORROW_RATE, Constants.BORROW_CASH_INFO_LEGAL);
		BigDecimal maxAmount = new BigDecimal(legalBorrowCfg != null ? legalBorrowCfg.getValue1() : "");
		
		bo.maxQuota = this.calculateMaxAmount(maxAmount);
		bo.minQuota = new BigDecimal(legalBorrowCfg != null ? legalBorrowCfg.getValue4() : "");
		bo.borrowCashDay = cfgBean.borrowCashDay;
		
		if (YesNoStatus.NO.getCode().equals(cfgBean.supuerSwitch) ) {
			bo.rejectCode = AfBorrowCashRejectType.SWITCH_OFF.name();
		}
		
		return bo;
	}
	
	/**
	 * 计算最多能计算多少额度 150取100 250.37 取200
	 * @param usableAmount
	 * @return
	 */
	private BigDecimal calculateMaxAmount(BigDecimal usableAmount) {
		Integer amount = usableAmount.intValue();
		return new BigDecimal(amount / 100 * 100);
	}

	
	@Override
	public BaseDao<AfBorrowCashDo, Long> getDao() {
		return null;
	}
	
	public final static class BorrowLegalHomeInfoBo{
		public String rejectCode; //拒绝码，通过则为 "PASS"
		
		public boolean isLogin;
		public String companyName;
		public String interestRate;
		public String poundageRate;
		public String overdueRate;
		public String lender;
		public String borrowCashDay;
		
		public BigDecimal maxQuota;
		public BigDecimal minQuota;
		
		public boolean hasBorrow;
		public Long borrowId;
		public String borrowStatus;
		public BigDecimal borrowAmount;
		public BigDecimal borrowArrivalAmount;
		public BigDecimal borrowRestAmount;
		public Date borrowGmtApply;
		public Date borrowGmtPlanRepayment;
		public boolean isBorrowOverdue;
	}

}
