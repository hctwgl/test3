package com.ald.fanbei.api.dal.domain.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.ald.fanbei.api.dal.domain.AfBorrowDo;

public class AfBorrowDto extends AfBorrowDo {

	private static final long serialVersionUID = 7506645755979915414L;
	
	private Date payDate;
	
	private BigDecimal priceAmount;
	
	private BigDecimal bankAmount;
	
	private BigDecimal saleAmount;

	public Date getPayDate() {
		return payDate;
	}

	public void setPayDate(Date payDate) {
		this.payDate = payDate;
	}

	public BigDecimal getPriceAmount() {
		return priceAmount;
	}

	public void setPriceAmount(BigDecimal priceAmount) {
		this.priceAmount = priceAmount;
	}

	public BigDecimal getBankAmount() {
		return bankAmount;
	}

	public void setBankAmount(BigDecimal bankAmount) {
		this.bankAmount = bankAmount;
	}

	public BigDecimal getSaleAmount() {
		return saleAmount;
	}

	public void setSaleAmount(BigDecimal saleAmount) {
		this.saleAmount = saleAmount;
	}
}
