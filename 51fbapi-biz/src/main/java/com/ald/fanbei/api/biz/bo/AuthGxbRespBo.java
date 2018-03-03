package com.ald.fanbei.api.biz.bo;

import java.io.Serializable;

public class AuthGxbRespBo implements Serializable{

	private static final long serialVersionUID = 8630223491731007225L;
	
	private String retCode;//返回码
	private String retMsg;//返回信息
	private String data;//返回信息
	public String getRetCode() {
		return retCode;
	}
	public void setRetCode(String retCode) {
		this.retCode = retCode;
	}
	public String getRetMsg() {
		return retMsg;
	}
	public void setRetMsg(String retMsg) {
		this.retMsg = retMsg;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	
}
