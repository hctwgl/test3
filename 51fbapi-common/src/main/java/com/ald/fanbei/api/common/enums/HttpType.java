package com.ald.fanbei.api.common.enums;

/**
* @ClassName: HttpType
* @Description: TODO(这里用一句话描述这个类的作用)
* @author qiao
* @Copyright (c) 浙江阿拉丁电子商务股份有限公司 All Rights Reserved.
* @date 2018年3月2日 下午3:30:46
*
*/
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
