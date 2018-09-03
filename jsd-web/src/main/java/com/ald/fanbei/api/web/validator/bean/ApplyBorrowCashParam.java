package com.ald.fanbei.api.web.validator.bean;

import java.math.BigDecimal;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.jsd.JsdGoodsInfoBo;

@Component("applyBorrowCashParam")
public class ApplyBorrowCashParam {
	@NotNull
	private String productNo;
	
	@NotNull
	private String borrowNo;
	
	@NotNull
	@DecimalMin("0")
	private BigDecimal amount;
	
	@NotNull
	@Pattern(regexp = "^[0-9]*[1-9][0-9]*$")
	private String term;
	
	@NotNull
	private String unit;
	
	private String loanRemark;
	private String repayRemark;
	
	@NotNull
	private String bankNo;
	
	private String isTying;
	private String tyingType;
	
	private JsdGoodsInfoBo goodsInfo;

	public String getProductNo() {
		return productNo;
	}

	public void setProductNo(String productNo) {
		this.productNo = productNo;
	}

	public String getBorrowNo() {
		return borrowNo;
	}

	public void setBorrowNo(String borrowNo) {
		this.borrowNo = borrowNo;
	}

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

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
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

	public String getBankNo() {
		return bankNo;
	}

	public void setBankNo(String bankNo) {
		this.bankNo = bankNo;
	}

	public String getIsTying() {
		return isTying;
	}

	public void setIsTying(String isTying) {
		this.isTying = isTying;
	}

	public String getTyingType() {
		return tyingType;
	}

	public void setTyingType(String tyingType) {
		this.tyingType = tyingType;
	}

	public JsdGoodsInfoBo getGoodsInfo() {
		return goodsInfo;
	}

	public void setGoodsInfo(JsdGoodsInfoBo goodsInfo) {
		this.goodsInfo = goodsInfo;
	}
}
