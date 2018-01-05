package com.ald.fanbei.api.dal.domain.query;

import java.util.Date;

import com.ald.fanbei.api.common.page.Page;
import com.ald.fanbei.api.dal.domain.AfBorrowBillDo;

public class AfBorrowBillQuery extends Page<AfBorrowBillDo>{

	private static final long serialVersionUID = 5059432397386863022L;

	private int billYear;
	
	private int billMonth;
	
	private Long userId;
	
	private String overdueStatus;
	
	private Integer isOut;
	
	private String status;
	
	private Date outDayStr;
	
	private Date outDayEnd;

	public int getBillYear() {
		return billYear;
	}

	public void setBillYear(int billYear) {
		this.billYear = billYear;
	}

	public int getBillMonth() {
		return billMonth;
	}

	public void setBillMonth(int billMonth) {
		this.billMonth = billMonth;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getOverdueStatus() {
		return overdueStatus;
	}

	public void setOverdueStatus(String overdueStatus) {
		this.overdueStatus = overdueStatus;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getOutDayStr() {
		return outDayStr;
	}

	public void setOutDayStr(Date outDayStr) {
		this.outDayStr = outDayStr;
	}

	public Date getOutDayEnd() {
		return outDayEnd;
	}

	public void setOutDayEnd(Date outDayEnd) {
		this.outDayEnd = outDayEnd;
	}

	public Integer getIsOut() {
		return isOut;
	}

	public void setIsOut(Integer isOut) {
		this.isOut = isOut;
	}
	
}
