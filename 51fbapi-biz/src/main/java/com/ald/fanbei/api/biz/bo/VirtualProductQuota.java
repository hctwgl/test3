package com.ald.fanbei.api.biz.bo;

import java.math.BigDecimal;

public class VirtualProductQuota {
    BigDecimal amount;
    BigDecimal recentDay;
    BigDecimal totalAmount;
    BigDecimal dayAmount;

    public BigDecimal getAmount() {
	return amount;
    }

    public BigDecimal getRecentDay() {
	return recentDay;
    }

    public BigDecimal getTotalAmount() {
	return totalAmount;
    }

    public void setAmount(BigDecimal amount) {
	this.amount = amount;
    }

    public void setRecentDay(BigDecimal recentDay) {
	this.recentDay = recentDay;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
	this.totalAmount = totalAmount;
    }

    public BigDecimal getDayAmount() {
	return dayAmount;
    }

    public void setDayAmount(BigDecimal dayAmount) {
	this.dayAmount = dayAmount;
    }

    @Override
    public String toString() {
	return "VirtualProductQuota [amount=" + amount + ", recentDay=" + recentDay + ", totalAmount=" + totalAmount + ", dayAmount=" + dayAmount + "]";
    }

}
