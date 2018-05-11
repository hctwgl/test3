package com.ald.fanbei.api.web.validator.bean;

import java.math.BigDecimal;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Component;

@Component("recycleRepayDoParam")
public class RecycleRepayDoParam {
	@NotNull
	@DecimalMin("0")
	public BigDecimal repaymentAmount;
	
	public String payPwd;
	
	@NotNull
	public Long cardId;
	
	@Min(value=0)
	public Long borrowId;
}
