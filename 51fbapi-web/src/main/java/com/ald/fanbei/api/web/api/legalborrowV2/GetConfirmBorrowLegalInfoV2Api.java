package com.ald.fanbei.api.web.api.legalborrowV2;

import com.ald.fanbei.api.biz.bo.RiskVerifyRespBo;
import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.biz.third.util.SmsUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfBorrowCashType;
import com.ald.fanbei.api.common.enums.RiskStatus;
import com.ald.fanbei.api.common.enums.YesNoStatus;
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
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.dbunit.util.Base64;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @类描述：
 * 
 * @author Jiang Rongbo 2017年3月24日下午6:28:50
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getConfirmBorrowLegalInfoV2Api")
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
	AfUserCouponService afUserCouponService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();

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
			// 可以借钱
			String amountStr = ObjectUtils.toString(requestDataVo.getParams().get("amount"));
			String type = ObjectUtils.toString(requestDataVo.getParams().get("type"));
			if (StringUtils.equals(amountStr, "") || AfBorrowCashType.findRoleTypeByCode(type) == null) {
				// 推送处理
				smsUtil.sendBorrowCashErrorChannel(context.getUserName());
				return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.BORROW_CASH_AMOUNT_ERROR);
			}
			// 后台配置的金额限制(用户的借款额度根据可用额度进行限制)
			AfResourceDo rateInfoDo = afResourceService.getConfigByTypesAndSecType(Constants.BORROW_RATE,
					Constants.BORROW_CASH_INFO_LEGAL);
			if (rateInfoDo != null) {
				BigDecimal minAmount = new BigDecimal(rateInfoDo.getValue4());
				BigDecimal maxAmount = new BigDecimal(rateInfoDo.getValue1());
				BigDecimal amount = new BigDecimal(amountStr);
				if (amount.compareTo(minAmount) < 0 || amount.compareTo(maxAmount) > 0) {
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

			BigDecimal poundageRate = new BigDecimal(rate.get("poundage").toString());

			Object poundageRateCash = getUserPoundageRate(userId);
			if (poundageRateCash != null) {
				poundageRate = new BigDecimal(poundageRateCash.toString());
			}

			BigDecimal serviceAmountDay = poundageRate.multiply(amount);

			Integer day = NumberUtil.objToIntDefault(type, 0);

			BigDecimal serviceAmount = serviceAmountDay.multiply(new BigDecimal(day));
			data.put("serviceAmount", serviceAmount);
			data.put("amount", amount);
			data.put("arrivalAmount", BigDecimalUtil.subtract(amount, serviceAmount));
			data.put("banKName", afUserBankcardDo.getBankName());
			data.put("bankCard", afUserBankcardDo.getCardNumber());
			data.put("type", type);

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
}
