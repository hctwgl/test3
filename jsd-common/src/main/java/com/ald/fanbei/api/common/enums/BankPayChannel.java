package com.ald.fanbei.api.common.enums;

public enum BankPayChannel {
    KUAIJIE("KUAIJIE", "快捷支付(有短验)"), 
    DAISHOU("DAISHOU", "代收"),
    KUAIJIE_NO_SMS("KUAIJIE_NO_SMS", "快捷支付(无短验)"),
    XIEYI("XIEYI", "协议");

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
