package com.ald.fanbei.api.dal.domain.dto;

import java.io.Serializable;

public class UpsBankStatusDto implements Serializable {

    private static final long serialVersionUID = 1L;

    // 银行支付最低限额
    private Double limitDown = 0.0;
    // 银行支付最高限额
    private Double limitUp = 0.0;
    // 银行支付每日限额
    private Double dailyLimit = 0.0;
    // 银行是否维护中
    private Integer isMaintain = 0;
    // 维护起始时间 "yyyy-MM-dd HH:mm:ss"
    private String maintainStarttime = "";
    // 维护结束时间"yyyy-MM-dd HH:mm:ss"
    private String maintainEndtime = "";

    private int scale = 10000;

    public Double getLimitDown() {
	return limitDown;
    }

    public void setLimitDown(Double limitDown) {
	this.limitDown = limitDown * scale;
    }

    public Double getLimitUp() {
	return limitUp;
    }

    public void setLimitUp(Double limitUp) {
	this.limitUp = limitUp * scale;
    }

    public Double getDailyLimit() {
	return dailyLimit;
    }

    public void setDailyLimit(Double dailyLimit) {
	this.dailyLimit = dailyLimit * scale;
    }

    public Integer getIsMaintain() {
	return isMaintain;
    }

    public void setIsMaintain(Integer isMaintain) {
	this.isMaintain = isMaintain;
    }

    public String getMaintainStarttime() {
	return maintainStarttime;
    }

    public void setMaintainStarttime(String maintainStarttime) {
	this.maintainStarttime = maintainStarttime;
    }

    public String getMaintainEndtime() {
	return maintainEndtime;
    }

    public void setMaintainEndtime(String maintainEndtime) {
	this.maintainEndtime = maintainEndtime;
    }

    @Override
    public String toString() {
	return "UpsBankStatus [limitDown=" + limitDown + ", limitUp=" + limitUp + ", dailyLimit=" + dailyLimit + ", isMaintain=" + isMaintain + ", maintainStarttime=" + maintainStarttime + ", maintainEndtime=" + maintainEndtime + "]";
    }
}
