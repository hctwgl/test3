package com.ald.fanbei.api.dal.domain.dto;

import java.math.BigDecimal;
import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;

public class BoluomeAirplaneDto {
    private Integer canCancel;
    private String channel;
    private Integer count;
    private String displayStatus;
    @JSONField(name="id")
    private String third_order_no;
    private String name;
    private BigDecimal orderPrice;
    private String orderType;
    private BigDecimal price;
    private Integer status;
    private String userId;
    private String userPhone;
    private BoluomeAirplaneContactDto contactor;
    private List<BoluomeAirplaneFlightDto> flights;
    private List<BoluomeAirplanePassengerDto> passengers;

    public Integer getCanCancel() {
	return canCancel;
    }

    public void setCanCancel(Integer canCancel) {
	this.canCancel = canCancel;
    }

    public String getChannel() {
	return channel;
    }

    public void setChannel(String channel) {
	this.channel = channel;
    }

    public BoluomeAirplaneContactDto getContactor() {
	return contactor;
    }

    public void setContactor(BoluomeAirplaneContactDto contactor) {
	this.contactor = contactor;
    }

    public Integer getCount() {
	return count;
    }

    public void setCount(Integer count) {
	this.count = count;
    }

    public String getDisplayStatus() {
	return displayStatus;
    }

    public void setDisplayStatus(String displayStatus) {
	this.displayStatus = displayStatus;
    }

    public List<BoluomeAirplaneFlightDto> getFlights() {
	return flights;
    }

    public void setFlights(List<BoluomeAirplaneFlightDto> flights) {
	this.flights = flights;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public BigDecimal getOrderPrice() {
	return orderPrice;
    }

    public void setOrderPrice(BigDecimal orderPrice) {
	this.orderPrice = orderPrice;
    }

    public String getOrderType() {
	return orderType;
    }

    public void setOrderType(String orderType) {
	this.orderType = orderType;
    }

    public List<BoluomeAirplanePassengerDto> getPassengers() {
	return passengers;
    }

    public void setPassengers(List<BoluomeAirplanePassengerDto> passengers) {
	this.passengers = passengers;
    }

    public BigDecimal getPrice() {
	return price;
    }

    public void setPrice(BigDecimal price) {
	this.price = price;
    }

    public Integer getStatus() {
	return status;
    }

    public void setStatus(Integer status) {
	this.status = status;
    }

    public String getUserId() {
	return userId;
    }

    public void setUserId(String userId) {
	this.userId = userId;
    }

    public String getUserPhone() {
	return userPhone;
    }

    public void setUserPhone(String userPhone) {
	this.userPhone = userPhone;
    }

    @Override
    public String toString() {
	return "BoluomeAirplaneDto [canCancel=" + canCancel + ", channel=" + channel + ", contactor=" + contactor + ", count=" + count + ", displayStatus=" + displayStatus + ", flights=" + flights + ", name=" + name + ", orderPrice=" + orderPrice + ", orderType=" + orderType + ", passengers=" + passengers + ", price=" + price + ", status=" + status + ", userId=" + userId + ", userPhone=" + userPhone + "]";
    }

}
