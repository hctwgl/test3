package com.ald.fanbei.api.common.enums;

public enum JsdRepayType {

    ONLINE( "app线上还款","ONLINE"),
    WITHHOLD( "代扣还款","WITHHOLD"),
    INITIATIVE( "主动还款","INITIATIVE"),
    OFFLINE( "线下还款","OFFLINE"),
    COLLECT( "催收逾期还款","COLLECT");

    private String code;
    private String name;


    JsdRepayType(String code, String name) {
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
