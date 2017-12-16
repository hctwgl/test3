package com.ald.fanbei.api.web.api.legalborrow;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.RiskVerifyRespBo;
import com.ald.fanbei.api.biz.service.AfBorrowCacheAmountPerdayService;
import com.ald.fanbei.api.biz.service.AfBorrowCashService;
import com.ald.fanbei.api.biz.service.AfBorrowLegalGoodsService;
import com.ald.fanbei.api.biz.service.AfBorrowLegalOrderCashService;
import com.ald.fanbei.api.biz.service.AfGameResultService;
import com.ald.fanbei.api.biz.service.AfGameService;
import com.ald.fanbei.api.biz.service.AfGoodsService;
import com.ald.fanbei.api.biz.service.AfRecommendUserService;
import com.ald.fanbei.api.biz.service.AfRenewalDetailService;
import com.ald.fanbei.api.biz.service.AfRepaymentBorrowCashService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.service.AfUserCouponService;
import com.ald.fanbei.api.biz.service.AfUserOperationLogService;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfBorrowCashReviewStatus;
import com.ald.fanbei.api.common.enums.AfBorrowCashStatus;
import com.ald.fanbei.api.common.enums.AfBorrowCashType;
import com.ald.fanbei.api.common.enums.AfCounponStatus;
import com.ald.fanbei.api.common.enums.AfResourceSecType;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.AfUserOperationLogRefType;
import com.ald.fanbei.api.common.enums.AfUserOperationLogType;
import com.ald.fanbei.api.common.enums.RiskStatus;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfBorrowCacheAmountPerdayDo;
import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfBorrowLegalOrderCashDo;
import com.ald.fanbei.api.dal.domain.AfGoodsDo;
import com.ald.fanbei.api.dal.domain.AfRenewalDetailDo;
import com.ald.fanbei.api.dal.domain.AfRepaymentBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.AfUserOperationLogDo;
import com.ald.fanbei.api.web.api.borrowCash.GetBorrowCashBase;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.AfScrollbarVo;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;

/**
 * @类描述：申请借钱
 * 
 * @author Jiang Rongbo 2017年3月25日下午1:06:18
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getLegalBorrowCashHomeInfoApi")
public class GetLegalBorrowCashHomeInfoApi extends GetBorrowCashBase implements ApiHandle {

	protected final Logger maidianLog = LoggerFactory.getLogger("FBMD_BI");// 埋点日志

	@Resource
	AfResourceService afResourceService;
	@Resource
	AfBorrowCashService afBorrowCashService;
	@Resource
	AfUserAccountService afUserAccountService;
	@Resource
	AfRenewalDetailService afRenewalDetailService;
	@Resource
	AfBorrowCacheAmountPerdayService afBorrowCacheAmountPerdayService;
	@Resource
	AfRepaymentBorrowCashService afRepaymentBorrowCashService;
	@Resource
	AfUserOperationLogService afUserOperationLogService;
	@Resource
	AfUserAuthService afUserAuthService;
	@Resource
	AfGameResultService afGameResultService;
	@Resource
	AfUserCouponService afUserCouponService;
	@Resource
	AfGameService afGameService;
	@Resource
	BizCacheUtil bizCacheUtil;
	@Resource
	AfRecommendUserService afRecommendUserService;
	@Resource
	RiskUtil riskUtil;
	@Resource
	AfBorrowLegalOrderCashService afBorrowLegalOrderCashService;
	@Resource
	AfBorrowLegalGoodsService afBorrowLegalGoodsService;
	@Resource
	AfGoodsService afGoodsService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		Long userId = context.getUserId();
		// 判断用户是否登录
		if (userId != null) {
			return processLogin(requestDataVo, context, request);
		} else {
			return processUnLogin(requestDataVo, context, request);
		}
	}

	private ApiHandleResponse processLogin(RequestDataVo requestDataVo, FanbeiContext context,
			HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("isLogin", "Y");
		Long userId = context.getUserId();
		// 获取当前环境
		String type = ConfigProperties.get(Constants.CONFKEY_INVELOMENT_TYPE);
		List<Object> bannerList = new ArrayList<Object>();
		List<Object> bannerListForShop = new ArrayList<Object>();
		List<AfResourceDo> borrowHomeConfigList = afResourceService.selectBorrowHomeConfigByAllTypes();

		if (Constants.INVELOMENT_TYPE_ONLINE.equals(type) || Constants.INVELOMENT_TYPE_TEST.equals(type)) {
			bannerList = getBannerObjectWithResourceDolist(
					afResourceService.getResourceHomeListByTypeOrderBy(AfResourceType.BorrowTopBanner.getCode()));
			// 另一个banner
			bannerListForShop = getBannerObjectWithResourceDolist(
					afResourceService.getResourceHomeListByTypeOrderBy(AfResourceType.BorrowShopBanner.getCode()));
		} else if (Constants.INVELOMENT_TYPE_PRE_ENV.equals(type)) {
			bannerList = getBannerObjectWithResourceDolist(afResourceService
					.getResourceHomeListByTypeOrderByOnPreEnv(AfResourceType.BorrowTopBanner.getCode()));
			// 另一个banner
			bannerListForShop = getBannerObjectWithResourceDolist(afResourceService
					.getResourceHomeListByTypeOrderByOnPreEnv(AfResourceType.BorrowShopBanner.getCode()));
		}
		AfScrollbarVo scrollbarVo = new AfScrollbarVo();
		List<Object> bannerResultList = new ArrayList<>();

		Map<String, Object> rate = getObjectWithResourceDolist(borrowHomeConfigList);

		String inRejectLoan = YesNoStatus.NO.getCode();
		String unfinished = YesNoStatus.NO.getCode();
		AfBorrowCashDo afBorrowCash = afBorrowCashService.getNowUnfinishedBorrowCashByUserId(userId);
		if (afBorrowCash != null) {
			unfinished = YesNoStatus.YES.getCode();
		}

		AfUserAccountDo account = afUserAccountService.getUserAccountByUserId(userId);
		if (account == null) {
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
		}

		// 获取后台配置的最大金额和最小金额
		AfResourceDo rateInfoDo = afResourceService.getConfigByTypesAndSecType(Constants.BORROW_RATE,
				Constants.BORROW_CASH_INFO_LEGAL);
		String strMinAmount = "0";
		String strMaxAmount = "0";
		if (rateInfoDo != null) {
			strMinAmount = rateInfoDo.getValue4();
			strMaxAmount = rateInfoDo.getValue1();
		}

		BigDecimal minAmount = new BigDecimal(strMinAmount);
		BigDecimal maxAmount = new BigDecimal(strMaxAmount);

		// 获取可用额度
		BigDecimal usableAmount = account.getAuAmount().subtract(account.getUsedAmount());

		// 计算最高借款金额
		maxAmount = maxAmount.compareTo(usableAmount) < 0 ? maxAmount : usableAmount;
		// 获取搭售商品价格
		BigDecimal bankRate = new BigDecimal(rate.get("bankRate").toString());
		BigDecimal bankDouble = new BigDecimal(rate.get("bankDouble").toString());
		BigDecimal serviceRate = bankRate.multiply(bankDouble).divide(new BigDecimal(360), 6, RoundingMode.HALF_UP);
		BigDecimal poundageRate = new BigDecimal(rate.get("poundage").toString());

		if (userId != null) {
			Object poundageRateCash = bizCacheUtil.getObject(Constants.RES_BORROW_CASH_POUNDAGE_RATE + userId);
			if (poundageRateCash != null) {
				poundageRate = new BigDecimal(poundageRateCash.toString());
			} else {
				try {
					RiskVerifyRespBo riskResp = riskUtil.getUserLayRate(userId.toString());
					String poundage = riskResp.getPoundageRate();
					if (!StringUtils.isBlank(riskResp.getPoundageRate())) {
						logger.info("comfirmBorrowCash get user poundage rate from risk: consumerNo="
								+ riskResp.getConsumerNo() + ",poundageRate=" + poundage);
						bizCacheUtil.saveObject(Constants.RES_BORROW_CASH_POUNDAGE_RATE + userId, poundage,
								Constants.SECOND_OF_ONE_MONTH);
						bizCacheUtil.saveObject(Constants.RES_BORROW_CASH_POUNDAGE_TIME + userId, new Date(),
								Constants.SECOND_OF_ONE_MONTH);
					}
				} catch (Exception e) {
					logger.info(userId + "从风控获取分层用户额度失败：" + e);
				}
			}
		}
		// 计算原始利率
		BigDecimal oriRate = serviceRate.add(poundageRate);
		double newServiceRate = 0;
		double newInterestRate = 0;
		BigDecimal newRate = null;
		if (rateInfoDo != null) {
			String borrowRate = rateInfoDo.getValue2();
			JSONArray array = JSONObject.parseArray(borrowRate);
			double totalRate = 0;
			for (int i = 0; i < array.size(); i++) {
				JSONObject info = array.getJSONObject(i);
				String borrowTag = info.getString("borrowTag");
				if (StringUtils.equals("INTEREST_RATE", borrowTag)) {
					if (StringUtils.equals(AfBorrowCashType.SEVEN.getName(), borrowTag)) {
						newInterestRate = info.getDouble("borrowSevenDay");
						totalRate += newInterestRate;
					} else {
						newInterestRate = info.getDouble("borrowFourteenDay");
						totalRate += newInterestRate;
					}
				}
				if (StringUtils.equals("SERVICE_RATE", borrowTag)) {
					if (StringUtils.equals(AfBorrowCashType.SEVEN.getName(), borrowTag)) {
						newServiceRate = info.getDouble("borrowSevenDay");
						totalRate += newServiceRate;
					} else {
						newServiceRate = info.getDouble("borrowFourteenDay");
						totalRate += newServiceRate;
					}
				}
			}
			newRate = BigDecimal.valueOf(totalRate / 100);
		} else {
			newRate = BigDecimal.valueOf(0.36);
		}

		newRate = newRate.divide(BigDecimal.valueOf(Constants.ONE_YEAY_DAYS), 6, RoundingMode.HALF_UP);
		BigDecimal profitAmount = oriRate.subtract(newRate).multiply(maxAmount).multiply(BigDecimal.valueOf(14));
		Long goodsId = afBorrowLegalGoodsService.getGoodsIdByProfitAmout(profitAmount);
		if (goodsId != null) {
			AfGoodsDo goodsInfo = afGoodsService.getGoodsById(goodsId);
			BigDecimal saleAmount = goodsInfo.getSaleAmount();
			maxAmount = maxAmount.subtract(saleAmount);
		}

		// 判断是否可借钱，用户可用额度>=最低借款金额 + 最低借款金额借14天匹配的商品金额
		BigDecimal minProfitAmount = oriRate.subtract(newRate).multiply(minAmount).multiply(BigDecimal.valueOf(14));
		Long tmpGoodsId = afBorrowLegalGoodsService.getGoodsIdByProfitAmout(minProfitAmount);
		BigDecimal tmpLimitAmount = BigDecimal.ZERO;
		if (tmpGoodsId != null) {
			AfGoodsDo goodsInfo = afGoodsService.getGoodsById(tmpGoodsId);
			BigDecimal saleAmount = goodsInfo.getSaleAmount();
			tmpLimitAmount = saleAmount.add(minAmount);
		}
		if (usableAmount.compareTo(tmpLimitAmount) >= 0) {
			data.put("canBorrow", "Y");
		} else {
			data.put("canBorrow", "N");
		}

		// 增加判断，如果前面还有没有还的借款，优先还掉
		AfBorrowCashDo afBorrowCashDo = afBorrowCashService.getNowTransedBorrowCashByUserId(userId);
		if (afBorrowCashDo == null) {
			afBorrowCashDo = afBorrowCashService.getBorrowCashByUserId(userId);
		}
		if (afBorrowCashDo == null) {
			data.put("status", "DEFAULT");
		} else {
			data.put("status", afBorrowCashDo.getStatus());

			if (StringUtils.equals(afBorrowCashDo.getStatus(), AfBorrowCashStatus.transedfail.getCode())
					|| StringUtils.equals(afBorrowCashDo.getStatus(), AfBorrowCashStatus.transeding.getCode())) {
				data.put("status", AfBorrowCashStatus.waitTransed.getCode());
			} else if (!StringUtils.equals(afBorrowCashDo.getStatus(), AfBorrowCashStatus.transed.getCode())
					&& usableAmount.compareTo(minAmount) < 0
					&& StringUtils.equals(unfinished, YesNoStatus.NO.getCode())) {
				inRejectLoan = YesNoStatus.YES.getCode();
			}
			data.put("jfbAmount", account.getJfbAmount());
			data.put("rebateAmount", account.getRebateAmount());
			data.put("amount", afBorrowCashDo.getAmount());
			data.put("arrivalAmount", afBorrowCashDo.getArrivalAmount());
			// 计算总的还款金额，包括商品借款金额
			BigDecimal allAmount = BigDecimalUtil.add(afBorrowCashDo.getAmount(), afBorrowCashDo.getSumOverdue(),
					afBorrowCashDo.getOverdueAmount(), afBorrowCashDo.getRateAmount(), afBorrowCashDo.getSumRate(),
					afBorrowCashDo.getPoundage(), afBorrowCashDo.getSumRenewalPoundage());
			// 查询借款相关商品借款,计算总还款金额
			AfBorrowLegalOrderCashDo afBorrowLegalOrderCash = afBorrowLegalOrderCashService
					.getBorrowLegalOrderCashByBorrowId(afBorrowCashDo.getRid());

			// 计算已经还款金额
			BigDecimal paidAmount = afBorrowCashDo.getRepayAmount();
			// 计算逾期金额
			BigDecimal overdueAmount = afBorrowCashDo.getOverdueAmount();
			if (afBorrowLegalOrderCash != null) {
				BigDecimal orderCashAmount = afBorrowLegalOrderCash.getAmount();
				BigDecimal orderCashOverdueAmount = afBorrowLegalOrderCash.getOverdueAmount();
				BigDecimal repaidAmount = afBorrowLegalOrderCash.getRepaidAmount();
				BigDecimal poundageAmount = afBorrowLegalOrderCash.getPoundageAmount();
				BigDecimal interestAmount = afBorrowLegalOrderCash.getInterestAmount();
				allAmount = allAmount.add(orderCashAmount).add(orderCashOverdueAmount).add(poundageAmount)
						.add(interestAmount).subtract(repaidAmount);
				paidAmount = paidAmount.add(repaidAmount);
				overdueAmount = overdueAmount.add(orderCashOverdueAmount);
			}
			// 减掉借款已还金额
			BigDecimal returnAmount = BigDecimalUtil.subtract(allAmount, afBorrowCashDo.getRepayAmount());

			// 计算服务费和手续费
			BigDecimal serviceFee = afBorrowCashDo.getPoundage();
			BigDecimal interestFee = afBorrowCashDo.getRateAmount();
			data.put("serviceFee", serviceFee);
			data.put("interestFee", interestFee);

			data.put("returnAmount", returnAmount);
			data.put("paidAmount", paidAmount);
			data.put("overdueAmount", overdueAmount);
			data.put("type", AfBorrowCashType.findRoleTypeByName(afBorrowCashDo.getType()).getCode());
			long currentTime = System.currentTimeMillis();
			Date now = DateUtil.getEndOfDate(new Date(currentTime));

			data.put("overdueStatus", "N");
			// 如果预计还款日在今天之前，且为待还款状态，则已逾期，逾期天数=现在减去预计还款日
			if (StringUtils.equals(afBorrowCashDo.getStatus(), "TRANSED")
					&& afBorrowCashDo.getGmtPlanRepayment().before(now)) {
				long day = DateUtil.getNumberOfDatesBetween(afBorrowCashDo.getGmtPlanRepayment(), now);
				data.put("overdueDay", day);
				data.put("overdueStatus", "Y");
			}
			if (afBorrowCashDo.getGmtPlanRepayment() != null) {
				data.put("repaymentDay", afBorrowCashDo.getGmtPlanRepayment());
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(now);
				Calendar calendarRepay = Calendar.getInstance();
				calendarRepay.setTime(afBorrowCashDo.getGmtPlanRepayment());
				Long chaTime = DateUtil.getNumberOfDaysBetween(calendar, calendarRepay);
				data.put("deadlineDay", chaTime);
			}

			data.put("gmtArrival", afBorrowCashDo.getGmtArrival());
			data.put("reviewStatus", afBorrowCashDo.getReviewStatus());
			data.put("rid", afBorrowCashDo.getRid());

			data.put("renewalStatus", "N");
			AfRenewalDetailDo afRenewalDetailDo = afRenewalDetailService
					.getRenewalDetailByBorrowId(afBorrowCashDo.getRid());
			if (afRenewalDetailDo != null && StringUtils.equals(afRenewalDetailDo.getStatus(), "P")) {
				data.put("renewalStatus", "P");
			} else if (StringUtils.equals(afBorrowCashDo.getStatus(), "TRANSED")) {
				AfResourceDo duedateResource = afResourceService
						.getConfigByTypesAndSecType(Constants.RES_RENEWAL_DAY_LIMIT, Constants.RES_BETWEEN_DUEDATE);
				BigDecimal betweenDuedate = new BigDecimal(duedateResource.getValue());// 续期的距离预计还款日的最小天数差
				AfResourceDo amountResource = afResourceService
						.getConfigByTypesAndSecType(Constants.RES_RENEWAL_DAY_LIMIT, Constants.RES_AMOUNT_LIMIT);
				BigDecimal amountLimit = new BigDecimal(amountResource.getValue());// 配置的未还金额限制

				long betweenGmtPlanRepayment = DateUtil.getNumberOfDatesBetween(now,
						afBorrowCashDo.getGmtPlanRepayment());
				BigDecimal waitPaidAmount = afBorrowCashDo.getAmount().add(afBorrowCashDo.getSumOverdue())
						.add(afBorrowCashDo.getSumRate()).subtract(afBorrowCashDo.getRepayAmount());
				// 当前日期与预计还款时间之前的天数差小于配置的betweenDuedate，并且未还款金额大于配置的限制金额时，可续期
				if (betweenDuedate.compareTo(new BigDecimal(betweenGmtPlanRepayment)) > 0
						&& waitPaidAmount.compareTo(amountLimit) >= 0) {
					AfRepaymentBorrowCashDo afRepaymentBorrowCashDo = afRepaymentBorrowCashService
							.getLastRepaymentBorrowCashByBorrowId(afBorrowCashDo.getRid());
					if (null == afRepaymentBorrowCashDo || (null != afRepaymentBorrowCashDo
							&& !StringUtils.equals(afBorrowCashDo.getStatus(), "P"))) {
						data.put("renewalStatus", "Y");
					}
				}
			}

			// 判断是否显示续借按钮
			BigDecimal tmpAmount = afBorrowCashDo.getAmount().multiply(new BigDecimal(0.06));
			tmpAmount = tmpAmount.compareTo(BigDecimalUtil.ONE_HUNDRED) > 0 ? tmpAmount : BigDecimalUtil.ONE_HUNDRED;
			if (returnAmount.compareTo(tmpAmount) < 0) {
				data.put("renewalStatus", "N");
			}
		}
		BigDecimal bankService = bankRate.multiply(bankDouble).divide(new BigDecimal(Constants.ONE_YEAY_DAYS), 6,
				RoundingMode.HALF_UP);

		data.put("bankDoubleRate", bankService.toString());
		data.put("overdueRate", rate.get("overduePoundage"));
		data.put("maxAmount", calculateMaxAmount(maxAmount));
		data.put("minAmount", minAmount);
		data.put("borrowCashDay", rate.get("borrowCashDay"));
		data.put("lender", rate.get("lender"));

		int currentDay = Integer.parseInt(DateUtil.getNowYearMonthDay());
		AfBorrowCacheAmountPerdayDo currentAmount = afBorrowCacheAmountPerdayService.getSigninByDay(currentDay);
		if (currentAmount == null) {
			AfBorrowCacheAmountPerdayDo temp = new AfBorrowCacheAmountPerdayDo();
			temp.setAmount(new BigDecimal(0));
			temp.setDay(currentDay);
			temp.setNums(0l);
			afBorrowCacheAmountPerdayService.addBorrowCacheAmountPerday(temp);
			currentAmount = temp;
		}

		// 是否放款总开关
		if (!StringUtils.equals(rate.get("supuerSwitch").toString(), YesNoStatus.YES.getCode())
				|| currentAmount.getAmount().compareTo(new BigDecimal((String) rate.get("amountPerDay"))) >= 0) {
			data.put("canBorrow", "N");
		}
		BigDecimal nums = new BigDecimal((String) rate.get("nums"));
		data.put("loanMoney", nums.multiply(currentAmount.getAmount()));
		data.put("loanNum", nums.multiply(BigDecimal.valueOf(currentAmount.getNums())));

		String jumpToRejectPage = YesNoStatus.NO.getCode();
		String jumpPageBannerUrl = "";

		if (afBorrowCashDo != null && AfBorrowCashStatus.closed.getCode().equals(afBorrowCashDo.getStatus())
				&& AfBorrowCashReviewStatus.refuse.getCode().equals(afBorrowCashDo.getReviewStatus())) {
			// 借款被拒绝
			AfResourceDo afResourceDo = afResourceService.getConfigByTypesAndSecType(
					AfResourceType.RiskManagementBorrowcashLimit.getCode(),
					AfResourceSecType.RejectTimePeriod.getCode());
			if (afResourceDo != null && AfCounponStatus.O.getCode().equals(afResourceDo.getValue4())) {
				Integer rejectTimePeriod = NumberUtil.objToIntDefault(afResourceDo.getValue1(), 0);
				Date desTime = DateUtil.addDays(afBorrowCashDo.getGmtCreate(), rejectTimePeriod);
				if (DateUtil.getNumberOfDatesBetween(DateUtil.formatDateToYYYYMMdd(desTime), DateUtil.getToday()) < 0) {
					// 风控拒绝日期内
					inRejectLoan = YesNoStatus.YES.getCode();
				}
			}

			// 如果存在风控限制，需校验是否需要跳转至不通过页面等信息
			if (YesNoStatus.YES.getCode().equals(inRejectLoan)) {
				// 从用户操作日志中获取用户是否存在在现金借款操作
				AfUserOperationLogDo afUserOperationLogDo = new AfUserOperationLogDo(userId,
						AfUserOperationLogType.RISKBORROWCASH.getCode(),
						AfUserOperationLogRefType.AFBORROWCASH.getCode(), afBorrowCashDo.getRid() + "");
				Integer riskBcNums = afUserOperationLogService.getNumsByUserAndType(afUserOperationLogDo);
				if (NumberUtil.isNullOrZero(riskBcNums)) {
					jumpToRejectPage = YesNoStatus.YES.getCode();
					// 同时插入操作记录
					afUserOperationLogService.addUserOperationLog(afUserOperationLogDo);
				}
			}
		}

		AfUserAuthDo afUserAuthDo = afUserAuthService.getUserAuthInfoByUserId(userId);

		if (StringUtils.equals(RiskStatus.NO.getCode(), afUserAuthDo.getRiskStatus())) {
			inRejectLoan = YesNoStatus.YES.getCode();
		}
		if (YesNoStatus.NO.getCode().equals(afUserAuthDo.getZmStatus())
				&& !YesNoStatus.YES.getCode().equals(afUserAuthDo.getRiskStatus())) {
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.ZM_STATUS_EXPIRED);
		}

		if (StringUtils.equals(RiskStatus.YES.getCode(), afUserAuthDo.getRiskStatus())
				&& usableAmount.compareTo(minAmount) < 0 && StringUtils.equals(unfinished, YesNoStatus.NO.getCode())) {
			inRejectLoan = YesNoStatus.YES.getCode();
		}

		// 如果需要跳转至不通过页面，则获取对应banner图地址
		if (YesNoStatus.YES.getCode().equals(jumpToRejectPage)) {
			// 获取不通过页面内banner图对应地址
			AfResourceDo bannerResourceDo = afResourceService.getConfigByTypesAndSecType(
					AfResourceType.RiskManagementBorrowcashLimit.getCode(),
					AfResourceSecType.RejectPageBannerUrl.getCode());
			if (bannerResourceDo != null && AfCounponStatus.O.getCode().equals(bannerResourceDo.getValue4())) {
				jumpPageBannerUrl = bannerResourceDo.getValue1();
			}
		}
		if (inRejectLoan.equals("Y")) {
			if (bannerListForShop != null) {
				for (Object obj : bannerListForShop) {
					Map<String, Object> map = (Map<String, Object>) obj;
					String content = (String) map.get("content");
					if (StringUtils.isNotBlank(content)) {
						if (content.contains("=")) {
							map.put("content", content + "&linkType=appLoanBanner");
						} else {
							map.put("content", content + "?linkType=appLoanBanner");
						}
					}
				}
			}
			bannerResultList = bannerListForShop;
			AfResourceDo resourceDo = afResourceService.getScrollbarByType();
			scrollbarVo = getAfScrollbarVo(resourceDo);
		} else {
			bannerResultList = bannerList;
			List<AfResourceDo> recommend_imgs = afRecommendUserService.getActivieResourceByType("RECOMMEND_IMG");// 获取活动图片
			if (recommend_imgs != null && recommend_imgs.size() > 0) {
				for (AfResourceDo afResourceDo : recommend_imgs) {
					Map<String, Object> map = Maps.newHashMap();
					map.put("imageUrl", afResourceDo.getValue() + "?name=RECOMMEND_IMG");
					map.put("titleName", afResourceDo.getName());
					map.put("type", "RECOMMEND_IMG");
					bannerResultList.add(0, map);
				}
			}
		}

		data.put("scrollbar", scrollbarVo);
		data.put("bannerList", bannerResultList);
		data.put("inRejectLoan", inRejectLoan);
		data.put("jumpToRejectPage", jumpToRejectPage);
		data.put("jumpPageBannerUrl", jumpPageBannerUrl);

		// 还款处理中金额处理
		String existRepayingMoney = YesNoStatus.NO.getCode();
		BigDecimal repayingMoney = BigDecimal.valueOf(0.00);
		// 如果借款记录存在，统计还款处理中金额
		if (afBorrowCashDo != null) {
			repayingMoney = afRepaymentBorrowCashService.getRepayingTotalAmountByBorrowId(afBorrowCashDo.getRid());
		}
		if (repayingMoney.compareTo(BigDecimal.ZERO) > 0) {
			existRepayingMoney = YesNoStatus.YES.getCode();
		}
		data.put("existRepayingMoney", existRepayingMoney);
		data.put("repayingMoney", repayingMoney);

		try {
			// 用户点击借钱页面时去风控获取用户的借钱手续费
			getUserPoundageRate(userId, data, inRejectLoan, rate.get("poundage").toString());
		} catch (Exception e) {
			bizCacheUtil.saveObject(Constants.RES_BORROW_CASH_POUNDAGE_RATE + userId, rate.get("poundage").toString(),
					Constants.SECOND_OF_ONE_MONTH);
			logger.info(userId + "从风控获取分层用户额度失败：" + e);
		}

		Object poundageRateCash = bizCacheUtil.getObject(Constants.RES_BORROW_CASH_POUNDAGE_RATE + userId);
		if (poundageRateCash != null) {
			data.put("poundageRate", poundageRateCash.toString());
		} else {
			data.put("poundageRate", rate.get("poundage").toString());
		}

		// 爬取商品开关
		AfResourceDo isWorm = afResourceService.getConfigByTypesAndSecType(Constants.THIRD_GOODS_TYPE,
				Constants.THIRD_GOODS_IS_WORM_SECTYPE);
		if (null != isWorm) {
			data.put("isWorm", isWorm.getValue());
		} else {
			data.put("isWorm", 0);
		}

		resp.setResponseData(data);
		return resp;
	}

	private ApiHandleResponse processUnLogin(RequestDataVo requestDataVo, FanbeiContext context,
			HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("isLogin", "N");

		List<AfResourceDo> list = afResourceService.selectBorrowHomeConfigByAllTypes();
		List<Object> bannerList = new ArrayList<Object>();
		String type = ConfigProperties.get(Constants.CONFKEY_INVELOMENT_TYPE);

		if (Constants.INVELOMENT_TYPE_ONLINE.equals(type) || Constants.INVELOMENT_TYPE_TEST.equals(type)) {
			bannerList = getBannerObjectWithResourceDolist(
					afResourceService.getResourceHomeListByTypeOrderBy(AfResourceType.BorrowTopBanner.getCode()));
		} else if (Constants.INVELOMENT_TYPE_PRE_ENV.equals(type)) {
			bannerList = getBannerObjectWithResourceDolist(afResourceService
					.getResourceHomeListByTypeOrderByOnPreEnv(AfResourceType.BorrowTopBanner.getCode()));
		}

		Map<String, Object> rate = getObjectWithResourceDolist(list);

		BigDecimal bankRate = new BigDecimal(rate.get("bankRate").toString());
		BigDecimal bankDouble = new BigDecimal(rate.get("bankDouble").toString());
		BigDecimal bankService = bankRate.multiply(bankDouble).divide(new BigDecimal(360), 6, RoundingMode.HALF_UP);

		data.put("bankDoubleRate", bankService.toString());
		data.put("poundageRate", rate.get("poundage"));
		data.put("overdueRate", rate.get("overduePoundage"));

		// 获取后台配置的最大金额和最小金额
		AfResourceDo rateInfoDo = afResourceService.getConfigByTypesAndSecType(Constants.BORROW_RATE,
				Constants.BORROW_CASH_INFO_LEGAL);

		String strMinAmount = rateInfoDo.getValue4();
		String strMaxAmount = rateInfoDo.getValue1();

		data.put("maxAmount", strMaxAmount);
		data.put("minAmount", strMinAmount);

		data.put("borrowCashDay", rate.get("borrowCashDay"));
		data.put("lender", rate.get("lender"));
		data.put("bannerList", bannerList);

		int currentDay = Integer.parseInt(DateUtil.getNowYearMonthDay());
		AfBorrowCacheAmountPerdayDo currentAmount = afBorrowCacheAmountPerdayService.getSigninByDay(currentDay);
		if (currentAmount == null) {
			AfBorrowCacheAmountPerdayDo temp = new AfBorrowCacheAmountPerdayDo();
			temp.setAmount(new BigDecimal(0));
			temp.setDay(currentDay);
			afBorrowCacheAmountPerdayService.addBorrowCacheAmountPerday(temp);
			currentAmount = temp;
		}
		if (!StringUtils.equals(rate.get("supuerSwitch").toString(), YesNoStatus.YES.getCode())
				|| currentAmount.getAmount().compareTo(new BigDecimal((String) rate.get("amountPerDay"))) >= 0) {
			data.put("canBorrow", "N");
		} else {
			data.put("canBorrow", "Y");
		}
		BigDecimal nums = new BigDecimal((String) rate.get("nums"));
		data.put("loanMoney", nums.multiply(currentAmount.getAmount()));
		data.put("loanNum", nums.multiply(BigDecimal.valueOf(currentAmount.getNums())));
		// 爬取商品开关
		AfResourceDo isWorm = afResourceService.getConfigByTypesAndSecType(Constants.THIRD_GOODS_TYPE,
				Constants.THIRD_GOODS_IS_WORM_SECTYPE);
		if (null != isWorm) {
			data.put("isWorm", isWorm.getValue());
		} else {
			data.put("isWorm", 0);
		}
		resp.setResponseData(data);
		return resp;
	}

	private void getUserPoundageRate(Long userId, Map<String, Object> data, String inRejectLoan, String poundage) {
		Date saveRateDate = (Date) bizCacheUtil.getObject(Constants.RES_BORROW_CASH_POUNDAGE_TIME + userId);
		if (saveRateDate == null || DateUtil.compareDate(new Date(), DateUtil.addDays(saveRateDate, 1))) {
			try {
				RiskVerifyRespBo riskResp = riskUtil.getUserLayRate(userId + "");
				if (riskResp == null) {
					throw new FanbeiException("get user poundage rate error");
				}
				String poundageRate = riskResp.getPoundageRate();
				if (!StringUtils.isBlank(riskResp.getPoundageRate())) {
					logger.info("get user poundage rate from risk: consumerNo=" + riskResp.getConsumerNo()
							+ ",poundageRate=" + poundageRate);
					Object poundageRateCash = bizCacheUtil.getObject(Constants.RES_BORROW_CASH_POUNDAGE_RATE + userId);

					BigDecimal userPoundageRate = BigDecimal.ZERO;
					if (poundageRateCash != null) {
						userPoundageRate = new BigDecimal(poundageRateCash.toString());
					}

					if (new BigDecimal(poundageRate).compareTo(userPoundageRate) < 0
							&& StringUtils.equals(inRejectLoan, YesNoStatus.NO.getCode())) {
						data.put("showRatePopup", "Y");
					}
					bizCacheUtil.saveObject(Constants.RES_BORROW_CASH_POUNDAGE_RATE + userId, poundageRate,
							Constants.SECOND_OF_ONE_MONTH);

					bizCacheUtil.saveObject(Constants.RES_BORROW_CASH_POUNDAGE_TIME + userId, new Date(),
							Constants.SECOND_OF_ONE_MONTH);
				} else {
					bizCacheUtil.saveObject(Constants.RES_BORROW_CASH_POUNDAGE_RATE + userId, poundage,
							Constants.SECOND_OF_ONE_MONTH);
				}
			} catch (Exception e) {
				logger.error("get user poundage rate error => {}", e.getMessage());
				throw new FanbeiException("get user poundage rate error");
			}
		}
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

	public static AfScrollbarVo getAfScrollbarVo(AfResourceDo resourceDo) {
		AfScrollbarVo scrollbarVo = new AfScrollbarVo();
		if (resourceDo != null) {
			scrollbarVo.setContent(resourceDo.getDescription());
			scrollbarVo.setType(resourceDo.getValue1());
			scrollbarVo.setName(resourceDo.getName());
			scrollbarVo.setWordUrl(resourceDo.getValue2());
		}
		return scrollbarVo;

	}
}