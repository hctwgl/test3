package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfBorrowCashService;
import com.ald.fanbei.api.biz.service.AfBorrowRecycleGoodsService;
import com.ald.fanbei.api.biz.service.AfBorrowRecycleOrderService;
import com.ald.fanbei.api.biz.service.AfBorrowRecycleService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.service.impl.AfResourceServiceImpl.BorrowLegalCfgBean;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.biz.third.util.yibaopay.JsonUtils;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.enums.AfBorrowCashRejectType;
import com.ald.fanbei.api.common.enums.AfBorrowCashReviewStatus;
import com.ald.fanbei.api.common.enums.AfBorrowCashStatus;
import com.ald.fanbei.api.common.enums.AfCounponStatus;
import com.ald.fanbei.api.common.enums.AfResourceSecType;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.RiskStatus;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.dao.AfBorrowCashDao;
import com.ald.fanbei.api.dal.dao.AfRepaymentBorrowCashDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountDao;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfBorrowRecycleOrderDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.dto.AfBorrowCashDto;

/**
 * @author ZJF
 * @version 1.0.0 初始化
 * @date 2017-12-10 10:14:21
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afBorrowRecycleService")
public class AfBorrowRecycleServiceImpl extends ParentServiceImpl<AfBorrowCashDo, Long> implements AfBorrowRecycleService {

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
	@SuppressWarnings("unchecked")
	public BorrowRecycleHomeInfoBo getRecycleInfo(Long userId) {
		AfUserAccountDo userAccount = afUserAccountDao.getUserAccountInfoByUserId(userId);
		BorrowLegalCfgBean cfgBean = afResourceService.getBorrowLegalCfgInfo();

		BorrowRecycleHomeInfoBo bo = new BorrowRecycleHomeInfoBo();
		bo.rejectCode = AfBorrowCashRejectType.PASS.name();
		bo.minQuota = cfgBean.minAmount;
		AfBorrowCashDo cashDo = afBorrowCashDao.fetchLastRecycleByUserId(userAccount.getUserId());
		if (cashDo == null){
			bo.isBorrowOverdue = false;
			return bo;
		}
		AfBorrowRecycleOrderDo orderDo = afBorrowRecycleOrderService.getBorrowRecycleOrderByBorrowId(cashDo.getRid());
		Map<String,String> goodsMap=JsonUtils.fromJsonString(orderDo.getPropertyValue(),Map.class);
		if (goodsMap != null){
			bo.goodsName=orderDo.getGoodsName();
			bo.goodsModel=goodsMap.get("goodsModel");
			bo.goodsPrice=new BigDecimal(goodsMap.get("maxRecyclePrice"));
		}
		bo.recycleStatus=cashDo.getStatus();
		bo.borrowGmtApply=cashDo.getGmtCreate();
		bo.borrowGmtPlanRepayment=cashDo.getGmtPlanRepayment();
		bo.defaultFine = BigDecimalUtil.add(cashDo.getRateAmount(),cashDo.getOverdueAmount());
		bo.repayingAmount = cashDo.getRepayAmount();
		bo.borrowAmount = cashDo.getAmount();
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
	public List<BorrowRecycleHomeInfoBo> getRecycleRecord(Long userId) {
		List<AfBorrowCashDto> doList=afBorrowCashDao.getBorrowRecycleListByUserId(userId);
		List<BorrowRecycleHomeInfoBo> boList=new ArrayList<>();
		for(AfBorrowCashDto cashDo:doList){
			BorrowRecycleHomeInfoBo bo=new BorrowRecycleHomeInfoBo();
			bo.borrowId=cashDo.getRid();
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
    @SuppressWarnings("unchecked")
	private void addRecycleGoodsInfos(BorrowRecycleHomeInfoBo bo,AfBorrowCashDo cashDo){
		AfBorrowRecycleOrderDo recycleOrderDo=afBorrowRecycleOrderService.getBorrowRecycleOrderByBorrowId(cashDo.getRid());
		if (recycleOrderDo != null){
			Map<String,String> goodsMap=JsonUtils.fromJsonString(recycleOrderDo.getPropertyValue(),Map.class);
			bo.goodsName=recycleOrderDo.getGoodsName();
			bo.goodsModel=goodsMap.get("goodsModel");
			bo.goodsPrice=new BigDecimal(goodsMap.get("maxRecyclePrice"));
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
	
	@Override
	public BaseDao<AfBorrowCashDo, Long> getDao() {
		return null;
	}
	
	public final static class BorrowRecycleHomeInfoBo{
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
		public BigDecimal defaultFine;
		public Date borrowGmtApply;
		public Date borrowGmtPlanRepayment;
		public boolean isBorrowOverdue;

		//回收状态与borrowStatus用同一个数据库属性
		public int restUseDays;
		public String recycleStatus;
		public String goodsName;
		public String goodsModel;
		public BigDecimal goodsPrice;
		public Date arrivalGmt;
		public String reBankId;
		public String reBankName;
		public BigDecimal overdueAmount;
	}


}
