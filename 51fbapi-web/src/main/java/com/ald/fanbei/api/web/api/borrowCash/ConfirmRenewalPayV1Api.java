package com.ald.fanbei.api.web.api.borrowCash;

import com.ald.fanbei.api.biz.bo.UpsCollectRespBo;
import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.third.util.yibaopay.YiBaoUtility;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.UserUtil;
import com.ald.fanbei.api.dal.domain.*;
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
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.ald.fanbei.api.dal.domain.AfResourceDo;

/**
 * @author honghzengpei 2017/9/8 11:33
 * @类描述：订单支付
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("confirmRenewalPayV1Api")
public class ConfirmRenewalPayV1Api implements ApiHandle {
    @Resource
    BizCacheUtil bizCacheUtil;
    @Resource
    AfBorrowCashService afBorrowCashService;
    @Resource
    AfUserCouponService afUserCouponService;
    @Resource
    AfUserAccountService afUserAccountService;
    @Resource
    AfUserBankcardService afUserBankcardService;
    @Resource
    AfRenewalDetailService afRenewalDetailService;
    @Resource
    AfRepaymentBorrowCashService afRepaymentBorrowCashService;
    @Resource
    AfResourceService afResourceService;

    @Resource
    YiBaoUtility yiBaoUtility;

    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
        Long userId = context.getUserId();
//		BigDecimal repaymentAmount = NumberUtil.objToBigDecimalDefault(ObjectUtils.toString(requestDataVo.getParams().get("repaymentAmount")), BigDecimal.ZERO);
//		BigDecimal actualAmount = NumberUtil.objToBigDecimalDefault(ObjectUtils.toString(requestDataVo.getParams().get("actualAmount")), BigDecimal.ZERO);
        BigDecimal userAmount = NumberUtil.objToBigDecimalDefault(ObjectUtils.toString(requestDataVo.getParams().get("rebateAmount")), BigDecimal.ZERO);

        Long borrowId = NumberUtil.objToLongDefault(ObjectUtils.toString(requestDataVo.getParams().get("borrowId")), 0l);
        String payPwd = ObjectUtils.toString(requestDataVo.getParams().get("payPwd"), "").toString();
        Long cardId = NumberUtil.objToLongDefault(ObjectUtils.toString(requestDataVo.getParams().get("cardId")), 0l);
        BigDecimal jfbAmount = NumberUtil.objToBigDecimalDefault(ObjectUtils.toString(requestDataVo.getParams().get("jfbAmount")), BigDecimal.ZERO);
    	BigDecimal renewalAmount = NumberUtil.objToBigDecimalDefault(requestDataVo.getParams().get("renewalAmount"), BigDecimal.ZERO);
		

        List<AfResourceDo> afResourceDoList = afResourceService.getConfigByTypes("PAY_ZFB");
        List<AfResourceDo> afResourceDoList1 = afResourceService.getConfigByTypes("PAY_WX");
        AfResourceDo zfbDo = null;
        AfResourceDo wxDo = null;
        if(afResourceDoList !=null && afResourceDoList.size()>0){
            zfbDo = afResourceDoList.get(0);
        }
        if(afResourceDoList1 !=null && afResourceDoList1.size()>0){
            wxDo = afResourceDoList1.get(0);
        }


        if (borrowId == 0) {
            throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_NOT_EXIST_ERROR);
        }

        String lockKey = Constants.CACHEKEY_APPLY_RENEWAL_LOCK + userId;
        boolean isGetLock = bizCacheUtil.getLock30Second(lockKey, "1");

        if (!isGetLock) {
            return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.RENEWAL_ORDER_NOT_EXIST_ERROR);
        }
        try {
            AfRepaymentBorrowCashDo afRepaymentBorrowCashDo = afRepaymentBorrowCashService.getLastRepaymentBorrowCashByBorrowId(borrowId);
            if (null != afRepaymentBorrowCashDo && StringUtils.equals(afRepaymentBorrowCashDo.getStatus(), "P")) {
                throw new FanbeiException("There is a repayment is processing", FanbeiExceptionCode.HAVE_A_REPAYMENT_PROCESSING_ERROR);
            }

            AfRenewalDetailDo afRenewalDetailDo = afRenewalDetailService.getRenewalDetailByBorrowId(borrowId);
            if (null != afRenewalDetailDo && StringUtils.equals(afRenewalDetailDo.getStatus(), "P")) {
                throw new FanbeiException("There is a renewal is processing", FanbeiExceptionCode.HAVE_A_PROCESS_RENEWAL_DETAIL);
            }

            AfUserAccountDo userDto = afUserAccountService.getUserAccountByUserId(userId);
            if (userDto == null) {
                throw new FanbeiException("Account is invalid", FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
            }
            if (cardId == -2 || cardId>0) {
                String inputOldPwd = UserUtil.getPassword(payPwd, userDto.getSalt());
                if (!StringUtils.equals(inputOldPwd, userDto.getPassword())) {
                    return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_PAY_PASSWORD_INVALID_ERROR);
                }
            }

            AfBorrowCashDo afBorrowCashDo = afBorrowCashService.getBorrowCashByrid(borrowId);
            if (afBorrowCashDo == null || !ObjectUtils.equals(afBorrowCashDo.getStatus(), "TRANSED")) {
                throw new FanbeiException("Nothing order can renewal", FanbeiExceptionCode.RENEWAL_ORDER_NOT_EXIST_ERROR);
            }

            if (userDto.getRebateAmount().compareTo(userAmount) < 0) {
                throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_REPAY_AMOUNT__ERROR);
            }

            BigDecimal jfb = BigDecimalUtil.divide(jfbAmount, new BigDecimal(100));
            if (userDto.getJfbAmount().compareTo(jfbAmount) < 0) {
                throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_REPAY_AMOUNT__ERROR);
            }

            AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(Constants.RES_RENEWAL_DAY_LIMIT, Constants.RES_ALLOW_RENEWAL_DAY);
            BigDecimal allowRenewalDay = new BigDecimal(resource.getValue());// 允许续期天数
			/*AfResourceDo poundageResource = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE, Constants.RES_BORROW_CASH_POUNDAGE);
			BigDecimal borrowCashPoundage = new BigDecimal(poundageResource.getValue());// 借钱手续费率（日）
			Object poundageRateCash = bizCacheUtil.getObject(Constants.RES_BORROW_CASH_POUNDAGE_RATE + afBorrowCashDo.getUserId());
			if (poundageRateCash != null) {
				borrowCashPoundage = new BigDecimal(poundageRateCash.toString());
			}*/
            BigDecimal borrowCashPoundage = afBorrowCashDo.getPoundageRate();
            /*
            AfResourceDo capitalRateResource = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE, Constants.RENEWAL_CAPITAL_RATE);
            BigDecimal renewalCapitalRate = new BigDecimal(capitalRateResource.getValue());// 续借应还借钱金额比例
            BigDecimal capital = afBorrowCashDo.getAmount().multiply(renewalCapitalRate).setScale(2, RoundingMode.HALF_UP);

            //续借本金
            BigDecimal allAmount = BigDecimalUtil.add(afBorrowCashDo.getAmount(), afBorrowCashDo.getSumOverdue(), afBorrowCashDo.getSumRate());
            BigDecimal waitPaidAmount = BigDecimalUtil.subtract(allAmount, afBorrowCashDo.getRepayAmount()).subtract(capital);
            // 本期手续费  = 未还金额 * 续期天数 * 借钱手续费率（日）
            BigDecimal poundage = waitPaidAmount.multiply(allowRenewalDay).multiply(borrowCashPoundage).setScale(2, RoundingMode.HALF_UP);

            // 续期应缴费用(利息+手续费+滞纳金+要还本金)
            BigDecimal repaymentAmount = BigDecimalUtil.add(afBorrowCashDo.getRateAmount(), poundage, afBorrowCashDo.getOverdueAmount(), capital);
*/
            //fmf_add 续借由用户手动操作  strat
        	BigDecimal capital = BigDecimalUtil.add(afBorrowCashDo.getAmount(),afBorrowCashDo.getSumOverdue(),afBorrowCashDo.getSumRate()).subtract(afBorrowCashDo.getRepayAmount()).subtract(renewalAmount);
    		
    		
    		// 续借本金
    		BigDecimal allAmount = BigDecimalUtil.add(afBorrowCashDo.getAmount(), afBorrowCashDo.getSumOverdue(), afBorrowCashDo.getSumRate());
    		BigDecimal waitPaidAmount = BigDecimalUtil.subtract(allAmount, afBorrowCashDo.getRepayAmount()).subtract(capital);;
    		// 本期手续费 = 未还金额 * 续期天数 * 借钱手续费率（日）
    		BigDecimal poundage = waitPaidAmount.multiply(allowRenewalDay).multiply(borrowCashPoundage).setScale(2, RoundingMode.HALF_UP);

    		// 续期应缴费用(利息+手续费+滞纳金+要还本金)
    		BigDecimal repaymentAmount = BigDecimalUtil.add(afBorrowCashDo.getRateAmount(), poundage, afBorrowCashDo.getOverdueAmount(), capital);
    		//fmf_add 续借由用户手动操作    end
           
    		if (context.getAppVersion() < 380) {
                waitPaidAmount = BigDecimalUtil.subtract(allAmount, afBorrowCashDo.getRepayAmount());
                repaymentAmount = BigDecimalUtil.add(afBorrowCashDo.getRateAmount(), poundage, afBorrowCashDo.getOverdueAmount());
            }
        	
            BigDecimal actualAmount = BigDecimalUtil.subtract(repaymentAmount, jfb).subtract(userAmount);

            if(! yiBaoUtility.checkCanNext(userId,1)){
                return new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.HAVE_A_PROCESS_RENEWAL_DETAIL);
            }


            Map<String, Object> map;
            if (cardId == -2) {// 余额支付
                map = afRenewalDetailService.createRenewal(afBorrowCashDo, jfbAmount, repaymentAmount, actualAmount, userAmount, capital, borrowId, cardId, userId, "", userDto, context.getAppVersion());

                resp.addResponseData("refId", map.get("refId"));
                resp.addResponseData("type", map.get("type"));
            } else if (cardId == -1) {// 微信支付
//				map = afRenewalDetailService.createRenewal(afBorrowCashDo, jfbAmount, repaymentAmount, actualAmount, userAmount, capital, borrowId, cardId, userId, "", userDto, context.getAppVersion());
//				resp.setResponseData(map);
                if(wxDo !=null && wxDo.getValue().toLowerCase().equals("true")){
                    map = afRenewalDetailService.createRenewalYiBao(afBorrowCashDo, jfbAmount, repaymentAmount, actualAmount, userAmount, capital, borrowId, cardId, userId, "", userDto, context.getAppVersion());
                    map.put("userNo",userDto.getUserName());
                    map.put("userType","USER_ID");
                    map.put("directPayType","WX");
                    resp.setResponseData(map);
                }else{
                    return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.WEBCHAT_NOT_USERD);
                }
            }
            else  if(cardId ==-3){
                if(zfbDo !=null && zfbDo.getValue().toLowerCase().equals("true")) {
                    map = afRenewalDetailService.createRenewalYiBao(afBorrowCashDo, jfbAmount, repaymentAmount, actualAmount, userAmount, capital, borrowId, cardId, userId, "", userDto, context.getAppVersion());
                    map.put("userNo", userDto.getUserName());
                    map.put("userType", "USER_ID");
                    map.put("directPayType", "ZFB");
                    resp.setResponseData(map);
                }
                else{
                    return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.ZFB_NOT_USERD);
                }
            }

            else if (cardId > 0) {// 银行卡支付
                AfUserBankcardDo card = afUserBankcardService.getUserBankcardById(cardId);
                if (null == card) {
                    throw new FanbeiException(FanbeiExceptionCode.USER_BANKCARD_NOT_EXIST_ERROR);
                }
                map = afRenewalDetailService.createRenewal(afBorrowCashDo, jfbAmount, repaymentAmount, actualAmount, userAmount, capital, borrowId, cardId, userId, request.getRemoteAddr(), userDto, context.getAppVersion());

                // 代收
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
       catch (Exception e){
            throw  e;
       }
        finally {
            bizCacheUtil.delCache(lockKey);
        }

    }
}
