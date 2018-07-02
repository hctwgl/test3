package com.ald.fanbei.api.common.enums;

public enum  DsedNoticeType {

    PAY("打款", "PAY"),
    REPAY("还款", "REPAY"),
    OVERDUE("逾期", "OVERDUE");

    public String desz;
    public String code;

    DsedNoticeType(String desz,String code) {
        this.desz = desz;
        this.code = code;
    }
}
