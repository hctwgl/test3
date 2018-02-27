package com.ald.fanbei.api.web.validator.bean;

import org.springframework.stereotype.Component;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Component("getWhiteLoanProtocolParam")
public class GetWhiteLoanProtocolParam {
	@NotNull
	@DecimalMin("0")
	public BigDecimal amount;

	public String loanRemark;

	public String repayRemark;

	@DecimalMin("0")
	public BigDecimal interestRate;

	@DecimalMin("0")
	public BigDecimal serviceRate;

	@DecimalMin("0")
	public BigDecimal overdueRate;
	
	@Min(value=0)
	public Integer nper;
	
	@Min(value=0)
	public Long loanId;

	@NotNull
	@DecimalMin("0")
	public BigDecimal totalServiceFee;
}
