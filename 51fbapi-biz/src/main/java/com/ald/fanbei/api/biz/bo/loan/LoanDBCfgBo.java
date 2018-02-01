package com.ald.fanbei.api.biz.bo.loan;

import java.math.BigDecimal;

public class LoanDBCfgBo {
	public String switch_ = "Y";
	public BigDecimal maxQuota = BigDecimal.valueOf(50000);
	public BigDecimal minQuota = BigDecimal.valueOf(500);
	public String interestRate = "0.020";
	public String poundageRate = "0.020";
	public String overdueRate = "0.36";
}
