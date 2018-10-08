package com.ald.fanbei.api.biz.service;

import java.math.BigDecimal;

import com.ald.fanbei.api.common.enums.JsdRepayType;

/**
 * @author CFP
 *	处理催收相关业务
 */
public interface JsdCollectionService {
	
	/**
	 * 催收还款通知
	 * @param repayAmount 还款总额
	 * @param curOutTradeNo	支付渠道提供的还款流水号
	 * @param borrowNo	对应借款编号
	 * @param orderId	借款对应商品订单号
	 * @param uid		借款用户Id
	 * @param type		还款方式
	 * @param reviewReuslt 兼容字段：还款审批结果
	 */
	public void nofityRepayment(BigDecimal repayAmount, String curOutTradeNo, String borrowNo, Long orderId, Long uid, JsdRepayType type, String reviewReuslt);
	
}
