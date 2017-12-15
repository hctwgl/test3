package com.ald.fanbei.api.biz.bo.assetside.edspay;

import java.io.Serializable;
import java.math.BigDecimal;

public class RepaymentPlan implements Serializable{
	private static final long serialVersionUID = 4204652534348461359L;
	private Long repaymentTime;//还款时间
	private Long repaymentDays;//还款天数(距借款开始时间的天数)
	private BigDecimal repaymentAmount;//还款本金
	private BigDecimal repaymentInterest;//还款利息
	private Integer repaymentPeriod;//当前期数(从0开始)
	public Long getRepaymentTime() {
		return repaymentTime;
	}
	public void setRepaymentTime(Long repaymentTime) {
		this.repaymentTime = repaymentTime;
	}
	public Long getRepaymentDays() {
		return repaymentDays;
	}
	public void setRepaymentDays(Long repaymentDays) {
		this.repaymentDays = repaymentDays;
	}
	public BigDecimal getRepaymentAmount() {
		return repaymentAmount;
	}
	public void setRepaymentAmount(BigDecimal repaymentAmount) {
		this.repaymentAmount = repaymentAmount;
	}
	public BigDecimal getRepaymentInterest() {
		return repaymentInterest;
	}
	public void setRepaymentInterest(BigDecimal repaymentInterest) {
		this.repaymentInterest = repaymentInterest;
	}
	public Integer getRepaymentPeriod() {
		return repaymentPeriod;
	}
	public void setRepaymentPeriod(Integer repaymentPeriod) {
		this.repaymentPeriod = repaymentPeriod;
	}
	
}
