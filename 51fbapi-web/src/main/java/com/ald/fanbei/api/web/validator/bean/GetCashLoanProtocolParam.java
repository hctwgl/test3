package com.ald.fanbei.api.web.validator.bean;

import org.springframework.stereotype.Component;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;

@Component("getCashLoanProtocolParam")
public class GetCashLoanProtocolParam {

	@NotNull
	@DecimalMin("0")
	private BigDecimal borrowAmount;
	@NotNull
	@Pattern(regexp = "(7|14)")
	private String type;
	@NotNull
	private BigDecimal poundage;

	public BigDecimal getBorrowAmount() {
		return borrowAmount;
	}

	public void setBorrowAmount(BigDecimal borrowAmount) {
		this.borrowAmount = borrowAmount;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public BigDecimal getPoundage() {
		return poundage;
	}

	public void setPoundage(BigDecimal poundage) {
		this.poundage = poundage;
	}
}
