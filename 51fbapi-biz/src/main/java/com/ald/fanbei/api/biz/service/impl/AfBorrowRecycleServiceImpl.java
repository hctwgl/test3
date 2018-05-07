package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.dal.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.ald.fanbei.api.biz.service.impl.AfResourceServiceImpl.BorrowLegalCfgBean;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.biz.third.util.yibaopay.JsonUtils;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.enums.AfBorrowCashRejectType;
import com.ald.fanbei.api.common.enums.AfBorrowCashReviewStatus;
import com.ald.fanbei.api.common.enums.AfBorrowCashStatus;
import com.ald.fanbei.api.common.enums.AfBorrowRecycleStatus;
import com.ald.fanbei.api.common.enums.AfCounponStatus;
import com.ald.fanbei.api.common.enums.AfLoanStatus;
import com.ald.fanbei.api.common.enums.AfResourceSecType;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.RiskStatus;
import com.ald.fanbei.api.common.enums.SceneType;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.AfBorrowCashDao;
import com.ald.fanbei.api.dal.dao.AfBorrowRecycleOrderDao;
import com.ald.fanbei.api.dal.dao.AfRepaymentBorrowCashDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountDao;
import com.ald.fanbei.api.dal.dao.BaseDao;
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
    ApplyLegalBorrowCashService borrowCashService;
    @Resource
    AfUserAuthService afUserAuthService;
    @Resource
    AfUserAuthStatusService afUserAuthStatusService;
    @Resource
    AfUserBankcardService afUserBankcardService;
    @Resource
    BizCacheUtil bizCacheUtil;
    @Resource
    RiskUtil riskUtil;
    @Resource
    TransactionTemplate transactionTemplate;
    @Resource
    AfIdNumberService idNumberService;
    @Resource
    private AfUserAccountDao afUserAccountDao;
    @Resource
    private AfBorrowCashDao afBorrowCashDao;
    @Resource
    private AfRepaymentBorrowCashDao afRepaymentBorrowCashDao;
    @Resource
    AfBorrowRecycleOrderDao borrowRecycleOrderDao;
    @Resource
    private AfBorrowRecycleOrderDao afBorrowRecycleOrderDao;
    @Resource
    private AfUserAccountSenceService afUserAccountSenceService;


    public BorrowRecycleHomeInfoBo getRecycleInfo(Long userId){
        AfUserAccountDo userAccount = afUserAccountDao.getUserAccountInfoByUserId(userId);
        if(userAccount!=null){
            return processLogin(userAccount);
        }else {
            return unLogin();
        }
    }
    public BorrowRecycleHomeInfoBo unLogin() {
        BorrowLegalCfgBean cfgBean = afResourceService.getBorrowLegalCfgInfo();
        BorrowRecycleHomeInfoBo bo = new BorrowRecycleHomeInfoBo();
        bo.rejectCode = AfBorrowCashRejectType.PASS.name();
        bo.isLogin = false;
        bo.borrowCashDay=cfgBean.borrowCashDay;
        bo.useableAmount = this.calculateMaxAmount(cfgBean.maxAmount);
        bo.minQuota = cfgBean.minAmount;
        if (YesNoStatus.NO.getCode().equals(cfgBean.supuerSwitch) ) {
            bo.rejectCode = AfBorrowCashRejectType.SWITCH_OFF.name();
        }
        return bo;
    }

    public BorrowRecycleHomeInfoBo processLogin(AfUserAccountDo userAccount) {
        BorrowLegalCfgBean cfgBean = afResourceService.getBorrowLegalCfgInfo();
        AfUserBankcardDo userBankcardDo=afUserBankcardService.getUserMainBankcardByUserId(userAccount.getUserId());
        BorrowRecycleHomeInfoBo bo = new BorrowRecycleHomeInfoBo();
        bo.isLogin = true;
        bo.minQuota = cfgBean.minAmount;
        bo.borrowCashDay=cfgBean.borrowCashDay;
        bo.useableAmount =this.calculateMaxAmount(afUserAccountSenceService.getLoanMaxPermitQuota(userAccount.getUserId(),SceneType.CASH,cfgBean.maxAmount));;
        if (userBankcardDo != null){
            bo.reMainBankId=userBankcardDo.getCardNumber();
            bo.reMainBankName= userBankcardDo.getBankName();
        }
        AfBorrowCashDo cashDo = afBorrowCashDao.fetchLastRecycleByUserId(userAccount.getUserId());
        bo.rejectCode=AfBorrowCashRejectType.PASS.name();
        checkCreditAction(bo,userAccount,cfgBean.minAmount,cfgBean.supuerSwitch);//h5返回认证状态判断并跳转页面
        if (cashDo == null) {
            bo.minQuota = cfgBean.minAmount;
            bo.isBorrowOverdue = false;
            bo.recycleStatus ="UNSUBMIT";
            return bo;
        }
        bo.hasBorrow=true;
        bo.borrowId=cashDo.getRid();
        AfBorrowRecycleOrderDo orderDo = afBorrowRecycleOrderDao.getBorrowRecycleOrderByBorrowId(cashDo.getRid());
        Map<String, String> goodsMap = JsonUtils.fromJsonString(orderDo.getPropertyValue(), Map.class);
        if (goodsMap != null) {
            bo.goodsName = orderDo.getGoodsName();
            bo.goodsUrl= orderDo.getGoodsImg();
            bo.goodsModel = goodsMap.get("goodsModel");
        }
        bo.recycleStatus =AfBorrowRecycleStatus.findByCashStatus(cashDo.getStatus()).getCode();
        AfRepaymentBorrowCashDo processRepayment = afRepaymentBorrowCashDao.getProcessingRepaymentByBorrowId(cashDo.getRid());
        if(processRepayment != null) {
            bo.recycleStatus = AfLoanStatus.REPAYING.name();
        }
        bo.borrowGmtApply = cashDo.getGmtCreate();
        bo.borrowGmtPlanRepayment = cashDo.getGmtPlanRepayment();
        bo.defaultFine = BigDecimalUtil.add(cashDo.getRateAmount(), cashDo.getOverdueAmount(),cashDo.getSumRate(),cashDo.getSumOverdue());
        bo.repayingAmount = cashDo.getRepayAmount();
        bo.borrowAmount = cashDo.getAmount();
        bo.restUseDays = -(int) DateUtil.getNumberOfDatesBetween(DateUtil.formatDateToYYYYMMdd(cashDo.getGmtPlanRepayment()), DateUtil.getToday());
        if (DateUtil.getNumberOfDatesBetween(DateUtil.formatDateToYYYYMMdd(cashDo.getGmtPlanRepayment()), DateUtil.getToday()) > 0) {
            bo.isBorrowOverdue = true;
        } else {
            bo.isBorrowOverdue = false;
        }
        AfBorrowCashRejectType rejectType = this.rejectCheck(cfgBean, userAccount, cashDo);
//        bo.rejectCode = rejectType.name();
        return bo;
    }

    void checkCreditAction(BorrowRecycleHomeInfoBo bo,AfUserAccountDo userAccount,BigDecimal minAmount,String supuerSwitch){
        AfUserAuthStatusDo afUserAuthStatusDo = afUserAuthStatusService.getAfUserAuthStatusByUserIdAndScene(userAccount.getUserId(), "CASH");
        AfUserAuthDo userAuth = afUserAuthService.getUserAuthInfoByUserId(userAccount.getUserId());
        AfIdNumberDo idNumberDo = idNumberService.getIdNumberInfoByUserId(userAccount.getUserId());
        if (YesNoStatus.NO.getCode().equals(supuerSwitch) ) {
            bo.rejectCode = AfBorrowCashRejectType.SWITCH_OFF.name();
        }
        if (userAuth == null){
            bo.rejectCode=AfBorrowCashRejectType.NO_AUTHZ.name();
            bo.action="DO_SCAN_ID";
        }else if (userAuth.getFacesStatus().equals("N")){
            bo.rejectCode=AfBorrowCashRejectType.NO_AUTHZ.name();
            bo.action="DO_SCAN_ID";
        }else if (userAuth.getBankcardStatus().equals("N")){
            bo.rejectCode=AfBorrowCashRejectType.NO_AUTHZ.name();
            bo.action="DO_BIND_CARD";
        }else if (userAuth.getRiskStatus().equals("N")){
            bo.rejectCode=AfBorrowCashRejectType.NO_PASS_STRO_RISK.name();
        }else if (userAuth.getRiskStatus().equals("A")||userAuth.getRiskStatus().equals("P")){
            bo.rejectCode=AfBorrowCashRejectType.NO_PASS_STRO_RISK.name();
        }else if (afUserAuthStatusDo != null && afUserAuthStatusDo.getStatus().equals("Y")){
            bo.rejectCode=AfBorrowCashRejectType.PASS.name();
            //检查额度
           if (borrowCashService.checkRiskRefusedResult(userAccount.getUserId())){
                bo.rejectCode=AfBorrowCashRejectType.NO_PASS_WEAK_RISK.name();
            }

        }
        if (minAmount.compareTo(userAccount.getAuAmount().subtract(userAccount.getUsedAmount())) > 0) {
            bo.rejectCode=AfBorrowCashRejectType.QUOTA_TOO_SMALL.name();
        }
        if (idNumberDo != null){
            bo.params = "{\"idNumber\":\""+idNumberDo.getCitizenId()+"\",\"realName\":\""+idNumberDo.getName()+"\"}";
        }
    }

    /**
     * 计算最多能计算多少额度 150取100 250.37 取200
     *
     * @param usableAmount
     * @return
     */
    private BigDecimal calculateMaxAmount(BigDecimal usableAmount) {
        // 可使用额度
        Integer amount = usableAmount.intValue();

        return new BigDecimal(amount / 100 * 100);

    }
    @Override
    public List<BorrowRecycleHomeInfoBo> getRecycleRecord(Long userId) {
        List<AfBorrowCashDto> doList = afBorrowCashDao.getBorrowRecycleListByUserId(userId);
        List<BorrowRecycleHomeInfoBo> boList = new ArrayList<>();
        for (AfBorrowCashDto cashDo : doList) {
            BorrowRecycleHomeInfoBo bo = new BorrowRecycleHomeInfoBo();
            bo.borrowId = cashDo.getRid();
            bo.borrowGmtApply = cashDo.getGmtCreate();
            bo.goodsPrice=cashDo.getAmount();
            bo.borrowGmtPlanRepayment = cashDo.getGmtPlanRepayment();
            bo.arrivalGmt = cashDo.getGmtArrival();
            bo.reBankId = cashDo.getCardNumber();
            bo.reBankName = cashDo.getCardName();
            bo.borrowStatus = cashDo.getStatus();
            bo.type = cashDo.getType();
            addRecycleGoodsInfos(bo, cashDo);
            bo.overdueAmount = BigDecimalUtil.add(cashDo.getRateAmount(), cashDo.getOverdueAmount(),cashDo.getSumRate(),cashDo.getSumOverdue());
            boList.add(bo);
        }
        return boList;
    }

    @Override
    public BorrowRecycleHomeInfoBo getRecycleRecordByBorrowId(Long borrowId) {
        AfBorrowCashDo cashDo = afBorrowCashDao.getBorrowCashByrid(borrowId);
        AfIdNumberDo idNumberDo = idNumberService.getIdNumberInfoByUserId(cashDo.getUserId());
        BorrowRecycleHomeInfoBo bo = new BorrowRecycleHomeInfoBo();
        bo.borrowNo = cashDo.getBorrowNo();
        bo.borrowId = cashDo.getRid();
        bo.borrowGmtApply = cashDo.getGmtCreate();
        bo.goodsPrice=cashDo.getAmount();
        bo.borrowGmtPlanRepayment = cashDo.getGmtPlanRepayment();
        bo.arrivalGmt = cashDo.getGmtArrival();
        bo.type = cashDo.getType();
        bo.reBankId = cashDo.getCardNumber();
        bo.reBankName = cashDo.getCardName();
        bo.borrowStatus = cashDo.getStatus();
        bo.params = "{\"idNumber\":\""+idNumberDo.getCitizenId()+"\",\"realName\":\""+idNumberDo.getName()+"\"}";
        addRecycleGoodsInfos(bo, cashDo);
        bo.overdueAmount = BigDecimalUtil.add(cashDo.getRateAmount(), cashDo.getOverdueAmount(),cashDo.getSumRate(),cashDo.getSumOverdue());
        return bo;
    }

    @Override
    public boolean isRecycleBorrow(Long borrowId) {
        if (afBorrowRecycleOrderDao.tuchByBorrowId(borrowId) != null) {
            return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private void addRecycleGoodsInfos(BorrowRecycleHomeInfoBo bo, AfBorrowCashDo cashDo) {
        AfBorrowRecycleOrderDo recycleOrderDo = afBorrowRecycleOrderDao.getBorrowRecycleOrderByBorrowId(cashDo.getRid());
        if (recycleOrderDo != null) {
            Map<String, String> goodsMap = JsonUtils.fromJsonString(recycleOrderDo.getPropertyValue(), Map.class);
            bo.goodsName = recycleOrderDo.getGoodsName();
            bo.goodsModel = goodsMap.get("goodsModel");
        }
    }

    private AfBorrowCashRejectType rejectCheck(BorrowLegalCfgBean cfgBean, AfUserAccountDo userAccount, AfBorrowCashDo lastBorrowCash) {
        // 借款总开关
        if (YesNoStatus.NO.getCode().equals(cfgBean.supuerSwitch)) {
            return AfBorrowCashRejectType.SWITCH_OFF;
        }

        AfUserAuthDo afUserAuthDo = afUserAuthService.getUserAuthInfoByUserId(userAccount.getUserId());
        if (afUserAuthDo == null) {
            return AfBorrowCashRejectType.NO_AUTHZ;
        }

        String authStatus = afUserAuthDo.getRiskStatus();
        if (RiskStatus.A.getCode().equals(authStatus)) {
            return AfBorrowCashRejectType.NO_AUTHZ;
        }

        if (RiskStatus.NO.getCode().equals(authStatus)) {
            return AfBorrowCashRejectType.NO_PASS_STRO_RISK;
        }

        // 检查上笔贷款
        if (lastBorrowCash != null && AfBorrowCashStatus.closed.getCode().equals(lastBorrowCash.getStatus())
                && AfBorrowCashReviewStatus.refuse.getCode().equals(lastBorrowCash.getReviewStatus())) {
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
        if (cfgBean.minAmount.compareTo(userAccount.getAuAmount()) > 0) {
            return AfBorrowCashRejectType.QUOTA_TOO_SMALL;
        }

        return AfBorrowCashRejectType.PASS;
    }

    @Override
    public BaseDao<AfBorrowCashDo, Long> getDao() {
        return null;
    }

    public final static class BorrowRecycleHomeInfoBo {
        public String rejectCode; //拒绝码，通过则为 "PASS"

        public boolean isLogin;
        public String companyName;
        public String interestRate;
        public String poundageRate;
        public String overdueRate;
        public String lender;
        public String borrowCashDay;
        public String params;
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
        public String type;
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
        public String borrowNo;
        public BigDecimal useableAmount;
        public String goodsUrl;
        public String reMainBankId;
        public String reMainBankName;
        public String action;

    }

    @Override
    public Long addBorrowRecord(final AfBorrowCashDo afBorrowCashDo, final AfBorrowRecycleOrderDo recycleOrderDo) {
        return transactionTemplate.execute(new TransactionCallback<Long>() {
            @Override
            public Long doInTransaction(TransactionStatus ts) {
                afBorrowCashService.addBorrowCash(afBorrowCashDo);
                Long borrowId = afBorrowCashDo.getRid();
                recycleOrderDo.setBorrowId(borrowId);
                afBorrowRecycleOrderDao.saveRecord(recycleOrderDo);
                return borrowId;
            }
        });
    }

}
