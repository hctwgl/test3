package com.ald.fanbei.api.web.api.legalborrowV2;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

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
import com.ald.fanbei.api.common.enums.AfBorrowCashType;
import com.ald.fanbei.api.common.enums.RiskStatus;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
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
		
		AfUserAuthDo authDo = afUserAuthService.getUserAuthInfoByUserId(userId);
		if (StringUtils.equals(YesNoStatus.NO.getCode(), authDo.getZmStatus())) {
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
					Constants.BORROW_CASH_INFO_LEGAL);
			if (rateInfoDo != null) {
				BigDecimal minAmount = new BigDecimal(rateInfoDo.getValue4());
				BigDecimal maxAmount = new BigDecimal(rateInfoDo.getValue1());
				if (param.getAmount().compareTo(minAmount) < 0 || param.getAmount().compareTo(maxAmount) > 0) {
					return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.APPLY_CASHED_AMOUNT_ERROR);
				}
			}

			BigDecimal usableAmount = BigDecimalUtil.subtract(accountDo.getAuAmount(), accountDo.getUsedAmount());
			BigDecimal accountBorrow = calculateMaxAmount(usableAmount);
			if (StringUtil.equals(authDo.getRiskStatus(), RiskStatus.YES.getCode())
					&& accountBorrow.compareTo(param.getAmount()) < 0) {
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

			BigDecimal poundageRate = new BigDecimal(rate.get("poundage").toString());

			Object poundageRateCash = getUserPoundageRate(userId);
			if (poundageRateCash != null) {
				poundageRate = new BigDecimal(poundageRateCash.toString());
			}
			Integer day = NumberUtil.objToIntDefault(param.getType(), 0);
			// FIXME 校验商品Id
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

	private Object getUserPoundageRate(Long userId) {
		Date saveRateDate = (Date) bizCacheUtil.getObject(Constants.RES_BORROW_CASH_POUNDAGE_TIME + userId);
		if (saveRateDate == null
				|| DateUtil.compareDate(new Date(System.currentTimeMillis()), DateUtil.addDays(saveRateDate, 1))) {
			try {
				RiskVerifyRespBo riskResp = riskUtil.getUserLayRate(userId.toString());
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
	
	private Map<String, Object> getRateInfo(String borrowRate, String borrowType, String tag) {
		Map<String, Object> rateInfo = Maps.newHashMap();
		double serviceRate = 0;
		double interestRate = 0;
		JSONArray array = JSONObject.parseArray(borrowRate);
		double totalRate = 0;
		for (int i = 0; i < array.size(); i++) {
			JSONObject info = array.getJSONObject(i);
			String borrowTag = info.getString(tag + "Tag");
			if (StringUtils.equals("INTEREST_RATE", borrowTag)) {
				if (StringUtils.equals(AfBorrowCashType.SEVEN.getCode(), borrowType)) {
					interestRate = info.getDouble(tag + "SevenDay");
					totalRate += interestRate;
				} else {
					interestRate = info.getDouble(tag + "FourteenDay");
					totalRate += interestRate;
				}
			}
			if (StringUtils.equals("SERVICE_RATE", borrowTag)) {
				if (StringUtils.equals(AfBorrowCashType.SEVEN.getCode(), borrowType)) {
					serviceRate = info.getDouble(tag + "SevenDay");
					totalRate += serviceRate;
				} else {
					serviceRate = info.getDouble(tag + "FourteenDay");
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
