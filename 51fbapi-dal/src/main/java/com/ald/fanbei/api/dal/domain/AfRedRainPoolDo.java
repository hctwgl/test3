package com.ald.fanbei.api.dal.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;

public class AfRedRainPoolDo extends AbstractSerial{
	private static final long serialVersionUID = 4877072582010412953L;
	
	private Integer id;
	private Integer num;
	private Long couponId;
	private String couponName;
	private String couponType;
	private Integer amount;
	private Date gmtCreate;
	private Date gmtModified;
	private String creator;
	private String modifier;
	private Boolean isDelete;

	private String couponEffectiveTime;
	private BigDecimal couponLimitAmount;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getNum() {
		return num;
	}
	public void setNum(Integer num) {
		this.num = num;
	}
	public Long getCouponId() {
		return couponId;
	}
	public void setCouponId(Long couponId) {
		this.couponId = couponId;
	}
	public String getCouponName() {
		return couponName;
	}
	public void setCouponName(String couponName) {
		this.couponName = couponName;
	}
	public String getCouponType() {
		return couponType;
	}
	public void setCouponType(String couponType) {
		this.couponType = couponType;
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
	public Boolean getIsDelete() {
		return isDelete;
	}
	public void setIsDelete(Boolean isDelete) {
		this.isDelete = isDelete;
	}
	public Integer getAmount() {
		return amount;
	}
	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	public String getCouponEffectiveTime() {
		return couponEffectiveTime;
	}

	public void setCouponEffectiveTime(String couponEffectiveTime) {
		this.couponEffectiveTime = couponEffectiveTime;
	}

	public BigDecimal getCouponLimitAmount() {
		return couponLimitAmount;
	}

	public void setCouponLimitAmount(BigDecimal couponLimitAmount) {
		this.couponLimitAmount = couponLimitAmount;
	}
}
