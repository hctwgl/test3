package com.ald.fanbei.api.dal.domain.query;

import java.io.Serializable;
import java.util.Date;


public class AfBusinessAccessRecordQuery implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String refType; //来源，借贷超市列表或banner
	private Long userId;
//	private Long refId;
	private Date beginTime;
	private Date endTime;
	private Boolean fromApp; //是否来自app，否的话是来自马甲包的 
	public String getRefType() {
		return refType;
	}
	public void setRefType(String refType) {
		this.refType = refType;
	}
	public Date getBeginTime() {
		return beginTime;
	}
	public void setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public Boolean getFromApp() {
		return fromApp;
	}
	public void setFromApp(Boolean fromApp) {
		this.fromApp = fromApp;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
}
