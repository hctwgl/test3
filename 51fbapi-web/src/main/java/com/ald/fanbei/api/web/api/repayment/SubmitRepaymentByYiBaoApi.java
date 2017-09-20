package com.ald.fanbei.api.web.api.repayment;

import com.ald.fanbei.api.biz.bo.UpsCollectRespBo;
import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.third.util.yibaopay.YiBaoUtility;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.BorrowBillStatus;
import com.ald.fanbei.api.common.enums.CouponStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.*;
import com.ald.fanbei.api.dal.domain.AfBorrowBillDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserCouponDto;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.dbunit.util.Base64;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author honghzengpei 2017/9/7 13:23
 * @类描述：还款
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("submitRepaymentByYiBaoApi")
public class SubmitRepaymentByYiBaoApi implements ApiHandle {
    @Resource
    private AfBorrowBillService afBorrowBillService;

    @Resource
    private AfUserCouponService afUserCouponService;

    @Resource
    private AfRepaymentService afRepaymentService;

    @Resource
    private AfUserAccountService afUserAccountService;

    @Resource
    private AfUserBankcardService afUserBankcardService;

    private BigDecimal showAmount;

    @Resource
    private YiBaoUtility yiBaoUtility;

    @Resource
    private AfResourceService afResourceService;


    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo,
                                     FanbeiContext context, HttpServletRequest request) {
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
        Long userId = context.getUserId();
        BigDecimal repaymentAmount = NumberUtil.objToBigDecimalDefault(ObjectUtils.toString(requestDataVo.getParams().get("repaymentAmount")), BigDecimal.ZERO);
        //String billIds = ObjectUtils.toString(requestDataVo.getParams().get("billIds"));
//        BigDecimal actualAmount = NumberUtil.objToBigDecimalDefault(ObjectUtils.toString(requestDataVo.getParams().get("actualAmount")), BigDecimal.ZERO);
        Long couponId = NumberUtil.objToLongDefault(ObjectUtils.toString(requestDataVo.getParams().get("couponId")), 0l);
        //BigDecimal rebateAmount = NumberUtil.objToBigDecimalDefault(ObjectUtils.toString(requestDataVo.getParams().get("rebateAmount")), BigDecimal.ZERO);
        Long cardId = NumberUtil.objToLongDefault(ObjectUtils.toString(requestDataVo.getParams().get("cardId")), 0l);
        String payPwd = ObjectUtils.toString(requestDataVo.getParams().get("payPwd"), "").toString();
//        BigDecimal jfbAmount = NumberUtil.objToBigDecimalDefault(ObjectUtils.toString(requestDataVo.getParams().get("jfbAmount")), BigDecimal.ZERO);

        Integer useRebateAmount = NumberUtil.objToIntDefault(requestDataVo.getParams().get("useRebateAmount"), 0); //是否使用余额

        BigDecimal jfbAmount = BigDecimal.ZERO;

        String billIds = "";
        List<AfBorrowBillDo> afborrowBillList = afBorrowBillService.getAllBorrowNoPayByUserId(userId);
        BigDecimal amountSum = BigDecimal.ZERO;
        boolean outAll = true;
        for (AfBorrowBillDo afBorrowBillDo : afborrowBillList) {
            BigDecimal pAmount = afBorrowBillDo.getBillAmount();
            if (isOut(afBorrowBillDo.getBillYear(), afBorrowBillDo.getBillMonth())) {
                if (repaymentAmount.subtract(amountSum).compareTo(pAmount) >= 0) {
                    amountSum = amountSum.add(pAmount);
                    if (billIds != "") {
                        billIds += ",";
                    }
                    billIds += afBorrowBillDo.getRid();
                } else {
                    outAll = false;
                }
            } else {
                if (!outAll) break;
                if (repaymentAmount.subtract(amountSum).compareTo(pAmount) >= 0) {
                    amountSum = amountSum.add(pAmount);
                    if (billIds != "") {
                        billIds += ",";
                    }
                    billIds += afBorrowBillDo.getRid();
                }

            }
        }

        if(amountSum.compareTo(BigDecimal.ZERO)<=0){
            throw new FanbeiException("Account is invalid", FanbeiExceptionCode.AMOUNT_IS_LESS);
        }

        BigDecimal allRepaymentAmount = repaymentAmount;
        repaymentAmount = amountSum;
//        BigDecimal yuer = allRepaymentAmount.subtract(repaymentAmount);   //多出来的钱      钱到帐后。这笔钱要进入

        BigDecimal rebateAmount = BigDecimal.ZERO;
        AfUserAccountDo afUserAccountDo = afUserAccountService.getUserAccountByUserId(userId);
        if (afUserAccountDo == null) {
            throw new FanbeiException("Account is invalid", FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
        }

        if(useRebateAmount.intValue() ==1){
            rebateAmount = afUserAccountDo.getRebateAmount();
        }


        if (cardId > 0 || cardId == -2) {//支付密码验证
            String inputOldPwd = UserUtil.getPassword(payPwd, afUserAccountDo.getSalt());
            if (!StringUtils.equals(inputOldPwd, afUserAccountDo.getPassword())) {
                return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_PAY_PASSWORD_INVALID_ERROR);
            }
        }

        if (!yiBaoUtility.checkCanNext(userId, 2)) {
            return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.BORROW_BILL_IS_REPAYING);
        }


        if (StringUtil.isEmpty(billIds)) {
            throw new FanbeiException("borrow bill not exist error", FanbeiExceptionCode.BORROW_BILL_NOT_EXIST_ERROR);
        }
        List<Long> billIdList = CollectionConverterUtil.convertToListFromArray(billIds.split(","), new Converter<String, Long>() {
            @Override
            public Long convert(String source) {
                return Long.parseLong(source);
            }
        });
        List<AfBorrowBillDo> borrowBillList = afBorrowBillService.getBorrowBillByIds(billIdList);
        if (constainsRepayingBill(borrowBillList)) {
            return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.BORROW_BILL_IS_REPAYING);
        }
        AfBorrowBillDo billDo = afBorrowBillService.getBillAmountByIds(billIds);
        if (billDo.getCount() == 0 || repaymentAmount.compareTo(billDo.getBillAmount()) != 0) {
            logger.info("repaymentAmount=" + repaymentAmount + ",billDo=" + billDo);
            throw new FanbeiException("borrow bill update error", FanbeiExceptionCode.BORROW_BILL_UPDATE_ERROR);
        }
        AfUserCouponDto coupon = afUserCouponService.getUserCouponById(couponId);
        if (null != coupon && !coupon.getStatus().equals(CouponStatus.NOUSE.getCode())) {
            throw new FanbeiException(FanbeiExceptionCode.USER_COUPON_ERROR);
        }

        List<AfResourceDo> afResourceDoList = afResourceService.getConfigByTypes("PAY_ZFB");
        List<AfResourceDo> afResourceDoList1 = afResourceService.getConfigByTypes("PAY_WX");
        AfResourceDo zfbDo = null;
        AfResourceDo wxDo = null;
        if (afResourceDoList != null && afResourceDoList.size() > 0) {
            zfbDo = afResourceDoList.get(0);
        }
        if (afResourceDoList1 != null && afResourceDoList1.size() > 0) {
            wxDo = afResourceDoList1.get(0);
        }


        showAmount = repaymentAmount;
        //使用优惠券结算金额
        if (coupon != null) {
            showAmount = BigDecimalUtil.subtract(repaymentAmount, coupon.getAmount());
        }
        //优惠券金额大于还款金额其他数据处理
        if (showAmount.compareTo(BigDecimal.ZERO) <= 0) {
            logger.info(afUserAccountDo.getUserName() + "coupon repayment");
            jfbAmount = BigDecimal.ZERO;
            rebateAmount = BigDecimal.ZERO;
            showAmount = BigDecimal.ZERO;
        }
//        BigDecimal myjfb = BigDecimalUtil.divide(afUserAccountDo.getJfbAmount(), new BigDecimal(100));
//        //使用集分宝处理
//        if (jfbAmount.compareTo(BigDecimal.ZERO) > 0 && showAmount.compareTo(myjfb) > 0) {
//
//            showAmount = BigDecimalUtil.subtract(showAmount, myjfb);
//            jfbAmount = afUserAccountDo.getJfbAmount();
//        } else if (jfbAmount.compareTo(BigDecimal.ZERO) > 0 && showAmount.compareTo(myjfb) <= 0) {
//            //集分宝金额大于还款金额
//            jfbAmount = BigDecimalUtil.multiply(showAmount, new BigDecimal(100));
//            rebateAmount = BigDecimal.ZERO;
//            showAmount = BigDecimal.ZERO;
//        }



        //余额处理
        if (rebateAmount.compareTo(BigDecimal.ZERO) > 0 && showAmount.compareTo(afUserAccountDo.getRebateAmount()) > 0) {
            showAmount = BigDecimalUtil.subtract(showAmount, afUserAccountDo.getRebateAmount());
            rebateAmount = afUserAccountDo.getRebateAmount();
        } else if (rebateAmount.compareTo(BigDecimal.ZERO) > 0 && showAmount.compareTo(afUserAccountDo.getRebateAmount()) <= 0) {
            rebateAmount = showAmount;
            showAmount = BigDecimal.ZERO;
        }

        if(showAmount.compareTo(BigDecimal.ZERO) == 0){  //
            cardId =-2L;
        }



        Map<String, Object> map;
        if (cardId == -2) {//余额支付
            map = afRepaymentService.createRepayment(jfbAmount, allRepaymentAmount, showAmount, coupon, rebateAmount, billIds,
                    cardId, userId, billDo, "", afUserAccountDo);
            resp.addResponseData("refId", map.get("refId"));
            resp.addResponseData("type", map.get("type"));
        } else if (cardId == -1) {
            //微信支付
            if (wxDo != null && wxDo.getValue().toLowerCase().equals("true")) {
                map = afRepaymentService.createRepaymentYiBao(jfbAmount, allRepaymentAmount, showAmount, coupon, rebateAmount, billIds,
                        cardId, userId, billDo, "", afUserAccountDo);
                map.put("userNo", afUserAccountDo.getUserName());
                map.put("userType", "USER_ID");
                map.put("directPayType", "WX");
                resp.setResponseData(map);
            } else {
                return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.WEBCHAT_NOT_USERD);
            }
        } else if (cardId == -3) {
            if (zfbDo != null && zfbDo.getValue().toLowerCase().equals("true")) {
                //支付宝
                map = afRepaymentService.createRepaymentYiBao(jfbAmount, allRepaymentAmount, showAmount, coupon, rebateAmount, billIds,
                        cardId, userId, billDo, "", afUserAccountDo);
                map.put("userNo", afUserAccountDo.getUserName());
                map.put("userType", "USER_ID");
                map.put("directPayType", "ZFB");
                resp.setResponseData(map);
            } else {
                return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.ZFB_NOT_USERD);
            }

        } else if (cardId > 0) {//银行卡支付
            AfUserBankcardDo card = afUserBankcardService.getUserBankcardById(cardId);
            if (null == card) {
                throw new FanbeiException(FanbeiExceptionCode.USER_BANKCARD_NOT_EXIST_ERROR);
            }
            map = afRepaymentService.createRepayment(jfbAmount, allRepaymentAmount, showAmount, coupon, rebateAmount, billIds,
                    cardId, userId, billDo, request.getRemoteAddr(), afUserAccountDo);
            //代收
            UpsCollectRespBo upsResult = (UpsCollectRespBo) map.get("resp");
            if (!upsResult.isSuccess()) {
                throw new FanbeiException("bank card pay error", FanbeiExceptionCode.BANK_CARD_PAY_ERR);
            }
            Map<String, Object> newMap = new HashMap<String, Object>();
            newMap.put("outTradeNo", upsResult.getOrderNo());
            newMap.put("tradeNo", upsResult.getTradeNo());
            newMap.put("cardNo", Base64.encodeString(upsResult.getCardNo()));
            newMap.put("refId", map.get("refId"));
            newMap.put("type", map.get("type"));
            resp.setResponseData(newMap);
        }
        return resp;
    }

    /**
     * 查看是否存在还款中的账单
     *
     * @param borrowBillList
     * @return
     */
    private boolean constainsRepayingBill(List<AfBorrowBillDo> borrowBillList) {
        if (CollectionUtil.isEmpty(borrowBillList)) {
            return false;
        }
        boolean constainRepaying = false;
        for (AfBorrowBillDo borrowBillInfo : borrowBillList) {
            if (BorrowBillStatus.DEALING.getCode().equals(borrowBillInfo.getStatus())) {
                constainRepaying = true;
                break;
            }
        }
        return constainRepaying;
    }

    private boolean isOut(int year, int month) {
        Date d = new Date();
        Calendar c1 = Calendar.getInstance();
        c1.set(Calendar.YEAR, year);
        c1.set(Calendar.MONTH, month - 1);
        c1.set(Calendar.DAY_OF_MONTH, 10);
        c1.set(Calendar.HOUR_OF_DAY, 0);
        c1.set(Calendar.MINUTE, 0);
        c1.set(Calendar.SECOND, 0);
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String a = s.format(c1.getTime());
        boolean flag = c1.getTime().before(d);
        return flag;
    }
}
