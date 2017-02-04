package com.ald.fanbei.api.dal.domain.dto;

import java.math.BigDecimal;

import com.ald.fanbei.api.dal.domain.AfUserCouponDo;

public class AfUserCouponDto extends AfUserCouponDo{

	private static final long serialVersionUID = 1833447250830899472L;

	private String		name;//优惠券名称
	
	private BigDecimal  amount;//优惠券金额
	
	private BigDecimal  limitAmount;//起始金额
	
	private String		useRule;//使用须知

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getLimitAmount() {
		return limitAmount;
	}

	public void setLimitAmount(BigDecimal limitAmount) {
		this.limitAmount = limitAmount;
	}

	public String getUseRule() {
		return useRule;
	}

	public void setUseRule(String useRule) {
		this.useRule = useRule;
	}
	
}
