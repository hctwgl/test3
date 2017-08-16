package com.ald.fanbei.api.biz.bo;

import java.util.HashMap;

import org.apache.commons.lang.StringUtils;

import com.ald.fanbei.api.biz.service.boluome.BoluomeCore;

public class BoluomeOrderCancelRequestBo extends HashMap<String, String>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3833791916068912907L;
	private String orderId;//订单Id
	private Long timestamp;//签名时间戳
	private String sign;//签名参数
	private String orderType;//订单类型
	private String reason; //取消订单原因
	private String appKey;
	
	public String getAppKey() {
		return appKey;
	}
	public void setAppKey(String appKey) {
		this.appKey = appKey;
		this.put(BoluomeCore.APP_KEY, appKey);
	}
	public String getOrderType() {
		return orderType;
	}
	public void setOrderType(String orderType) {
		this.orderType = orderType;
		this.put(BoluomeCore.ORDER_TYPE, orderType);
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
		this.put(BoluomeCore.REASON, reason);
	}
	/**
	 * @return the orderId
	 */
	public String getOrderId() {
		return orderId;
	}
	/**
	 * @param orderId the orderId to set
	 */
	public void setOrderId(String orderId) {
		this.orderId = orderId;
		this.put(BoluomeCore.ORDER_ID, orderId);
	}
	
	/**
	 * @return the timestamp
	 */
	public Long getTimestamp() {
		return timestamp;
	}
	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
		this.put(BoluomeCore.TIME_STAMP, timestamp + StringUtils.EMPTY);
	}
	/**
	 * @return the sign
	 */
	public String getSign() {
		return sign;
	}
	/**
	 * @param sign the sign to set
	 */
	public void setSign(String sign) {
		this.sign = sign;
		this.put(BoluomeCore.SIGN, sign);
	}
}
