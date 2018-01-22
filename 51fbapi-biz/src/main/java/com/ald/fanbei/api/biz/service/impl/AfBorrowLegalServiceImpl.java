package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfBorrowCacheAmountPerdayService;
import com.ald.fanbei.api.biz.service.AfBorrowCashService;
import com.ald.fanbei.api.biz.service.AfBorrowLegalService;
import com.ald.fanbei.api.biz.service.AfRenewalDetailService;
import com.ald.fanbei.api.biz.service.AfRepaymentBorrowCashService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.service.AfUserOperationLogService;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
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
	AfRenewalDetailService afRenewalDetailService;
	@Resource
	AfBorrowCacheAmountPerdayService afBorrowCacheAmountPerdayService;
	@Resource
	AfRepaymentBorrowCashService afRepaymentBorrowCashService;
	@Resource
	AfUserOperationLogService afUserOperationLogService;
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
	public Map<String, Object> getHomeInfo(Long userId){
		AfUserAccountDo userAccount = afUserAccountDao.getUserAccountInfoByUserId(userId);
		if(userAccount != null) {
			return processLogin(userAccount);
		}else{
			return processUnlogin();
		}
	}
	
	private Map<String, Object> processLogin(AfUserAccountDo userAccount) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("isLogin", YesNoStatus.YES.getCode());
		data.put("isRejectGlobal", false);		// 全局拒绝借款
		
		this.dealResoure(data, userAccount); 	// 处理 后台配置 信息
		this.dealBorrow(data, userAccount);  	// 处理 借款/续期 信息
		
		this.dealFinal(data, userAccount);		// 汇总处理

		return data;
	}
	
	private void dealResoure(Map<String, Object> data, AfUserAccountDo userAccount) {
		Map<String, Object> oldBorrowCfg = afResourceService.getBorrowCfgInfo();

		//获取配置的公司名称
		AfResourceDo companyInfo = afResourceService.getConfigByTypesAndSecType(ResourceType.BORROW_CASH_COMPANY_NAME.getCode(), AfResourceSecType.BORROW_CASH_COMPANY_NAME.getCode());
		data.put("companyName", companyInfo != null? companyInfo.getValue() : ""); // 债权公司名称
		
		//获取配置的银行利率
		BigDecimal bankRate = new BigDecimal(oldBorrowCfg.get("bankRate").toString());
		BigDecimal bankDouble = new BigDecimal(oldBorrowCfg.get("bankDouble").toString());
		BigDecimal bankService = bankRate.multiply(bankDouble).divide(new BigDecimal(Constants.ONE_YEAY_DAYS), 6, RoundingMode.HALF_UP);
		data.put("bankDoubleRate", bankService.toString());				// 逾期费率
		
		//获取用户 最大/最小 借款额
		AfResourceDo legalBorrowCfg = afResourceService.getConfigByTypesAndSecType(Constants.BORROW_RATE, Constants.BORROW_CASH_INFO_LEGAL);
		BigDecimal maxAmount = new BigDecimal(legalBorrowCfg != null ? legalBorrowCfg.getValue1() : "");
		BigDecimal minAmount = new BigDecimal(legalBorrowCfg != null ? legalBorrowCfg.getValue4() : "");
		BigDecimal usableAmount = userAccount.getAuAmount().subtract(userAccount.getUsedAmount());
		maxAmount = maxAmount.compareTo(usableAmount) < 0 ? maxAmount : usableAmount;
		data.put("maxAmount", this.calculateMaxAmount(maxAmount));		// 逾期费率
		data.put("minAmount", minAmount);								// 逾期费率
		
		data.put("overdueRate", oldBorrowCfg.get("overduePoundage")); 	// 逾期费率
		data.put("borrowCashDay", oldBorrowCfg.get("borrowCashDay")); 
		data.put("lender", oldBorrowCfg.get("lender")); 				// 债权人信息
	}
	
	private void dealBorrow(Map<String, Object> data, AfUserAccountDo userAccount) {
		Long userId = userAccount.getUserId();
		
		AfBorrowCashDo cashDo = afBorrowCashDao.fetchLastByUserId(userId);
		if(cashDo == null) {
			data.put("hasBorrow", false);
			return;
		}
		
		String status = cashDo.getStatus();
		if( AfBorrowCashStatus.finsh.getCode().equals(status) 
				|| AfBorrowCashStatus.transedfail.getCode().equals(status)
				|| AfBorrowCashStatus.closed.getCode().equals(status)) {
			data.put("hasBorrow", false);
			return;
		}
		
		data.put("hasBorrow", true);
		data.put("borrowId", cashDo.getRid());
		data.put("borrowStatus", status); 											// 借款状态
		data.put("borrowAmount", cashDo.getAmount());								// 借款额
		data.put("borrowArrivalAmount", cashDo.getArrivalAmount());					// 借款实际到款额
		data.put("borrowRestAmount", afBorrowCashService.calculateLegalRestAmount(cashDo));// 剩余应还额
		data.put("borrowGmtApply", cashDo.getGmtCreate());
		data.put("borrowGmtPlanRepayment", cashDo.getGmtPlanRepayment());			// 最后还款日
		if(cashDo.getOverdueAmount().compareTo(BigDecimal.ZERO) > 0) { 
			data.put("borrowIsOverdue", true);										// 借款是否有逾期未还
		}
		
		afBorrowCacheAmountPerdayService.record(); // TODO
		
		// 处理因为风控拒绝的借款
		if ( AfBorrowCashStatus.closed.getCode().equals(cashDo.getStatus()) 
				&& AfBorrowCashReviewStatus.refuse.getCode().equals(cashDo.getReviewStatus()) ) {
			AfResourceDo afResourceDo = afResourceService.getConfigByTypesAndSecType(AfResourceType.RiskManagementBorrowcashLimit.getCode(), AfResourceSecType.RejectTimePeriod.getCode());
			if (afResourceDo != null && AfCounponStatus.O.getCode().equals(afResourceDo.getValue4())) {
				Integer rejectTimePeriod = NumberUtil.objToIntDefault(afResourceDo.getValue1(), 0);
				Date desTime = DateUtil.addDays(cashDo.getGmtCreate(), rejectTimePeriod);
				if (DateUtil.getNumberOfDatesBetween(DateUtil.formatDateToYYYYMMdd(desTime), DateUtil.getToday()) < 0) { // 风控拒绝日期内
					data.put("isRejectGlobal", true);
					data.put("rejectReason", "近期借款被风控拒绝过");
				}
			}
		}
	}
	
	private void dealFinal(Map<String, Object> data, AfUserAccountDo userAccount) {
		AfUserAuthDo afUserAuthDo = afUserAuthService.getUserAuthInfoByUserId(userAccount.getUserId());
		if (StringUtils.equals(RiskStatus.NO.getCode(), afUserAuthDo.getRiskStatus())) {
			data.put("isRejectGlobal", true);
			data.put("rejectReason", "风控审核未通过");
		}
		
		if (YesNoStatus.NO.getCode().equals(afUserAuthDo.getZmStatus()) && YesNoStatus.YES.getCode().equals(afUserAuthDo.getRiskStatus())) {
			throw new FanbeiException(FanbeiExceptionCode.ZM_STATUS_EXPIRED);
		}
	}
	
	private Map<String, Object> processUnlogin(){
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("isLogin", YesNoStatus.NO.getCode());
		
		// TODO
		
		return data;
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

}
