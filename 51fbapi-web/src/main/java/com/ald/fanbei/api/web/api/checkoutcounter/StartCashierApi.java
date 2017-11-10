package com.ald.fanbei.api.web.api.checkoutcounter;

import com.ald.fanbei.api.biz.bo.RiskCreditBo;
import com.ald.fanbei.api.biz.bo.RiskQueryOverdueOrderRespBo;
import com.ald.fanbei.api.biz.bo.RiskVirtualProductQuotaRespBo;
import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.service.boluome.BoluomeUtil;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.*;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.*;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.dal.domain.dto.AfUserAccountDto;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.BoluomeConfirmOrderVo;
import com.ald.fanbei.api.web.vo.CashierTypeVo;
import com.ald.fanbei.api.web.vo.CashierVo;
import com.ald.fanbei.api.web.vo.ConfirmOrderVo;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.ObjectUtils;
import org.dbunit.util.Base64;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 启动收银台
 * 获取收银台相关配置
 */
@Component("startCashierApi")
public class StartCashierApi implements ApiHandle {
    private static final String USABLED_AMOUNT = "USABLED_AMOUNT";
    private static final String CREDIT_PAYMENT = "CREDIT_PAYMENT|";
    @Resource
    AfOrderService afOrderService;
    @Resource
    AfUserAccountService afUserAccountService;
    @Resource
    AfUserAuthService afUserAuthService;
    @Resource
    AfUserBankcardService afUserBankcardService;
    @Resource
    AfResourceService afResourceService;
    @Resource
    RiskUtil riskUtil;
    @Resource
    BizCacheUtil bizCacheUtil;
    @Resource
    BoluomeUtil boluomeUtil;
    @Resource
    AfUserVirtualAccountService afUserVirtualAccountService;
    @Resource
    AfBorrowService afBorrowService;
    @Resource
    AfBorrowBillService afBorrowBillService;
    @Resource
    AfBorrowCashService afBorrowCashService;
    @Resource
    AfCheckoutCounterService afCheckoutCounterService;

    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
        Map<String, Object> params = requestDataVo.getParams();
        Long orderId = NumberUtil.objToLongDefault(requestDataVo.getParams().get("orderId"), null);
        String orderType = ObjectUtils.toString(params.get("orderType"), "");//添加默认订单类型，todo 记得改回来
        String plantform = ObjectUtils.toString(params.get("plantform"), null);
        String thirdOrderNo = orderType.equals(OrderType.BOLUOME.getCode()) ? ObjectUtils.toString(params.get("orderId"), null) : "";
        AfOrderDo orderInfo = null;
        if (orderType.equals(OrderType.AGENTBUY.getCode()) || orderType.equals(OrderType.SELFSUPPORT.getCode()) || orderType.equals(OrderType.TRADE.getCode())) {
            orderInfo = afOrderService.getOrderById(orderId);
        } else if (orderType.equals(OrderType.BOLUOME.getCode())) {
            //region 菠萝蜜独立逻辑
            if (StringUtils.isEmpty(thirdOrderNo) || StringUtils.isEmpty(plantform)) {
                logger.error("orderId or plantform is empty");
                return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PARAM_ERROR);
            }
            orderInfo = afOrderService.getThirdOrderInfoByOrderTypeAndOrderNo(plantform, thirdOrderNo);
            if (orderInfo == null) {
                try {
                    orderInfo = boluomeUtil.orderSearch(thirdOrderNo);
                } catch (UnsupportedEncodingException e) {
                    logger.info("order compensation query error e = {}", e);
                }
                // 订单补偿
                afOrderService.syncOrderInfo(thirdOrderNo, plantform, orderInfo);
            }
            //endregion
        }
        Long userId = orderInfo.getUserId();
        AfCheckoutCounterDo checkoutCounter;
        CashierVo cashierVo = new CashierVo();
        cashierVo.setCurrentTime(new Date());
        cashierVo.setGmtPayEnd(orderInfo.getGmtPayEnd());


        //兼容各类订单类型
        if (orderInfo.getOrderType().equals(OrderType.BOLUOME.getCode())) {
            cashierVo.setCountDown(true);
            checkoutCounter = afCheckoutCounterService.getByType(orderInfo.getOrderType(), orderInfo.getSecType());
        } else {
            checkoutCounter = afCheckoutCounterService.getByType(orderInfo.getOrderType(), "");
        }

        AfUserAccountDto userDto = afUserAccountService.getUserAndAccountByUserId(userId);
        AfUserAuthDo authDo = afUserAuthService.getUserAuthInfoByUserId(userId);
        //判断额度支付是否可用
        cashierVo.setOrderId(orderInfo.getRid());
        cashierVo.setOrderType(orderType);
        cashierVo.setAmount(orderInfo.getActualAmount());
        cashierVo.setRebatedAmount(orderInfo.getRebateAmount());
        cashierVo.setAp(canConsume(userDto, authDo, orderInfo, checkoutCounter));

        AfUserBankcardDo bankInfo = afUserBankcardService.getUserMainBankcardByUserId(userId);

        cashierVo.setMainBankCard(bankInfo);
        String isSupplyCertify = "N";
        if (StringUtil.equals(authDo.getFundStatus(), YesNoStatus.YES.getCode()) && StringUtil.equals(authDo.getJinpoStatus(), YesNoStatus.YES.getCode()) &&
                StringUtil.equals(authDo.getCreditStatus(), YesNoStatus.YES.getCode()) && StringUtil.equals(authDo.getAlipayStatus(), YesNoStatus.YES.getCode())
                && StringUtil.equals(authDo.getChsiStatus(), YesNoStatus.YES.getCode())) {
            isSupplyCertify = "Y";
        }

        cashierVo.setIsSupplyCertify(isSupplyCertify);
        cashierVo.setFaceStatus(authDo.getFacesStatus());
        cashierVo.setRiskStatus(authDo.getRiskStatus());
        if (bankInfo == null) {
            cashierVo.setIsValid(YesNoStatus.NO.getCode());
        }
        cashierVo.setIdNumber(userDto.getIdNumber());
        cashierVo.setRealName(userDto.getRealName());
        cashierVo.setRealNameScore(authDo.getRealnameScore());
        if (cashierVo.getAp().getStatus().equals(YesNoStatus.NO.getCode())) {
            //分期不可用，那么额度支付也不可用
            CashierTypeVo creditVo = new CashierTypeVo(cashierVo.getAp().getStatus(), cashierVo.getAp().getReasonType());
            creditVo.setUseableAmount(cashierVo.getAp().getUseableAmount());
            creditVo.setPayAmount(cashierVo.getAp().getPayAmount());
            cashierVo.setCredit(creditVo);
        } else {
            CashierTypeVo creditVo = canCredit(userDto, authDo, orderInfo, checkoutCounter);
            creditVo.setUseableAmount(cashierVo.getAp().getUseableAmount());
            creditVo.setPayAmount(cashierVo.getAp().getPayAmount());
            cashierVo.setCredit(creditVo);
        }

        if (cashierVo.getAp().getStatus().equals(YesNoStatus.NO.getCode())) {
            //分期不能用
            if (cashierVo.getAp().getReasonType().equals(CashierReasonType.USE_ABLED_LESS.getCode())) {
                //这时候使用组合支付
                CashierTypeVo cpVo = canCP(userDto, authDo, orderInfo, checkoutCounter);
                cpVo.setPayAmount(cashierVo.getAmount().subtract(cashierVo.getAp().getPayAmount()));
                cashierVo.setCp(cpVo);
            } else {
                //其他原因不能分期，自然也不能组合支付
                cashierVo.setCp(new CashierTypeVo(cashierVo.getAp().getStatus(), cashierVo.getAp().getReasonType()));
            }
        } else {
            //能分期自然不能组合
            cashierVo.setCp(new CashierTypeVo(YesNoStatus.NO.getCode(), CashierReasonType.NOT_NEED.getCode()));
        }
        cashierVo.setWx(canWX(userDto, authDo, orderInfo, checkoutCounter));
        cashierVo.setBank(canBankpay(userDto, authDo, orderInfo, checkoutCounter));
        cashierVo.setAli(canAlipay(userDto, authDo, orderInfo, checkoutCounter));
        resp.setResponseData(cashierVo);
        return resp;
    }

    /**
     * 组合支付验证,前置条件分期支付验证通过
     *
     * @param userDto         用户账户信息
     * @param authDo          用户授权信息
     * @param orderInfo       订单信息
     * @param checkoutCounter 收银台信息
     * @return
     */
    private CashierTypeVo canCP(AfUserAccountDto userDto, AfUserAuthDo authDo, AfOrderDo orderInfo, AfCheckoutCounterDo checkoutCounter) {
        if (StringUtil.isEmpty(checkoutCounter.getCppayStatus()) || checkoutCounter.getCppayStatus().equals(YesNoStatus.NO.getCode())) {
            return new CashierTypeVo(YesNoStatus.NO.getCode(), CashierReasonType.CASHIER.getCode());
        }
        return new CashierTypeVo(YesNoStatus.YES.getCode());
    }

    /**
     * 微信支付验证
     *
     * @param userDto         用户账户信息
     * @param authDo          用户授权信息
     * @param orderInfo       订单信息
     * @param checkoutCounter 收银台信息
     * @return
     */
    private CashierTypeVo canWX(AfUserAccountDto userDto, AfUserAuthDo authDo, AfOrderDo orderInfo, AfCheckoutCounterDo checkoutCounter) {
        if (StringUtil.isEmpty(checkoutCounter.getWxpayStatus()) || checkoutCounter.getWxpayStatus().equals(YesNoStatus.NO.getCode())) {
            return new CashierTypeVo(YesNoStatus.NO.getCode(), CashierReasonType.CASHIER.getCode());
        }
        return new CashierTypeVo(YesNoStatus.YES.getCode());
    }

    /**
     * 银行卡支付支付验证
     *
     * @param userDto         用户账户信息
     * @param authDo          用户授权信息
     * @param orderInfo       订单信息
     * @param checkoutCounter 收银台信息
     * @return
     */
    private CashierTypeVo canBankpay(AfUserAccountDto userDto, AfUserAuthDo authDo, AfOrderDo orderInfo, AfCheckoutCounterDo checkoutCounter) {
        if (StringUtil.isEmpty(checkoutCounter.getBankpayStatus()) || checkoutCounter.getBankpayStatus().equals(YesNoStatus.NO.getCode())) {
            return new CashierTypeVo(YesNoStatus.NO.getCode(), CashierReasonType.CASHIER.getCode());
        }
        return new CashierTypeVo(YesNoStatus.YES.getCode());
    }

    /**
     * 支付宝支付验证
     *
     * @param userDto         用户账户信息
     * @param authDo          用户授权信息
     * @param orderInfo       订单信息
     * @param checkoutCounter 收银台信息
     * @return
     */
    private CashierTypeVo canAlipay(AfUserAccountDto userDto, AfUserAuthDo authDo, AfOrderDo orderInfo, AfCheckoutCounterDo checkoutCounter) {
        if (StringUtil.isEmpty(checkoutCounter.getAlipayStatus()) || checkoutCounter.getAlipayStatus().equals(YesNoStatus.NO.getCode())) {
            return new CashierTypeVo(YesNoStatus.NO.getCode(), CashierReasonType.CASHIER.getCode());
        }
        return new CashierTypeVo(YesNoStatus.YES.getCode());
    }

    /**
     * 分期支付验证
     *
     * @param userDto         用户账户信息
     * @param authDo          用户授权信息
     * @param orderInfo       订单信息
     * @param checkoutCounter 收银台信息
     * @return
     */
    private CashierTypeVo canConsume(AfUserAccountDto userDto, AfUserAuthDo authDo, AfOrderDo orderInfo, AfCheckoutCounterDo checkoutCounter) {
        if (StringUtil.isEmpty(checkoutCounter.getInstallmentStatus()) || checkoutCounter.getInstallmentStatus().equals(YesNoStatus.NO.getCode())) {
            return new CashierTypeVo(YesNoStatus.NO.getCode(), CashierReasonType.CASHIER.getCode());
        }

        //审核状态判定
        String status = getIsAuth(userDto, authDo, orderInfo);
        if (status.equals(YesNoStatus.YES.getCode())) {
            AfResourceDo consumeMinResource = afResourceService.getSingleResourceBytype("CONSUME_MIN_AMOUNT");
            BigDecimal minAmount = consumeMinResource == null ? BigDecimal.ZERO : new BigDecimal(consumeMinResource.getValue());
            if (orderInfo.getActualAmount().compareTo(minAmount) <= 0) {
                return new CashierTypeVo(YesNoStatus.NO.getCode(), CashierReasonType.CONSUME_MIN_AMOUNT.getCode());
            }
            //额度判断
            BigDecimal userabledAmount = userDto.getAuAmount().subtract(userDto.getUsedAmount()).subtract(userDto.getFreezeAmount());
            AfResourceDo usabledMinResource = afResourceService.getSingleResourceBytype("NEEDUP_MIN_AMOUNT");
            BigDecimal usabledMinAmount = usabledMinResource == null ? BigDecimal.ZERO : new BigDecimal(usabledMinResource.getValue());
            if (userabledAmount.compareTo(usabledMinAmount) < 0) {
                return new CashierTypeVo(YesNoStatus.NO.getCode(), CashierReasonType.NEEDUP.getCode());
            }

            if (userabledAmount.compareTo(orderInfo.getActualAmount()) < 0) {
                //额度不够
                CashierTypeVo cashierTypeVo = new CashierTypeVo(YesNoStatus.NO.getCode(), CashierReasonType.USE_ABLED_LESS.getCode());
                riskProcess(cashierTypeVo, orderInfo, userDto,usabledMinAmount);
                return cashierTypeVo;
            } else {
                CashierTypeVo cashierTypeVo = new CashierTypeVo(YesNoStatus.YES.getCode());
                riskProcess(cashierTypeVo, orderInfo, userDto,usabledMinAmount);
                return cashierTypeVo;
            }
        } else {
            return new CashierTypeVo(YesNoStatus.NO.getCode(), CashierReasonType.NEEDAUTH.getCode());
        }
    }

    /**
     * 信用支付验证 前置条件分期支付验证
     *
     * @param userDto         用户信息
     * @param authDo          验证信息
     * @param orderInfo       订单信息
     * @param checkoutCounter 收银台配置
     * @return
     */
    private CashierTypeVo canCredit(AfUserAccountDto userDto, AfUserAuthDo authDo, AfOrderDo orderInfo, AfCheckoutCounterDo checkoutCounter) {
        if (StringUtil.isEmpty(checkoutCounter.getCreditStatus()) || checkoutCounter.getCreditStatus().equals(YesNoStatus.NO.getCode())) {
            return new CashierTypeVo(YesNoStatus.NO.getCode(), CashierReasonType.CASHIER.getCode());
        }
        AfUserBankcardDo card = afUserBankcardService.getUserMainBankcardByUserId(orderInfo.getUserId());
        String cardNo = card.getCardNumber();
        String riskOrderNo = riskUtil.getOrderNo("crep", cardNo.substring(cardNo.length() - 4, cardNo.length()));
        String state = riskUtil.creditPayment(userDto.getUserId().toString(), riskOrderNo);
        if(state.equals(YesNoStatus.YES.getCode())){
            CashierTypeVo cashierTypeVo = new CashierTypeVo(YesNoStatus.YES.getCode());
            cashierTypeVo.setPayAmount(orderInfo.getActualAmount());
            return cashierTypeVo;
        }  else{
            CashierTypeVo cashierTypeVo = new CashierTypeVo(YesNoStatus.NO.getCode(),CashierReasonType.RISK_CREDIT_PAYMENT.getCode());
            return cashierTypeVo;
        }

    }

    /**
     * 用户的验证信息过滤
     *
     * @param userDto
     * @param authDo
     * @param orderInfo
     * @return
     */
    private String getIsAuth(AfUserAccountDto userDto, AfUserAuthDo authDo, AfOrderDo orderInfo) {
        String status = YesNoStatus.NO.getCode();
        if (userDto.getAuAmount().compareTo(BigDecimal.ZERO) > 0) {
            if (StringUtil.equals(YesNoStatus.YES.getCode(), authDo.getIvsStatus())// 反欺诈分已验证
                    && StringUtil.equals(YesNoStatus.YES.getCode(), authDo.getZmStatus())// 芝麻信用已验证
                    && StringUtil.equals(YesNoStatus.YES.getCode(), authDo.getTeldirStatus())// 通讯录匹配状态
                    && StringUtil.equals(YesNoStatus.YES.getCode(), authDo.getMobileStatus())// 手机运营商
                    && StringUtil.equals(YesNoStatus.YES.getCode(), authDo.getRiskStatus())) { // 强风控状态
                status = YesNoStatus.YES.getCode();
            }
        }
        return status;
    }

    /**
     * 风控处理
     *
     * @param cashierTypeVo 支付的方式
     * @param orderInfo     订单信息
     * @param userDto       用户账户信息
     */
    private void riskProcess(CashierTypeVo cashierTypeVo, AfOrderDo orderInfo, AfUserAccountDto userDto,BigDecimal usabledMinAmount) {
        // 风控逾期订单处理
        RiskQueryOverdueOrderRespBo resp = riskUtil.queryOverdueOrder(orderInfo.getUserId() + StringUtil.EMPTY);
        String rejectCode =  resp.getRejectCode();
        //region 测试借钱逾期白名单
        List<AfResourceDo> afResourceList = afResourceService.getConfigByTypes("overduecash_test_user");
        if (afResourceList.size() > 0) {
            if (afResourceList.get(0).getValue()!=null&&afResourceList.get(0).getValue().contains(String.valueOf(userDto.getUserName()))) {
                rejectCode=RiskErrorCode.OVERDUE_BORROW_CASH.getCode();
            }
        }
        //endregion

        //region 测试分期逾期白名单
        List<AfResourceDo> afResourceList1 = afResourceService.getConfigByTypes("overdue_test_user");
        if (afResourceList1.size() > 0) {
            if (afResourceList1.get(0).getValue()!=null&&afResourceList1.get(0).getValue().contains(String.valueOf(userDto.getUserName()))) {
                rejectCode=RiskErrorCode.OVERDUE_BORROW.getCode();
            }
        }
        //endregion

        if (StringUtil.isNotBlank(rejectCode)) {
            RiskErrorCode erorrCode = RiskErrorCode.findRoleTypeByCode(rejectCode);

            switch (erorrCode) {
                case OVERDUE_BORROW:
                    String borrowNo = resp.getBorrowNo();
                    AfBorrowDo borrowInfo = afBorrowService.getBorrowInfoByBorrowNo(borrowNo);
                    Long billId = afBorrowBillService.getOverduedAndNotRepayBillId(borrowInfo.getRid());
                    cashierTypeVo.setBillId(billId);
                    cashierTypeVo.setOverduedCode(erorrCode.getCode());
                    cashierTypeVo.setStatus(YesNoStatus.NO.getCode());
                    cashierTypeVo.setReasonType(CashierReasonType.OVERDUE_BORROW.getCode());
                    return;
                case OVERDUE_BORROW_CASH:
                    AfBorrowCashDo cashInfo = afBorrowCashService.getNowTransedBorrowCashByUserId(userDto.getUserId());
                    cashierTypeVo.setOverduedCode(erorrCode.getCode());
                    cashierTypeVo.setJfbAmount(userDto.getJfbAmount());
                    cashierTypeVo.setUserRebateAmount(userDto.getRebateAmount());
                    BigDecimal allAmount = BigDecimalUtil.add(cashInfo.getAmount(), cashInfo.getSumOverdue(), cashInfo.getOverdueAmount(), cashInfo.getRateAmount(), cashInfo.getSumRate());
                    BigDecimal repaymentAmount = BigDecimalUtil.subtract(allAmount, cashInfo.getRepayAmount());
                    cashierTypeVo.setRepaymentAmount(repaymentAmount);
                    cashierTypeVo.setBorrowId(cashInfo.getRid());
                    cashierTypeVo.setStatus(YesNoStatus.NO.getCode());
                    cashierTypeVo.setReasonType(CashierReasonType.OVERDUE_BORROW_CASH.getCode());
                    return;
                default:
                    cashierTypeVo.setStatus(YesNoStatus.NO.getCode());
                    cashierTypeVo.setReasonType("未知原因：" + rejectCode);
                    return;
            }
        }
        Map<String, Object> virtualMap = afOrderService.getVirtualCodeAndAmount(orderInfo);
        BigDecimal useableAmount = userDto.getAuAmount().subtract(userDto.getUsedAmount()).subtract(userDto.getFreezeAmount());
        if (afOrderService.isVirtualGoods(virtualMap)) {
            cashierTypeVo.setIsVirtualGoods(YesNoStatus.YES.getCode());
            String virtualCode = afOrderService.getVirtualCode(virtualMap);
            VirtualGoodsCateogy virtualGoodsCateogy = VirtualGoodsCateogy.findRoleTypeByCode(virtualCode);
            BigDecimal totalVirtualAmount = afOrderService.getVirtualAmount(virtualMap);
            BigDecimal leftAmount = afUserVirtualAccountService.getCurrentMonthLeftAmount(orderInfo.getUserId(), virtualCode, totalVirtualAmount);
            cashierTypeVo.setCategoryName(virtualGoodsCateogy.getName());
            //每月总额度
            cashierTypeVo.setTotalVirtualAmount(totalVirtualAmount);

            // 虚拟剩余额度大于信用可用额度 则为可用额度
            leftAmount = leftAmount.compareTo(useableAmount) > 0 ? useableAmount : leftAmount;
            cashierTypeVo.setVirtualGoodsUsableAmount(leftAmount);
            if (leftAmount.compareTo(BigDecimal.ZERO) <= 0) {
                cashierTypeVo.setStatus(YesNoStatus.NO.getCode());
                cashierTypeVo.setReasonType(CashierReasonType.VIRTUAL_GOODS_LIMIT.getCode());
            } else {
                if(leftAmount.compareTo(orderInfo.getActualAmount()) > 0){
                    cashierTypeVo.setUseableAmount(leftAmount);
                    cashierTypeVo.setPayAmount(orderInfo.getActualAmount());
                    cashierTypeVo.setStatus(YesNoStatus.YES.getCode());
                }else{
                    cashierTypeVo.setUseableAmount(leftAmount);
                    cashierTypeVo.setPayAmount(leftAmount);
                    cashierTypeVo.setReasonType(CashierReasonType.USE_ABLED_LESS.getCode());
                    cashierTypeVo.setStatus(YesNoStatus.NO.getCode());
                }
                if(leftAmount.compareTo(usabledMinAmount)<=0 ){
                    cashierTypeVo.setReasonType(CashierReasonType.NEEDUP_VIRTUAL.getCode());
                    cashierTypeVo.setStatus(YesNoStatus.NO.getCode());
                }
            }
        } else {
            cashierTypeVo.setIsVirtualGoods(YesNoStatus.NO.getCode());
            cashierTypeVo.setUseableAmount(useableAmount);
            cashierTypeVo.setPayAmount(useableAmount.compareTo(orderInfo.getActualAmount()) > 0 ? orderInfo.getActualAmount() : useableAmount);
        }
    }

}
