package com.ald.fanbei.api.common.enums;

public enum UserAccountSceneType {
    ONLINE("ONLINE", "线上分期"), OFFLINE("OFFLINE", "线下分期"), CASH("CASH", "现金");

    private String code;
    private String name;

    UserAccountSceneType(String code, String name) {
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
