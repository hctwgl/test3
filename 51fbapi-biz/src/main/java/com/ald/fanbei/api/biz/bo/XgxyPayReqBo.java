package com.ald.fanbei.api.biz.bo;

import java.util.Date;

public class XgxyPayReqBo extends XgxyReqBo{

    private String borrowNo;  //借款编号
    private String status;  //放款结果
    private Date gmtArrival;  //放款到账时间
    private String reason;  //失败原因

    public String getBorrowNo() {
        return borrowNo;
    }

    public void setBorrowNo(String borrowNo) {
        this.borrowNo = borrowNo;
        this.put("borrowNo", borrowNo);

    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
        this.put("status", status);

    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
        this.put("reason", reason);

    }

    public Date getGmtArrival() {
        return gmtArrival;
    }

    public void setGmtArrival(Date gmtArrival) {
        this.gmtArrival = gmtArrival;
        this.put("gmtArrival", String.valueOf(gmtArrival));

    }
}
