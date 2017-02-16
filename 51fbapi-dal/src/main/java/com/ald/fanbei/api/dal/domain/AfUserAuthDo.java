package com.ald.fanbei.api.dal.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 *@类现描述：用户授权do
 *@author chenjinhu 2017年2月15日 下午2:46:19
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfUserAuthDo extends AbstractSerial {
	private static final long serialVersionUID = -6047858096252444067L;
	private Long rid;
	private Date gmtModified;
	private Long userId;
	private String zmStatus;
	private Date gmtZm;
	private Integer zmScore;
	private String isvStatus;
	private Date gmtIsv;
	private Integer isvScore;
	private String tdStatus;
	private Date gmtTd;
	private Integer tdCore;
	private String facesStatus;
	private BigDecimal similarDegree;
	private String mobileStatus;
	private String bankcardStatus;
	private String teldirStatus;
	private String ydStatus;
	
	public Long getRid() {
		return rid;
	}
	public void setRid(Long rid) {
		this.rid = rid;
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
	public String getZmStatus() {
		return zmStatus;
	}
	public void setZmStatus(String zmStatus) {
		this.zmStatus = zmStatus;
	}
	public Date getGmtZm() {
		return gmtZm;
	}
	public void setGmtZm(Date gmtZm) {
		this.gmtZm = gmtZm;
	}
	public Integer getZmScore() {
		return zmScore;
	}
	public void setZmScore(Integer zmScore) {
		this.zmScore = zmScore;
	}
	public String getIsvStatus() {
		return isvStatus;
	}
	public void setIsvStatus(String isvStatus) {
		this.isvStatus = isvStatus;
	}
	public Date getGmtIsv() {
		return gmtIsv;
	}
	public void setGmtIsv(Date gmtIsv) {
		this.gmtIsv = gmtIsv;
	}
	public Integer getIsvScore() {
		return isvScore;
	}
	public void setIsvScore(Integer isvScore) {
		this.isvScore = isvScore;
	}
	public String getTdStatus() {
		return tdStatus;
	}
	public void setTdStatus(String tdStatus) {
		this.tdStatus = tdStatus;
	}
	public Date getGmtTd() {
		return gmtTd;
	}
	public void setGmtTd(Date gmtTd) {
		this.gmtTd = gmtTd;
	}
	public Integer getTdCore() {
		return tdCore;
	}
	public void setTdCore(Integer tdCore) {
		this.tdCore = tdCore;
	}
	public String getFacesStatus() {
		return facesStatus;
	}
	public void setFacesStatus(String facesStatus) {
		this.facesStatus = facesStatus;
	}
	public BigDecimal getSimilarDegree() {
		return similarDegree;
	}
	public void setSimilarDegree(BigDecimal similarDegree) {
		this.similarDegree = similarDegree;
	}
	public String getMobileStatus() {
		return mobileStatus;
	}
	public void setMobileStatus(String mobileStatus) {
		this.mobileStatus = mobileStatus;
	}
	public String getBankcardStatus() {
		return bankcardStatus;
	}
	public void setBankcardStatus(String bankcardStatus) {
		this.bankcardStatus = bankcardStatus;
	}
	public String getTeldirStatus() {
		return teldirStatus;
	}
	public void setTeldirStatus(String teldirStatus) {
		this.teldirStatus = teldirStatus;
	}
	public String getYdStatus() {
		return ydStatus;
	}
	public void setYdStatus(String ydStatus) {
		this.ydStatus = ydStatus;
	}
	
}
