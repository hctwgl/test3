package com.ald.fanbei.api.dal.domain.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class PayResultInfoDto implements Serializable{

    private OrderInfoDto orderInfoDto;

    private List<Object> bannerList;

    private BigDecimal couponCondition;

    private BigDecimal couponAmount;

    public OrderInfoDto getOrderInfoDto() {
        return orderInfoDto;
    }

    public void setOrderInfoDto(OrderInfoDto orderInfoDto) {
        this.orderInfoDto = orderInfoDto;
    }

    public List<Object> getBannerList() {
        return bannerList;
    }

    public void setBannerList(List<Object> bannerList) {
        this.bannerList = bannerList;
    }

    public void setCouponCondition(BigDecimal couponCondition) {
        this.couponCondition = couponCondition;
    }

    public void setCouponAmount(BigDecimal couponAmount) {
        this.couponAmount = couponAmount;
    }

    public BigDecimal getCouponCondition() {
        return couponCondition;
    }

    public BigDecimal getCouponAmount() {
        return couponAmount;
    }

    @Override
    public String toString() {
        return "PayResultInfoDto{" +
                "orderInfoDto=" + orderInfoDto +
                ", bannerList=" + bannerList +
                '}';
    }
}
