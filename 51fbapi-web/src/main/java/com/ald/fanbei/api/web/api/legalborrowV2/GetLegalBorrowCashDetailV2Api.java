package com.ald.fanbei.api.web.api.legalborrowV2;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.util.NumberWordFormat;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfBorrowCashStatus;
import com.ald.fanbei.api.common.enums.AfBorrowCashType;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.dao.AfBorrowLegalOrderRepaymentDao;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.web.api.borrowCash.GetBorrowCashBase;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @类描述：
 * 
 * @author Jiang Rongbo 2017年3月25日下午1:07:02
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getLegalBorrowCashDetailV2Api")
public class GetLegalBorrowCashDetailV2Api extends GetBorrowCashBase implements ApiHandle {

	@Resource
	AfResourceService afResourceService;
	@Resource
	AfBorrowCashService afBorrowCashService;
	@Resource
	AfUserAccountService afUserAccountService;
	@Resource
	AfRenewalDetailService afRenewalDetailService;
	@Resource
	AfRepaymentBorrowCashService afRepaymentBorrowCashService;

	@Resource
	AfBorrowLegalOrderCashService afBorrowLegalOrderCashService;
	@Resource
	AfBorrowLegalOrderRepaymentDao afBorrowLegalOrderRepaymentDao;
	@Resource
	NumberWordFormat numberWordFormat;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		Long rid = NumberUtil.objToLongDefault(requestDataVo.getParams().get("borrowId"), 0l);
		AfUserAccountDo account = afUserAccountService.getUserAccountByUserId(userId);

		if (account == null) {
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SYSTEM_ERROR);
		}
		AfBorrowCashDo afBorrowCashDo = afBorrowCashService.getBorrowCashByrid(rid);
		if (afBorrowCashDo == null) {
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SYSTEM_ERROR);
		}
		BigDecimal paidAmount = afRepaymentBorrowCashService.getRepaymentAllAmountByBorrowId(rid);
		Map<String, Object> data = objectWithAfBorrowCashDo(afBorrowCashDo, context.getAppVersion());
		data.put("paidAmount", NumberUtil.objToBigDecimalDefault(paidAmount, BigDecimal.ZERO));
		data.put("rebateAmount", account.getRebateAmount());

		// 还款处理中金额处理
		String existRepayingMoney = YesNoStatus.NO.getCode();
		BigDecimal repayingMoney = BigDecimal.valueOf(0.00);
		BigDecimal repayingOrderMoney = BigDecimal.ZERO;
		// 如果借款记录存在，统计还款处理中金额
		repayingMoney = afRepaymentBorrowCashService.getRepayingTotalAmountByBorrowId(afBorrowCashDo.getRid());
		repayingOrderMoney = afBorrowLegalOrderRepaymentDao.getOrderRepayingTotalAmountByBorrowId(afBorrowCashDo.getRid());
		if(null != repayingOrderMoney && (repayingOrderMoney.compareTo(BigDecimal.ZERO) > 0)){
			repayingMoney = repayingMoney.add(repayingOrderMoney);
		}
		if (repayingMoney.compareTo(BigDecimal.ZERO) > 0) {
			existRepayingMoney = YesNoStatus.YES.getCode();
		}
		data.put("existRepayingMoney", existRepayingMoney);
		logger.info("getLegalBorrowCashDetail, data = " + data);
		resp.setResponseData(data);

		return resp;
	}

	public Map<String, Object> objectWithAfBorrowCashDo(AfBorrowCashDo afBorrowCashDo, Integer appVersion) {
		Map<String, Object> data = new HashMap<String, Object>();
		if (afBorrowCashDo.getGmtPlanRepayment() == null) {
//			Integer day = NumberUtil.objToIntDefault(AfBorrowCashType.findRoleTypeByName(afBorrowCashDo.getType()).getCode(), 7);
			Integer day = numberWordFormat.borrowTime(afBorrowCashDo.getType());
			Date createEnd = DateUtil.getEndOfDatePrecisionSecond(afBorrowCashDo.getGmtCreate());
			Date repaymentDay = DateUtil.addDays(createEnd, day - 1);
			afBorrowCashDo.setGmtPlanRepayment(repaymentDay);
		}
		data.put("rid", afBorrowCashDo.getRid());
		data.put("amount", afBorrowCashDo.getAmount());
		data.put("gmtCreate", afBorrowCashDo.getGmtCreate());
		data.put("status", afBorrowCashDo.getStatus());

		if (StringUtils.equals(afBorrowCashDo.getStatus(), AfBorrowCashStatus.transedfail.getCode()) || StringUtils.equals(afBorrowCashDo.getStatus(), AfBorrowCashStatus.transeding.getCode())) {
			data.put("status", AfBorrowCashStatus.waitTransed.getCode());
		}

//		AfBorrowCashType borrowCashType = AfBorrowCashType.findRoleTypeByName(afBorrowCashDo.getType());

		data.put("gmtLastRepay", afBorrowCashDo.getGmtPlanRepayment());

		data.put("renewalStatus", "N");
		AfRenewalDetailDo afRenewalDetailDo = afRenewalDetailService.getRenewalDetailByBorrowId(afBorrowCashDo.getRid());
		if (afRenewalDetailDo != null && StringUtils.equals(afRenewalDetailDo.getStatus(), "P")) {
			logger.info("renewalStatus = "+afRenewalDetailDo.getStatus());
			data.put("renewalStatus", "P");
		} else if (StringUtils.equals(afBorrowCashDo.getStatus(), "TRANSED")) {
			AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(Constants.RES_RENEWAL_DAY_LIMIT, Constants.RES_ALLOW_RENEWAL_DAY_NEW);
			BigDecimal allowRenewalDay = new BigDecimal(resource.getValue());// 允许续期天数
			AfResourceDo duedateResource = afResourceService.getConfigByTypesAndSecType(Constants.RES_RENEWAL_DAY_LIMIT, Constants.RES_BETWEEN_DUEDATE);
			BigDecimal betweenDuedate = new BigDecimal(duedateResource.getValue());// 续期的距离预计还款日的最小天数差
			AfResourceDo amountResource = afResourceService.getConfigByTypesAndSecType(Constants.RES_RENEWAL_DAY_LIMIT, Constants.RES_AMOUNT_LIMIT);
			BigDecimal amountLimit = new BigDecimal(amountResource.getValue());// 配置的未还金额限制
			// 如果借款记录存在，统计还款处理中金额
			BigDecimal repayingMoney = afRepaymentBorrowCashService.getRepayingTotalAmountByBorrowId(afBorrowCashDo.getRid());
			if (repayingMoney.compareTo(BigDecimal.ZERO) > 0) {
				data.put("status", AfBorrowCashStatus.repaying.getCode());
			}

			Date nowDate = DateUtil.getEndOfDate(new Date());
			long betweenGmtPlanRepayment = DateUtil.getNumberOfDatesBetween(nowDate, afBorrowCashDo.getGmtPlanRepayment());

			BigDecimal waitPaidAmount = BigDecimalUtil.add(afBorrowCashDo.getAmount(), afBorrowCashDo.getOverdueAmount(), afBorrowCashDo.getSumOverdue(), afBorrowCashDo.getRateAmount(), afBorrowCashDo.getSumRate(), afBorrowCashDo.getPoundage(), afBorrowCashDo.getSumRenewalPoundage()).subtract(afBorrowCashDo.getRepayAmount());
			// 当前日期与预计还款时间之前的天数差小于配置的betweenDuedate，并且未还款金额大于配置的限制金额时，可续期
			if (betweenDuedate.compareTo(new BigDecimal(betweenGmtPlanRepayment)) > 0 && waitPaidAmount.compareTo(amountLimit) >= 0) {
				data.put("renewalDay", allowRenewalDay);
				data.put("renewalAmount", waitPaidAmount);
				AfRepaymentBorrowCashDo afRepaymentBorrowCashDo = afRepaymentBorrowCashService.getLastRepaymentBorrowCashByBorrowId(afBorrowCashDo.getRid());
				if (null == afRepaymentBorrowCashDo || (null != afRepaymentBorrowCashDo && !StringUtils.equals(afBorrowCashDo.getStatus(), "P"))) {
					data.put("renewalStatus", "Y");
				}
			}

		}

		data.put("type", numberWordFormat.borrowTime(afBorrowCashDo.getType()));
		data.put("arrivalAmount", afBorrowCashDo.getArrivalAmount());
		data.put("rejectReason", afBorrowCashDo.getReviewDetails());
		data.put("serviceAmount", afBorrowCashDo.getPoundage());
		data.put("gmtArrival", afBorrowCashDo.getGmtArrival());
		data.put("borrowNo", afBorrowCashDo.getBorrowNo());
		data.put("bankCard", afBorrowCashDo.getCardNumber());
		data.put("bankName", afBorrowCashDo.getCardName());
		data.put("gmtArrival", afBorrowCashDo.getGmtArrival());
		data.put("gmtClose", afBorrowCashDo.getGmtClose());

		BigDecimal allAmount = BigDecimalUtil.add(afBorrowCashDo.getAmount(), afBorrowCashDo.getSumOverdue(), afBorrowCashDo.getOverdueAmount(), afBorrowCashDo.getRateAmount(), afBorrowCashDo.getSumRate(), afBorrowCashDo.getPoundage(), afBorrowCashDo.getSumRenewalPoundage());
		BigDecimal showAmount = BigDecimalUtil.subtract(allAmount, afBorrowCashDo.getRepayAmount());

		// 查询新利率配置
		AfResourceDo rateInfoDo = afResourceService.getConfigByTypesAndSecType(Constants.BORROW_RATE, Constants.BORROW_CASH_INFO_LEGAL_NEW);
		// 判断是否显示续借按钮
		String renewalRate = rateInfoDo.getValue();

		BigDecimal tmpAmount = afBorrowCashDo.getAmount().multiply(new BigDecimal(Double.valueOf(renewalRate) / 100)).setScale(2, BigDecimal.ROUND_HALF_UP);
		tmpAmount = tmpAmount.compareTo(BigDecimalUtil.ONE_HUNDRED) > 0 ? tmpAmount : BigDecimalUtil.ONE_HUNDRED;

		if (showAmount.compareTo(tmpAmount) <= 0) {
			data.put("renewalStatus", "N");
		}
		// 服务费和手续费
		data.put("serviceFee", afBorrowCashDo.getSumRenewalPoundage().add(afBorrowCashDo.getPoundage()));

		data.put("interestFee", afBorrowCashDo.getSumRate().add(afBorrowCashDo.getRateAmount()));

		data.put("returnAmount", showAmount);

		data.put("overdueDay", afBorrowCashDo.getOverdueDay());
		data.put("overdueAmount", afBorrowCashDo.getSumOverdue().add(afBorrowCashDo.getOverdueAmount()));
		// overdueStatus处理，根据预计还款时间来做，主要用于前端app展示
		if (DateUtil.afterDay(afBorrowCashDo.getGmtPlanRepayment(), new Date())) {
			data.put("overdueStatus", YesNoStatus.NO.getCode());
		} else {
			data.put("overdueStatus", YesNoStatus.YES.getCode());
		}

		data.put("rid", afBorrowCashDo.getRid());
		data.put("renewalNum", afBorrowCashDo.getRenewalNum());
		return data;

	}



}