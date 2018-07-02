package com.ald.fanbei.api.dal.domain.dto;

import java.math.BigDecimal;

import com.ald.fanbei.api.common.AbstractSerial;

public class BoluomeCouponDto extends AbstractSerial {
    /**
     * @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么)
     */
    private static final long serialVersionUID = 1L;

    private CouponInfoDto coupon;
    private String couponId;

    /**
     * 优惠劵金额
     */
    private BigDecimal couponPrice;

    /**
     * 优惠卷描述
     */
    private String couponTitle;

    public CouponInfoDto getCoupon() {
	return coupon;
    }

    public void setCoupon(CouponInfoDto coupon) {
	this.coupon = coupon;
	if (coupon != null) {
	    this.couponPrice = this.coupon.getPrice();
	    this.couponTitle = this.coupon.getTitle();
	}
    }

    public String getCouponId() {
	return couponId;
    }

    public void setCouponId(String couponId) {
	this.couponId = couponId;
    }

    /**
     * 获取优惠劵金额
     *
     * @return 优惠劵金额
     */
    public BigDecimal getCouponPrice() {
	return this.couponPrice;
    }

    /**
     * 设置优惠劵金额
     * 
     * @param couponPrice
     *            要设置的优惠劵金额
     */
    public void setCouponPrice(BigDecimal couponPrice) {
	this.couponPrice = couponPrice;
    }

    /**
     * 获取优惠卷描述
     *
     * @return 优惠卷描述
     */
    public String getCouponTitle() {
	return this.couponTitle;
    }

    /**
     * 设置优惠卷描述
     * 
     * @param couponTitle
     *            要设置的优惠卷描述
     */
    public void setCouponTitle(String couponTitle) {
	this.couponTitle = couponTitle;
    }

}
