package com.ald.fanbei.api.biz.bo;

import com.ald.fanbei.api.common.AbstractSerial;

public class ZhimaAuthResultBo extends AbstractSerial {

	private static final long serialVersionUID = 7314789104106260680L;
	private String openId;
	private String errorMssage;
	private String errorCode;
	private String appId;
	private boolean success;
	public String getOpenId() {
		return openId;
	}
	public void setOpenId(String openId) {
		this.openId = openId;
	}
	public String getErrorMssage() {
		return errorMssage;
	}
	public void setErrorMssage(String errorMssage) {
		this.errorMssage = errorMssage;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
}
