package com.ald.fanbei.api.web.validator.bean;

import org.springframework.stereotype.Component;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Component("LoanAllRepayDoParam")
public class getWhiteLoanProtocolParam {
	@NotNull
	@DecimalMin("0")
	public BigDecimal repaymentAmount;
	
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
