package com.ald.fanbei.api.dal.domain.dto;

import java.util.Date;

public class SecondKillDateVo {
	private Date startTime;
	private String startDate;
	private int status;
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	@Override
	public String toString() {
		return "SecondKillDateVo [startTime=" + startTime + ", startDate=" + startDate + ", status=" + status + "]";
	}
	
	

}

