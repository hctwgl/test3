package com.ald.fanbei.api.dal.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;

public class AfBorrowCacheAmountPerdayDo extends AbstractSerial {
	private static final long serialVersionUID = -403356284858562027L;

	private Long rid;
	private Date gmtCreate;
	private Date gmtModified;
	private Integer day;
	private BigDecimal amount;
	private Long nums;

	public Long getRid() {
		return rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public Date getGmtCreate() {
		return gmtCreate;
	}

	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}

	public Date getGmtModified() {
		return gmtModified;
	}

	public void setGmtModified(Date gmtModified) {
		this.gmtModified = gmtModified;
	}

	public Integer getDay() {
		return day;
	}

	public void setDay(Integer day) {
		this.day = day;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Long getNums() {
		return nums;
	}

	public void setNums(Long nums) {
		this.nums = nums;
	}

}
