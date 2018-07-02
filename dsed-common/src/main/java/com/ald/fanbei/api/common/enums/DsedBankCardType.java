package com.ald.fanbei.api.common.enums;

public enum  DsedBankCardType {

    DEBIT(00, "借记卡"),
    CREDIT(01, "借贷卡"),
    OTHER(02, "其它"),
    ALL(03,"所有");

    private Integer code;
    private String name;


    DsedBankCardType(Integer code, String name) {
        this.code = code;
        this.name = name;
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
