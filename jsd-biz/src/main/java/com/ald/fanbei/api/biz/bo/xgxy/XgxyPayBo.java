package com.ald.fanbei.api.biz.bo.xgxy;

import java.util.Date;

public class XgxyPayBo {

    private String tradeNo;
    private String borrowNo;
    private String status;
    private Date gmtArrival;
    private String reason;
    private Long timestamp;

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

	/**
	 * @return the tradeNo
	 */
	public String getTradeNo() {
		return tradeNo;
	}

	/**
	 * @param tradeNo the tradeNo to set
	 */
	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
}
