package com.ald.fanbei.api.web.vo.afu;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 致诚阿福
 * @author chenxuankai 2017年11月22日18:23:18
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class LoanRecord implements Serializable {
	
	private String name;//姓名
	private String certNo;//身份证号
	private String loanDate;//借款时间
	private Integer periods;//期数，默认取1	
	private BigDecimal loanAmount;//借款金额	
	private String approvalStatusCode;//审批结果码 枚举
	private String loanStatusCode;//还款状态码 枚举
	private String loanTypeCode;//借款类型码 枚举
	private BigDecimal overdueAmount;//逾期金额
	private String overdueStatus;//逾期情况 枚举
	private Integer overdueTotal;//历史逾期总次数，如果没有逾期，就不写，不能写0 
	private Integer overdueM3;//历史逾期次数M3+次数以上，没有就不写，不能写0
	private Integer overdueM6;//历史逾期次数M6+次数以上，没有就不写，不能写0
	
	public LoanRecord() {}

	public LoanRecord(String name, String certNo, String loanDate,
			Integer periods, BigDecimal loanAmount, String approvalStatusCode,
			String loanStatusCode, String loanTypeCode, BigDecimal overdueAmount,
			String overdueStatus, Integer overdueTotal, Integer overdueM3,
			Integer overdueM6) {
		this.name = name;
		this.certNo = certNo;
		this.loanDate = loanDate;
		this.periods = periods;
		this.loanAmount = loanAmount;
		this.approvalStatusCode = approvalStatusCode;
		this.loanStatusCode = loanStatusCode;
		this.loanTypeCode = loanTypeCode;
		this.overdueAmount = overdueAmount;
		this.overdueStatus = overdueStatus;
		this.overdueTotal = overdueTotal;
		this.overdueM3 = overdueM3;
		this.overdueM6 = overdueM6;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCertNo() {
		return certNo;
	}

	public void setCertNo(String certNo) {
		this.certNo = certNo;
	}

	public String getLoanDate() {
		return loanDate;
	}

	public void setLoanDate(String loanDate) {
		this.loanDate = loanDate;
	}

	public Integer getPeriods() {
		return periods;
	}

	public void setPeriods(Integer periods) {
		this.periods = periods;
	}

	public BigDecimal getLoanAmount() {
		return loanAmount;
	}

	public void setLoanAmount(BigDecimal loanAmount) {
		this.loanAmount = loanAmount;
	}

	public String getApprovalStatusCode() {
		return approvalStatusCode;
	}

	public void setApprovalStatusCode(String approvalStatusCode) {
		this.approvalStatusCode = approvalStatusCode;
	}

	public String getLoanStatusCode() {
		return loanStatusCode;
	}

	public void setLoanStatusCode(String loanStatusCode) {
		this.loanStatusCode = loanStatusCode;
	}

	public String getLoanTypeCode() {
		return loanTypeCode;
	}

	public void setLoanTypeCode(String loanTypeCode) {
		this.loanTypeCode = loanTypeCode;
	}

	public BigDecimal getOverdueAmount() {
		return overdueAmount;
	}

	public void setOverdueAmount(BigDecimal overdueAmount) {
		this.overdueAmount = overdueAmount;
	}

	public String getOverdueStatus() {
		return overdueStatus;
	}

	public void setOverdueStatus(String overdueStatus) {
		this.overdueStatus = overdueStatus;
	}

	public Integer getOverdueTotal() {
		return overdueTotal;
	}

	public void setOverdueTotal(Integer overdueTotal) {
		this.overdueTotal = overdueTotal;
	}

	public Integer getOverdueM3() {
		return overdueM3;
	}

	public void setOverdueM3(Integer overdueM3) {
		this.overdueM3 = overdueM3;
	}

	public Integer getOverdueM6() {
		return overdueM6;
	}

	public void setOverdueM6(Integer overdueM6) {
		this.overdueM6 = overdueM6;
	}

	
	
}
