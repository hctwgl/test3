package com.ald.fanbei.api.web.api.legalborrow;


import com.ald.fanbei.api.biz.bo.UpsCollectRespBo;
import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.third.util.yibaopay.YiBaoUtility;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.YesNoStatus;
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
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**  
 * @Description: 续期确认支付（合规）
 * @Copyright (c) 浙江阿拉丁电子商务股份有限公司 All Rights Reserved.
 * @author yanghailong
 * @date 2017年12月8日
 */
@Component("confirmLegalRenewalPayApi")
public class ConfirmLegalRenewalPayApi implements ApiHandle {
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
    AfUserAuthService afUserAuthService;
    @Resource
    AfBorrowLegalOrderService afBorrowLegalOrderService;
    @Resource
    AfBorrowLegalOrderCashService afBorrowLegalOrderCashService;
    @Resource
    AfRenewalLegalDetailService afRenewalLegalDetailService;

    @Resource
    YiBaoUtility yiBaoUtility;

    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
        Long userId = context.getUserId();
//		BigDecimal repaymentAmount = NumberUtil.objToBigDecimalDefault(ObjectUtils.toString(requestDataVo.getParams().get("repaymentAmount")), BigDecimal.ZERO);
//		BigDecimal actualAmount = NumberUtil.objToBigDecimalDefault(ObjectUtils.toString(requestDataVo.getParams().get("actualAmount")), BigDecimal.ZERO);
       
        //账户余额
        //BigDecimal userAmount = NumberUtil.objToBigDecimalDefault(ObjectUtils.toString(requestDataVo.getParams().get("rebateAmount")), BigDecimal.ZERO);
        //借款id
        Long borrowId = NumberUtil.objToLongDefault(ObjectUtils.toString(requestDataVo.getParams().get("borrowId")), 0l);
        //支付密码 md5
        String payPwd = ObjectUtils.toString(requestDataVo.getParams().get("payPwd"), "").toString();
        //银行卡id -1:微信支付 -2:余额支付 -3支付宝支付
        Long cardId = NumberUtil.objToLongDefault(ObjectUtils.toString(requestDataVo.getParams().get("cardId")), 0l);
        //集分宝个数
        //BigDecimal jfbAmount = NumberUtil.objToBigDecimalDefault(ObjectUtils.toString(requestDataVo.getParams().get("jfbAmount")), BigDecimal.ZERO);
        //续借金额
        BigDecimal renewalAmount = NumberUtil.objToBigDecimalDefault(requestDataVo.getParams().get("renewalAmount"), BigDecimal.ZERO);
        
        Long goodsId = NumberUtil.objToLongDefault(ObjectUtils.toString(requestDataVo.getParams().get("goodsId")), 0l);
        String deliveryUser = ObjectUtils.toString(requestDataVo.getParams().get("deliveryUser"), "").toString();
        String deliveryPhone = ObjectUtils.toString(requestDataVo.getParams().get("deliveryPhone"), "").toString();
        String address = ObjectUtils.toString(requestDataVo.getParams().get("address"), "").toString();
        
        //用户认证信息
        AfUserAuthDo afUserAuthDo = afUserAuthService.getUserAuthInfoByUserId(userId);

        if(StringUtils.equals(YesNoStatus.NO.getCode(), afUserAuthDo.getZmStatus())){
            return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.ZM_STATUS_EXPIRED);
        }

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
        if (goodsId == 0) {
        	throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_NOT_EXIST_ERROR);
        }

        String lockKey = Constants.CACHEKEY_APPLY_RENEWAL_LOCK + userId;
        boolean isGetLock = bizCacheUtil.getLock30Second(lockKey, "1");

        if (!isGetLock) {
            return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.RENEWAL_ORDER_NOT_EXIST_ERROR);
        }
        try {
        	//获取最近一次还款记录
            AfRepaymentBorrowCashDo afRepaymentBorrowCashDo = afRepaymentBorrowCashService.getLastRepaymentBorrowCashByBorrowId(borrowId);
            if (null != afRepaymentBorrowCashDo && StringUtils.equals(afRepaymentBorrowCashDo.getStatus(), "P")) {
                throw new FanbeiException("There is a repayment is processing", FanbeiExceptionCode.HAVE_A_REPAYMENT_PROCESSING_ERROR);
            }

            //获取最近一次续期记录
            AfRenewalDetailDo afRenewalDetailDo = afRenewalDetailService.getRenewalDetailByBorrowId(borrowId);
            if (null != afRenewalDetailDo && StringUtils.equals(afRenewalDetailDo.getStatus(), "P")) {
                throw new FanbeiException("There is a renewal is processing", FanbeiExceptionCode.HAVE_A_PROCESS_RENEWAL_DETAIL);
            }

            //获取用户信息
            AfUserAccountDo userDto = afUserAccountService.getUserAccountByUserId(userId);
            if (userDto == null) {
                throw new FanbeiException("Account is invalid", FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
            }
            
            // 余额支付或银行卡支付
            if (cardId == -2 || cardId>0) {
                String inputOldPwd = UserUtil.getPassword(payPwd, userDto.getSalt());
                if (!StringUtils.equals(inputOldPwd, userDto.getPassword())) {
                    return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_PAY_PASSWORD_INVALID_ERROR);
                }
            }

            //获取借款记录
            AfBorrowCashDo afBorrowCashDo = afBorrowCashService.getBorrowCashByrid(borrowId);
            if (afBorrowCashDo == null || !ObjectUtils.equals(afBorrowCashDo.getStatus(), "TRANSED")) {
                throw new FanbeiException("Nothing order can renewal", FanbeiExceptionCode.RENEWAL_ORDER_NOT_EXIST_ERROR);
            }

            /*if (userDto.getRebateAmount().compareTo(userAmount) < 0) {
                throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_REPAY_AMOUNT__ERROR);
            }*/

            /*BigDecimal jfb = BigDecimalUtil.divide(jfbAmount, new BigDecimal(100));
            if (userDto.getJfbAmount().compareTo(jfbAmount) < 0) {
                throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_REPAY_AMOUNT__ERROR);
            }*/

            // 续期天数
            AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(Constants.RES_RENEWAL_DAY_LIMIT, Constants.RES_ALLOW_RENEWAL_DAY);
            BigDecimal allowRenewalDay = new BigDecimal(resource.getValue());// 允许续期天数
			/*AfResourceDo poundageResource = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE, Constants.RES_BORROW_CASH_POUNDAGE);
			BigDecimal borrowCashPoundage = new BigDecimal(poundageResource.getValue());// 借钱手续费率（日）
			Object poundageRateCash = bizCacheUtil.getObject(Constants.RES_BORROW_CASH_POUNDAGE_RATE + afBorrowCashDo.getUserId());
			if (poundageRateCash != null) {
				borrowCashPoundage = new BigDecimal(poundageRateCash.toString());
			}*/
            
            // 借钱手续费率（日）
            BigDecimal borrowCashPoundage = afBorrowCashDo.getPoundageRate();
            // 查询新利率配置
    		AfResourceDo rateInfoDo = afResourceService.getConfigByTypesAndSecType(Constants.BORROW_RATE,Constants.BORROW_CASH_INFO_LEGAL);
    		
    		//借款利率
    		BigDecimal newRate = null;
    		//借款手续费率
    		BigDecimal newServiceRate = null;
    		
    		if (rateInfoDo != null) {
    			String borrowRate = rateInfoDo.getValue2();
    			JSONArray array = JSONObject.parseArray(borrowRate);
    			double rate = 0;
    			double serviceRate = 0;
    			for (int i = 0; i < array.size(); i++) {
    				JSONObject info = array.getJSONObject(i);
    				String borrowTag = info.getString("borrowTag");
    				if (StringUtils.equals("INTEREST_RATE", borrowTag)) {
    						rate = info.getDouble("borrowSevenDay");
    				}
    				if (StringUtils.equals("SERVICE_RATE", borrowTag)) {
    					serviceRate = info.getDouble("borrowSevenDay");
    				}
    			}
    			newRate = BigDecimal.valueOf(rate / 100);
    			newServiceRate = BigDecimal.valueOf(serviceRate / 100);
    		}else{
    			throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_RATE_ERROR);
    		}		
            
            
            BigDecimal capital =BigDecimal.ZERO;

            AfResourceDo capitalRateResource = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE, Constants.RENEWAL_CAPITAL_RATE);
            BigDecimal renewalCapitalRate = new BigDecimal(capitalRateResource.getValue());// 续借应还借钱金额比例
            capital = afBorrowCashDo.getAmount().multiply(renewalCapitalRate).setScale(2, RoundingMode.HALF_UP);
       
            if (renewalAmount.compareTo(BigDecimalUtil.ONE_HUNDRED) < 0) {   //判断续借金额是否大于100
                throw new FanbeiException(
                        FanbeiExceptionCode.RENEWAL_CASH_REPAY_AMOUNT_LESS_ONE_HUNDRED);
            }
           
            //---------
            //上一笔订单记录
    		AfBorrowLegalOrderDo afBorrowLegalOrder = afBorrowLegalOrderService.getLastBorrowLegalOrderByBorrowId(afBorrowCashDo.getRid());
    		if(afBorrowLegalOrder==null){
    			throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_ORDER_NOT_EXIST_ERROR);
    		}
    		AfBorrowLegalOrderCashDo afBorrowLegalOrderCash = afBorrowLegalOrderCashService.getBorrowLegalOrderCashByBorrowLegalOrderId(afBorrowLegalOrder.getRid());

    		//上期总手续费
    		BigDecimal poundage = BigDecimal.ZERO;
    		//上期借款手续费
    		BigDecimal borrowPoundage = BigDecimal.ZERO;
    		//上期订单手续费
    		BigDecimal orderPoundage = afBorrowLegalOrderCash.getPoundageAmount();
    		//上期借款利息
    		BigDecimal borrowRateAmount = BigDecimal.ZERO;
    		//上期订单利息
    		BigDecimal orderRateAmount = afBorrowLegalOrderCash.getInterestAmount();
    		BigDecimal oneYeayDays = new BigDecimal(Constants.ONE_YEAY_DAYS);
    		
    		if(afBorrowCashDo.getRenewalNum()>0){
    			//续借过
    			AfRenewalDetailDo renewalDetail = afRenewalLegalDetailService.getLastRenewalDetailByBorrowId(afBorrowCashDo.getRid());
    			// 续期手续费 = 上期续借金额 * 上期续借天数 * 借款手续费率  / 360
    			borrowPoundage = renewalDetail.getRenewalAmount().multiply(allowRenewalDay).multiply(newServiceRate).divide(oneYeayDays).setScale(2, RoundingMode.HALF_UP);
    			// 续期利息 = 上期续借金额 * 上期续借天数  * 借款利率 / 360
    			borrowRateAmount = renewalDetail.getRenewalAmount().multiply(allowRenewalDay).multiply(newRate).divide(oneYeayDays).setScale(2, RoundingMode.HALF_UP);
    		}else {
    			//未续借过
    			borrowPoundage = afBorrowCashDo.getPoundage();
    			borrowRateAmount = afBorrowCashDo.getRateAmount();
    		}
    		
    		// 续借本金（总） 
    		BigDecimal allAmount = BigDecimalUtil.add(afBorrowCashDo.getAmount(), afBorrowCashDo.getSumOverdue(),afBorrowCashDo.getSumRate(),borrowPoundage);
    		// 续期金额 = 续借本金（总）  - 借款已还金额 - 续借需要支付本金
    		BigDecimal waitPaidAmount = BigDecimalUtil.subtract(allAmount, afBorrowCashDo.getRepayAmount()).subtract(capital);
    		
    		//判断续借金额是否大于借款金额
    		BigDecimal allRenewalAmount= BigDecimalUtil.subtract(allAmount, afBorrowCashDo.getRepayAmount());
    		if (renewalAmount.compareTo(allRenewalAmount) >0) {
    			throw new FanbeiException(
    					FanbeiExceptionCode.RENEWAL_CASH_REPAY_AMOUNT_MORE_BORROW_ERROR);
    		}
    		
    		//借款已还金额
    		BigDecimal borrowRepayAmount = afBorrowCashDo.getRepayAmount();
    		//订单已还金额
    		BigDecimal orderRepayAmount = afBorrowLegalOrderCash.getRepaidAmount();

    		//上期逾期费（借款和订单）
    		BigDecimal borrowOverdueAmount = afBorrowCashDo.getOverdueAmount();
    		BigDecimal orderOverdueAmount = afBorrowLegalOrderCash.getOverdueAmount();

    		//上期订单借款金额
    		BigDecimal orderAmount = afBorrowLegalOrderCash.getAmount();
    		
    		
    		//根据订单已还金额大小，重置： 订单的逾期费、手续费、利率费、订单本金
    		if(orderRepayAmount.compareTo(BigDecimalUtil.add(orderOverdueAmount,orderPoundage,orderRateAmount))>=0){
    			orderOverdueAmount = BigDecimal.ZERO;
    			orderPoundage = BigDecimal.ZERO;
    			orderRateAmount = BigDecimal.ZERO;
    			orderAmount = orderAmount.subtract(orderRepayAmount.subtract(BigDecimalUtil.add(orderOverdueAmount, orderPoundage,orderRateAmount)));
    		}else {
    			if(orderRepayAmount.compareTo(BigDecimalUtil.add(orderOverdueAmount,orderPoundage))>=0){
    				orderOverdueAmount = BigDecimal.ZERO;
    				orderPoundage = BigDecimal.ZERO;
    				orderRateAmount = orderRateAmount.subtract(orderRepayAmount.subtract(BigDecimalUtil.add(orderOverdueAmount, orderPoundage)));
    			}else {
    				if(orderRepayAmount.compareTo(BigDecimalUtil.add(orderOverdueAmount))>0){
    					orderOverdueAmount = BigDecimal.ZERO;
    					orderPoundage = orderPoundage.subtract(orderRepayAmount.subtract(orderOverdueAmount));
    				}else {
    					orderOverdueAmount = orderOverdueAmount.subtract(orderRepayAmount);
    				}
    			}
    		}
    		
    		//根据借款已还金额大小，重置： 借款逾期费、手续费、利率费
    		if(borrowRepayAmount.compareTo(BigDecimalUtil.add(borrowOverdueAmount,borrowPoundage,borrowRateAmount))>=0){
    			borrowOverdueAmount = BigDecimal.ZERO;
    			borrowPoundage = BigDecimal.ZERO;
    			borrowRateAmount = BigDecimal.ZERO;
    		}else {
    			if(borrowRepayAmount.compareTo(BigDecimalUtil.add(borrowOverdueAmount,borrowPoundage))>=0){
    				borrowOverdueAmount = BigDecimal.ZERO;
    				borrowPoundage = BigDecimal.ZERO;
    				borrowRateAmount = borrowRateAmount.subtract(borrowRepayAmount.subtract(BigDecimalUtil.add(borrowOverdueAmount, borrowPoundage)));
    			}else {
    				if(borrowRepayAmount.compareTo(BigDecimalUtil.add(borrowOverdueAmount))>0){
    					borrowOverdueAmount = BigDecimal.ZERO;
    					borrowPoundage = borrowPoundage.subtract(borrowRepayAmount.subtract(borrowOverdueAmount));
    				}else {
    					borrowOverdueAmount = borrowOverdueAmount.subtract(borrowRepayAmount);
    				}
    			}
    		}
    		
    		//上期总手续费
    		poundage = BigDecimalUtil.add(borrowPoundage,orderPoundage);
    		//上期总逾期费
    		BigDecimal overdueAmount = BigDecimalUtil.add(borrowOverdueAmount,orderOverdueAmount);
    		//上期总利息
    		BigDecimal rateAmount = BigDecimalUtil.add(borrowRateAmount,orderRateAmount);

    		// 续期应缴费用(上期总利息+上期总手续费+上期总逾期费+要还本金  +上期待还订单)
    		BigDecimal repaymentAmount = BigDecimalUtil.add(rateAmount, poundage, overdueAmount, capital,orderAmount);

    		//---------
    		
    		//fmf_add 续借由用户手动操作    end

    		/*if (context.getAppVersion() < 380) {
                waitPaidAmount = BigDecimalUtil.subtract(allAmount, afBorrowCashDo.getRepayAmount());
                repaymentAmount = BigDecimalUtil.add(afBorrowCashDo.getRateAmount(), poundage, afBorrowCashDo.getOverdueAmount());
            }*/

            //BigDecimal actualAmount = BigDecimalUtil.subtract(repaymentAmount, jfb).subtract(userAmount);
    		BigDecimal actualAmount = repaymentAmount;

            if(! yiBaoUtility.checkCanNext(userId,1)){
                return new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.HAVE_A_PROCESS_RENEWAL_DETAIL);
            }

            BigDecimal jfbAmount = BigDecimal.ZERO;
            BigDecimal userAmount = BigDecimal.ZERO;
            Map<String, Object> map;
            /*if (cardId == -2) {// 余额支付
                map = afRenewalDetailService.createRenewal(afBorrowCashDo, jfbAmount, repaymentAmount, actualAmount, userAmount, capital, borrowId, cardId, userId, "", userDto, context.getAppVersion());

                resp.addResponseData("refId", map.get("refId"));
                resp.addResponseData("type", map.get("type"));
            } */
            if (cardId == -1) {// 微信支付
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
            else  if(cardId ==-3){ //支付宝支付
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
                AfResourceDo afResource = afResourceService.getSingleResourceBytype("bank_repay_limit_" + card.getBankCode());
                if (afResource != null && afResource.getValue().equals(card.getBankCode())) {
                    Long limitValue = Long.valueOf(afResource.getValue1());//限制金额
                    if (actualAmount.compareTo(new BigDecimal(limitValue)) > 0) {
                        throw new FanbeiException(FanbeiExceptionCode.USER_BANKCARD_RENEW_LIMIT_ERROR);//提示语
                    }
                }
                //map = afRenewalDetailService.createRenewal(afBorrowCashDo, jfbAmount, repaymentAmount, actualAmount, userAmount, capital, borrowId, cardId, userId, request.getRemoteAddr(), userDto, context.getAppVersion());
                map = afRenewalLegalDetailService.createLegalRenewal(afBorrowCashDo, jfbAmount, repaymentAmount, actualAmount, userAmount, capital, borrowId, cardId, userId, request.getRemoteAddr(), userDto, context.getAppVersion(), goodsId, deliveryUser, deliveryPhone, address);

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
