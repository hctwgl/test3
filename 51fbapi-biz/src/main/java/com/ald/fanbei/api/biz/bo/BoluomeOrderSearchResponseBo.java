package com.ald.fanbei.api.biz.bo;

import com.ald.fanbei.api.common.AbstractSerial;
import com.ald.fanbei.api.dal.domain.AfOrderDo;

public class BoluomeOrderSearchResponseBo {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5844694723452212426L;
	private String orderId;
	private String orderTitle;
	private long price;
	private int status;
	private String orderType;
	private long createdTime;
	private String userId;
	private long expriedTime;
	private String displayStatus;
	private String detailUrl;
	private String channel;
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getOrderTitle() {
		return orderTitle;
	}
	public void setOrderTitle(String orderTitle) {
		this.orderTitle = orderTitle;
		
	}
	public long getPrice() {
		return price;
	}
	public void setPrice(long price) {
		this.price = price;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getOrderType() {
		return orderType;
	}
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
	public long getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(long createdTime) {
		this.createdTime = createdTime;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public long getExpriedTime() {
		return expriedTime;
	}
	public void setExpriedTime(long expriedTime) {
		this.expriedTime = expriedTime;
	}
	public String getDisplayStatus() {
		return displayStatus;
	}
	public void setDisplayStatus(String displayStatus) {
		this.displayStatus = displayStatus;
	}
	public String getDetailUrl() {
		return detailUrl;
	}
	public void setDetailUrl(String detailUrl) {
		this.detailUrl = detailUrl;
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	
	
}
