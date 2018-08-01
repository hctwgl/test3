package com.ald.fanbei.api.common.enums;

public enum DsedRepayType {

    ONLINE( "app线上还款","ONLINE"),
    COLLECT( "催收逾期还款","COLLECT");

    private String code;
    private String name;


    DsedRepayType(String code, String name) {
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
