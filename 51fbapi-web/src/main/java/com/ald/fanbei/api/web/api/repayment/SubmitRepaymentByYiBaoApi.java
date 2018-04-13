package com.ald.fanbei.api.web.api.repayment;

import com.ald.fanbei.api.biz.bo.UpsCollectRespBo;
import com.ald.fanbei.api.biz.bo.thirdpay.ThirdBizType;
import com.ald.fanbei.api.biz.bo.thirdpay.ThirdPayTypeEnum;
import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.third.util.yibaopay.YiBaoUtility;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.BorrowBillStatus;
import com.ald.fanbei.api.common.enums.CouponStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.*;
import com.ald.fanbei.api.dal.dao.AfRepaymentDetalDao;
import com.ald.fanbei.api.dal.domain.*;
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


    @Resource
    private YiBaoUtility yiBaoUtility;

    @Resource
    private AfResourceService afResourceService;

    @Resource
    private AfRepaymentDetalDao afRepaymentDetalDao;
    @Resource
    AfRepaymentBorrowCashService afRepaymentBorrowCashService;
    @Resource
    AfUserWithholdService afUserWithholdService;
    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo,
                                     FanbeiContext context, HttpServletRequest request) {
        BigDecimal showAmount;
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
        Long userId = context.getUserId();
        BigDecimal repaymentAmount = NumberUtil.objToBigDecimalDefault(ObjectUtils.toString(requestDataVo.getParams().get("repaymentAmount")), BigDecimal.ZERO);
        String billIds = ObjectUtils.toString(requestDataVo.getParams().get("billIds"));
        BigDecimal actualAmount = NumberUtil.objToBigDecimalDefault(ObjectUtils.toString(requestDataVo.getParams().get("actualAmount")), BigDecimal.ZERO);
        Long couponId = NumberUtil.objToLongDefault(ObjectUtils.toString(requestDataVo.getParams().get("couponId")), 0l);
        BigDecimal rebateAmount = NumberUtil.objToBigDecimalDefault(ObjectUtils.toString(requestDataVo.getParams().get("rebateAmount")), BigDecimal.ZERO);
        Long cardId = NumberUtil.objToLongDefault(ObjectUtils.toString(requestDataVo.getParams().get("cardId")), 0l);
        String payPwd = ObjectUtils.toString(requestDataVo.getParams().get("payPwd"), "").toString();
        BigDecimal jfbAmount = NumberUtil.objToBigDecimalDefault(ObjectUtils.toString(requestDataVo.getParams().get("jfbAmount")), BigDecimal.ZERO);
	String bankPayType = ObjectUtils.toString(requestDataVo.getParams().get("bankChannel"),null);
        AfUserAccountDo afUserAccountDo = afUserAccountService.getUserAccountByUserId(userId);


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

        if (afUserAccountDo == null) {
            throw new FanbeiException("Account is invalid", FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
        }

        String borrowProcessingNO = afRepaymentBorrowCashService.getProcessingRepayNo(context.getUserId());
        if (!StringUtil.isEmpty(borrowProcessingNO)) {
            throw new FanbeiException("还款处理中,无法进行还款操作", true);
        }
        if (cardId > 0 || cardId == -2) {//支付密码验证
            String inputOldPwd = UserUtil.getPassword(payPwd, afUserAccountDo.getSalt());
            if (!StringUtils.equals(inputOldPwd, afUserAccountDo.getPassword())) {
                return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_PAY_PASSWORD_INVALID_ERROR);
            }
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
        BigDecimal myjfb = BigDecimalUtil.divide(afUserAccountDo.getJfbAmount(), new BigDecimal(100));
        //使用集分宝处理
        if (jfbAmount.compareTo(BigDecimal.ZERO) > 0 && showAmount.compareTo(myjfb) > 0) {

            showAmount = BigDecimalUtil.subtract(showAmount, myjfb);
            jfbAmount = afUserAccountDo.getJfbAmount();
        } else if (jfbAmount.compareTo(BigDecimal.ZERO) > 0 && showAmount.compareTo(myjfb) <= 0) {
            //集分宝金额大于还款金额
            jfbAmount = BigDecimalUtil.multiply(showAmount, new BigDecimal(100));
            rebateAmount = BigDecimal.ZERO;
            showAmount = BigDecimal.ZERO;
        }
        //余额处理
        if (rebateAmount.compareTo(BigDecimal.ZERO) > 0 && showAmount.compareTo(afUserAccountDo.getRebateAmount()) > 0) {
            showAmount = BigDecimalUtil.subtract(showAmount, afUserAccountDo.getRebateAmount());
            rebateAmount = afUserAccountDo.getRebateAmount();
        } else if (rebateAmount.compareTo(BigDecimal.ZERO) > 0 && showAmount.compareTo(afUserAccountDo.getRebateAmount()) <= 0) {
            rebateAmount = showAmount;
            showAmount = BigDecimal.ZERO;
        }

        if (actualAmount.compareTo(showAmount) != 0) {
            throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_REPAY_AMOUNT__ERROR);
        }


        //遍历账单，加锁
   
        String[] billStr = billIds.split(",");
        String billIds1 = "";
        Map<String, Object> map;
        try {
             for (int i = 0; i < billStr.length; i++) {
                 String billId1 = billStr[i];
                 if (afBorrowBillService.updateBorrowBillLockById(billId1) > 0) {
                     if (billIds1.equals("")) {
                         billIds1 = billId1;
                     } else {
                         billIds1 = billIds1 + "," + billId1;
                     }
                 } else {
                     throw new FanbeiException(FanbeiExceptionCode.BORROW_BILL_IS_REPAYING);
                 }
             }

            if (cardId.longValue() == -2) {//余额支付
                //用户账户余额校验添加
                if (afUserAccountDo.getRebateAmount().compareTo(actualAmount) < 0) {
                    return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_ACCOUNT_MONEY_LESS);
                }
                map = afRepaymentService.createRepaymentByBankOrRebate(jfbAmount, repaymentAmount, actualAmount, coupon, rebateAmount, billIds,
                        cardId, userId, billDo, "", afUserAccountDo,null);
                resp.addResponseData("refId", map.get("refId"));
                resp.addResponseData("type", map.get("type"));
            } else if (cardId.longValue() == -1) {//微信支付

                if (context.getAppVersion() < 395) {
                    throw new FanbeiException(FanbeiExceptionCode.WEBCHAT_NOT_USERD);
                }
                if (!afResourceService.checkThirdPayByType(ThirdBizType.REPAYMENT, ThirdPayTypeEnum.WXPAY)) {
                    throw new FanbeiException(FanbeiExceptionCode.WEBCHAT_NOT_USERD);
                }

                map = afRepaymentService.createRepaymentByZfbOrWechat(jfbAmount, repaymentAmount, actualAmount, coupon, rebateAmount, billIds, cardId, userId, billDo, "", afUserAccountDo,null);
                map.put("userNo", afUserAccountDo.getUserName());
                map.put("userType", "USER_ID");
                map.put("directPayType", "WX");
                resp.setResponseData(map);
            } else if (cardId.longValue() == -3) {
                if (context.getAppVersion() < 395) {
                    throw new FanbeiException(FanbeiExceptionCode.ZFB_NOT_USERD);
                }
                if (!afResourceService.checkThirdPayByType(ThirdBizType.REPAYMENT, ThirdPayTypeEnum.ZFBPAY)) {
                    throw new FanbeiException(FanbeiExceptionCode.ZFB_NOT_USERD);
                }

                map = afRepaymentService.createRepaymentByZfbOrWechat(jfbAmount, repaymentAmount, actualAmount, coupon, rebateAmount, billIds, cardId, userId, billDo, "", afUserAccountDo,null);
                map.put("userNo", afUserAccountDo.getUserName());
                map.put("userType", "USER_ID");
                map.put("directPayType", "ZFB");
                resp.setResponseData(map);
            } else if (cardId.longValue() > 0) {//银行卡支付
        	
                AfUserBankcardDo card = afUserBankcardService.getUserBankcardById(cardId);
                if (null == card) {
                    throw new FanbeiException(FanbeiExceptionCode.USER_BANKCARD_NOT_EXIST_ERROR);
                }
                map = afRepaymentService.createRepaymentByBankOrRebate(jfbAmount, repaymentAmount, actualAmount, coupon, rebateAmount, billIds,
                        cardId, userId, billDo, request.getRemoteAddr(), afUserAccountDo,bankPayType);
                resp.setResponseData(map);
            }
        } catch (FanbeiException e) {
            logger.error("borrowbill repayment fail" + e);
            throw e;
        } catch (Exception e) {
            logger.error("sys exception", e);
            throw new FanbeiException("sys exception", FanbeiExceptionCode.SYSTEM_ERROR);
        } finally {
            //如果有其中一笔账单解锁
            if (StringUtil.isNotEmpty(billIds1)) {
                //分期账单解锁
                afBorrowBillService.updateBorrowBillUnLockByIds(billIds1);
            }
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
