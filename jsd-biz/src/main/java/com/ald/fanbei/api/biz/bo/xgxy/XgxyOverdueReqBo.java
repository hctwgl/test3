package com.ald.fanbei.api.biz.bo.xgxy;

import java.util.HashMap;

public class XgxyOverdueReqBo extends XgxyReqBo {

    private String borrowNo;

    private String overdueDays;

    private String curPeriod;


    public String getBorrowNo() {
        return borrowNo;
    }

    public void setBorrowNo(String borrowNo) {
        this.borrowNo = borrowNo;
        this.put("borrowNo",borrowNo);
    }

    public String getOverdueDays() {
        return overdueDays;
    }

    public void setOverdueDays(String overdueDays) {
        this.overdueDays = overdueDays;
        this.put("overdueDays",overdueDays);

    }

    public String getCurPeriod() {
        return curPeriod;
    }

    public void setCurPeriod(String curPeriod) {
        this.curPeriod = curPeriod;
        this.put("curPeriod",curPeriod);

    }
}
