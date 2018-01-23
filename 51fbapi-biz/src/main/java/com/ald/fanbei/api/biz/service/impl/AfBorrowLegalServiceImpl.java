package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfBorrowCacheAmountPerdayService;
import com.ald.fanbei.api.biz.service.AfBorrowCashService;
import com.ald.fanbei.api.biz.service.AfBorrowLegalService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.AfBorrowCashRejectType;
import com.ald.fanbei.api.common.enums.AfBorrowCashReviewStatus;
import com.ald.fanbei.api.common.enums.AfBorrowCashStatus;
import com.ald.fanbei.api.common.enums.AfCounponStatus;
import com.ald.fanbei.api.common.enums.AfResourceSecType;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.ResourceType;
import com.ald.fanbei.api.common.enums.RiskStatus;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
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
	AfBorrowCacheAmountPerdayService afBorrowCacheAmountPerdayService;
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
		BorrowLegalHomeInfoBo bo = new BorrowLegalHomeInfoBo();
		bo.isLogin = true;
		bo.rejectCode = AfBorrowCashRejectType.PASS.name();
		
		this.dealResource(bo, userAccount); 		// 处理 额度 信息
		this.dealBorrow(bo, userAccount);  	// 处理 借款/续期 信息
		
		this.dealFinal(bo, userAccount);		// 汇总处理

		return bo;
	}
	
	private void dealResource(BorrowLegalHomeInfoBo bo, AfUserAccountDo userAccount) {
		Map<String, Object> oldBorrowCfg = afResourceService.getBorrowCfgInfo();

		//获取配置的公司名称
		AfResourceDo companyInfo = afResourceService.getConfigByTypesAndSecType(ResourceType.BORROW_CASH_COMPANY_NAME.getCode(), AfResourceSecType.BORROW_CASH_COMPANY_NAME.getCode());
		bo.companyName = companyInfo != null? companyInfo.getValue() : "";
		 
		//获取配置的银行利率 TODO
		BigDecimal bankRate = new BigDecimal(oldBorrowCfg.get("bankRate").toString());
		BigDecimal bankDouble = new BigDecimal(oldBorrowCfg.get("bankDouble").toString());
		BigDecimal bankService = bankRate.multiply(bankDouble).divide(new BigDecimal(Constants.ONE_YEAY_DAYS), 6, RoundingMode.HALF_UP);
		bo.interestRate = bankService.toString();
		bo.overdueRate = oldBorrowCfg.get("overduePoundage").toString();
		bo.borrowCashDay = oldBorrowCfg.get("borrowCashDay").toString();
		bo.lender = oldBorrowCfg.get("lender").toString();
		
		//获取用户 最大/最小 借款额
		AfResourceDo legalBorrowCfg = afResourceService.getConfigByTypesAndSecType(Constants.BORROW_RATE, Constants.BORROW_CASH_INFO_LEGAL);
		BigDecimal maxAmount = new BigDecimal(legalBorrowCfg != null ? legalBorrowCfg.getValue1() : "");
		BigDecimal usableAmount = userAccount.getAuAmount().subtract(userAccount.getUsedAmount());
		maxAmount = maxAmount.compareTo(usableAmount) < 0 ? maxAmount : usableAmount;
		bo.maxQuota = this.calculateMaxAmount(maxAmount);
		
		bo.minQuota = new BigDecimal(legalBorrowCfg != null ? legalBorrowCfg.getValue4() : "");
		
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
		
		afBorrowCacheAmountPerdayService.record(); // TODO 意义?
	}
	
	private void dealFinal(BorrowLegalHomeInfoBo bo, AfUserAccountDo userAccount) {
		// 借款总开关
		Map<String, Object> oldBorrowCfg = afResourceService.getBorrowCfgInfo();
		if (YesNoStatus.NO.getCode().equals(oldBorrowCfg.get("supuerSwitch").toString()) ) {
			bo.rejectCode = AfBorrowCashRejectType.SWITCH_OFF.name();
			return;
		}
		
		AfUserAuthDo afUserAuthDo = afUserAuthService.getUserAuthInfoByUserId(userAccount.getUserId());
		//检查是否认证过
		if (StringUtils.equals(RiskStatus.NO.getCode(), afUserAuthDo.getRiskStatus())) {
			bo.rejectCode = AfBorrowCashRejectType.NO_AUTHZ.name();
			return;
		}
		
		//检查强风控
		// TODO
		
		//检查弱风控
		if ( AfBorrowCashStatus.closed.getCode().equals(bo.borrowStatus) 
					&& AfBorrowCashReviewStatus.refuse.getCode().equals(bo.borrowStatus) ) {
			AfResourceDo afResourceDo = afResourceService.getConfigByTypesAndSecType(AfResourceType.RiskManagementBorrowcashLimit.getCode(), AfResourceSecType.RejectTimePeriod.getCode());
			if (afResourceDo != null && AfCounponStatus.O.getCode().equals(afResourceDo.getValue4())) {
				Integer rejectTimePeriod = NumberUtil.objToIntDefault(afResourceDo.getValue1(), 0);
				Date desTime = DateUtil.addDays(bo.borrowGmtApply, rejectTimePeriod);
				if (DateUtil.getNumberOfDatesBetween(DateUtil.formatDateToYYYYMMdd(desTime), DateUtil.getToday()) < 0) { // 风控拒绝日期内
					bo.rejectCode = AfBorrowCashRejectType.NO_PASS_WEAK_RISK.name();
					return;
				}
			}
		}
		
		//检查额度
		if(bo.minQuota.compareTo(userAccount.getAuAmount()) > 0) {
			bo.rejectCode = AfBorrowCashRejectType.QUOTA_TOO_SMALL.name();
			return;
		}
		
		if ( YesNoStatus.NO.getCode().equals(afUserAuthDo.getZmStatus()) 
					&& YesNoStatus.YES.getCode().equals(afUserAuthDo.getRiskStatus()) ) {
			throw new FanbeiException(FanbeiExceptionCode.ZM_STATUS_EXPIRED);
		}
	}
	
	private BorrowLegalHomeInfoBo processUnlogin(){
		BorrowLegalHomeInfoBo bo = new BorrowLegalHomeInfoBo();
		
		bo.isLogin = false;

		bo.interestRate = null;	// TODO
		bo.poundageRate = null; // TODO 
		bo.overdueRate = null; 	// TODO
		bo.companyName = null; 	// TODO
		bo.borrowCashDay = null;// TODO
		bo.lender = null;		// TODO

		// 获取后台配置的最大金额和最小金额
		AfResourceDo rateInfoDo = afResourceService.getConfigByTypesAndSecType(Constants.BORROW_RATE, Constants.BORROW_CASH_INFO_LEGAL);
		bo.maxQuota = new BigDecimal(rateInfoDo.getValue4());
		bo.minQuota = new BigDecimal(rateInfoDo.getValue1());
		
		afBorrowCacheAmountPerdayService.record(); // TODO 意义?

		// TODO 借款总开关配置判断
		
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
