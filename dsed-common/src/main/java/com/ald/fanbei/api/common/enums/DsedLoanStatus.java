package com.ald.fanbei.api.common.enums;

public enum  DsedLoanStatus {

    APPLY("申请/未审核", "APPLY"),
    TRANSFERING("打款中", "TRANSEDING"),
    TRANSFERRED("已经打款/待还款", "TRANSED"),
    REPAYING("还款中", "REPAYING"),
    FINISHED("已结清", "FINISHED"),
    CLOSED("关闭", "CLOSED");

    public String desz;
    public String code;

    DsedLoanStatus(String desz,String code) {
        this.desz = desz;
        this.code = code;
    }
}
