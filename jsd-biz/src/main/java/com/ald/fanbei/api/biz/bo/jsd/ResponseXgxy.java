package com.ald.fanbei.api.biz.bo.jsd;

import java.io.Serializable;

public class ResponseXgxy implements Serializable {

	private static final long serialVersionUID = -7947422672590193046L;

	private Integer code;
	private String message;
	private String data;

	public ResponseXgxy() {}

	public ResponseXgxy(Integer code, String message, String data) {
		this.code = code;
		this.message = message;
		this.data = data;
	}
	public Integer getCode() {
		return code;
	}
	public void setCode(Integer code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	
}
