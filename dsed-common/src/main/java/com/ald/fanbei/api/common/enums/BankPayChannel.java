package com.ald.fanbei.api.common.enums;

public enum BankPayChannel {
    KUAIJIE("KUAIJIE", "快捷支付"), DAIKOU("DAIKOU", "代扣");

    private String code;
    private String name;

    private BankPayChannel(String code, String name) {
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
