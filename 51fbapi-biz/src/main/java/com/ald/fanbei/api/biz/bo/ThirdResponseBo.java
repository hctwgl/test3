package com.ald.fanbei.api.biz.bo;

import com.ald.fanbei.api.common.AbstractSerial;

public class ThirdResponseBo extends AbstractSerial{

	/**
	 * 
	 */
	private static final long serialVersionUID = -205074030363359340L;
	private String code;
	private String msg;
	private String data;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}

}
