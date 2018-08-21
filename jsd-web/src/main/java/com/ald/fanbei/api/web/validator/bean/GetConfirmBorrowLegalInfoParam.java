package com.ald.fanbei.api.web.validator.bean;

import java.math.BigDecimal;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.springframework.stereotype.Component;

@Component("getConfirmBorrowLegalInfoParam")
public class GetConfirmBorrowLegalInfoParam {
	
	@DecimalMin("0")
	@NotNull
	private BigDecimal amount;
	
	@NotNull
	@Pattern(regexp="^[0-9]*[1-9][0-9]*$")

	private String type;
	
	@NotNull
	@Min(0)
	private Long goodsId;
	
	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Long getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(Long goodsId) {
		this.goodsId = goodsId;
	}

	
}
