package com.ald.fanbei.api.dal.domain.dto;

import java.math.BigDecimal;

public class OrderInfoDto {
    private String orderStatus;
    private BigDecimal actualAmount;
    private BigDecimal rebateAmount;
    private String goodsName;
    private String consignee;
    private String consigneeMobile;
    private String address;

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public BigDecimal getActualAmount() {
        return actualAmount;
    }

    public void setActualAmount(BigDecimal actualAmount) {
        this.actualAmount = actualAmount;
    }

    public BigDecimal getRebateAmount() {
        return rebateAmount;
    }

    public void setRebateAmount(BigDecimal rebateAmount) {
        this.rebateAmount = rebateAmount;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getConsignee() {
        return consignee;
    }

    public void setConsignee(String consignee) {
        this.consignee = consignee;
    }

    public String getConsigneeMobile() {
        return consigneeMobile;
    }

    public void setConsigneeMobile(String consigneeMobile) {
        this.consigneeMobile = consigneeMobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "OrderInfoDto{" +
                "orderStatus='" + orderStatus + '\'' +
                ", actualAmount=" + actualAmount +
                ", rebateAmount=" + rebateAmount +
                ", goodsName='" + goodsName + '\'' +
                ", consignee='" + consignee + '\'' +
                ", consigneeMobile='" + consigneeMobile + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
