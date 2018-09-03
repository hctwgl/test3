package com.ald.fanbei.api.web.validator.bean;

import java.math.BigDecimal;

import javax.validation.constraints.DecimalMin;
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
	private String loanRemark;
	@NotNull
	private String repayRemark;

	private String goodsName;

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

	public String getLoanRemark() {
		return loanRemark;
	}

	public void setLoanRemark(String loanRemark) {
		this.loanRemark = loanRemark;
	}

	public String getRepayRemark() {
		return repayRemark;
	}

	public void setRepayRemark(String repayRemark) {
		this.repayRemark = repayRemark;
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
