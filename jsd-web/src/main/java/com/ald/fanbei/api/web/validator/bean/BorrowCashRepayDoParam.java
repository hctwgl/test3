package com.ald.fanbei.api.web.validator.bean;

import org.springframework.stereotype.Component;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Component("LoanRepayDoParam")
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
	@DecimalMin("0")
	public int period;

	@NotNull
	public Long timestamp;


}
