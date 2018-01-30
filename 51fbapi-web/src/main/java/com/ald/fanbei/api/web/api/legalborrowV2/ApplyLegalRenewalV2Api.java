package com.ald.fanbei.api.web.api.legalborrowV2;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.biz.util.ProtocolUtil;
import com.ald.fanbei.api.dal.domain.*;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;


/**  
 * @Description: 获取续期详情信息（合规V2）
 * @Copyright (c) 浙江阿拉丁电子商务股份有限公司 All Rights Reserved.
 * @author yanghailong
 * @date 2018年1月5日
 */
@Component("applyLegalRenewalV2Api")
public class ApplyLegalRenewalV2Api implements ApiHandle {

	@Resource
	BizCacheUtil bizCacheUtil;
	@Resource
	AfResourceService afResourceService;
	@Resource
	AfBorrowCashService afBorrowCashService;
	@Resource
	AfRepaymentBorrowCashService afRepaymentBorrowCashService;
	@Resource
	AfRenewalDetailService afRenewalDetailService;
	@Resource
	AfRenewalLegalDetailService afRenewalLegalDetailService;
	@Resource
	ProtocolUtil protocolUtil;
	@Resource
	AfUserService afUserService;
	@Resource
	RiskUtil riskUtil;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		
		// 借款id
		Long borrowId = NumberUtil.objToLongDefault(requestDataVo.getParams().get("borrowId"), 0l);
		
		// 借款记录
		AfBorrowCashDo afBorrowCashDo = afBorrowCashService.getBorrowCashByrid(borrowId);
		logger.info("applyLegalRenewalV2Api afBorrowCashDo record = {} " , afBorrowCashDo);
		if (afBorrowCashDo == null) {
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SYSTEM_ERROR);
		}
		
		// 还款记录
		AfRepaymentBorrowCashDo afRepaymentBorrowCashDo = afRepaymentBorrowCashService.getLastRepaymentBorrowCashByBorrowId(borrowId);
		if (null != afRepaymentBorrowCashDo && StringUtils.equals(afRepaymentBorrowCashDo.getStatus(), "P")) {
			throw new FanbeiException("There is a repayment is processing", FanbeiExceptionCode.HAVE_A_REPAYMENT_PROCESSING_ERROR);
		}
		
		// 续期数据
		Map<String, Object> data = objectWithAfBorrowCashDo(afBorrowCashDo);
		
		logger.info("applyLegalRenewalV2Api data = {} " , data);
		resp.setResponseData(data);

		// add续期前逾期状态
		try{
			List<AfRenewalDetailDo> renewalDetailList= afRenewalDetailService.getRenewalDetailListByBorrowId(borrowId);
			if(renewalDetailList == null || renewalDetailList.size()==0){
				afBorrowCashDo.setRdBeforeOverdueStatus(afBorrowCashDo.getOverdueStatus());
				afBorrowCashService.updateAfBorrowCashService(afBorrowCashDo);
			}
		}catch(Exception e){
			e.getStackTrace();
		}
		
		return resp;
	}

	private Map<String, Object> objectWithAfBorrowCashDo(AfBorrowCashDo afBorrowCashDo) {
		
		Map<String, Object> data = new HashMap<String, Object>();

		// 获取续期天数
		AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(Constants.RES_RENEWAL_DAY_LIMIT, Constants.RES_ALLOW_RENEWAL_DAY_NEW);
		// 允许续期天数
		BigDecimal allowRenewalDay = new BigDecimal(resource.getValue());
		
		// 续借需还本金比例
		AfResourceDo capitalRateResource = afResourceService.getConfigByTypesAndSecType(Constants.BORROW_RATE, Constants.BORROW_CASH_INFO_LEGAL_NEW);
		BigDecimal renewalCapitalRate = (new BigDecimal(capitalRateResource.getValue())).divide(new BigDecimal(100));
		// 续借需要支付本金 = 借款金额 * 续借需还本金比例
		//BigDecimal capital = afBorrowCashDo.getAmount().multiply(renewalCapitalRate).setScale(2, RoundingMode.HALF_UP);

		
		// 上期借款利息
		BigDecimal borrowRateAmount = afBorrowCashDo.getRateAmount();
		// 上期借款手续费
		BigDecimal borrowPoundage = afBorrowCashDo.getPoundage();
		// 上期借款逾期费
		BigDecimal borrowOverdueAmount = afBorrowCashDo.getOverdueAmount();
		
		// 本金（总） 
		BigDecimal allAmount = BigDecimalUtil.add(afBorrowCashDo.getAmount(), afBorrowCashDo.getSumOverdue(),afBorrowCashDo.getSumRate(),afBorrowCashDo.getSumRenewalPoundage());
		JSONObject response = riskUtil.getPayCaptal(afBorrowCashDo,"40",afBorrowCashDo.getAmount());
		BigDecimal capital = new BigDecimal(response.getJSONObject("data").getString("money"));
		// 续期金额 = 续借本金（总）  - 借款已还金额 - 续借需要支付本金
		BigDecimal waitPaidAmount = BigDecimalUtil.subtract(allAmount, afBorrowCashDo.getRepayAmount()).subtract(capital);
		
		// 所有续借的金额
		BigDecimal allRenewalAmount= BigDecimalUtil.add(allAmount,borrowPoundage,borrowRateAmount,borrowOverdueAmount).subtract(afBorrowCashDo.getRepayAmount());
		
		// 续期应缴费用(上期借款利息+上期借款手续费+上期借款逾期费+要还本金)
		BigDecimal renewalPayAmount = BigDecimalUtil.add(borrowRateAmount, borrowPoundage, borrowOverdueAmount, capital);
		
		data.put("rid", afBorrowCashDo.getRid());
		data.put("rateAmount", borrowRateAmount);	// 上期利息
		data.put("poundage", borrowPoundage);	// 上期手续费
		data.put("overdueAmount", borrowOverdueAmount);	// 上期滞纳金
		data.put("capital", capital);	// 续借需要支付本金
		data.put("renewalPayAmount", renewalPayAmount);	// 续期应缴费用（注：不含续期订单金额）
		data.put("renewalAmount", waitPaidAmount);	// 续期金额
		data.put("renewalDay", allowRenewalDay);	// 续期天数
		data.put("allRenewalAmount", allRenewalAmount);	//所有续借的金额
		Map map = new HashMap();
		AfUserDo afUserDo = afUserService.getUserById(afBorrowCashDo.getUserId());
//		if (allowRenewalDay.compareTo(BigDecimal.valueOf(7)) == 0){
//			map.put("type","SEVEN");
//		}else {
//			map.put("type","FOURTEEN");
//		}
		map.put("type",resource.getValue());
		map.put("userName",afUserDo.getUserName());
		map.put("borrowId",afBorrowCashDo.getRid());
		map.put("renewalId","");
		map.put("renewalDay",allowRenewalDay);
		map.put("renewalAmount",waitPaidAmount);
		List<AfResourceDo> resourceDoList = protocolUtil.getProtocolList("renewal",map);
		data.put("resourceDoList",resourceDoList);
		return data;
	}

}

