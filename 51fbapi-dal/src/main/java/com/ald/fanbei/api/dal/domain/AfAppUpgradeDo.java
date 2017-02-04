/*
 *@Copyright (c) 2016, 杭州喜马拉雅家居有限公司 All Rights Reserved. 
 */
package com.ald.fanbei.api.dal.domain;

import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * 
 * @类描述：
 * @author Xiaotianjian 2017年1月20日下午6:10:35
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfAppUpgradeDo extends AbstractSerial {

	private static final long serialVersionUID = 200302325959484769L;
	
	private Long rid;
	private Integer isDelete;
	private Date gmtCreate;
	private Date gmtModified;
	private String creator;
	private String modifier;
	private String status;
	private Integer versionCode;
	private String versionName;
	private String versionDesc;
	private String apkUrl;
	private String upgradeRange;
	private String isForce;
	private String isSilence;
	private Integer minVersion;
	private Integer maxVersion;
	private String apkMd5;
	
	public Long getRid() {
		return rid;
	}
	public void setRid(Long rid) {
		this.rid = rid;
	}
	public Integer getIsDelete() {
		return isDelete;
	}
	public void setIsDelete(Integer isDelete) {
		this.isDelete = isDelete;
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
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public String getModifier() {
		return modifier;
	}
	public void setModifier(String modifier) {
		this.modifier = modifier;
	}
	public Integer getVersionCode() {
		return versionCode;
	}
	public void setVersionCode(Integer versionCode) {
		this.versionCode = versionCode;
	}
	public String getVersionName() {
		return versionName;
	}
	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}
	public String getVersionDesc() {
		return versionDesc;
	}
	public void setVersionDesc(String versionDesc) {
		this.versionDesc = versionDesc;
	}
	public String getApkUrl() {
		return apkUrl;
	}
	public void setApkUrl(String apkUrl) {
		this.apkUrl = apkUrl;
	}
	public Integer getMinVersion() {
		return minVersion;
	}
	public void setMinVersion(Integer minVersion) {
		this.minVersion = minVersion;
	}
	public Integer getMaxVersion() {
		return maxVersion;
	}
	public void setMaxVersion(Integer maxVersion) {
		this.maxVersion = maxVersion;
	}
	public String getApkMd5() {
		return apkMd5;
	}
	public void setApkMd5(String apkMd5) {
		this.apkMd5 = apkMd5;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getUpgradeRange() {
		return upgradeRange;
	}
	public void setUpgradeRange(String upgradeRange) {
		this.upgradeRange = upgradeRange;
	}
	public String getIsForce() {
		return isForce;
	}
	public void setIsForce(String isForce) {
		this.isForce = isForce;
	}
	public String getIsSilence() {
		return isSilence;
	}
	public void setIsSilence(String isSilence) {
		this.isSilence = isSilence;
	}
	
	
}
