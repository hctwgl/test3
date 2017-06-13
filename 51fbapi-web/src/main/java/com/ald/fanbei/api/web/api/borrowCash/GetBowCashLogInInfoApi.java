package com.ald.fanbei.api.web.api.borrowCash;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfBorrowCacheAmountPerdayService;
import com.ald.fanbei.api.biz.service.AfBorrowCashService;
import com.ald.fanbei.api.biz.service.AfRenewalDetailService;
import com.ald.fanbei.api.biz.service.AfRepaymentBorrowCashService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserOperationLogService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfBorrowCashStatus;
import com.ald.fanbei.api.common.enums.AfBorrowCashType;
import com.ald.fanbei.api.common.enums.AfCounponStatus;
import com.ald.fanbei.api.common.enums.AfResourceSecType;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.AfUserOperationLogRefType;
import com.ald.fanbei.api.common.enums.AfUserOperationLogType;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfBorrowCacheAmountPerdayDo;
import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfRenewalDetailDo;
import com.ald.fanbei.api.dal.domain.AfRepaymentBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserOperationLogDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * @类描述：
 * 
 * @author suweili 2017年3月24日下午3:48:48
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getBowCashLogInInfoApi")
public class GetBowCashLogInInfoApi extends GetBorrowCashBase implements ApiHandle {

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

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		List<AfResourceDo> list = afResourceService.selectBorrowHomeConfigByAllTypes();
		List<Object> bannerList = getBannerObjectWithResourceDolist(
				afResourceService.getResourceHomeListByTypeOrderBy(AfResourceType.BorrowTopBanner.getCode()));
		Map<String, Object> data = new HashMap<String, Object>();
		Map<String, Object> rate = getObjectWithResourceDolist(list);
		//hy 2017年06月13日16:48:35 增加判断，如果前面还有没有还的借款，优先还掉 start
		AfBorrowCashDo afBorrowCashDo = afBorrowCashService.getNowTransedBorrowCashByUserId(userId);
		if (afBorrowCashDo == null) {
			afBorrowCashDo = afBorrowCashService.getBorrowCashByUserId(userId);
		}
		//hy 2017年06月13日16:48:35 增加判断，如果前面还有没有还的借款，优先还掉 end

		AfUserAccountDo account = afUserAccountService.getUserAccountByUserId(userId);

		if (afBorrowCashDo == null) {
			data.put("status", "DEFAULT");
		} else {
			data.put("status", afBorrowCashDo.getStatus());

			if (StringUtils.equals(afBorrowCashDo.getStatus(), AfBorrowCashStatus.transedfail.getCode())
					|| StringUtils.equals(afBorrowCashDo.getStatus(), AfBorrowCashStatus.transeding.getCode())) {
				data.put("status", AfBorrowCashStatus.waitTransed.getCode());

			}
			data.put("jfbAmount", account.getJfbAmount());

			data.put("rebateAmount", account.getRebateAmount());

			data.put("amount", afBorrowCashDo.getAmount());
			data.put("arrivalAmount", afBorrowCashDo.getArrivalAmount());
			BigDecimal allAmount = BigDecimalUtil.add(afBorrowCashDo.getAmount(), afBorrowCashDo.getSumOverdue(),
					afBorrowCashDo.getOverdueAmount(), afBorrowCashDo.getRateAmount(), afBorrowCashDo.getSumRate());
			BigDecimal returnAmount = BigDecimalUtil.subtract(allAmount, afBorrowCashDo.getRepayAmount());
			data.put("returnAmount", returnAmount);
			data.put("paidAmount", afBorrowCashDo.getRepayAmount());
			data.put("overdueAmount", afBorrowCashDo.getOverdueAmount());
			data.put("type", AfBorrowCashType.findRoleTypeByName(afBorrowCashDo.getType()).getCode());
			Date now = DateUtil.getStartOfDate(new Date());

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
				BigDecimal amount_limit = new BigDecimal(amountResource.getValue());// 配置的未还金额限制

				long currentTime = System.currentTimeMillis();
				Date nowDate = new Date(currentTime);
				long betweenGmtPlanRepayment = DateUtil.getNumberOfDatesBetween(nowDate,
						afBorrowCashDo.getGmtPlanRepayment());
				BigDecimal waitPaidAmount = afBorrowCashDo.getAmount().subtract(afBorrowCashDo.getRepayAmount());
				// 当前日期与预计还款时间之前的天数差小于配置的betweenDuedate，并且未还款金额大于配置的限制金额时，可续期
				if (betweenDuedate.compareTo(new BigDecimal(betweenGmtPlanRepayment)) > 0
						&& waitPaidAmount.compareTo(amount_limit) >= 0) {
					AfRepaymentBorrowCashDo afRepaymentBorrowCashDo = afRepaymentBorrowCashService
							.getLastRepaymentBorrowCashByBorrowId(afBorrowCashDo.getRid());
					if (null == afRepaymentBorrowCashDo || (null != afRepaymentBorrowCashDo
							&& !StringUtils.equals(afBorrowCashDo.getStatus(), "P"))) {
						data.put("renewalStatus", "Y");
					}
				}
			}
		}
		BigDecimal bankRate = new BigDecimal(rate.get("bankRate").toString());
		BigDecimal bankDouble = new BigDecimal(rate.get("bankDouble").toString());
		BigDecimal bankService = bankRate.multiply(bankDouble).divide(new BigDecimal(360), 6, RoundingMode.HALF_UP);

		data.put("bankDoubleRate", bankService.toString());
		data.put("poundageRate", rate.get("poundage"));
		data.put("overdueRate", rate.get("overduePoundage"));
		data.put("maxAmount", rate.get("maxAmount"));
		data.put("minAmount", rate.get("minAmount"));
		data.put("borrowCashDay", rate.get("borrowCashDay"));
		data.put("bannerList", bannerList);
		data.put("lender", rate.get("lender"));
		if (account != null) {
			data.put("maxAmount", account.getBorrowCashAmount().stripTrailingZeros().toPlainString());
		}
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
		if (!StringUtils.equals(rate.get("supuerSwitch").toString(), YesNoStatus.YES.getCode())
				|| currentAmount.getAmount().compareTo(new BigDecimal((String) rate.get("amountPerDay"))) >= 0) {
			data.put("canBorrow", "N");
		} else {
			data.put("canBorrow", "Y");
		}
		BigDecimal nums = new BigDecimal((String) rate.get("nums"));
		data.put("loanMoney", nums.multiply(currentAmount.getAmount()));
		data.put("loanNum", nums.multiply(BigDecimal.valueOf(currentAmount.getNums())));

		// add by ck 20170603
		String inRejectLoan = YesNoStatus.NO.getCode();
		String jumpToRejectPage = YesNoStatus.NO.getCode();
		String jumpPageBannerUrl = "";

		if (afBorrowCashDo != null && AfBorrowCashStatus.closed.getCode().equals(afBorrowCashDo.getStatus())) {
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

		resp.setResponseData(data);
		return resp;
	}

}
