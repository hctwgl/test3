package com.ald.fanbei.api.dal.domain.dto;

import java.util.List;

public class PayResultInfoDto {

    private OrderInfoDto orderInfoDto;

    private List<Object> bannerList;

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

    @Override
    public String toString() {
        return "PayResultInfoDto{" +
                "orderInfoDto=" + orderInfoDto +
                ", bannerList=" + bannerList +
                '}';
    }
}
