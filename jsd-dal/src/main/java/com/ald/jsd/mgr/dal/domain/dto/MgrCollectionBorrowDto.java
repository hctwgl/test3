package com.ald.jsd.mgr.dal.domain.dto;

import java.math.BigDecimal;
import java.util.Date;

public class MgrCollectionBorrowDto {
	private String tradeNoXgxy;
	private String realName;
	private String account;
	private String status;
	private BigDecimal repaidAmount;
	private BigDecimal unrepayAmount;
	private Long overdueDays;
	private Date gmtDue;
	private Date gmtLastRepay;
	private BigDecimal reducibleAmount;
	private BigDecimal unReducibleAmount;
	private BigDecimal reducibleRate;
	private String mobile;
	
	public String getTradeNoXgxy() {
		return tradeNoXgxy;
	}
	public void setTradeNoXgxy(String tradeNoXgxy) {
		this.tradeNoXgxy = tradeNoXgxy;
	}
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public BigDecimal getRepaidAmount() {
		return repaidAmount;
	}
	public void setRepaidAmount(BigDecimal repaidAmount) {
		this.repaidAmount = repaidAmount;
	}
	public BigDecimal getUnrepayAmount() {
		return unrepayAmount;
	}
	public void setUnrepayAmount(BigDecimal unrepayAmount) {
		this.unrepayAmount = unrepayAmount;
	}
	public Long getOverdueDays() {
		return overdueDays;
	}
	public void setOverdueDays(Long overdueDays) {
		this.overdueDays = overdueDays;
	}
	public Date getGmtDue() {
		return gmtDue;
	}
	public void setGmtDue(Date gmtDue) {
		this.gmtDue = gmtDue;
	}
	public Date getGmtLastRepay() {
		return gmtLastRepay;
	}
	public void setGmtLastRepay(Date gmtLastRepay) {
		this.gmtLastRepay = gmtLastRepay;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public BigDecimal getReducibleAmount() {
		return reducibleAmount;
	}
	public void setReducibleAmount(BigDecimal reducibleAmount) {
		this.reducibleAmount = reducibleAmount;
	}
	public BigDecimal getUnReducibleAmount() {
		return unReducibleAmount;
	}
	public void setUnReducibleAmount(BigDecimal unReducibleAmount) {
		this.unReducibleAmount = unReducibleAmount;
	}
	public BigDecimal getReducibleRate() {
		return reducibleRate;
	}
	public void setReducibleRate(BigDecimal reducibleRate) {
		this.reducibleRate = reducibleRate;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
}
