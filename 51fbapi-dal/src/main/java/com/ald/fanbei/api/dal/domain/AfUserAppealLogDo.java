package com.ald.fanbei.api.dal.domain;

import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 *@类描述：用户申诉行为记录表
 *@author  zhujiangfeng
 */
public class AfUserAppealLogDo extends AbstractSerial {

	private static final long serialVersionUID = -3854489704238877350L;
	
	private Long rid;  
	
	private Long userId;
	private String oldMobile;
	private String newMobile;
	private String realName;
	private String citizenId;
	private String msg;
	private String status;
	
	private Date gmtCreate;
	private Date gmtModified;
	private Integer isDelete;
	
	public static AfUserAppealLogDo generate(Long userId, String oldMobile, String newMobile, AfUserAppealLogStatusEnum status) {
		AfUserAppealLogDo logDo = new AfUserAppealLogDo();
		logDo.userId = userId;
		logDo.oldMobile = oldMobile;
		logDo.newMobile = newMobile;
		logDo.status = status.name();
		return logDo;
	}
	
	public enum AfUserAppealLogStatusEnum{
		ING,SUCCESS,FAIL;
	}
	
	public Long getRid() {
		return rid;
	}
	public void setRid(Long rid) {
		this.rid = rid;
	}
	
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getNewMobile() {
		return newMobile;
	}
	public void setNewMobile(String newMobile) {
		this.newMobile = newMobile;
	}
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	public String getCitizenId() {
		return citizenId;
	}
	public void setCitizenId(String citizenId) {
		this.citizenId = citizenId;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
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
	public Integer getIsDelete() {
		return isDelete;
	}
	public void setIsDelete(Integer isDelete) {
		this.isDelete = isDelete;
	}
	public String getOldMobile() {
		return oldMobile;
	}
	public void setOldMobile(String oldMobile) {
		this.oldMobile = oldMobile;
	}
	 
}
