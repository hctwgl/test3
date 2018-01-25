package com.ald.fanbei.api.biz.bo.loan;

import java.math.BigDecimal;
import java.util.Date;

public class LoanHomeInfoBo {
	public String rejectCode; //拒绝码，通过则为 "PASS"
	
	public boolean isLogin;
	public BigDecimal maxQuota;
	public BigDecimal minQuota;
	public BigDecimal interestRate;
	public BigDecimal poundageRate;
	public BigDecimal overdueRate;
	public int periods;
	public String prdType;
	public String prdName;
	
	public boolean hasLoan;
	public Long loanId;
	public String loanStatus;
	public BigDecimal loanAmount;
	public BigDecimal loanArrivalAmount;
	public Date loanGmtApply;
	
	public Long curPeriodId;
	public BigDecimal curPeriodAmount;
	public BigDecimal curPeriodRestAmount;
	public Date curPeriodGmtPlanRepay;
	public boolean isOverdue;
	
}
