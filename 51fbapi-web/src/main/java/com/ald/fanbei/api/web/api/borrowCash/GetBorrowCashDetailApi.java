package com.ald.fanbei.api.web.api.borrowCash;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfBorrowCashService;
import com.ald.fanbei.api.biz.service.AfRenewalDetailService;
import com.ald.fanbei.api.biz.service.AfRepaymentBorrowCashService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfBorrowCashStatus;
import com.ald.fanbei.api.common.enums.AfBorrowCashType;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfRenewalDetailDo;
import com.ald.fanbei.api.dal.domain.AfRepaymentBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * @类描述：
 * @author suweili 2017年3月25日下午1:07:02
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getBorrowCashDetailApi")
public class GetBorrowCashDetailApi extends GetBorrowCashBase implements ApiHandle {

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
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		// AfBorrowCashDo afBorrowCashDo = afBorrowCashService.getBorrowCashByUserId(userId);
		Long rid = NumberUtil.objToLongDefault(requestDataVo.getParams().get("borrowId"), 0l);
		AfUserAccountDo account = afUserAccountService.getUserAccountByUserId(userId);

		if (account == null) {
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SYSTEM_ERROR);
		}
		AfBorrowCashDo afBorrowCashDo = afBorrowCashService.getBorrowCashByrid(rid);
		if (afBorrowCashDo == null) {
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SYSTEM_ERROR);
		}
		BigDecimal paidAmount =afRepaymentBorrowCashService.getRepaymentAllAmountByBorrowId(rid);
		Map<String, Object> data = objectWithAfBorrowCashDo(afBorrowCashDo, context.getAppVersion());
		data.put("paidAmount", NumberUtil.objToBigDecimalDefault(paidAmount, BigDecimal.ZERO));
		data.put("rebateAmount", account.getRebateAmount());
		data.put("jfbAmount", account.getJfbAmount());

		resp.setResponseData(data);

		return resp;
	}

	public Map<String, Object> objectWithAfBorrowCashDo(AfBorrowCashDo afBorrowCashDo, Integer appVersion) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("rid", afBorrowCashDo.getRid());
		data.put("amount", afBorrowCashDo.getAmount());
		data.put("gmtCreate", afBorrowCashDo.getGmtCreate());
		data.put("status", afBorrowCashDo.getStatus());

		if (StringUtils.equals(afBorrowCashDo.getStatus(), AfBorrowCashStatus.transedfail.getCode()) || StringUtils.equals(afBorrowCashDo.getStatus(), AfBorrowCashStatus.transeding.getCode())) {
			data.put("status", AfBorrowCashStatus.waitTransed.getCode());
		}
		
		AfBorrowCashType borrowCashType = AfBorrowCashType.findRoleTypeByName(afBorrowCashDo.getType());

		data.put("gmtLastRepay", afBorrowCashDo.getGmtPlanRepayment());
		
		data.put("renewalStatus", "N");
		AfRenewalDetailDo afRenewalDetailDo = afRenewalDetailService.getRenewalDetailByBorrowId(afBorrowCashDo.getRid());
		if(afRenewalDetailDo!=null && StringUtils.equals(afRenewalDetailDo.getStatus(), "P")) {
			data.put("renewalStatus", "P");
		} else if (StringUtils.equals(afBorrowCashDo.getStatus(), "TRANSED")) {
			AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(Constants.RES_RENEWAL_DAY_LIMIT, Constants.RES_ALLOW_RENEWAL_DAY);
			BigDecimal allowRenewalDay = new BigDecimal(resource.getValue());// 允许续期天数
			AfResourceDo duedateResource = afResourceService.getConfigByTypesAndSecType(Constants.RES_RENEWAL_DAY_LIMIT, Constants.RES_BETWEEN_DUEDATE);
			BigDecimal betweenDuedate = new BigDecimal(duedateResource.getValue());// 续期的距离预计还款日的最小天数差
			AfResourceDo amountResource = afResourceService.getConfigByTypesAndSecType(Constants.RES_RENEWAL_DAY_LIMIT, Constants.RES_AMOUNT_LIMIT);
			BigDecimal amount_limit = new BigDecimal(amountResource.getValue());// 配置的未还金额限制 
			//如果借款记录存在，统计还款处理中金额
			BigDecimal repayingMoney = afRepaymentBorrowCashService.getRepayingTotalAmountByBorrowId(afBorrowCashDo.getRid());
			if(repayingMoney.compareTo(BigDecimal.ZERO)>0){
				//兼容老版本 老版本没有还款状态
				if (appVersion < 363) {
					data.put("status", AfBorrowCashStatus.transed.getCode());
				} else {
					data.put("status", AfBorrowCashStatus.repaying.getCode());
				}
			}
	
			long currentTime = System.currentTimeMillis();
			Date nowDate = DateUtil.getEndOfDate(new Date(currentTime));
			long betweenGmtPlanRepayment = DateUtil.getNumberOfDatesBetween(nowDate, afBorrowCashDo.getGmtPlanRepayment());
			BigDecimal waitPaidAmount = BigDecimalUtil.add(afBorrowCashDo.getAmount(), afBorrowCashDo.getOverdueAmount(), afBorrowCashDo.getSumOverdue(), afBorrowCashDo.getRateAmount(), afBorrowCashDo.getSumRate()).subtract(afBorrowCashDo.getRepayAmount());
			// 当前日期与预计还款时间之前的天数差小于配置的betweenDuedate，并且未还款金额大于配置的限制金额时，可续期
			if (betweenDuedate.compareTo(new BigDecimal(betweenGmtPlanRepayment)) > 0 && waitPaidAmount.compareTo(amount_limit) >= 0) {
				data.put("renewalDay", allowRenewalDay);
				data.put("renewalAmount", waitPaidAmount);
				AfRepaymentBorrowCashDo afRepaymentBorrowCashDo = afRepaymentBorrowCashService.getLastRepaymentBorrowCashByBorrowId(afBorrowCashDo.getRid());
				if (null == afRepaymentBorrowCashDo || (null != afRepaymentBorrowCashDo && !StringUtils.equals(afBorrowCashDo.getStatus(), "P"))) {
					data.put("renewalStatus", "Y");
				} 
			}
			
			//当待还本金小于等于第一次续期时待还本金的10%时，不再显示续期入口
			AfResourceDo capitalRateResource = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE, Constants.RENEWAL_CAPITAL_RATE);
			BigDecimal renewalCapitalRate = new BigDecimal(capitalRateResource.getValue());// 续借应还借钱金额比例
			BigDecimal capital = afBorrowCashDo.getAmount().multiply(renewalCapitalRate).setScale(2, RoundingMode.HALF_UP);
			if (waitPaidAmount.compareTo(capital) <= 0) {
				data.put("renewalStatus", "N");
			}
		}
		
		data.put("type", borrowCashType.getCode());
		data.put("arrivalAmount", afBorrowCashDo.getArrivalAmount());
		data.put("rejectReason", afBorrowCashDo.getReviewDetails());
		data.put("serviceAmount", afBorrowCashDo.getPoundage());
		data.put("gmtArrival", afBorrowCashDo.getGmtArrival());
		data.put("borrowNo", afBorrowCashDo.getBorrowNo());
		data.put("bankCard", afBorrowCashDo.getCardNumber());
		data.put("bankName", afBorrowCashDo.getCardName());
		data.put("gmtArrival", afBorrowCashDo.getGmtArrival());
		data.put("gmtClose", afBorrowCashDo.getGmtClose());

		BigDecimal allAmount = BigDecimalUtil.add(afBorrowCashDo.getAmount(), afBorrowCashDo.getSumOverdue(),afBorrowCashDo.getOverdueAmount(),afBorrowCashDo.getRateAmount(), afBorrowCashDo.getSumRate());
		BigDecimal showAmount = BigDecimalUtil.subtract(allAmount, afBorrowCashDo.getRepayAmount());

		data.put("returnAmount", showAmount);

		data.put("overdueDay", afBorrowCashDo.getOverdueDay());
		data.put("overdueAmount", afBorrowCashDo.getOverdueAmount());
		data.put("overdueStatus", afBorrowCashDo.getOverdueStatus());
		data.put("rid", afBorrowCashDo.getRid());
		data.put("renewalNum", afBorrowCashDo.getRenewalNum());
		
		return data;

	}

}
