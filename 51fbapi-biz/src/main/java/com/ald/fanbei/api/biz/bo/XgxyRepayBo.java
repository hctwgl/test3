package com.ald.fanbei.api.biz.bo;

import java.util.List;

public class XgxyRepayBo {

    private String tradeNo;
    private String borrowNo;
    private String status;
    private String isFinish;
    private String reason;
    private String curPeriod;
    private String unrepayAmount;
    private String unrepayInterestFee;
    private String unrepayOverdueFee;
    private String unrepayServiceFee;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIsFinish() {
        return isFinish;
    }

    public void setIsFinish(String isFinish) {
        this.isFinish = isFinish;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getCurPeriod() {
        return curPeriod;
    }

    public void setCurPeriod(String curPeriod) {
        this.curPeriod = curPeriod;
    }

    public String getUnrepayAmount() {
        return unrepayAmount;
    }

    public void setUnrepayAmount(String unrepayAmount) {
        this.unrepayAmount = unrepayAmount;
    }

    public String getUnrepayInterestFee() {
        return unrepayInterestFee;
    }

    public void setUnrepayInterestFee(String unrepayInterestFee) {
        this.unrepayInterestFee = unrepayInterestFee;
    }

    public String getUnrepayOverdueFee() {
        return unrepayOverdueFee;
    }

    public void setUnrepayOverdueFee(String unrepayOverdueFee) {
        this.unrepayOverdueFee = unrepayOverdueFee;
    }

    public String getUnrepayServiceFee() {
        return unrepayServiceFee;
    }

    public void setUnrepayServiceFee(String unrepayServiceFee) {
        this.unrepayServiceFee = unrepayServiceFee;
    }
}
