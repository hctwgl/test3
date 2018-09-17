package com.ald.fanbei.api.common.enums;

public enum  JsdNoticeType {

	DELEGATEPAY("打款", "DELEGATEPAY"),
    REPAY("还款", "REPAY"),
    RENEW("续期", "RENEW"),
    COLLECT_RENEW("催收续期", "COLLECT_RENEW"),
    OVERDUE("逾期", "OVERDUE"),
    BIND("绑卡", "BIND"),
    COLLECT("催收逾期还款推送催收", "COLLECT"),
    XGXY_COLLECT("催收逾期还款推送西瓜", "XGXY_COLLECT"),
    OVERDUEREPAY("App主动逾期还款推送催收", "OVERDUEREPAY"),
    XGXY_OVERDUEREPAY("App主动逾期还款推送西瓜", "XGXY_OVERDUEREPAY");

    public String desz;
    public String code;

    JsdNoticeType(String desz,String code) {
        this.desz = desz;
        this.code = code;
    }
}
