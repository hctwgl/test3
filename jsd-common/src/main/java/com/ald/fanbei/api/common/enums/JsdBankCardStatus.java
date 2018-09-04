package com.ald.fanbei.api.common.enums;

public enum  JsdBankCardStatus {

    NEW("N", "新建状态"),
    BIND("B", "绑定状态"),
    UNBIND("U", "审核关闭");

    private String code;
    private String name;

    JsdBankCardStatus(String code, String name) {
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
