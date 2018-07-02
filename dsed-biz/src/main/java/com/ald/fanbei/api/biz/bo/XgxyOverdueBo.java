package com.ald.fanbei.api.biz.bo;

public class XgxyOverdueBo {

    private String tradeNo;
    private String borrowNo;
    private Number overdueDays;

    private String curPeriod;




    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public String getBorrowNo() {
        return borrowNo;
    }

    public void setBorrowNo(String borrowNo) {
        this.borrowNo = borrowNo;
    }

    public Number getOverdueDays() {
        return overdueDays;
    }

    public void setOverdueDays(Number overdueDays) {
        this.overdueDays = overdueDays;
    }

    public String getCurPeriod() {
        return curPeriod;
    }

    public void setCurPeriod(String curPeriod) {
        this.curPeriod = curPeriod;
    }
}
