package com.ald.fanbei.api.web.api.borrowCash;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.ald.fanbei.api.biz.util.NumberWordFormat;
import com.ald.fanbei.api.common.enums.*;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.util.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.dbunit.util.Base64;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.RiskVerifyRespBo;
import com.ald.fanbei.api.biz.service.AfBorrowCashService;
import com.ald.fanbei.api.biz.service.AfBorrowLegalOrderCashService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.service.AfUserBankcardService;
import com.ald.fanbei.api.biz.service.AfUserCouponService;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.biz.third.util.SmsUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfBorrowLegalOrderCashDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
import com.ald.fanbei.api.dal.domain.AfUserCouponDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * @类描述：
 * 
 * @author suweili 2017年3月24日下午6:28:50
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getConfirmBorrowInfoApi")
public class GetConfirmBorrowInfoApi extends GetBorrowCashBase implements ApiHandle {

	@Resource
	RiskUtil riskUtil;
	@Resource
	SmsUtil smsUtil;
	@Resource
	BizCacheUtil bizCacheUtil;
	@Resource
	AfResourceService afResourceService;
	@Resource
	AfUserAuthService afUserAuthService;
	@Resource
	AfBorrowCashService afBorrowCashService;
	@Resource
	AfUserBankcardService afUserBankcardService;
	@Resource
	AfUserAccountService afUserAccountService;
	@Resource
	AfUserCouponService afUserCouponService;

	@Resource
	NumberWordFormat numberWordFormat;
	@Resource
	AfBorrowLegalOrderCashService afBorrowLegalOrderCashService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();

		// FIXME 控制402版本借钱商品未还款，不能通过低版本借钱
		try {
			Integer version = context.getAppVersion();
			if (version <= 401) {
				// 查询是否与订单借款未还完
				AfBorrowCashDo borrowCashDo = afBorrowCashService.getBorrowCashByUserIdDescById(userId);
				if (borrowCashDo != null) {
					AfBorrowLegalOrderCashDo orderCashDo = afBorrowLegalOrderCashService
							.getBorrowLegalOrderCashByBorrowIdNoStatus(borrowCashDo.getRid());
					if (orderCashDo != null) {
						String status = orderCashDo.getStatus();
						if (StringUtils.equals(status, "AWAIT_REPAY") || StringUtils.equals(status, "PART_REPAID")) {
							return new ApiHandleResponse(requestDataVo.getId(),
									FanbeiExceptionCode.MUST_UPGRADE_NEW_VERSION_REPAY);
						}
					}
				}
			}
		} catch (Exception e) {
			// ignore error
		}
		AfResourceDo resourceDo = afResourceService.getConfigByTypesAndSecType(ResourceType.BORROW_CASH_SWITCH.getCode(),AfResourceSecType.BORROW_CASH_SWITCH.getCode());
		if (resourceDo != null && resourceDo.getValue().equals("Y")){
			if (context.getAppVersion() > 390){
				throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_STOP_ERROR);
			}else {
				throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_MAJIABAO_STOP_ERROR);
			}
		}
		AfUserAuthDo authDo = afUserAuthService.getUserAuthInfoByUserId(userId);
		if (StringUtils.equals(YesNoStatus.YES.getCode(), authDo.getRiskStatus())&& StringUtils.equals(YesNoStatus.NO.getCode(), authDo.getZmStatus())) {
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.ZM_STATUS_EXPIRED);
		}
		Map<String, Object> data = new HashMap<String, Object>();
		List<AfResourceDo> list = afResourceService.selectBorrowHomeConfigByAllTypes();
		Map<String, Object> rate = getObjectWithResourceDolist(list);
		if (!StringUtils.equals(rate.get("supuerSwitch").toString(), YesNoStatus.YES.getCode())) {
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.BORROW_CASH_SWITCH_NO);

		}
		data.put("realNameStatus", authDo.getRealnameStatus());
		if (StringUtil.equals(authDo.getRiskStatus(), RiskStatus.SECTOR.getCode())) {
			data.put("riskStatus", RiskStatus.A.getCode());
		} else {
			data.put("riskStatus", authDo.getRiskStatus());
		}

		data.put("faceStatus", authDo.getFacesStatus());

		AfUserAccountDo accountDo = afUserAccountService.getUserAccountByUserId(userId);

		data.put("idNumber", Base64.encodeString(accountDo.getIdNumber()));
		data.put("realName", accountDo.getRealName());

		// 判断是否绑定主卡
		data.put("isBind", authDo.getBankcardStatus());
		Boolean isPromote = true;
		// if (!StringUtils.equals(authDo.getZmStatus(),
		// YesNoStatus.YES.getCode()) ||
		// !StringUtils.equals(authDo.getFacesStatus(),
		// YesNoStatus.YES.getCode()) ||
		// !StringUtils.equals(authDo.getMobileStatus(),
		// YesNoStatus.YES.getCode()) ||
		// !StringUtils.equals(authDo.getYdStatus(), YesNoStatus.YES.getCode())
		// || !StringUtils.equals(authDo.getContactorStatus(),
		// YesNoStatus.YES.getCode()) ||
		// !StringUtils.equals(authDo.getLocationStatus(),
		// YesNoStatus.YES.getCode()) ||
		// !StringUtils.equals(authDo.getTeldirStatus(),
		// YesNoStatus.YES.getCode())) {
		if (!StringUtil.equals(authDo.getRiskStatus(), RiskStatus.YES.getCode())) {
			isPromote = false;
		}
		data.put("isPromote", isPromote ? YesNoStatus.YES.getCode() : YesNoStatus.NO.getCode());

		if (isPromote == true || StringUtils.equals(authDo.getBankcardStatus(), YesNoStatus.YES.getCode())) {
			// 可以借钱
			String amountStr = ObjectUtils.toString(requestDataVo.getParams().get("amount"));
			String type = ObjectUtils.toString(requestDataVo.getParams().get("type"));
			if (StringUtils.equals(amountStr, "") || !(numberWordFormat.isNumeric(type))) {
				// 推送处理
				smsUtil.sendBorrowCashErrorChannel(context.getUserName());
				return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.BORROW_CASH_AMOUNT_ERROR);
			}
			// 后台配置的金额限制(用户的借款额度根据可用额度进行限制)
			AfResourceDo limitRangeResource = afResourceService.getConfigByTypesAndSecType(
					AfResourceType.borrowRate.getCode(), AfResourceSecType.BorrowCashRange.getCode());
			if (limitRangeResource != null) {
				BigDecimal limitRangeStart = new BigDecimal(limitRangeResource.getValue1());
				BigDecimal limitRangeEnd = new BigDecimal(limitRangeResource.getValue());
				BigDecimal amount = new BigDecimal(amountStr);
				if (amount.compareTo(limitRangeStart) < 0 || amount.compareTo(limitRangeEnd) > 0) {
					return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.APPLY_CASHED_AMOUNT_ERROR);
				}
			}

			BigDecimal usableAmount = BigDecimalUtil.subtract(accountDo.getAuAmount(), accountDo.getUsedAmount());
			BigDecimal accountBorrow = calculateMaxAmount(usableAmount);
			if (StringUtil.equals(authDo.getRiskStatus(), RiskStatus.YES.getCode())
					&& accountBorrow.compareTo(new BigDecimal(amountStr)) < 0) {
				return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.BORROW_CASH_MORE_ACCOUNT_ERROR);
			}

			AfUserBankcardDo afUserBankcardDo = afUserBankcardService.getUserMainBankcardByUserId(userId);
			if (afUserBankcardDo == null) {
				return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_BANKCARD_NOT_EXIST_ERROR);
			}

			boolean isCanBorrowCash = afBorrowCashService.isCanBorrowCash(userId);
			if (!isCanBorrowCash) {
				return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.BORROW_CASH_STATUS_ERROR);
			}

			BigDecimal amount = NumberUtil.objToBigDecimalDefault(amountStr, BigDecimal.ZERO);

			// BigDecimal bankRate = new
			// BigDecimal(rate.get("bankRate").toString());
			// BigDecimal bankDouble = new
			// BigDecimal(rate.get("bankDouble").toString());
			// BigDecimal bankService =bankRate.multiply(bankDouble).divide(new
			// BigDecimal(360),6,RoundingMode.HALF_UP);
			BigDecimal poundageRate = new BigDecimal(rate.get("poundage").toString());

			// BigDecimal serviceRate =bankService.add(poundageRate);
			// BigDecimal serviceAmountDay = serviceRate.multiply(amount);
			JSONObject params = new JSONObject();
			String appName = (requestDataVo.getId().startsWith("i") ? "alading_ios" : "alading_and");
			params.put("ipAddress", CommonUtil.getIpAddr(request));
			params.put("appName",appName);
			data.put("bqsBlackBox",requestDataVo.getParams()==null?"":requestDataVo.getParams().get("bqsBlackBox"));
			data.put("blackBox",requestDataVo.getParams()==null?"":requestDataVo.getParams().get("blackBox"));
			Object poundageRateCash = getUserPoundageRate(userId,context.getUserName(),params);
			if (poundageRateCash != null) {
				poundageRate = new BigDecimal(poundageRateCash.toString());
			}

			BigDecimal serviceAmountDay = poundageRate.multiply(amount);// 新版本(v3.6)
																		// 服务费的计算
																		// 去掉利息

			try{
				logger.info("getConfirmBorrowInfoApi type="+type+",info="+JSON.toJSONString(requestDataVo.getParams()));
				AfResourceDo afResourceDo= afResourceService.getSingleResourceBytype("enabled_type_borrow");//是否允许这种类型的借款
				if(afResourceDo!=null&&afResourceDo.getValue().equals(YesNoStatus.YES.getCode())&&afResourceDo.getValue1().contains(type)){
					throw new FanbeiException(afResourceDo.getValue2(),true);
				}
			}catch (FanbeiException e){
				throw e;

			}catch (Exception e){
				logger.error("enabled_type_borrow error",e);
			}
			Integer day = NumberUtil.objToIntDefault(type, 0);

			BigDecimal serviceAmount = serviceAmountDay.multiply(new BigDecimal(day));
			data.put("serviceAmount", serviceAmount);
			data.put("amount", amount);
			data.put("arrivalAmount", BigDecimalUtil.subtract(amount, serviceAmount));
			data.put("banKName", afUserBankcardDo.getBankName());
			data.put("bankCard", afUserBankcardDo.getCardNumber());
			data.put("type", type);
			// 如果选择了优惠券，则不收任何手续费 add by jrb
			try {
				String couponId = ObjectUtils.toString(requestDataVo.getParams().get("couponId"));
				if (!StringUtils.isBlank(couponId)) {
					AfUserCouponDo afUserCouponDoTmp = new AfUserCouponDo();
					afUserCouponDoTmp.setCouponId(Long.parseLong(couponId));
					afUserCouponDoTmp.setUserId(userId);
					AfUserCouponDo afUserCouponDo = afUserCouponService.getUserCouponByDo(afUserCouponDoTmp);
					if (afUserCouponDo != null) {
						data.put("serviceAmount", BigDecimal.ZERO);
						data.put("arrivalAmount", data.get("amount"));
					}
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
			}

		}
		resp.setResponseData(data);
		return resp;
	}

	private Object getUserPoundageRate(Long userId,String userName,JSONObject params) {
		AfResourceDo resourceDo = afResourceService.getConfigByTypesAndSecType(AfResourceType.RISK_POUNDAGE_USERNAME_LIST.getCode(), AfResourceSecType.RISK_POUNDAGE_USERNAME_LIST.getCode());
		if(resourceDo!=null && "O".equals(resourceDo.getValue4()) && resourceDo.getValue().contains(userName)){
			//直接从风控系统取，没的话，走之前逻辑
			RiskVerifyRespBo riskResp = riskUtil.getUserLayRate(userId.toString(),params);
			String poundageRate = riskResp!=null?riskResp.getPoundageRate():"";
			if (!StringUtils.isBlank(poundageRate)) {
				logger.info("direct get user poundage rate from risk,not null: userName=" + userName + ",poundageRate=" + poundageRate);
				bizCacheUtil.saveObject(Constants.RES_BORROW_CASH_POUNDAGE_RATE + userId, poundageRate, Constants.SECOND_OF_ONE_MONTH);
				bizCacheUtil.saveObject(Constants.RES_BORROW_CASH_POUNDAGE_TIME + userId, new Date(System.currentTimeMillis()), Constants.SECOND_OF_ONE_MONTH);		
				return poundageRate;
			}else{
				logger.info("direct get user poundage rate from risk,is null,getUserPoundageRateByUserId:.userName=" + userName);
				return getUserPoundageRateByUserId(userId,params);
			}
		}else{
			logger.info("getUserPoundageRateByUserId from risk: userName=" + userName );
			return getUserPoundageRateByUserId(userId,params);
		}
	}
	
	private Object getUserPoundageRateByUserId(Long userId,JSONObject params){
		Date saveRateDate = (Date) bizCacheUtil.getObject(Constants.RES_BORROW_CASH_POUNDAGE_TIME + userId);
		if (saveRateDate == null
				|| DateUtil.compareDate(new Date(System.currentTimeMillis()), DateUtil.addDays(saveRateDate, 1))) {
			try {
				RiskVerifyRespBo riskResp = riskUtil.getUserLayRate(userId.toString(),params);
				String poundageRate = riskResp.getPoundageRate();
				if (!StringUtils.isBlank(riskResp.getPoundageRate())) {
					logger.info("comfirmBorrowCash get user poundage rate from risk: consumerNo="
							+ riskResp.getConsumerNo() + ",poundageRate=" + poundageRate);
					bizCacheUtil.saveObject(Constants.RES_BORROW_CASH_POUNDAGE_RATE + userId, poundageRate,
							Constants.SECOND_OF_ONE_MONTH);
					bizCacheUtil.saveObject(Constants.RES_BORROW_CASH_POUNDAGE_TIME + userId,
							new Date(System.currentTimeMillis()), Constants.SECOND_OF_ONE_MONTH);
				}
			} catch (Exception e) {
				logger.info(userId + "从风控获取分层用户额度失败：" + e);
			}
		}
		return bizCacheUtil.getObject(Constants.RES_BORROW_CASH_POUNDAGE_RATE + userId);
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
}
