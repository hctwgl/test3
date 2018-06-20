package com.ald.fanbei.api.web.validator.bean;

import java.math.BigDecimal;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Component;

@Component("LoanRepayDoParam")
public class LoanRepayDoParam {
	@Min(value=0)
	public Long userId;

	@NotNull
	@DecimalMin("0")
	public BigDecimal amount = BigDecimal.ZERO;

	@NotNull
	public String bankNo;

	@NotNull
	public String borrowNo;

	@NotNull
	@DecimalMin("0")
	public int curPeriod;


}
