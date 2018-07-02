package com.ald.fanbei.api.common.enums;

import org.apache.commons.lang.StringUtils;

public enum UserDrawStatus {
    NORMAL(0, "未签到"), SIGNIN(1, "未中奖"),WIN(2,"中奖");
    
    private Integer code;

    private String name;

    UserDrawStatus(Integer code, String name) {
	this.code = code;
	this.name = name;
    }

    public static PayType findRoleTypeByCode(String code) {
	for (PayType roleType : PayType.values()) {
	    if (StringUtils.equals(code, roleType.getCode())) {
		return roleType;
	    }
	}
	return null;
    }

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
}
