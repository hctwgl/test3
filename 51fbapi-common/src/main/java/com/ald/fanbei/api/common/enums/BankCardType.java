package com.ald.fanbei.api.common.enums;

public enum BankCardType {

    DEBIT(1, "借记卡"),
    CREDIT(2, "借贷卡"),
    OTHER(0, "其它"),
    ALL(3,"所有");

    private Integer code;
    private String name;


    BankCardType(Integer code, String name) {
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
