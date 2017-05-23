package com.ald.fanbei.api.dal.domain;

import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;

public class AfAuthYdDo extends AbstractSerial {
	private static final long serialVersionUID = 8072133949309884953L;
	
	private Long id;
	private Date gmtCreate;
	private Long userId;
	private String type;
	private String authParam;
	private String authResult;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Date getGmtCreate() {
		return gmtCreate;
	}
	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getAuthParam() {
		return authParam;
	}
	public void setAuthParam(String authParam) {
		this.authParam = authParam;
	}
	public String getAuthResult() {
		return authResult;
	}
	public void setAuthResult(String authResult) {
		this.authResult = authResult;
	}
	
}
