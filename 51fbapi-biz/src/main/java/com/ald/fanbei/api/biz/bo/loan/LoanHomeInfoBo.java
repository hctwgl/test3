package com.ald.fanbei.api.biz.bo.loan;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.ald.fanbei.api.dal.domain.AfLoanRateDo;

public class LoanHomeInfoBo {
	public String rejectCode; //拒绝码，通过则为 "PASS"
	
	public BigDecimal maxQuota;
	public BigDecimal maxPermitQuota;
	public BigDecimal minQuota;
	public List<AfLoanRateDo> loanRates;
	public int periods;
	public String prdType;
	public String prdName;
	
	public boolean hasLoan;
	public Long loanId;
	public String loanStatus;
	public BigDecimal loanAmount;
	public BigDecimal loanArrivalAmount;
	public Date loanGmtApply;
	public boolean isOverdue;
	public BigDecimal repayingAmount;
	
	public String periodIds;
	public BigDecimal periodsRestAmount;
	public BigDecimal periodsOverdueAmount;
	public Date periodsLastGmtPlanRepay;
	public String periodsStatus;
	public BigDecimal periodsUnChargeAmount;
}
