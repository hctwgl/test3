package com.ald.fanbei.api.biz.bo;

import com.ald.fanbei.api.common.exception.Auth51FundRespCode;


public class Auth51FundRespBo  {

	private static final long serialVersionUID = -7947422672590193046L;
	
	private Integer code;
	private String message;
	private String data;
	
	
	public Auth51FundRespBo() {
		super();
		this.code = Auth51FundRespCode.SUCCESS.getCode();
		this.message = Auth51FundRespCode.SUCCESS.getMsg();
	}
	
	public Auth51FundRespBo(Integer code, String message,String data) {
		super();
		this.code = code;
		this.message = message;
		this.data = data;
	}

	public Auth51FundRespBo(Auth51FundRespCode respCode,String data) {
		super();
		this.code = respCode.getCode();
		this.message = respCode.getDesc();
		this.data = data;
	}
	
	public void resetRespInfo(Auth51FundRespCode respCode) {
		this.code = respCode.getCode();
		this.message = respCode.getDesc();
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

	@Override
	public String toString() {
		return "Auth51FundRespBo [code=" + code + ", message=" + message
				+ ", data=" + data + "]";
	}
}
