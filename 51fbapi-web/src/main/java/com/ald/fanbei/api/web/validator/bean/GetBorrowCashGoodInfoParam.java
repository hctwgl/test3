package com.ald.fanbei.api.web.validator.bean;

import java.math.BigDecimal;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class GetBorrowCashGoodInfoParam {
	
	@NotNull
	@DecimalMin("0")
	private BigDecimal borrowAmount;
	
	@NotNull
	@Pattern(regexp = "(7|14)")
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
