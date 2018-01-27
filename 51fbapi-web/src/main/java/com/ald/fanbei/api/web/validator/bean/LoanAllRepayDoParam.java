package com.ald.fanbei.api.web.validator.bean;

import java.math.BigDecimal;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Component;

@Component("LoanRepayDoParam")
public class LoanAllRepayDoParam {
	@NotNull
	@DecimalMin("0")
	public BigDecimal currentPeriodAmount;
	
	@NotNull
	@DecimalMin("0")
	public BigDecimal actualAmount = BigDecimal.ZERO;
	
	@DecimalMin("0")
	public BigDecimal rebateAmount = BigDecimal.ZERO;
	
	@NotNull
	public String payPwd;
	
	@NotNull
	public Long cardId;
	
	@Min(value=0)
	public Long couponId;
	
	@Min(value=0)
	public Long loanId;
}
