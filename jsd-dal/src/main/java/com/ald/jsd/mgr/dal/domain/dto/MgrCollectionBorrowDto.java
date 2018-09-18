package com.ald.jsd.mgr.dal.domain.dto;

import java.math.BigDecimal;

public class MgrCollectionBorrowDto {
	private String tradeNoXgxy;
	private String realName;
	private String status;
	private BigDecimal repaidAmount;
	private BigDecimal unrepayAmount;
	private Long overdueDays;
	private Long gmtDue;
	private Long gmtLastRepay;
	
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
	public Long getGmtDue() {
		return gmtDue;
	}
	public void setGmtDue(Long gmtDue) {
		this.gmtDue = gmtDue;
	}
	public Long getGmtLastRepay() {
		return gmtLastRepay;
	}
	public void setGmtLastRepay(Long gmtLastRepay) {
		this.gmtLastRepay = gmtLastRepay;
	}
}
