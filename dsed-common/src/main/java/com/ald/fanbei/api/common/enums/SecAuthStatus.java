package com.ald.fanbei.api.common.enums;

public enum SecAuthStatus {
	INIT("A", "未认证"), 
	NO("N", "未通过认证"),
	PROCESSING("W", "认证中"),
	YES("Y", "已通过审核");

	private String code;
	private String name;

	SecAuthStatus(String code, String name) {
		this.code = code;
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
