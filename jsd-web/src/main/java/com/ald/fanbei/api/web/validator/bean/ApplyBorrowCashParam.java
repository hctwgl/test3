package com.ald.fanbei.api.web.validator.bean;

import java.math.BigDecimal;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.springframework.stereotype.Component;

@Component("applyBorrowCashParam")
public class ApplyBorrowCashParam {

	@NotNull
	@DecimalMin("0")
	private BigDecimal amount;
	@NotNull
	@Pattern(regexp = "^[0-9]*[1-9][0-9]*$")
	private String term;
	@NotNull
	private String borrowRemark;
	@NotNull
	private String refundRemark;
	@NotNull
	private String goodsName;
	@NotNull
	@DecimalMin("0")
	private BigDecimal goodsAmount;


	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public String getBorrowRemark() {
		return borrowRemark;
	}

	public void setBorrowRemark(String borrowRemark) {
		this.borrowRemark = borrowRemark;
	}

	public String getRefundRemark() {
		return refundRemark;
	}

	public void setRefundRemark(String refundRemark) {
		this.refundRemark = refundRemark;
	}

	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	public BigDecimal getGoodsAmount() {
		return goodsAmount;
	}

	public void setGoodsAmount(BigDecimal goodsAmount) {
		this.goodsAmount = goodsAmount;
	}
}
