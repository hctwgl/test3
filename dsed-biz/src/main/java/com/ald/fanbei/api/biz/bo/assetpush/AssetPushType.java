package com.ald.fanbei.api.biz.bo.assetpush;

import java.io.Serializable;
import java.util.List;
/**
 * 债权推送的类型
 * @author wujun
 * @version 1.0.0 初始化
 * @date 2018年2月27日下午2:52:20
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AssetPushType implements Serializable {

	private static final long serialVersionUID = 4347678991772430075L;
	String borrowCash;
	String collar;
	String trade;
	String boluome;
	String agencyBuy;
	String selfSupport;
	String tenement;
	public String getBorrowCash() {
		return borrowCash;
	}
	public void setBorrowCash(String borrowCash) {
		this.borrowCash = borrowCash;
	}
	public String getCollar() {
		return collar;
	}
	public void setCollar(String collar) {
		this.collar = collar;
	}
	public String getTrade() {
		return trade;
	}
	public void setTrade(String trade) {
		this.trade = trade;
	}
	public String getBoluome() {
		return boluome;
	}
	public void setBoluome(String boluome) {
		this.boluome = boluome;
	}
	public String getAgencyBuy() {
		return agencyBuy;
	}
	public void setAgencyBuy(String agencyBuy) {
		this.agencyBuy = agencyBuy;
	}
	public String getSelfSupport() {
		return selfSupport;
	}
	public void setSelfSupport(String selfSupport) {
		this.selfSupport = selfSupport;
	}
	public String getTenement() {
		return tenement;
	}
	public void setTenement(String tenement) {
		this.tenement = tenement;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
