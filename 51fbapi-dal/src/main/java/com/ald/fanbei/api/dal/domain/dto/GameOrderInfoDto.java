package com.ald.fanbei.api.dal.domain.dto;

import java.math.BigDecimal;

public class GameOrderInfoDto {

    public String getOrderNo() {
	return orderNo;
    }

    public void setOrderNo(String orderNo) {
	this.orderNo = orderNo;
    }

    public String getOrderStatus() {
	return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
	this.orderStatus = orderStatus;
    }

    public String getOrderType() {
	return orderType;
    }

    public void setOrderType(String orderType) {
	this.orderType = orderType;
    }

    public String getGoodsIcon() {
	return goodsIcon;
    }

    public void setGoodsIcon(String goodsIcon) {
	this.goodsIcon = goodsIcon;
    }

    public String getGoodsName() {
	return goodsName;
    }

    public void setGoodsName(String goodsName) {
	this.goodsName = goodsName;
    }

    public Integer getGoodsCount() {
	return goodsCount;
    }

    public void setGoodsCount(Integer goodsCount) {
	this.goodsCount = goodsCount;
    }

    public String getAcctType() {
	return acctType;
    }

    public void setAcctType(String acctType) {
	this.acctType = acctType;
    }

    public String getUserName() {
	return userName;
    }

    public void setUserName(String userName) {
	this.userName = userName;
    }

    public String getGameType() {
	return gameType;
    }

    public void setGameType(String gameType) {
	this.gameType = gameType;
    }

    public String getGameAcct() {
	return gameAcct;
    }

    public void setGameAcct(String gameAcct) {
	this.gameAcct = gameAcct;
    }

    public String getGameArea() {
	return gameArea;
    }

    public void setGameArea(String gameArea) {
	this.gameArea = gameArea;
    }

    public String getGameSrv() {
	return gameSrv;
    }

    public void setGameSrv(String gameSrv) {
	this.gameSrv = gameSrv;
    }

    public BigDecimal getOrderAmount() {
	return orderAmount;
    }

    public void setOrderAmount(BigDecimal orderAmount) {
	this.orderAmount = orderAmount;
    }

    public BigDecimal getRebateAmount() {
	return rebateAmount;
    }

    public void setRebateAmount(BigDecimal rebateAmount) {
	this.rebateAmount = rebateAmount;
    }

    public BigDecimal getCouponAmount() {
	return couponAmount;
    }

    public void setCouponAmount(BigDecimal couponAmount) {
	this.couponAmount = couponAmount;
    }

    public BigDecimal getActualAmount() {
	return actualAmount;
    }

    public void setActualAmount(BigDecimal actualAmount) {
	this.actualAmount = actualAmount;
    }

    public Long getGmtCreate() {
	return gmtCreate;
    }

    public void setGmtCreate(Long gmtCreate) {
	this.gmtCreate = gmtCreate;
    }

    public Long getGmtRebated() {
	return gmtRebated;
    }

    public void setGmtRebated(Long gmtRebated) {
	this.gmtRebated = gmtRebated;
    }

    public Long getGmtClosed() {
	return gmtClosed;
    }

    public void setGmtClosed(Long gmtClosed) {
	this.gmtClosed = gmtClosed;
    }

    public Long getGmtPay() {
	return gmtPay;
    }

    public void setGmtPay(Long gmtPay) {
	this.gmtPay = gmtPay;
    }

    public Long getGmtPayStart() {
	return gmtPayStart;
    }

    public void setGmtPayStart(Long gmtPayStart) {
	this.gmtPayStart = gmtPayStart;
    }

    public Long getGmtPayEnd() {
	return gmtPayEnd;
    }

    public void setGmtPayEnd(Long gmtPayEnd) {
	this.gmtPayEnd = gmtPayEnd;
    }

    public Long getOrderId() {
	return orderId;
    }

    public void setOrderId(Long orderId) {
	this.orderId = orderId;
    }

    public String getPlantform() {
	return plantform;
    }

    public void setPlantform(String plantform) {
	this.plantform = plantform;
    }

    private Long orderId;
    private String orderNo;
    private String orderStatus;
    private String orderType;
    private String goodsIcon;
    private String goodsName;
    private Integer goodsCount;
    private String acctType;
    private String userName;
    private String gameType;
    private String gameAcct;
    private String gameArea;
    private String gameSrv;
    private BigDecimal orderAmount;
    private BigDecimal rebateAmount;
    private BigDecimal couponAmount;
    private BigDecimal actualAmount;

    private Long gmtCreate;
    private Long gmtRebated;
    private Long gmtClosed;
    private Long gmtPay;
    private Long gmtPayStart = System.currentTimeMillis() / 1000;
    private Long gmtPayEnd;

    private String plantform;

    @Override
    public String toString() {
	return "GameOrderInfoDto [orderId=" + orderId + ", orderNo=" + orderNo + ", orderStatus=" + orderStatus + ", orderType=" + orderType + ", goodsIcon=" + goodsIcon + ", goodsName=" + goodsName + ", goodsCount=" + goodsCount + ", acctType=" + acctType + ", userName=" + userName + ", gameType=" + gameType + ", gameAcct=" + gameAcct + ", gameArea=" + gameArea + ", gameSrv=" + gameSrv + ", orderAmount=" + orderAmount + ", rebateAmount=" + rebateAmount + ", couponAmount=" + couponAmount + ", actualAmount=" + actualAmount + ", gmtCreate=" + gmtCreate + ", gmtRebated=" + gmtRebated + ", gmtClosed=" + gmtClosed + ", gmtPay=" + gmtPay + ", gmtPayStart=" + gmtPayStart + ", gmtPayEnd=" + gmtPayEnd + ", plantform=" + plantform + "]";
    }
}
