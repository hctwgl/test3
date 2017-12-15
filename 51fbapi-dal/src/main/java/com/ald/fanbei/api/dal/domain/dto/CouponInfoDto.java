package com.ald.fanbei.api.dal.domain.dto;

import java.math.BigDecimal;

public class CouponInfoDto {
    private BigDecimal price;
    private String title;

    public BigDecimal getPrice() {
	return price;
    }

    public void setPrice(BigDecimal price) {
	this.price = price;
    }

    public String getTitle() {
	return title;
    }

    public void setTitle(String title) {
	this.title = title;
    }

    @Override
    public String toString() {
	return "CouponInfoDto [price=" + price + ", title=" + title + "]";
    }

}
