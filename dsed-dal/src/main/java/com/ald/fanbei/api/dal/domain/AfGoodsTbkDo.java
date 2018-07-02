package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;

import java.math.BigDecimal;
import java.util.Date;

public class AfGoodsTbkDo extends AbstractSerial {


	private static final long serialVersionUID = 1741417239782937594L;

	private Long rid;
	private String numId;
	private Date gmtCreate;
	private Date gmtModified;
	private String creator;
	private String modifier;
	private String couponClickUrl;
	private Date couponStartTime;
	private Date couponEndTime;
	private BigDecimal couponInfo;
	private Long couponRemainCount;
	private Long couponTotalCount;
	private Date eventStartTime;
	private Date eventEndTime;
	private String nick;
	private String provcity;
	private String sellerId;
	private Integer type;
	private Integer userType;
	private Long volume;
	private BigDecimal zkFinalPrice;
	private BigDecimal zkFinalPrice_wap;
	private String libId;
	private Long categoryId;
	private Long sort;

	public Long getRid() {
		return rid;
	}

	public void setRid(Long Rid) {
		this.rid = rid;
	}

	public String getNumId() {
		return numId;
	}

	public void setNumId(String numId) {
		this.numId = numId;
	}

	public Date getGmtCreate() {
		return gmtCreate;
	}

	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}

	public Date getGmtModified() {
		return gmtModified;
	}

	public void setGmtModified(Date gmtModified) {
		this.gmtModified = gmtModified;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getModifier() {
		return modifier;
	}

	public void setModifier(String modifier) {
		this.modifier = modifier;
	}

	public String getCouponClickUrl() {
		return couponClickUrl;
	}

	public void setCouponClickUrl(String couponClickUrl) {
		this.couponClickUrl = couponClickUrl;
	}

	public Date getCouponStartTime() {
		return couponStartTime;
	}

	public void setCouponStartTime(Date couponStartTime) {
		this.couponStartTime = couponStartTime;
	}

	public Date getCouponEndTime() {
		return couponEndTime;
	}

	public void setCouponEndTime(Date couponEndTime) {
		this.couponEndTime = couponEndTime;
	}

	public BigDecimal getCouponInfo() {
		return couponInfo;
	}

	public void setCouponInfo(BigDecimal couponInfo) {
		this.couponInfo = couponInfo;
	}

	public Long getCouponRemainCount() {
		return couponRemainCount;
	}

	public void setCouponRemainCount(Long couponRemainCount) {
		this.couponRemainCount = couponRemainCount;
	}

	public Long getCouponTotalCount() {
		return couponTotalCount;
	}

	public void setCouponTotalCount(Long couponTotalCount) {
		this.couponTotalCount = couponTotalCount;
	}

	public Date getEventStartTime() {
		return eventStartTime;
	}

	public void setEventStartTime(Date eventStartTime) {
		this.eventStartTime = eventStartTime;
	}

	public Date getEventEndTime() {
		return eventEndTime;
	}

	public void setEventEndTime(Date eventEndTime) {
		this.eventEndTime = eventEndTime;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getProvcity() {
		return provcity;
	}

	public void setProvcity(String provcity) {
		this.provcity = provcity;
	}

	public String getSellerId() {
		return sellerId;
	}

	public void setSellerId(String sellerId) {
		this.sellerId = sellerId;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getUserType() {
		return userType;
	}

	public void setUserType(Integer userType) {
		this.userType = userType;
	}

	public Long getVolume() {
		return volume;
	}

	public void setVolume(Long volume) {
		this.volume = volume;
	}

	public BigDecimal getZkFinalPrice() {
		return zkFinalPrice;
	}

	public void setZkFinalPrice(BigDecimal zkFinalPrice) {
		this.zkFinalPrice = zkFinalPrice;
	}

	public BigDecimal getZkFinalPrice_wap() {
		return zkFinalPrice_wap;
	}

	public void setZkFinalPrice_wap(BigDecimal zkFinalPrice_wap) {
		this.zkFinalPrice_wap = zkFinalPrice_wap;
	}

	public String getLibId() {
		return libId;
	}

	public void setLibId(String libId) {
		this.libId = libId;
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public Long getSort() {
		return sort;
	}

	public void setSort(Long sort) {
		this.sort = sort;
	}
}
