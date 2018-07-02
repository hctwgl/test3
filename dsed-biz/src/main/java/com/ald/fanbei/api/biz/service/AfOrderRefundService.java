package com.ald.fanbei.api.biz.service;

import java.util.Date;

import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.dal.domain.AfOrderRefundDo;

/**
 *@类描述：
 *@author xiaotianjian 2017年3月29日下午3:53:02
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfOrderRefundService {

	int addOrderRefund(AfOrderRefundDo orderRefundInfo);
	
	int updateOrderRefund(AfOrderRefundDo orderRefundInfo);
	
	AfOrderRefundDo getOrderRefundByOrderId(Long orderId);
	
	/**
	 * 获取最近退款号码
	 * @param current
	 * @return
	 */
	String getCurrentLastRefundNo(Date current);
	
	/**
	 * 根据id获取退款信息
	 * @param refundId
	 * @return
	 */
	AfOrderRefundDo getRefundInfoById(Long refundId);
	
	/**
	 * 处理菠萝觅退款，以及后台代买退款
	 * @param orderRefundInfo
	 * @param isBoluome 是否是菠萝觅订单
	 *
	 * @return
	 */
	int dealWithOrderRefund(AfOrderRefundDo orderRefundInfo, AfOrderDo orderInfo, boolean isBoluome);
	
	/**
	 * 处理自营商品退款成功
	 * @param orderRefundInfo
	 * @return
	 */
	int dealWithSelfGoodsOrderRefund(AfOrderRefundDo orderRefundInfo, AfOrderDo orderInfo);
	
	/**
	 * 处理自营商品退款失败
	 * @param orderRefundInfo
	 * @return
	 */
	int dealWithSelfGoodsOrderRefundFail(AfOrderRefundDo orderRefundInfo, AfOrderDo orderInfo);

	/**
	 * 处理商户退款
	 * @param refundInfo
	 * @param orderInfo
	 * @return
	 */
	int dealWithTradeOrderRefund(AfOrderRefundDo refundInfo, AfOrderDo orderInfo);

	/**
	 * 处理商户退款失败
	 * @param refundInfo
	 * @param orderInfo
	 * @return
	 */
	int dealWithTradeRefundFail(AfOrderRefundDo refundInfo, AfOrderDo orderInfo);
	
	
}
