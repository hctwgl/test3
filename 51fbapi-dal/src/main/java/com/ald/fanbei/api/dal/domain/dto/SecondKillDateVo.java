package com.ald.fanbei.api.dal.domain.dto;
/**  
 * @Title: SecondKillDateVo.java
 * @Package com.ald.fanbei.api.web.vo
 * @Description: TODO(用一句话描述该文件做什么)
 * @Copyright (c) 浙江阿拉丁电子商务股份有限公司 All Rights Reserved.
 * @author qiao
 * @date 2018年1月15日 下午7:42:59
 * @version V1.0  
 */

import java.util.Date;

public class SecondKillDateVo {
	private Date startTime;
	private String startDate;
	private int status;
	private Long meetingId;
	
	public Long getMeetingId() {
		return meetingId;
	}
	public void setMeetingId(Long meetingId) {
		this.meetingId = meetingId;
	}
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
		return "SecondKillDateVo [startTime=" + startTime + ", startDate=" + startDate + ", status=" + status
				+ ", meetingId=" + meetingId + "]";
	}

	

}
