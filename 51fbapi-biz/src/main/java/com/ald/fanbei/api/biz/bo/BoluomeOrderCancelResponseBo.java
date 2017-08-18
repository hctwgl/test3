package com.ald.fanbei.api.biz.bo;

import com.ald.fanbei.api.common.AbstractSerial;

public class BoluomeOrderCancelResponseBo extends AbstractSerial{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7160629665849041419L;
	private boolean success;
	private Long code;
	private String msg;
	
	public Long getCode() {
		return code;
	}
	public void setCode(Long code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
}
