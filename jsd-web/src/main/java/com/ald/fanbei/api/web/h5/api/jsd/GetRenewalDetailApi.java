package com.ald.fanbei.api.web.h5.api.jsd;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.JsdBorrowCashRenewalService;
import com.ald.fanbei.api.biz.service.JsdBorrowCashRepaymentService;
import com.ald.fanbei.api.biz.service.JsdBorrowCashService;
import com.ald.fanbei.api.biz.service.JsdBorrowLegalOrderCashService;
import com.ald.fanbei.api.biz.service.JsdBorrowLegalOrderService;
import com.ald.fanbei.api.biz.service.JsdResourceService;
import com.ald.fanbei.api.biz.service.impl.JsdResourceServiceImpl.ResourceRateInfoBo;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.ResourceType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderCashDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderDo;
import com.ald.fanbei.api.dal.domain.JsdResourceDo;
import com.ald.fanbei.api.web.common.Context;
import com.ald.fanbei.api.web.common.JsdH5Handle;
import com.ald.fanbei.api.web.common.JsdH5HandleResponse;
import com.alibaba.fastjson.JSONArray;


/**  
 * @Description: 极速贷获取续期详情
 * @Copyright (c) 浙江阿拉丁电子商务股份有限公司 All Rights Reserved.
 * @author yanghailong
 * @date 2018年8月28日
 */
@Component("getRenewalDetailApi")
public class GetRenewalDetailApi implements JsdH5Handle {

    @Resource
    private JsdBorrowLegalOrderCashService jsdBorrowLegalOrderCashService;
    @Resource
    private JsdBorrowLegalOrderService jsdBorrowLegalOrderService;
    @Resource
    private JsdBorrowCashService jsdBorrowCashService;
    @Resource
    private JsdBorrowCashRepaymentService jsdBorrowCashRepaymentService;
    @Resource
    private JsdBorrowCashRenewalService jsdBorrowCashRenewalService;
    @Resource
    private JsdResourceService jsdResourceService;

	@Override
	public JsdH5HandleResponse process(Context context) {
		// 西瓜借款borrowNo
		String tradeNoXgxy = ObjectUtils.toString(context.getDataMap().get("borrowNo"), "");
		// 请求时间戳
		String timestamp = ObjectUtils.toString(context.getDataMap().get("timestamp"), "");
		
		// 借款记录
		JsdBorrowCashDo borrowCashDo = jsdBorrowCashService.getByTradeNoXgxy(tradeNoXgxy);
		if (borrowCashDo == null) {
			throw new FanbeiException("borrowCash is not exist", FanbeiExceptionCode.SYSTEM_ERROR);
		}
		logger.info("getRenewalDetail jsdBorrowCash record = {} " , borrowCashDo);

		jsdBorrowCashRenewalService.checkCanRenewal(borrowCashDo);
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("delayInfo", this.getRenewalDetail(borrowCashDo));
		data.put("timestamp", timestamp);
		
		JsdH5HandleResponse resp = new JsdH5HandleResponse(200, "成功", data);

		return resp;
	}
	

	private JSONArray getRenewalDetail(JsdBorrowCashDo borrowCashDo) {
		JSONArray delayArray = new JSONArray();
		Map<String, Object> delayInfo = new HashMap<String, Object>();
		
		//上一笔订单记录
		JsdBorrowLegalOrderCashDo orderCashDo = jsdBorrowLegalOrderCashService.getLastOrderCashByBorrowId(borrowCashDo.getRid());
		if(orderCashDo == null)	throw new FanbeiException(FanbeiExceptionCode.RENEWAL_ORDER_NOT_EXIST_ERROR);
		
		JsdBorrowLegalOrderDo orderDo = jsdBorrowLegalOrderService.getById(orderCashDo.getBorrowLegalOrderId());
		if(orderDo==null) throw new FanbeiException(FanbeiExceptionCode.RENEWAL_ORDER_NOT_EXIST_ERROR);
		logger.info("last orderCash record = {} " , orderCashDo);
		
		JsdResourceDo renewalResource = jsdResourceService.getByTypeAngSecType(ResourceType.JSD_CONFIG.getCode(), ResourceType.JSD_RENEWAL_INFO.getCode());
		if(renewalResource==null) throw new FanbeiException(FanbeiExceptionCode.GET_JSD_RATE_ERROR);

		// 允许续期天数
		BigDecimal allowRenewalDay = new BigDecimal(renewalResource.getValue());
		
		// 续借需还本金比例
		BigDecimal renewalCapitalRate = new BigDecimal(renewalResource.getValue1());
		//续借需要支付本金 = 借款金额 * 续借需还本金比例
		BigDecimal capital = borrowCashDo.getAmount().multiply(renewalCapitalRate).setScale(2, RoundingMode.HALF_UP);
		
		// 上期订单待还本金
		BigDecimal waitOrderAmount = BigDecimalUtil.add(orderCashDo.getAmount(),orderCashDo.getSumRepaidInterest(),orderCashDo.getSumRepaidOverdue(),
				orderCashDo.getSumRepaidPoundage()).subtract(orderCashDo.getRepaidAmount());
		
		// 上期总利息
		BigDecimal rateAmount = BigDecimalUtil.add(borrowCashDo.getInterestAmount(),orderCashDo.getInterestAmount());
		// 上期总手续费
		BigDecimal poundage = BigDecimalUtil.add(borrowCashDo.getPoundageAmount(),orderCashDo.getPoundageAmount());
		// 上期总逾期费
		BigDecimal overdueAmount = BigDecimalUtil.add(borrowCashDo.getOverdueAmount(),orderCashDo.getOverdueAmount());
		
		// 续期应缴费用(上期总利息+上期总手续费+上期总逾期费+要还本金  +上期待还订单)
		BigDecimal renewalPayAmount = BigDecimalUtil.add(rateAmount, poundage, overdueAmount, capital, waitOrderAmount);
		
		String deferRemark = "上期利息"+rateAmount+
							 "元,赊销手续费"+poundage+
							 "元,上期逾期费"+overdueAmount+
							 "元,本金还款部分"+capital+
							 "元,上期商品价格"+waitOrderAmount+"元";
		
		BigDecimal principalAmount = BigDecimalUtil.add(borrowCashDo.getAmount(), borrowCashDo.getSumRepaidOverdue(), 
				borrowCashDo.getSumRepaidInterest(), borrowCashDo.getSumRepaidPoundage())
				.subtract(borrowCashDo.getRepayAmount().add(capital));
		
		delayInfo.put("principalAmount", principalAmount+"");	// 展期后剩余借款本金
		delayInfo.put("delayAmount", renewalPayAmount+"");	// 需支付总金额
		delayInfo.put("delayDay", allowRenewalDay+"");	// 续期天数
		delayInfo.put("delayRemark", deferRemark);	// 费用明细	展期金额的相关具体描述（多条说明用英文逗号,用间隔）
		this.getRenewalRate(delayInfo);
		delayInfo.put("totalDiffFee", this.getDiffFee(borrowCashDo, delayInfo).toPlainString());	// 展期后的利润差，西瓜会根据此金额匹配搭售商品 TODO
		
		delayArray.add(delayInfo);
		
		return delayArray;
	}
	
	
	private void getRenewalRate(Map<String, Object> delayInfo) {
		
		ResourceRateInfoBo rateInfo = jsdResourceService.getRateInfo(delayInfo.get("delayDay").toString());
		//借款手续费率
		delayInfo.put("interestRate", rateInfo.serviceRate);
		//借款利率
		delayInfo.put("serviceRate", rateInfo.interestRate);
	}
	
	private BigDecimal getDiffFee(JsdBorrowCashDo borrowCashDo, Map<String, Object> delayInfo) {
		
		BigDecimal interestRate = new BigDecimal(delayInfo.get("interestRate").toString());
		BigDecimal serviceRate = new BigDecimal(delayInfo.get("serviceRate").toString());
		BigDecimal renewalAmount = new BigDecimal(delayInfo.get("principalAmount").toString());
		BigDecimal renewalDay = new BigDecimal(delayInfo.get("delayDay").toString());

		// 借款利息手续费
		BigDecimal rateAmount = BigDecimalUtil.multiply(renewalAmount, interestRate, renewalDay.divide(new BigDecimal(Constants.ONE_YEAY_DAYS), 6, RoundingMode.HALF_UP));
		BigDecimal poundage = BigDecimalUtil.multiply(renewalAmount, serviceRate, renewalDay.divide(new BigDecimal(Constants.ONE_YEAY_DAYS), 6, RoundingMode.HALF_UP));
		
		// 用户分层利率
		BigDecimal riskDailyRate = borrowCashDo.getRiskDailyRate();
		// 总利润
		BigDecimal totalDiffFee = BigDecimalUtil.multiply(renewalAmount, renewalDay, riskDailyRate);
		
//		BigDecimal finalDiffProfit = totalDiffFee.subtract(rateAmount).subtract(poundage).subtract(orderCashService);
		BigDecimal diffProfit = totalDiffFee.subtract(rateAmount).subtract(poundage);
		BigDecimal finalDiffProfit = diffProfit.setScale(-1, RoundingMode.UP);	// 向上取十
		logger.info("jsd renewal diffProfit="+diffProfit+", return finalDiffProfit="+finalDiffProfit);
		if (finalDiffProfit.compareTo(BigDecimal.ZERO) <= 0) {
        	finalDiffProfit = BigDecimal.ZERO;
        }
		
		return finalDiffProfit;
	}
}

