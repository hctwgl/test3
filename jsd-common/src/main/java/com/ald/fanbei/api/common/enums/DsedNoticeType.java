package com.ald.fanbei.api.common.enums;

public enum  DsedNoticeType {

    PAY("打款", "PAY"),
    REPAY("还款", "REPAY"),
    OVERDUE("逾期", "OVERDUE"),
    COLLECT("催收逾期还款推送催收", "COLLECT"),
    XGXY_COLLECT("催收逾期还款推送西瓜", "XGXY_COLLECT"),
    OVERDUEREPAY("App主动逾期还款推送催收", "OVERDUEREPAY"),
    XGXY_OVERDUEREPAY("App主动逾期还款推送西瓜", "XGXY_OVERDUEREPAY");

    public String desz;
    public String code;

    DsedNoticeType(String desz,String code) {
        this.desz = desz;
        this.code = code;
    }
}
