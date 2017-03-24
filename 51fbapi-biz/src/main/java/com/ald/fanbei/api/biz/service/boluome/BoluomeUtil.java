package com.ald.fanbei.api.biz.service.boluome;

import com.ald.fanbei.api.common.enums.OrderStatus;


/**
 * 菠萝觅工具类
 * @类描述：
 * @author xiaotianjian 2017年3月24日下午9:07:55
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class BoluomeUtil {

	public static OrderStatus parseOrderType(String orderStatusStr) {
		//1.已经下单 2.待支付 3.已支付 4.已完成 6.退款中 7.已经退款 8.已取消 9.处理中 11.等待退款
		int orderStatus = Integer.parseInt(orderStatusStr);
		OrderStatus status = null;
		if (orderStatus == 1 || orderStatus == 2) {
			status = OrderStatus.NEW;
		} else if (orderStatus == 3) {
			status = OrderStatus.PAID;
		} else if (orderStatus == 4) {
			status = OrderStatus.FINISHED;
		} else if (orderStatus == 6) {
			status = OrderStatus.DEAL_REFUNDING;
		} else if (orderStatus == 7) {
			status = OrderStatus.REFUND_COMPLETE;
		} else if (orderStatus == 11) {
			status = OrderStatus.WAITING_REFUND;
		}
		return status;
	}
} 
