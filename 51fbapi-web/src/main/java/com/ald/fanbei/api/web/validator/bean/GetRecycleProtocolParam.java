package com.ald.fanbei.api.web.validator.bean;

import org.springframework.stereotype.Component;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Component("getRecycleProtocolParam")
public class GetRecycleProtocolParam {
	@NotNull
	@DecimalMin("0")
	public BigDecimal amount;

	@DecimalMin("0")
	public BigDecimal interestRate;

	@DecimalMin("0")
	public BigDecimal overdueRate;
	
	@Min(value=0)
	public Long borrowId;

	@DecimalMin("0")
	public BigDecimal totalServiceFee;
}
