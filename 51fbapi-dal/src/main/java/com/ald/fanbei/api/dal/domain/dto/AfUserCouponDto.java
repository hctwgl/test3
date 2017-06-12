package com.ald.fanbei.api.dal.domain.dto;

import java.math.BigDecimal;

import com.ald.fanbei.api.dal.domain.AfUserCouponDo;

public class AfUserCouponDto extends AfUserCouponDo{

	private static final long serialVersionUID = 1833447250830899472L;

	private String		name;//优惠券名称
	
	private BigDecimal  amount;//优惠券金额
	
	private BigDecimal  limitAmount;//起始金额
	
	private String		useRule;//使用须知
	private Integer validDays;//
	private String		willExpireStatus;//即将过去状态：Y：即将过期，N：没有即将过期


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

	/**
	 * @return the validDays
	 */
	public Integer getValidDays() {
		return validDays;
	}

	/**
	 * @param validDays the validDays to set
	 */
	public void setValidDays(Integer validDays) {
		this.validDays = validDays;
	}

	/**
	 * @return the willExpireStatus
	 */
	public String getWillExpireStatus() {
		return willExpireStatus;
	}

	/**
	 * @param willExpireStatus the willExpireStatus to set
	 */
	public void setWillExpireStatus(String willExpireStatus) {
		this.willExpireStatus = willExpireStatus;
	}
	
}
