package com.ald.fanbei.api.web.api.borrowCash;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfBorrowCashService;
import com.ald.fanbei.api.biz.service.AfBorrowLegalOrderCashService;
import com.ald.fanbei.api.biz.service.AfRenewalDetailService;
import com.ald.fanbei.api.biz.service.AfRepaymentBorrowCashService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
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
 * @author fumeiai 2017年5月17日下午20:11
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("applyRenewalApi")
public class ApplyRenewalApi implements ApiHandle {

	@Resource
	BizCacheUtil bizCacheUtil;
	@Resource
	AfResourceService afResourceService;
	@Resource
	AfBorrowCashService afBorrowCashService;
	@Resource
	AfUserAccountService afUserAccountService;
	@Resource
	AfRepaymentBorrowCashService afRepaymentBorrowCashService;
	@Resource
	AfRenewalDetailService afRenewalDetailService;
	@Resource
	AfBorrowLegalOrderCashService afBorrowLegalOrderCashService;
	@Resource
	RiskUtil riskUtil;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);

		Long rid = NumberUtil.objToLongDefault(requestDataVo.getParams().get("borrowId"), 0l);
		BigDecimal renewalAmount = NumberUtil.objToBigDecimalDefault(requestDataVo.getParams().get("renewalAmount"), BigDecimal.ZERO);
		
		AfBorrowCashDo afBorrowCashDo = afBorrowCashService.getBorrowCashByrid(rid);
		if (afBorrowCashDo == null) {
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SYSTEM_ERROR);
		}

		if(context.getAppVersion()<=375){
			throw new FanbeiException("续借功能维护中,为避免产生逾期，请及时还款",true);
		}
		// 对402版本借钱，低版本还款情况做控制
        afBorrowLegalOrderCashService.checkIllegalVersionInvoke(context.getAppVersion(), rid);
		
		AfRepaymentBorrowCashDo afRepaymentBorrowCashDo = afRepaymentBorrowCashService.getLastRepaymentBorrowCashByBorrowId(afBorrowCashDo.getRid());
		if (null != afRepaymentBorrowCashDo && StringUtils.equals(afRepaymentBorrowCashDo.getStatus(), "P")) {
			throw new FanbeiException("There is a repayment is processing", FanbeiExceptionCode.HAVE_A_REPAYMENT_PROCESSING_ERROR);
		}

		Map<String, Object> data = objectWithAfBorrowCashDo(afBorrowCashDo, context.getAppVersion(), renewalAmount);

		AfUserAccountDo userDto = afUserAccountService.getUserAccountByUserId(afBorrowCashDo.getUserId());

		data.put("rebateAmount", userDto.getRebateAmount());
		data.put("jfbAmount", userDto.getJfbAmount());

		resp.setResponseData(data);

		// fmf  add续期前逾期状态
		try{
			List<AfRenewalDetailDo> renewalDetailList= afRenewalDetailService.getRenewalDetailListByBorrowId(rid);
			if(renewalDetailList == null || renewalDetailList.size()==0){
				afBorrowCashDo.setRdBeforeOverdueStatus(afBorrowCashDo.getOverdueStatus());
				afBorrowCashService.updateAfBorrowCashService(afBorrowCashDo);
			}
		}catch(Exception e){
			e.getStackTrace();
		}
		return resp;
	}

	public Map<String, Object> objectWithAfBorrowCashDo(AfBorrowCashDo afBorrowCashDo, Integer appVersion,BigDecimal renewAmount) {
		Map<String, Object> data = new HashMap<String, Object>();

		AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(Constants.RES_RENEWAL_DAY_LIMIT, Constants.RES_ALLOW_RENEWAL_DAY_NEW);
		BigDecimal allowRenewalDay = new BigDecimal(resource.getValue());// 允许续期天数
		/*AfResourceDo poundageResource = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE, Constants.RES_BORROW_CASH_POUNDAGE);
		BigDecimal borrowCashPoundage = new BigDecimal(poundageResource.getValue());// 借钱手续费率（日）
		Object poundageRateCash = bizCacheUtil.getObject(Constants.RES_BORROW_CASH_POUNDAGE_RATE + afBorrowCashDo.getUserId());
		if (poundageRateCash != null) {
			borrowCashPoundage = new BigDecimal(poundageRateCash.toString());
		}
		*/

		BigDecimal borrowCashPoundage = afBorrowCashDo.getPoundageRate();
		BigDecimal capital =BigDecimal.ZERO;
		/*if(appVersion == 397){
			capital = BigDecimalUtil.add(afBorrowCashDo.getAmount(),afBorrowCashDo.getSumOverdue(),afBorrowCashDo.getSumRate()).subtract(afBorrowCashDo.getRepayAmount()).subtract(renewAmount);
		}else{
			AfResourceDo capitalRateResource = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE, Constants.RENEWAL_CAPITAL_RATE);
			BigDecimal renewalCapitalRate = new BigDecimal(capitalRateResource.getValue());// 借钱手续费率（日）
			capital = afBorrowCashDo.getAmount().multiply(renewalCapitalRate).setScale(2, RoundingMode.HALF_UP);
		}*/


		// 续借本金
		BigDecimal allAmount = BigDecimalUtil.add(afBorrowCashDo.getAmount(), afBorrowCashDo.getSumOverdue(), afBorrowCashDo.getSumRate());
		JSONObject response = riskUtil.getPayCaptal(afBorrowCashDo,"40",afBorrowCashDo.getAmount());
		capital = new BigDecimal(response.getJSONObject("data").getString("money"));
		BigDecimal waitPaidAmount = BigDecimalUtil.subtract(allAmount, afBorrowCashDo.getRepayAmount()).subtract(capital);

		BigDecimal allRenewalAmount= BigDecimalUtil.subtract(allAmount, afBorrowCashDo.getRepayAmount());
		if (renewAmount.compareTo(allRenewalAmount) >0) {   //判断续借金额是否大于借款金额
			throw new FanbeiException(
					FanbeiExceptionCode.RENEWAL_CASH_REPAY_AMOUNT_MORE_BORROW_ERROR);
		}
		// 本期手续费 = 未还金额 * 续期天数 * 借钱手续费率（日）
		BigDecimal poundage = waitPaidAmount.multiply(allowRenewalDay).multiply(borrowCashPoundage).setScale(2, RoundingMode.HALF_UP);

		// 续期应缴费用(利息+手续费+滞纳金+要还本金)
		BigDecimal renewalPayAmount = BigDecimalUtil.add(afBorrowCashDo.getRateAmount(), poundage, afBorrowCashDo.getOverdueAmount(), capital);

		if (appVersion < 380) {
			waitPaidAmount = BigDecimalUtil.subtract(allAmount, afBorrowCashDo.getRepayAmount());
			poundage = waitPaidAmount.multiply(allowRenewalDay).multiply(borrowCashPoundage).setScale(2, RoundingMode.HALF_UP);
			renewalPayAmount = BigDecimalUtil.add(afBorrowCashDo.getRateAmount(), poundage, afBorrowCashDo.getOverdueAmount());
		}

		data.put("rid", afBorrowCashDo.getRid());
		data.put("rateAmount", afBorrowCashDo.getRateAmount());// 上期利息
		data.put("overdueAmount", afBorrowCashDo.getOverdueAmount());// 滞纳金
		data.put("poundage", poundage);// 本期手续费
		data.put("capital", capital);// 续借需要支付本金
		data.put("renewalPayAmount", renewalPayAmount);// 续期应缴费用
		data.put("renewalAmount", waitPaidAmount);// 续期金额
		data.put("renewalDay", allowRenewalDay);// 续期天数
		
		data.put("allRenewalAmount", allRenewalAmount);//所有续借的金额
		return data;
	}

}
