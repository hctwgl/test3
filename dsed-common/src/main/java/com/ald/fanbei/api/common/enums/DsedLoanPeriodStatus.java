package com.ald.fanbei.api.common.enums;

public enum  DsedLoanPeriodStatus {
    AWAIT_REPAY("待还款"),
    REPAYING("划款中"),
    PART_REPAY("部分还款"),
    FINISHED("已结清"),
    CLOSED("已结清");

    public String desz;

    DsedLoanPeriodStatus(String desz) {
        this.desz = desz;
    }
}
