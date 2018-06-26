package com.ald.fanbei.api.web.validator.bean;

import org.springframework.stereotype.Component;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Component("dsedApplyLoanParam")
public class DsedApplyLoanParam {

	public String prdType; 
	
	@DecimalMin("0")
	public BigDecimal amount;
	
	@Min(value=1)
	public int period;
	
	public String remark;
	
	public String loanRemark;
	
	@NotNull
	public String repayRemark;
}
