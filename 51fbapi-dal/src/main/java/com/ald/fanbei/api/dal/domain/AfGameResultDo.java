package com.ald.fanbei.api.dal.domain;

import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;

public class AfGameResultDo extends AbstractSerial{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1672054173720928129L;
	private Long rid;
	private Date gmtCreate;
	private Date gmtModified;
	private Long userId;
	private Long gameId;
	private String userName;
	private String userAvata;
	private String result;
	private String item;
	private String code;
	private Long lotteryResult;
	public Long getRid() {
		return rid;
	}
	public void setRid(Long rid) {
		this.rid = rid;
	}
	
	public Long getGameId() {
		return gameId;
	}
	public void setGameId(Long gameId) {
		this.gameId = gameId;
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
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserAvata() {
		return userAvata;
	}
	public void setUserAvata(String userAvata) {
		this.userAvata = userAvata;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getItem() {
		return item;
	}
	public void setItem(String item) {
		this.item = item;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public Long getLotteryResult() {
		return lotteryResult;
	}
	public void setLotteryResult(Long lotteryResult) {
		this.lotteryResult = lotteryResult;
	}
	
	
}
