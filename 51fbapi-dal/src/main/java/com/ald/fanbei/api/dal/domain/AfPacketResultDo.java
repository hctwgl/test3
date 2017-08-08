package com.ald.fanbei.api.dal.domain;

import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;

public class AfPacketResultDo extends AbstractSerial{
	
	private static final long serialVersionUID = 1672054173720928129L;
	private Long rid;
	private Date gmtCreate;
	private Date gmtModified;
	private Long userId;
	private Long couponId;
	private String prizeDate;
	private Long gameId;
	public Long getRid() {
		return rid;
	}
	public void setRid(Long rid) {
		this.rid = rid;
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
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getCouponId() {
		return couponId;
	}
	public void setCouponId(Long couponId) {
		this.couponId = couponId;
	}
	public String getPrizeDate() {
		return prizeDate;
	}
	public void setPrizeDate(String prizeDate) {
		this.prizeDate = prizeDate;
	}
	public Long getGameId() {
		return gameId;
	}
	public void setGameId(Long gameId) {
		this.gameId = gameId;
	}
	
}
