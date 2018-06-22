package com.ald.fanbei.api.biz.bo;

import java.util.Date;

public class XgxyPayBo {

    private String trade;
    private String borrowNo;
    private String status;
    private Date gmtArrival;
    private String reason;

    public String getTrade() {
        return trade;
    }

    public void setTrade(String trade) {
        this.trade = trade;
    }

    public String getBorrowNo() {

        return borrowNo;
    }

    public void setBorrowNo(String borrowNo) {
        this.borrowNo = borrowNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getGmtArrival() {
        return gmtArrival;
    }

    public void setGmtArrival(Date gmtArrival) {
        this.gmtArrival = gmtArrival;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
