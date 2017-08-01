package com.ald.fanbei.api.dal.domain.dto;

import com.alibaba.fastjson.annotation.JSONField;

import java.math.BigDecimal;
import java.util.Date;

public class AfTradeOrderDto {

	private Long id;
	private String orderNo;
	private Date gmtCreate;
	private BigDecimal actualAmount;
	private String username;
	private String orderStatus;
	private String withDrawStatus;
	private String icon;

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	public Date getGmtCreate() {
		return gmtCreate;
	}

	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}

	public BigDecimal getActualAmount() {
		return actualAmount;
	}

	public void setActualAmount(BigDecimal actualAmount) {
		this.actualAmount = actualAmount;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getWithDrawStatus() {
		return withDrawStatus;
	}

	public void setWithDrawStatus(String withDrawStatus) {
		this.withDrawStatus = withDrawStatus;
	}

}
