package com.ald.fanbei.api.web.api.repaycash;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.dbunit.util.Base64;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.UpsCollectRespBo;
import com.ald.fanbei.api.biz.bo.thirdpay.ThirdBizType;
import com.ald.fanbei.api.biz.bo.thirdpay.ThirdPayTypeEnum;
import com.ald.fanbei.api.biz.service.AfBorrowCashService;
import com.ald.fanbei.api.biz.service.AfBorrowLegalOrderCashService;
import com.ald.fanbei.api.biz.service.AfRenewalDetailService;
import com.ald.fanbei.api.biz.service.AfRepaymentBorrowCashService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserBankcardService;
import com.ald.fanbei.api.biz.service.AfUserCouponService;
import com.ald.fanbei.api.biz.service.AfUserWithholdService;
import com.ald.fanbei.api.biz.third.util.yibaopay.YiBaoUtility;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfBorrowCashRepmentStatus;
import com.ald.fanbei.api.common.enums.AfRenewalDetailStatus;
import com.ald.fanbei.api.common.enums.CouponStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.UserUtil;
import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfRenewalDetailDo;
import com.ald.fanbei.api.dal.domain.AfRepaymentBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserCouponDto;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * @author honghzengpei 2017/9/7 13:48
 * @类描述：现金贷还款
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getConfirmRepayInfoV1Api")
public class GetConfirmRepayInfoV1Api implements ApiHandle {
	BigDecimal showAmount;
	@Resource
	AfUserCouponService afUserCouponService;
	@Resource
	AfUserAccountService afUserAccountService;
	@Resource
	AfUserBankcardService afUserBankcardService;
	@Resource
	AfRepaymentBorrowCashService afRepaymentBorrowCashService;

	@Resource
	AfBorrowCashService afBorrowCashService;
	@Resource
	AfRenewalDetailService afRenewalDetailService;
	@Resource
	AfResourceService afResourceService;

	@Resource
	YiBaoUtility yiBaoUtility;

	@Resource
	BizCacheUtil bizCacheUtil;
	@Resource
	AfUserWithholdService afUserWithholdService;

	@Resource
	AfBorrowLegalOrderCashService afBorrowLegalOrderCashService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		BigDecimal repaymentAmount = NumberUtil.objToBigDecimalDefault(
				ObjectUtils.toString(requestDataVo.getParams().get("repaymentAmount")), BigDecimal.ZERO);
		BigDecimal actualAmount = NumberUtil.objToBigDecimalDefault(
				ObjectUtils.toString(requestDataVo.getParams().get("actualAmount")), BigDecimal.ZERO);

		Long couponId = NumberUtil.objToLongDefault(ObjectUtils.toString(requestDataVo.getParams().get("couponId")),
				0l);
		BigDecimal userAmount = NumberUtil.objToBigDecimalDefault(
				ObjectUtils.toString(requestDataVo.getParams().get("rebateAmount")), BigDecimal.ZERO);
		Long borrowId = NumberUtil.objToLongDefault(ObjectUtils.toString(requestDataVo.getParams().get("borrowId")),
				0l);
		String payPwd = ObjectUtils.toString(requestDataVo.getParams().get("payPwd"), "").toString();
		Long cardId = NumberUtil.objToLongDefault(ObjectUtils.toString(requestDataVo.getParams().get("cardId")), 0l);
		BigDecimal jfbAmount = NumberUtil.objToBigDecimalDefault(
				ObjectUtils.toString(requestDataVo.getParams().get("jfbAmount")), BigDecimal.ZERO);
		 String bankPayType = ObjectUtils.toString(requestDataVo.getParams().get("payType"),null);
		// 对402版本借钱，低版本还款情况做控制
		afBorrowLegalOrderCashService.checkIllegalVersionInvoke(context.getAppVersion(), borrowId);

		AfRepaymentBorrowCashDo rbCashDo = afRepaymentBorrowCashService.getLastRepaymentBorrowCashByBorrowId(borrowId);
		if (borrowId == 0) {
			throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_NOT_EXIST_ERROR);
		}
		if (rbCashDo != null && StringUtils.equals(rbCashDo.getStatus(), AfBorrowCashRepmentStatus.PROCESS.getCode())) {
			throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_REPAY_PROCESS_ERROR);
		}
		// 判断是否存在续期处理中的记录,防止续期和还款交叉,导致最后记录更新失败
		AfRenewalDetailDo lastAfRenewalDetailDo = afRenewalDetailService.getRenewalDetailByBorrowId(borrowId);
		if (lastAfRenewalDetailDo != null
				&& AfRenewalDetailStatus.PROCESS.getCode().equals(lastAfRenewalDetailDo.getStatus())) {
			throw new FanbeiException(FanbeiExceptionCode.HAVE_A_PROCESS_RENEWAL_DETAIL);
		}

		AfUserAccountDo userDto = afUserAccountService.getUserAccountByUserId(userId);
		if (userDto == null) {
			throw new FanbeiException("Account is invalid", FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
		}
		if (cardId == -2 || cardId > 0) {
			String inputOldPwd = UserUtil.getPassword(payPwd, userDto.getSalt());
			if (!StringUtils.equals(inputOldPwd, userDto.getPassword())) {
				return new ApiHandleResponse(requestDataVo.getId(),
						FanbeiExceptionCode.USER_PAY_PASSWORD_INVALID_ERROR);
			}
		}

		AfUserCouponDto coupon = afUserCouponService.getUserCouponById(couponId);
		if (null != coupon && !coupon.getStatus().equals(CouponStatus.NOUSE.getCode())) {
			throw new FanbeiException(FanbeiExceptionCode.USER_COUPON_ERROR);
		}

		// if (!yiBaoUtility.checkCanNext(userId, 0)) {
		// return new ApiHandleResponse(requestDataVo.getId(),
		// FanbeiExceptionCode.HAVE_A_REPAYMENT_PROCESSING_ERROR);
		// }

		AfBorrowCashDo afBorrowCashDo = afBorrowCashService.getBorrowCashByrid(borrowId);
		if (afBorrowCashDo != null) {

			BigDecimal allAmount = BigDecimalUtil.add(afBorrowCashDo.getAmount(), afBorrowCashDo.getSumOverdue(),
					afBorrowCashDo.getOverdueAmount(), afBorrowCashDo.getRateAmount(), afBorrowCashDo.getSumRate());

			BigDecimal temAmount = BigDecimalUtil.subtract(allAmount, afBorrowCashDo.getRepayAmount());
			if (temAmount.compareTo(repaymentAmount) < 0) {
				throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_REPAY_AMOUNT_MORE_BORROW_ERROR);
			}
		}

		// bizCacheUtil.delCache(CacheConstants.RESOURCE.RESOURCE_CONFIG_TYPES_LIST.getCode());
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
		// 使用优惠券结算金额
		if (coupon != null) {
			showAmount = BigDecimalUtil.subtract(repaymentAmount, coupon.getAmount());
		}
		// 优惠券金额大于还款金额其他数据处理
		if (showAmount.compareTo(BigDecimal.ZERO) <= 0) {
			logger.info(userDto.getUserName() + "coupon repayment");
			jfbAmount = BigDecimal.ZERO;
			userAmount = BigDecimal.ZERO;
			showAmount = BigDecimal.ZERO;
		}
		BigDecimal myjfb = BigDecimalUtil.divide(userDto.getJfbAmount(), new BigDecimal(100));
		// 使用集分宝处理
		if (jfbAmount.compareTo(BigDecimal.ZERO) > 0 && showAmount.compareTo(myjfb) > 0) {

			showAmount = BigDecimalUtil.subtract(showAmount, myjfb);
			jfbAmount = userDto.getJfbAmount();
		} else if (jfbAmount.compareTo(BigDecimal.ZERO) > 0 && showAmount.compareTo(myjfb) <= 0) {
			// 集分宝金额大于还款金额
			jfbAmount = BigDecimalUtil.multiply(showAmount, new BigDecimal(100));
			userAmount = BigDecimal.ZERO;
			showAmount = BigDecimal.ZERO;
		}
		// 余额处理
		if (userAmount.compareTo(BigDecimal.ZERO) > 0 && showAmount.compareTo(userDto.getRebateAmount()) > 0) {
			showAmount = BigDecimalUtil.subtract(showAmount, userDto.getRebateAmount());
			userAmount = userDto.getRebateAmount();
		} else if (userAmount.compareTo(BigDecimal.ZERO) > 0 && showAmount.compareTo(userDto.getRebateAmount()) <= 0) {
			userAmount = showAmount;
			showAmount = BigDecimal.ZERO;
		}

		if (actualAmount.compareTo(showAmount) != 0) {
			throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_REPAY_AMOUNT__ERROR);
		}
		// 用户是否发起代扣
		if (afUserWithholdService.getCountByUserId(userId) > 0) {
			// 将该笔订单加锁，防止同时还款
			if (afBorrowCashService.updateBorrowCashLock(borrowId) == 0) {
				logger.info("borrowcash repayment fail for lock,userId:" + userId + ",borrowId:" + borrowId);
				throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_REPAY_PROCESS_ERROR);
			}
		}
		Map<String, Object> map = null;
		try {

			if (cardId == -2) {// 余额支付
				map = afRepaymentBorrowCashService.createRepayment(jfbAmount, repaymentAmount, actualAmount, coupon,
						userAmount, borrowId, cardId, userId, "", userDto,null,"");
				resp.addResponseData("refId", map.get("refId"));
				resp.addResponseData("type", map.get("type"));
			} else if (cardId == -1) {// 微信支付
				if (context.getAppVersion() < 395) {
					throw new FanbeiException(FanbeiExceptionCode.WEBCHAT_NOT_USERD);
				}
				if (!afResourceService.checkThirdPayByType(ThirdBizType.CASH_REPAYMENT, ThirdPayTypeEnum.WXPAY)) {
					throw new FanbeiException(FanbeiExceptionCode.WEBCHAT_NOT_USERD);
				}
				// if (wxDo != null &&
				// wxDo.getValue().toLowerCase().equals("true")) {
				map = afRepaymentBorrowCashService.createRepaymentYiBao(jfbAmount, repaymentAmount, actualAmount,
						coupon, userAmount, borrowId, cardId, userId, "", userDto,null);
				map.put("userNo", userDto.getUserName());
				map.put("userType", "USER_ID");
				map.put("directPayType", "WX");
				resp.setResponseData(map);
				// } else {
				// return new ApiHandleResponse(requestDataVo.getId(),
				// FanbeiExceptionCode.WEBCHAT_NOT_USERD);
				// }
			} else if (cardId == -3) { // 支付宝支付
				if (context.getAppVersion() < 395) {
					throw new FanbeiException(FanbeiExceptionCode.ZFB_NOT_USERD);
				}
				if (!afResourceService.checkThirdPayByType(ThirdBizType.CASH_REPAYMENT, ThirdPayTypeEnum.ZFBPAY)) {
					throw new FanbeiException(FanbeiExceptionCode.ZFB_NOT_USERD);
				}
				// if (zfbDo != null &&
				// zfbDo.getValue().toLowerCase().equals("true")) {
				map = afRepaymentBorrowCashService.createRepaymentYiBao(jfbAmount, repaymentAmount, actualAmount,
						coupon, userAmount, borrowId, cardId, userId, "", userDto,null);
				map.put("userNo", userDto.getUserName());
				map.put("userType", "USER_ID");
				map.put("directPayType", "ZFB");
				resp.setResponseData(map);
				// } else {
				// return new ApiHandleResponse(requestDataVo.getId(),
				// FanbeiExceptionCode.ZFB_NOT_USERD);
				// }
			} else if (cardId == -4) {

			} else if (cardId > 0) {// 银行卡支付
				AfUserBankcardDo card = afUserBankcardService.getUserBankcardById(cardId);
				if (null == card) {
					throw new FanbeiException(FanbeiExceptionCode.USER_BANKCARD_NOT_EXIST_ERROR);
				}

				AfResourceDo afResource = afResourceService
						.getSingleResourceBytype("bank_repay_limit_" + card.getBankCode());
				if (afResource != null && afResource.getValue().equals(card.getBankCode())) {
					Long limitValue = Long.valueOf(afResource.getValue1());// 限制金额
					if (actualAmount.compareTo(new BigDecimal(limitValue)) > 0) {
						throw new FanbeiException(FanbeiExceptionCode.USER_BANKCARD_LIMIT_ERROR);// 提示语
					}
				}

				map = afRepaymentBorrowCashService.createRepayment(jfbAmount, repaymentAmount, actualAmount, coupon,
						userAmount, borrowId, cardId, userId, request.getRemoteAddr(), userDto,bankPayType,"");

				// 代收
				UpsCollectRespBo upsResult = null;
				if (map.get("resp") != null && map.get("resp") instanceof UpsCollectRespBo) {
					upsResult = (UpsCollectRespBo) map.get("resp");
				}
				if (upsResult == null || !upsResult.isSuccess()) {
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
		} catch (FanbeiException e) {
			logger.error("borrowcash repayment fail" + e);
			throw e;
		} catch (Exception e) {
			logger.error("sys exception", e);
			throw new FanbeiException("sys exception", FanbeiExceptionCode.SYSTEM_ERROR);
		} finally {
			// 借款账单解锁
			afBorrowCashService.updateBorrowCashUnLock(borrowId);
		}
		// 在返回前进行爱上街内部异常捕获校验并向用户反馈
		validThirdReqExistFanbeiError(map);
		// 向客户端反馈结果
		return resp;
	}

	private void validThirdReqExistFanbeiError(Map<String, Object> map) {
		if (map != null && map.get(Constants.THIRD_REQ_EXCEP_KEY) != null
				&& (map.get(Constants.THIRD_REQ_EXCEP_KEY) instanceof FanbeiException)) {
			FanbeiException reqExp = (FanbeiException) map.get(Constants.THIRD_REQ_EXCEP_KEY);
			logger.error(
					"validThirdReqExistFanbeiError exist error and throw,reqExpmessage=" + reqExp.getErrorCode() != null
							? reqExp.getErrorCode().getDesc() : "");
			throw reqExp;
		}
	}
}
