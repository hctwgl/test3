package com.ald.fanbei.api.common.enums;

public enum GameGoodsType {
    GAME("GAME", "游戏"), AMUSEMENT("AMUSEMENT", "娱乐");

    private String code;
    private String name;

    GameGoodsType(String code, String name) {
	this.code = code;
	this.name = name;
    }

    public String getName() {
	return name;
    }

    public String getCode() {
	return code;
    }

    public void setCode(String code) {
	this.code = code;
    }

    public void setName(String name) {
	this.name = name;
    }
}
