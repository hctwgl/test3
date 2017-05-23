package com.ald.fanbei.api.dal.domain;

import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 *@类现描述：查询芝麻信用结果
 *@author chenjinhu 2017年2月17日 下午2:00:23
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfAuthZmDo extends AbstractSerial{
	private static final long serialVersionUID = 2771650579787381428L;
	private Long id;
	private Date gmtCreate;
	private Date gmtModified;
	private Long userId;
	private String idNumber;
	private String realName;
	private String openId;
	private String scoreResult;
	private String ivsResult;
	private String watchlistResult;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
	public String getIdNumber() {
		return idNumber;
	}
	public void setIdNumber(String idNumber) {
		this.idNumber = idNumber;
	}
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	public String getOpenId() {
		return openId;
	}
	public void setOpenId(String openId) {
		this.openId = openId;
	}
	public String getScoreResult() {
		return scoreResult;
	}
	public void setScoreResult(String scoreResult) {
		this.scoreResult = scoreResult;
	}
	public String getIvsResult() {
		return ivsResult;
	}
	public void setIvsResult(String ivsResult) {
		this.ivsResult = ivsResult;
	}
	public String getWatchlistResult() {
		return watchlistResult;
	}
	public void setWatchlistResult(String watchlistResult) {
		this.watchlistResult = watchlistResult;
	}
	
}
