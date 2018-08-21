package com.ald.fanbei.api.web.validator.bean;

import java.math.BigDecimal;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.springframework.stereotype.Component;

@Component("getBorrowCashGoodInfoParam")
public class GetBorrowCashGoodInfoParam {
	
	@NotNull
	@DecimalMin("0")
	private BigDecimal borrowAmount;
	
	@NotNull
	@Pattern(regexp = "^[0-9]*[1-9][0-9]*$")
	private String  borrowType;

	public BigDecimal getBorrowAmount() {
		return borrowAmount;
	}

	public void setBorrowAmount(BigDecimal borrowAmount) {
		this.borrowAmount = borrowAmount;
	}

	public String getBorrowType() {
		return borrowType;
	}

	public void setBorrowType(String borrowType) {
		this.borrowType = borrowType;
	}
}
