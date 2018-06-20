package com.ald.fanbei.api.common.enums;

public enum  DsedLoanStatus {

    APPLY("申请/未审核", "APPLY"),
    TRANSFERING("打款中", "TRANSEDING"),
    TRANSFERRED("已经打款/待还款", "TRANSED"),
    REPAYING("还款中", "REPAYING"),
    FINISHED("已结清", "FINSH"),
    CLOSED("关闭", "CLOSED");

    public String desz;
    public String referBorrowCashCode;

    DsedLoanStatus(String desz,String referBorrowCashCode) {
        this.desz = desz;
        this.referBorrowCashCode = referBorrowCashCode;
    }
}
