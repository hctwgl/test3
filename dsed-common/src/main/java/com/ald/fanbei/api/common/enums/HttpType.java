package com.ald.fanbei.api.common.enums;

public enum HttpType {
	AppH5(0,"AppH5"),
	H5(1,"H5");
	
	private Integer code;
	private String name;
	
	public Integer getCode() {
		return code;
	}
	public void setCode(Integer code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	private HttpType(Integer code, String name) {
		this.code = code;
		this.name = name;
	}
	
	
}


