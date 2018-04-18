package com.ald.fanbei.api.web.api.barlyClearance;

import com.ald.fanbei.api.biz.bo.UpsCollectRespBo;
import com.ald.fanbei.api.biz.bo.barlyClearance.AllBarlyClearanceBo;
import com.ald.fanbei.api.biz.bo.barlyClearance.AllBarlyClearanceDetailBo;
import com.ald.fanbei.api.biz.bo.thirdpay.ThirdBizType;
import com.ald.fanbei.api.biz.bo.thirdpay.ThirdPayTypeEnum;
import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.CouponStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.common.util.UserUtil;
import com.ald.fanbei.api.dal.dao.AfUserSeedDao;
import com.ald.fanbei.api.dal.domain.AfBorrowBillDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
import com.ald.fanbei.api.dal.domain.AfUserSeedDo;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author honghzengpei 2017/11/28 13:31
 * @类描述：订单支付
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("submitClearApi")
public class SubmitClearApi implements ApiHandle {

    @Resource
    AfBorrowBillService afBorrowBillService;

    @Resource
    AfUserCouponService afUserCouponService;

    @Resource
    AfUserAccountService afUserAccountService;

    @Resource
    AfRepaymentService afRepaymentService;

    @Resource
    AfUserWithholdService afUserWithholdService;

    @Resource
    AfResourceService afResourceService;

    @Resource
    AfUserBankcardService afUserBankcardService;
    @Resource
    AfUserSeedDao afUserSeedDao;
    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
        Long userId = context.getUserId();


//        AfUserSeedDo afUserSeedDo = afUserSeedDao.getAfUserSeedDoByUserId(userId);
//        if(afUserSeedDo !=null){
//            throw new FanbeiException(FanbeiExceptionCode.ZZYH_ERROR);
            //FanbeiException fbEX = new FanbeiException(FanbeiExceptionCode.ZZYH_ERROR);
//            ApiHandleResponse resp1 = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.ZZYH_ERROR);
//            return resp1;
//        }

        Long billId = NumberUtil.objToLongDefault(ObjectUtils.toString(requestDataVo.getParams().get("billId")), 0L);   //0 全部结清   其它订单结清
        BigDecimal repayAmount = NumberUtil.objToBigDecimalDefault(ObjectUtils.toString(requestDataVo.getParams().get("repayAmount")),BigDecimal.ZERO);         //帐单金额
        BigDecimal rebateAmount = NumberUtil.objToBigDecimalDefault(ObjectUtils.toString(requestDataVo.getParams().get("rabteAmount")),BigDecimal.ZERO);         //余额
        Long couponId = NumberUtil.objToLongDefault(ObjectUtils.toString(requestDataVo.getParams().get("couponId")), 0l);
        String payPwd = ObjectUtils.toString(requestDataVo.getParams().get("payPwd"), "").toString();
        Long cardId = NumberUtil.objToLongDefault(ObjectUtils.toString(requestDataVo.getParams().get("cardId")), 0l);
	    String bankPayType = ObjectUtils.toString(requestDataVo.getParams().get("bankChannel"),null);

        AfUserAccountDo afUserAccountDo = afUserAccountService.getUserAccountByUserId(userId);
        AfUserCouponDto coupon = afUserCouponService.getUserCouponById(couponId);
        if(null != coupon &&!coupon.getStatus().equals(CouponStatus.NOUSE.getCode())){
            throw new FanbeiException(FanbeiExceptionCode.USER_COUPON_ERROR);
        }


        List<AllBarlyClearanceBo> list = afBorrowBillService.getAllClear(userId,billId);
        String billIds = getBillIds(list);

        if(!checkAmount(list,repayAmount)){
            throw new FanbeiException("borrow bill not exist error", FanbeiExceptionCode.AMOUNT_COMPARE_ERROR);
        }

        BigDecimal showAmount = repayAmount;  //实际支付金额
        if(coupon!=null){
            showAmount = BigDecimalUtil.subtract(showAmount, coupon.getAmount());
        }
        if(showAmount.compareTo(BigDecimal.ZERO)<=0){
            rebateAmount = BigDecimal.ZERO;
            showAmount = BigDecimal.ZERO;
        }

        if(showAmount.compareTo(BigDecimal.ZERO)!=0){
            //余额处理
            if(rebateAmount.compareTo(BigDecimal.ZERO)>0&&showAmount.compareTo(afUserAccountDo.getRebateAmount())>0){
                showAmount = BigDecimalUtil.subtract(showAmount, afUserAccountDo.getRebateAmount());
                rebateAmount = afUserAccountDo.getRebateAmount();
            }else if(rebateAmount.compareTo(BigDecimal.ZERO)>0 &&showAmount.compareTo(afUserAccountDo.getRebateAmount())<=0){
                rebateAmount = showAmount;
                showAmount = BigDecimal.ZERO;
            }
        }

        if(cardId>0 || cardId ==-2){//支付密码验证
            String inputOldPwd = UserUtil.getPassword(payPwd, afUserAccountDo.getSalt());
            if (!StringUtils.equals(inputOldPwd, afUserAccountDo.getPassword())) {
                return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_PAY_PASSWORD_INVALID_ERROR);
            }
        }

        AfBorrowBillDo billDo = afBorrowBillService.getBillAmountByIds(billIds);


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
                if (afUserAccountDo.getRebateAmount().compareTo(showAmount) < 0) {
                    return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_ACCOUNT_MONEY_LESS);
                }
                map = afRepaymentService.createRepaymentByBankOrRebate(BigDecimal.ZERO, repayAmount, showAmount, coupon, rebateAmount, billIds,
                        cardId, userId, billDo, "", afUserAccountDo,null);
                resp.setResponseData(map);
            } else if (cardId.longValue() == -1) {//微信支付
                if (context.getAppVersion() < 395) {
                    throw new FanbeiException(FanbeiExceptionCode.WEBCHAT_NOT_USERD);
                }
                if (!afResourceService.checkThirdPayByType(ThirdBizType.REPAYMENT, ThirdPayTypeEnum.WXPAY)) {
                    throw new FanbeiException(FanbeiExceptionCode.WEBCHAT_NOT_USERD);
                }


                map = afRepaymentService.createRepaymentByZfbOrWechat(BigDecimal.ZERO, repayAmount, showAmount, coupon, rebateAmount, billIds, cardId, userId, billDo, "", afUserAccountDo,null);
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

                map = afRepaymentService.createRepaymentByZfbOrWechat(BigDecimal.ZERO, repayAmount, showAmount, coupon, rebateAmount, billIds, cardId, userId, billDo, "", afUserAccountDo,null);
                map.put("userNo", afUserAccountDo.getUserName());
                map.put("userType", "USER_ID");
                map.put("directPayType", "ZFB");
                resp.setResponseData(map);
            } else if (cardId.longValue() > 0) {//银行卡支付
                AfUserBankcardDo card = afUserBankcardService.getUserBankcardById(cardId);
                if (null == card) {
                    throw new FanbeiException(FanbeiExceptionCode.USER_BANKCARD_NOT_EXIST_ERROR);
                }
                map = afRepaymentService.createRepaymentByBankOrRebate(BigDecimal.ZERO, repayAmount, showAmount, coupon, rebateAmount, billIds,
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
     * 金额check
     * @param list
     * @param repayAmont
     * @return
     */
    private boolean checkAmount(List<AllBarlyClearanceBo> list , BigDecimal repayAmont){
        Boolean ret = false;
        BigDecimal allAmount = BigDecimal.ZERO;
        for(AllBarlyClearanceBo allBarlyClearanceBo : list){
            allAmount = allAmount.add(allBarlyClearanceBo.getAmount());
        }
        if(allAmount.compareTo(repayAmont) ==0){
            ret = true;
        }
        return ret;
    }

    private String getBillIds(List<AllBarlyClearanceBo> list){
        String billIds = "";
        for(AllBarlyClearanceBo allBarlyClearanceBo: list){
            for(AllBarlyClearanceDetailBo allBarlyClearanceDetailBo : allBarlyClearanceBo.getDetailList()){
                if(!billIds.equals("")){
                    billIds +=",";
                }
                billIds += allBarlyClearanceDetailBo.getBillId();
            }
        }
        return billIds;
    }
}
