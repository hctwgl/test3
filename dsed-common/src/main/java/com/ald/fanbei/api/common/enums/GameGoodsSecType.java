package com.ald.fanbei.api.common.enums;

public enum GameGoodsSecType {
    QQ("QQ", "qq会员"), VIDEO("VIDEO", "视频会员");

    private String code;
    private String name;

    GameGoodsSecType(String code, String name) {
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
