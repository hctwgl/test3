package com.ald.fanbei.api.dal.domain;

import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;

public class AfGameFivebabyDo extends AbstractSerial {
	private static final long serialVersionUID = 1687675196064885167L;
	
	private Long rid;
	private Date gmtCreate;
	private Date gmtModified;
	private Long userId;
	private Integer item1Count;
	private Integer item2Count;
	private Integer item3Count;
	private Integer item4Count;
	private Integer item5Count;
	private String isFinish;
	private String isAward;
	
	public Long getRid() {
		return rid;
	}
	public void setRid(Long rid) {
		this.rid = rid;
	}
	public Date getGmtCreate() {
		return gmtCreate;
	}
	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}
	public Date getGmtModified() {
		return gmtModified;
	}
	public void setGmtModified(Date gmtModified) {
		this.gmtModified = gmtModified;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Integer getItem1Count() {
		return item1Count;
	}
	public void setItem1Count(Integer item1Count) {
		this.item1Count = item1Count;
	}
	public Integer getItem2Count() {
		return item2Count;
	}
	public void setItem2Count(Integer item2Count) {
		this.item2Count = item2Count;
	}
	public Integer getItem3Count() {
		return item3Count;
	}
	public void setItem3Count(Integer item3Count) {
		this.item3Count = item3Count;
	}
	public Integer getItem4Count() {
		return item4Count;
	}
	public void setItem4Count(Integer item4Count) {
		this.item4Count = item4Count;
	}
	public Integer getItem5Count() {
		return item5Count;
	}
	public void setItem5Count(Integer item5Count) {
		this.item5Count = item5Count;
	}
	public String getIsFinish() {
		return isFinish;
	}
	public void setIsFinish(String isFinish) {
		this.isFinish = isFinish;
	}
	public String getIsAward() {
		return isAward;
	}
	public void setIsAward(String isAward) {
		this.isAward = isAward;
	}
}
