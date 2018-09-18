package com.ald.jsd.mgr.dal.domain.dto;

import java.math.BigDecimal;
import java.util.Date;

public class MgrCollectionRepaymentDto {
	private String tradeNo;
	private String realName;
	private String account;
	private String repayWay;
	private BigDecimal amount;
	private Date gmtCreate;
	private Date gmtRepay;
	private String reviewStatus;
	
	public String getTradeNo() {
		return tradeNo;
	}
	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getRepayWay() {
		return repayWay;
	}
	public void setRepayWay(String repayWay) {
		this.repayWay = repayWay;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public Date getGmtCreate() {
		return gmtCreate;
	}
	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}
	public Date getGmtRepay() {
		return gmtRepay;
	}
	public void setGmtRepay(Date gmtRepay) {
		this.gmtRepay = gmtRepay;
	}
	public String getReviewStatus() {
		return reviewStatus;
	}
	public void setReviewStatus(String reviewStatus) {
		this.reviewStatus = reviewStatus;
	}
}
