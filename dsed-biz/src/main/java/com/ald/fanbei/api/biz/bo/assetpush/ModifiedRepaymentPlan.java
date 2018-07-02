package com.ald.fanbei.api.biz.bo.assetpush;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 退货更新债权的还款计划
 * @author wujun
 * @version 1.0.0 初始化
 * @date 2017年12月15日下午4:57:18
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */

public class ModifiedRepaymentPlan implements Serializable{
	
	private String repaymentNo;//还款编号
	private BigDecimal repaymentAmount;//还款本金
	private BigDecimal repaymentInterest;//还款利息
	private Integer repaymentStatus;//还款状态(0:未还[Default],1:已还)
	private Integer repaymentYesTime;//还款时间(还款状态为1时必传)
	private Integer isOverdue;//是否逾期(0:否[Default],1:是)
	private Integer isPrepayment;//是否提前还款(0:否[Default],1:是)
	private Integer repaymentPeriod;//还款期数(可选)
	public String getRepaymentNo() {
		return repaymentNo;
	}
	public void setRepaymentNo(String repaymentNo) {
		this.repaymentNo = repaymentNo;
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
	public Integer getRepaymentStatus() {
		return repaymentStatus;
	}
	public void setRepaymentStatus(Integer repaymentStatus) {
		this.repaymentStatus = repaymentStatus;
	}
	public Integer getRepaymentYesTime() {
		return repaymentYesTime;
	}
	public void setRepaymentYesTime(Integer repaymentYesTime) {
		this.repaymentYesTime = repaymentYesTime;
	}
	public Integer getIsOverdue() {
		return isOverdue;
	}
	public void setIsOverdue(Integer isOverdue) {
		this.isOverdue = isOverdue;
	}
	public Integer getIsPrepayment() {
		return isPrepayment;
	}
	public void setIsPrepayment(Integer isPrepayment) {
		this.isPrepayment = isPrepayment;
	}
	public Integer getRepaymentPeriod() {
		return repaymentPeriod;
	}
	public void setRepaymentPeriod(Integer repaymentPeriod) {
		this.repaymentPeriod = repaymentPeriod;
	}
	
		
}
