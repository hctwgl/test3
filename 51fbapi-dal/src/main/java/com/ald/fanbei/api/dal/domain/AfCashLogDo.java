/**
 * 
 */
package com.ald.fanbei.api.dal.domain;

import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * @类描述：
 * @author suweili 2017年3月21日下午5:05:17
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfCashLogDo extends AbstractSerial {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private Date gmtCreate;
	private Date gmtModified;
	private String creator;
	private String status;
	private Long cashRecordId;
	private Long userId;
	private String content;
	private Date gmtAuth;
	private String authUser;
	private Date gmtDeliver;
	private String deliverUser;
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * @return the gmtCreate
	 */
	public Date getGmtCreate() {
		return gmtCreate;
	}
	/**
	 * @param gmtCreate the gmtCreate to set
	 */
	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}

	/**
	 * @return the creator
	 */
	public String getCreator() {
		return creator;
	}
	/**
	 * @param creator the creator to set
	 */
	public void setCreator(String creator) {
		this.creator = creator;
	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * @return the cashRecordId
	 */
	public Long getCashRecordId() {
		return cashRecordId;
	}
	/**
	 * @param cashRecordId the cashRecordId to set
	 */
	public void setCashRecordId(Long cashRecordId) {
		this.cashRecordId = cashRecordId;
	}
	/**
	 * @return the userId
	 */
	public Long getUserId() {
		return userId;
	}
	/**
	 * @param userId the userId to set
	 */
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}
	/**
	 * @param content the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}
	/**
	 * @return the gmtAuth
	 */
	public Date getGmtAuth() {
		return gmtAuth;
	}
	/**
	 * @param gmtAuth the gmtAuth to set
	 */
	public void setGmtAuth(Date gmtAuth) {
		this.gmtAuth = gmtAuth;
	}
	/**
	 * @return the authUser
	 */
	public String getAuthUser() {
		return authUser;
	}
	/**
	 * @param authUser the authUser to set
	 */
	public void setAuthUser(String authUser) {
		this.authUser = authUser;
	}
	/**
	 * @return the gmtDeliver
	 */
	public Date getGmtDeliver() {
		return gmtDeliver;
	}
	/**
	 * @param gmtDeliver the gmtDeliver to set
	 */
	public void setGmtDeliver(Date gmtDeliver) {
		this.gmtDeliver = gmtDeliver;
	}
	/**
	 * @return the deliverUser
	 */
	public String getDeliverUser() {
		return deliverUser;
	}
	/**
	 * @param deliverUser the deliverUser to set
	 */
	public void setDeliverUser(String deliverUser) {
		this.deliverUser = deliverUser;
	}
	/**
	 * @return the gmtModified
	 */
	public Date getGmtModified() {
		return gmtModified;
	}
	/**
	 * @param gmtModified the gmtModified to set
	 */
	public void setGmtModified(Date gmtModified) {
		this.gmtModified = gmtModified;
	}
	
	

}
