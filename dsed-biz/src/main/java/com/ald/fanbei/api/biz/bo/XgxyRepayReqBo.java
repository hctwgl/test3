package com.ald.fanbei.api.biz.bo;

public class XgxyRepayReqBo extends XgxyReqBo{

    private String borrowNo;  //借款编号
    private String status;  //放款结果

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
}
