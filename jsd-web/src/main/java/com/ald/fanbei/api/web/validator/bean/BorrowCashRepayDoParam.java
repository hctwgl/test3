package com.ald.fanbei.api.web.validator.bean;

import java.math.BigDecimal;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Component;

@Component("borrowCashRepayDoParam")
public class BorrowCashRepayDoParam {
	@NotNull
	@DecimalMin("0")
	public BigDecimal amount = BigDecimal.ZERO;

	@NotNull
	public String bankNo;

	@NotNull
	public String borrowNo;

	@NotNull
	public String repayNo;

	@NotNull
	//@DecimalMin("0")
	public String period;


	public String timestamp;


}
