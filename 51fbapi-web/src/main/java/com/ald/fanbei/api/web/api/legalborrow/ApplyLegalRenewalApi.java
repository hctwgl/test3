package com.ald.fanbei.api.web.api.legalborrow;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfBorrowCashService;
import com.ald.fanbei.api.biz.service.AfBorrowLegalOrderCashService;
import com.ald.fanbei.api.biz.service.AfBorrowLegalOrderService;
import com.ald.fanbei.api.biz.service.AfRenewalDetailService;
import com.ald.fanbei.api.biz.service.AfRenewalLegalDetailService;
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
import com.ald.fanbei.api.dal.domain.AfBorrowLegalOrderCashDo;
import com.ald.fanbei.api.dal.domain.AfBorrowLegalOrderDo;
import com.ald.fanbei.api.dal.domain.AfRenewalDetailDo;
import com.ald.fanbei.api.dal.domain.AfRepaymentBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;


/**  
 * @Description: 获取续期详情信息（合规）
 * @Copyright (c) 浙江阿拉丁电子商务股份有限公司 All Rights Reserved.
 * @author yanghailong
 * @date 2017年12月8日
 */
@Component("applyLegalRenewalApi")
public class ApplyLegalRenewalApi implements ApiHandle {

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
	AfBorrowLegalOrderService afBorrowLegalOrderService;
	@Resource
	AfRenewalLegalDetailService afRenewalLegalDetailService;
	

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		//借款id
		Long rid = NumberUtil.objToLongDefault(requestDataVo.getParams().get("borrowId"), 0l);
		//续借金额
		BigDecimal renewalAmount = NumberUtil.objToBigDecimalDefault(requestDataVo.getParams().get("renewalAmount"), BigDecimal.ZERO);
		
		// 对405版本借钱，在低版本续期情况做控制
		afBorrowLegalOrderService.checkIllegalVersionInvoke(context.getAppVersion(), rid);
		
		//借款记录
		AfBorrowCashDo afBorrowCashDo = afBorrowCashService.getBorrowCashByrid(rid);
		if (afBorrowCashDo == null) {
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SYSTEM_ERROR);
		}
		logger.info("applyLegalRenewalApi afBorrowCashDo record = {} " , afBorrowCashDo);

		//还款记录
		AfRepaymentBorrowCashDo afRepaymentBorrowCashDo = afRepaymentBorrowCashService.getLastRepaymentBorrowCashByBorrowId(afBorrowCashDo.getRid());
		if (null != afRepaymentBorrowCashDo && StringUtils.equals(afRepaymentBorrowCashDo.getStatus(), "P")) {
			throw new FanbeiException("There is a repayment is processing", FanbeiExceptionCode.HAVE_A_REPAYMENT_PROCESSING_ERROR);
		}
		
		Map<String, Object> data = objectWithAfBorrowCashDo(afBorrowCashDo, context.getAppVersion(), renewalAmount);

		AfUserAccountDo userDto = afUserAccountService.getUserAccountByUserId(afBorrowCashDo.getUserId());

		data.put("rebateAmount", userDto.getRebateAmount());
		data.put("jfbAmount", userDto.getJfbAmount());
		logger.info("applyLegalRenewalApi data = {} " , data);
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

		//获取续期天数
		AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(Constants.RES_RENEWAL_DAY_LIMIT, Constants.RES_ALLOW_RENEWAL_DAY_NEW);
		BigDecimal allowRenewalDay = new BigDecimal(resource.getValue());// 允许续期天数
		
		//上一笔订单记录
		AfBorrowLegalOrderDo afBorrowLegalOrder = afBorrowLegalOrderService.getLastBorrowLegalOrderByBorrowId(afBorrowCashDo.getRid());
		if(afBorrowLegalOrder==null){
			throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_ORDER_NOT_EXIST_ERROR);
		}
		AfBorrowLegalOrderCashDo afBorrowLegalOrderCash = afBorrowLegalOrderCashService.getBorrowLegalOrderCashByBorrowLegalOrderId(afBorrowLegalOrder.getRid());
		if(afBorrowLegalOrderCash == null){
			throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_ORDER_NOT_EXIST_ERROR);
		}
		logger.info("applyLegalRenewalApi last afBorrowLegalOrderCash record = {} " , afBorrowLegalOrderCash);
		//续借需要支付本金
		BigDecimal capital =BigDecimal.ZERO;
		
		//续借需还本金比例
		AfResourceDo capitalRateResource = afResourceService.getConfigByTypesAndSecType(Constants.BORROW_RATE, Constants.BORROW_CASH_INFO_LEGAL_NEW);
		BigDecimal renewalCapitalRate = (new BigDecimal(capitalRateResource.getValue())).divide(new BigDecimal(100));// 续借需还本金比例
		//续借需要支付本金 = 借款金额 * 续借需还本金比例
		capital = afBorrowCashDo.getAmount().multiply(renewalCapitalRate).setScale(2, RoundingMode.HALF_UP);

		//上期借款手续费
		BigDecimal borrowPoundage = afBorrowCashDo.getPoundage();
		//上期订单手续费
		BigDecimal orderPoundage = NumberUtil.objToBigDecimalDefault(afBorrowLegalOrderCash.getPoundageAmount(),BigDecimal.ZERO);
		//上期借款利息
		BigDecimal borrowRateAmount = afBorrowCashDo.getRateAmount();
		//上期订单利息
		BigDecimal orderRateAmount = NumberUtil.objToBigDecimalDefault(afBorrowLegalOrderCash.getInterestAmount(),BigDecimal.ZERO);
		
		// 本金（总） 
		BigDecimal allAmount = BigDecimalUtil.add(afBorrowCashDo.getAmount(), afBorrowCashDo.getSumOverdue(),afBorrowCashDo.getSumRate(),afBorrowCashDo.getSumRenewalPoundage());
		// 续期金额 = 续借本金（总）  - 借款已还金额 - 续借需要支付本金
		BigDecimal waitPaidAmount = BigDecimalUtil.subtract(allAmount, afBorrowCashDo.getRepayAmount()).subtract(capital);
		
		
		//上期逾期费（借款和订单）
		BigDecimal borrowOverdueAmount = afBorrowCashDo.getOverdueAmount();
		BigDecimal orderOverdueAmount = afBorrowLegalOrderCash.getOverdueAmount();
		
		//上期订单借款金额
		BigDecimal orderAmount = afBorrowLegalOrderCash.getAmount();
		// 上期订单待还本金
		BigDecimal waitOrderAmount = BigDecimalUtil.add(orderAmount,afBorrowLegalOrderCash.getSumRepaidInterest(),afBorrowLegalOrderCash.getSumRepaidOverdue(),
				afBorrowLegalOrderCash.getSumRepaidPoundage()).subtract(afBorrowLegalOrderCash.getRepaidAmount());
		
		//所有续借的金额
		BigDecimal allRenewalAmount= BigDecimalUtil.add(allAmount,borrowPoundage,borrowRateAmount,afBorrowCashDo.getOverdueAmount(),waitOrderAmount,orderOverdueAmount,orderPoundage,orderRateAmount)
													.subtract(afBorrowCashDo.getRepayAmount());

		//上期总手续费
		BigDecimal poundage = BigDecimalUtil.add(borrowPoundage,orderPoundage);
		//上期总逾期费
		BigDecimal overdueAmount = BigDecimalUtil.add(borrowOverdueAmount,orderOverdueAmount);
		//上期总利息
		BigDecimal rateAmount = BigDecimalUtil.add(borrowRateAmount,orderRateAmount);

		// 续期应缴费用(上期总利息+上期总手续费+上期总逾期费+要还本金  +上期待还订单)
		BigDecimal renewalPayAmount = BigDecimalUtil.add(rateAmount, poundage, overdueAmount, capital, waitOrderAmount);

		data.put("rid", afBorrowCashDo.getRid());
		data.put("rateAmount", rateAmount);// 上期利息
		data.put("overdueAmount", overdueAmount);// 上期滞纳金
		data.put("poundage", poundage);// 上期手续费
		data.put("capital", capital);// 续借需要支付本金
		data.put("renewalPayAmount", renewalPayAmount);// 续期应缴费用
		data.put("renewalAmount", waitPaidAmount);// 续期金额
		data.put("renewalDay", allowRenewalDay);// 续期天数
		data.put("goodsOrderAmount", waitOrderAmount);// 上期借款订单的待还本金
		
		data.put("allRenewalAmount", allRenewalAmount);//所有续借的金额
		return data;
	}

}

