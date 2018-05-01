package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.third.util.yibaopay.JsonUtils;
import com.ald.fanbei.api.dal.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.impl.AfResourceServiceImpl.BorrowLegalCfgBean;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
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
import com.ald.fanbei.api.dal.dao.AfRepaymentBorrowCashDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountDao;
import com.ald.fanbei.api.dal.dao.BaseDao;

/**
 * 参考 {@link getLegalBorrowCashHomeInfoV2Api}
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
	@Resource
	private AfRepaymentBorrowCashDao afRepaymentBorrowCashDao;
	@Resource
	private AfBorrowRecycleGoodsService afBorrowRecycleGoodsService;
	@Resource
	private AfBorrowRecycleOrderService afBorrowRecycleOrderService;

	@Override
	public BorrowLegalHomeInfoBo getHomeInfo(Long userId){
		AfUserAccountDo userAccount = afUserAccountDao.getUserAccountInfoByUserId(userId);
		if(userAccount != null) {
			return processLogin(userAccount);
		}else{
			return processUnlogin();
		}
	}

	@Override
	public BorrowLegalHomeInfoBo getRecycleInfo(Long userId) {
		AfUserAccountDo userAccount = afUserAccountDao.getUserAccountInfoByUserId(userId);
		BorrowLegalCfgBean cfgBean = afResourceService.getBorrowLegalCfgInfo();

		BorrowLegalHomeInfoBo bo = new BorrowLegalHomeInfoBo();
		bo.rejectCode = AfBorrowCashRejectType.PASS.name();
		bo.minQuota = cfgBean.minAmount;
		AfBorrowCashDo cashDo = afBorrowCashDao.fetchLastRecycleByUserId(userAccount.getUserId());
		bo.recycleStatus=cashDo.getStatus();
		bo.borrowGmtApply=cashDo.getGmtCreate();
		bo.borrowGmtPlanRepayment=cashDo.getGmtPlanRepayment();
		bo.restUseDays=(int) ((bo.borrowGmtPlanRepayment.getTime() - bo.borrowGmtApply.getTime())) / (1000*3600*24);
		if(DateUtil.getNumberOfDatesBetween(DateUtil.formatDateToYYYYMMdd(cashDo.getGmtPlanRepayment()), DateUtil.getToday())> 0) {
			bo.isBorrowOverdue = true;
		}else {
			bo.isBorrowOverdue = false;
		}
		AfBorrowCashRejectType rejectType = this.rejectCheck(cfgBean, userAccount, cashDo);
		bo.rejectCode = rejectType.name();
		return bo;
	}

	@Override
	public List<BorrowLegalHomeInfoBo> getRecycleRecord(Long userId,Integer start) {
		List<AfBorrowCashDo> doList=afBorrowCashDao.getBorrowRecycleListByUserId(userId,start);
		List<BorrowLegalHomeInfoBo> boList=new ArrayList<>();
		for(AfBorrowCashDo cashDo:doList){
			BorrowLegalHomeInfoBo bo=new BorrowLegalHomeInfoBo();
			bo.recycleId=cashDo.getRecycleId();
			bo.borrowGmtApply=cashDo.getGmtCreate();
			bo.borrowGmtPlanRepayment=cashDo.getGmtPlanRepayment();
			bo.arrivalGmt=cashDo.getGmtArrival();
			bo.reBankId=cashDo.getCardNumber();
			bo.reBankName=cashDo.getCardName();
			bo.borrowStatus=cashDo.getStatus();
			bo.restUseDays=(int) ((bo.borrowGmtPlanRepayment.getTime() - bo.borrowGmtApply.getTime())) / (1000*3600*24);
			addRecycleGoodsInfos(bo,cashDo);
			bo.overdueAmount=afBorrowCashService.calculateLegalRestOverdue(cashDo);
			boList.add(bo);
		}
		return boList;
	}
    void addRecycleGoodsInfos(BorrowLegalHomeInfoBo bo,AfBorrowCashDo cashDo){
		AfBorrowRecycleOrderDo recycleOrderDo=afBorrowRecycleOrderService.getBorrowRecycleOrderById(cashDo.getRecycleId());
		Map<String,String> goodsMap=JsonUtils.fromJsonString(recycleOrderDo.getPropertyValue(),Map.class);
		bo.goodsName=recycleOrderDo.getName();
		bo.goodsModel=goodsMap.get("goodsModel");
		bo.goodsPrice=new BigDecimal(goodsMap.get("maxRecyclePrice"));
	}
	private BorrowLegalHomeInfoBo processLogin(AfUserAccountDo userAccount) {
		BorrowLegalCfgBean cfgBean = afResourceService.getBorrowLegalCfgInfo();
		
		BorrowLegalHomeInfoBo bo = new BorrowLegalHomeInfoBo();
		bo.rejectCode = AfBorrowCashRejectType.PASS.name();
		bo.isLogin = true;
		
		bo.minQuota = cfgBean.minAmount;
		bo.borrowCashDay = cfgBean.borrowCashDay;
		
		AfBorrowCashDo cashDo = afBorrowCashDao.fetchLastByUserId(userAccount.getUserId());
		this.dealBorrow(bo, userAccount, cashDo);  		// 处理 借款/续期 信息
		AfBorrowCashRejectType rejectType = this.rejectCheck(cfgBean, userAccount, cashDo);
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
	
	private void dealBorrow(BorrowLegalHomeInfoBo bo, AfUserAccountDo userAccount, AfBorrowCashDo lastBorrowCash) {
		if(lastBorrowCash == null) {
			bo.hasBorrow = false;
			return;
		}
		
		String status = lastBorrowCash.getStatus();
		if( AfBorrowCashStatus.finsh.getCode().equals(status) 
				|| AfBorrowCashStatus.closed.getCode().equals(status)) {
			bo.hasBorrow = false;
			return;
		}
		AfRepaymentBorrowCashDo repayment = afRepaymentBorrowCashDao.getProcessingRepaymentByBorrowId(lastBorrowCash.getRid());
		if(repayment != null) {
			status = AfBorrowCashStatus.repaying.getCode();
			bo.repayingAmount = repayment.getRepaymentAmount();
		}
		bo.borrowStatus = status;
		
		bo.hasBorrow = true;
		bo.borrowId = lastBorrowCash.getRid();
		bo.borrowAmount = lastBorrowCash.getAmount();
		bo.borrowArrivalAmount = lastBorrowCash.getArrivalAmount();
		bo.borrowRestAmount = afBorrowCashService.calculateLegalRestAmount(lastBorrowCash);
		bo.borrowGmtApply = lastBorrowCash.getGmtCreate();
		bo.borrowGmtPlanRepayment = lastBorrowCash.getGmtPlanRepayment();
		if(lastBorrowCash.getOverdueAmount().compareTo(BigDecimal.ZERO) > 0) { 
			bo.isBorrowOverdue = true;
		}else {
			bo.isBorrowOverdue = false;
		}
	}
	private AfBorrowCashRejectType rejectCheck(BorrowLegalCfgBean cfgBean, AfUserAccountDo userAccount, AfBorrowCashDo lastBorrowCash) {
		// 借款总开关
		if (YesNoStatus.NO.getCode().equals(cfgBean.supuerSwitch) ) {
			return AfBorrowCashRejectType.SWITCH_OFF;
		}
		
		AfUserAuthDo afUserAuthDo = afUserAuthService.getUserAuthInfoByUserId(userAccount.getUserId());
		if(afUserAuthDo == null) {
			return AfBorrowCashRejectType.NO_AUTHZ;
		}
		
		String authStatus = afUserAuthDo.getRiskStatus();
		if(RiskStatus.A.getCode().equals(authStatus)) {
			return AfBorrowCashRejectType.NO_AUTHZ;
		}
		
		if (RiskStatus.NO.getCode().equals(authStatus)) {
			return AfBorrowCashRejectType.NO_PASS_STRO_RISK;
		}
		
		// 检查上笔贷款
		if (lastBorrowCash != null && AfBorrowCashStatus.closed.getCode().equals(lastBorrowCash.getStatus()) 
					&& AfBorrowCashReviewStatus.refuse.getCode().equals(lastBorrowCash.getReviewStatus()) ) {
			AfResourceDo afResourceDo = afResourceService.getConfigByTypesAndSecType(AfResourceType.RiskManagementBorrowcashLimit.getCode(), AfResourceSecType.RejectTimePeriod.getCode());
			if (afResourceDo != null && AfCounponStatus.O.getCode().equals(afResourceDo.getValue4())) {
				Integer rejectTimePeriod = NumberUtil.objToIntDefault(afResourceDo.getValue1(), 0);
				Date desTime = DateUtil.addDays(lastBorrowCash.getGmtCreate(), rejectTimePeriod);
				if (DateUtil.getNumberOfDatesBetween(DateUtil.formatDateToYYYYMMdd(desTime), DateUtil.getToday()) < 0) { // 风控拒绝日期内
					return AfBorrowCashRejectType.NO_PASS_WEAK_RISK;
				}
			}
		}
		
		//检查额度
		if(cfgBean.minAmount.compareTo(userAccount.getAuAmount()) > 0) {
			return AfBorrowCashRejectType.QUOTA_TOO_SMALL;
		}
		
		return AfBorrowCashRejectType.PASS;
	}
	
	private BorrowLegalHomeInfoBo processUnlogin(){
		BorrowLegalCfgBean cfgBean = afResourceService.getBorrowLegalCfgInfo();
		
		BorrowLegalHomeInfoBo bo = new BorrowLegalHomeInfoBo();
		bo.rejectCode = AfBorrowCashRejectType.PASS.name();
		bo.isLogin = false;
		bo.maxQuota = this.calculateMaxAmount(cfgBean.maxAmount);
		bo.minQuota = cfgBean.minAmount;
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
		public BigDecimal repayingAmount;
		public Date borrowGmtApply;
		public Date borrowGmtPlanRepayment;
		public boolean isBorrowOverdue;

		//回收状态与borrowStatus用同一个数据库属性
		public int restUseDays;
		public String recycleStatus;
		public Long recycleId;
		public String goodsName;
		public String goodsModel;
		public BigDecimal goodsPrice;
		public Date arrivalGmt;
		public String reBankId;
		public String reBankName;
		public BigDecimal overdueAmount;
	}


}
