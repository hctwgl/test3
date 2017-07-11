package com.ald.fanbei.api.dal.domain;

import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;

public class AfGameConfDo extends AbstractSerial {
	private static final long serialVersionUID = 5379780003038446731L;
	private Long rid;
	private Date gmtCreate;
	private Date gmtModified;
	private String creator;
	private String modifier;
	private Long gameId;
	private String type;
	private Date gmtSend;
	private String rule;
	
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
	public Long getGameId() {
		return gameId;
	}
	public void setGameId(Long gameId) {
		this.gameId = gameId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Date getGmtSend() {
		return gmtSend;
	}
	public void setGmtSend(Date gmtSend) {
		this.gmtSend = gmtSend;
	}
	public String getRule() {
		return rule;
	}
	public void setRule(String rule) {
		this.rule = rule;
	}
	
	
}
