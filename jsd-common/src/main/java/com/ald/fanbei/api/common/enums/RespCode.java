package com.ald.fanbei.api.common.enums;

public enum RespCode {
	SUCC(100,"成功"),
	
	FAIL(900, "通用错误码"),
	
	LOGIN_EXPIRE(901, "登陆过期"),
	PARAMS_ERROR(902, "参数错误"),
	SYS_ERROR(999, "系统异常"),
	BORROW_INFO_IS_NULL(300, "借款信息为空");


	public int code;
	public String desc;
	
	RespCode(int code, String desc){
		this.code = code;
		this.desc = desc;
	}
}
