package com.ald.fanbei.api.dal.domain.dto;

public class UserDeGoodsCoupon {

    public Integer getThreshold() {
	return threshold;
    }

    public void setThreshold(Integer threshold) {
	this.threshold = threshold;
    }

    public Integer getCouponId() {
	return couponId;
    }

    public void setCouponId(Integer couponId) {
	this.couponId = couponId;
    }

    public String getCouponName() {
	return couponName;
    }

    public void setCouponName(String couponName) {
	this.couponName = couponName;
    }

    public Integer getState() {
	return state;
    }

    public void setState(Integer state) {
	this.state = state;
    }

    private Integer threshold;
    private Integer couponId;
    private String couponName;
    private Integer state;

    @Override
    public String toString() {
	return "UserDeGoodsCoupon [threshold=" + threshold + ", couponId=" + couponId + ", couponName=" + couponName + ", state=" + state + "]";
    }

}
