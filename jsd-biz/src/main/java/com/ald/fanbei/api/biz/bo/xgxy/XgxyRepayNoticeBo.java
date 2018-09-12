package com.ald.fanbei.api.biz.bo.xgxy;

import java.util.Date;

public class XgxyRepayNoticeBo {

    private String tradeNo;
    private String repayNo;
    private String isFinish;
    private String period;
    private String amount;
    private String type;
    private String borrowNo;
    private String reason;
    private String status;
    private String openId;
    private Long timestamp;




    public static XgxyRepayNoticeBo gen(String tradeNo,String repayNo,String isFinish,String period,String amount,String type,String borrowNo,String reason,String status,String openId,Long timestamp) {
    	XgxyRepayNoticeBo bo = new XgxyRepayNoticeBo();
        bo.tradeNo=tradeNo;
        bo.repayNo=repayNo;
        bo.isFinish=isFinish;
        bo.period=period;
        bo.amount=amount;
        bo.type=type;
        bo.borrowNo=borrowNo;
        bo.reason=reason;
        bo.status=status;
        bo.openId=openId;
        bo.timestamp=timestamp;
    	return bo;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public String getRepayNo() {
        return repayNo;
    }

    public void setRepayNo(String repayNo) {
        this.repayNo = repayNo;
    }

    public String getIsFinish() {
        return isFinish;
    }

    public void setIsFinish(String isFinish) {
        this.isFinish = isFinish;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBorrowNo() {
        return borrowNo;
    }

    public void setBorrowNo(String borrowNo) {
        this.borrowNo = borrowNo;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
