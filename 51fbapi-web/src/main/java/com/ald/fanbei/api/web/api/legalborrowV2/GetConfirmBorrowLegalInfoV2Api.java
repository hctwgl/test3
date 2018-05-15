package com.ald.fanbei.api.web.api.legalborrowV2;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.ald.fanbei.api.common.util.*;
import com.ald.fanbei.api.common.enums.*;
import org.apache.commons.lang.StringUtils;
import org.dbunit.util.Base64;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.RiskVerifyRespBo;
import com.ald.fanbei.api.biz.service.AfBorrowCashService;
import com.ald.fanbei.api.biz.service.AfBorrowLegalGoodsService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.service.AfUserBankcardService;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.biz.third.util.SmsUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
import com.ald.fanbei.api.web.api.borrowCash.GetBorrowCashBase;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.validator.Validator;
import com.ald.fanbei.api.web.validator.bean.GetConfirmBorrowLegalInfoParam;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;

/**
 * @类描述：
 *
 * @author Jiang Rongbo 2017年3月24日下午6:28:50
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getConfirmBorrowLegalInfoV2Api")
@Validator("getConfirmBorrowLegalInfoParam")
public class GetConfirmBorrowLegalInfoV2Api extends GetBorrowCashBase implements ApiHandle {

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
	AfBorrowLegalGoodsService afBorrowLegalGoodsService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();

		GetConfirmBorrowLegalInfoParam param =  (GetConfirmBorrowLegalInfoParam)requestDataVo.getParamObj();
		try{
			AfResourceDo afResourceDo= afResourceService.getSingleResourceBytype("enabled_type_borrow");//是否允许这种类型的借款
			if(afResourceDo!=null&&afResourceDo.getValue().equals(YesNoStatus.YES.getCode())&&afResourceDo.getValue1().contains(param.getType())){
				throw new FanbeiException(afResourceDo.getValue2(),true);
			}
		}catch (FanbeiException e){
			throw e;

		}catch (Exception e){
			logger.error("enabled_type_borrow error",e);
		}
		AfUserAuthDo authDo = afUserAuthService.getUserAuthInfoByUserId(userId);
		if (StringUtils.equals(YesNoStatus.YES.getCode(), authDo.getRiskStatus())&&StringUtils.equals(YesNoStatus.NO.getCode(), authDo.getZmStatus())) {
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

		if (!StringUtil.equals(authDo.getRiskStatus(), RiskStatus.YES.getCode())) {
			isPromote = false;
		}
		data.put("isPromote", isPromote ? YesNoStatus.YES.getCode() : YesNoStatus.NO.getCode());

		if (isPromote == true || StringUtils.equals(authDo.getBankcardStatus(), YesNoStatus.YES.getCode())) {
			// 后台配置的金额限制(用户的借款额度根据可用额度进行限制)
			AfResourceDo rateInfoDo = afResourceService.getConfigByTypesAndSecType(Constants.BORROW_RATE,
					Constants.BORROW_CASH_INFO_LEGAL_NEW);
			if (rateInfoDo != null) {
				BigDecimal minAmount = new BigDecimal(rateInfoDo.getValue4());
				BigDecimal maxAmount = new BigDecimal(rateInfoDo.getValue1());
				if (param.getAmount().compareTo(minAmount) < 0 || param.getAmount().compareTo(maxAmount) > 0) {
					throw new FanbeiException(FanbeiExceptionCode.APPLY_CASHED_AMOUNT_ERROR);
				}
			}

			BigDecimal usableAmount = BigDecimalUtil.subtract(accountDo.getAuAmount(), accountDo.getUsedAmount());
			BigDecimal accountBorrow = calculateMaxAmount(usableAmount);
			if (StringUtil.equals(authDo.getRiskStatus(), RiskStatus.YES.getCode())
					&& accountBorrow.compareTo(param.getAmount()) < 0) {
				throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_MORE_ACCOUNT_ERROR);
			}

			AfUserBankcardDo afUserBankcardDo = afUserBankcardService.getUserMainBankcardByUserId(userId);
			if (afUserBankcardDo == null) {
				throw new FanbeiException(FanbeiExceptionCode.USER_BANKCARD_NOT_EXIST_ERROR);
			}
			AfResourceDo resourceDo = afResourceService.getConfigByTypesAndSecType(ResourceType.BORROW_CASH_SWITCH.getCode(),AfResourceSecType.BORROW_CASH_SWITCH.getCode());
			boolean isCanBorrowCash = afBorrowCashService.isCanBorrowCash(userId);
			if (!isCanBorrowCash) {
				if (resourceDo != null && resourceDo.getValue().equals("Y")){
					throw new FanbeiException(FanbeiExceptionCode.JSD_BORROW_CASH_STATUS_ERROR);
				}else {
					throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_STATUS_ERROR);
				}
			}else if (resourceDo != null && resourceDo.getValue().equals("Y")){
				if (context.getAppVersion() > 390){
				throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_STOP_ERROR);
				}else {
					throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_MAJIABAO_STOP_ERROR);
				}
			}

			BigDecimal poundageRate = new BigDecimal(rate.get("poundage").toString());

			String appName = (requestDataVo.getId().startsWith("i") ? "alading_ios" : "alading_and");
			data.put("ipAddress", CommonUtil.getIpAddr(request));
			data.put("appName",appName);data.put("bqsBlackBox",requestDataVo.getParams()==null?"":requestDataVo.getParams().get("bqsBlackBox"));
			data.put("blackBox",requestDataVo.getParams()==null?"":requestDataVo.getParams().get("blackBox"));
			Object poundageRateCash = getUserPoundageRate(userId,data,param.getType());
			if (poundageRateCash != null) {
				poundageRate = new BigDecimal(poundageRateCash.toString());
			}
			Integer day = NumberUtil.objToIntDefault(param.getType(), 0);
			//  校验商品Id
			BigDecimal newRate = null;
			if (rateInfoDo != null) {
				String borrowRate = rateInfoDo.getValue2();
				Map<String, Object> rateInfo = getRateInfo(borrowRate, param.getType(), "borrow");
				double totalRate = (double) rateInfo.get("totalRate");
				newRate = BigDecimal.valueOf(totalRate / 100);
			} else {
				newRate = BigDecimal.valueOf(0.36);
			}

			newRate = newRate.divide(BigDecimal.valueOf(Constants.ONE_YEAY_DAYS), 6, RoundingMode.HALF_UP);
			BigDecimal profitAmount = poundageRate.subtract(newRate).multiply(param.getAmount()).multiply(new BigDecimal(day));
			if (profitAmount.compareTo(BigDecimal.ZERO) <= 0) {
				profitAmount = BigDecimal.ZERO;
			}
			// 如果用户未登录，则利润空间为0
			if (userId == null) {
				profitAmount = BigDecimal.ZERO;
			}
			logger.info("UserId = "+ userId +",Calculated profitAmount = " + profitAmount );
			List<Long> allGoodsId = afBorrowLegalGoodsService.getGoodsIdByProfitAmoutForV2(profitAmount);
			if(!allGoodsId.contains(param.getGoodsId())) {
				throw new FanbeiException("请检查网络，稍后重新再试！",true);
			}

			data.put("serviceAmount", "0");
			data.put("amount", param.getAmount());
			data.put("arrivalAmount", param.getAmount());
			data.put("banKName", afUserBankcardDo.getBankName());
			data.put("bankCard", afUserBankcardDo.getCardNumber());
			data.put("type", param.getType());

		}
		resp.setResponseData(data);
		return resp;
	}

	private Object getUserPoundageRate(Long userId,Map<String, Object> data,String borrowType) {
		BigDecimal orgRate = null;
		try {
			RiskVerifyRespBo riskResp = riskUtil.getUserLayRate(userId.toString(),new JSONObject(data),borrowType);
			String poundageRate = riskResp.getPoundageRate();
			if (!StringUtils.isBlank(riskResp.getPoundageRate())) {
				orgRate = new BigDecimal(poundageRate);
			}
		} catch (Exception e) {
			logger.info(userId + "从风控获取分层用户额度失败：" + e);
		}
		return orgRate;

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

	private Map<String, Object> getRateInfo(String borrowRate, String borrowType, String tag) {
		AfResourceDo afResourceDo = afResourceService.getConfigByTypesAndSecType(ResourceType.BORROW_RATE.getCode(), AfResourceSecType.BORROW_CASH_INFO_LEGAL_NEW.getCode());
		String oneDay = "";
		String twoDay = "";
		if(null != afResourceDo){
			oneDay = afResourceDo.getTypeDesc().split(",")[0];
			twoDay = afResourceDo.getTypeDesc().split(",")[1];
		}
		Map<String, Object> rateInfo = Maps.newHashMap();
		double serviceRate = 0;
		double interestRate = 0;
		JSONArray array = JSONObject.parseArray(borrowRate);
		double totalRate = 0;
		for (int i = 0; i < array.size(); i++) {
			JSONObject info = array.getJSONObject(i);
			String borrowTag = info.getString(tag + "Tag");
			if (StringUtils.equals("INTEREST_RATE", borrowTag)) {
				if (StringUtils.equals(oneDay, borrowType)) {
					interestRate = info.getDouble(tag + "FirstType");
					totalRate += interestRate;
				} else if (StringUtils.equals(twoDay, borrowType)){
					interestRate = info.getDouble(tag + "SecondType");
					totalRate += interestRate;
				}
			}
			if (StringUtils.equals("SERVICE_RATE", borrowTag)) {
				if (StringUtils.equals(oneDay, borrowType)) {
					serviceRate = info.getDouble(tag + "FirstType");
					totalRate += serviceRate;
				} else if (StringUtils.equals(twoDay, borrowType)){
					serviceRate = info.getDouble(tag + "SecondType");
					totalRate += serviceRate;
				}
			}

		}
		rateInfo.put("serviceRate", serviceRate);
		rateInfo.put("interestRate", interestRate);
		rateInfo.put("totalRate", totalRate);
		return rateInfo;
	}
}
