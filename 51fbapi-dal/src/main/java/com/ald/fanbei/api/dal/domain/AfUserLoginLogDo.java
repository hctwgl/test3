/*
 *@Copyright (c) 2016, 杭州喜马拉雅家居有限公司 All Rights Reserved. 
 */
package com.ald.fanbei.api.dal.domain;

import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * 
 * @类描述：用户登录日志信息
 * @author Xiaotianjian 2017年1月19日下午4:09:57
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */

public class AfUserLoginLogDo extends AbstractSerial {
	
	private static final long serialVersionUID = 7221169701064508898L;
	
	private Integer rid;
	private Date gmtCreate;
	private String userName;//用户名
	private Integer appVersion;//版本号（整数）
	private String osType;//操作系统_版本,如：android_6.1.0
	private String phoneType;//手机型号：如iPhone6s,HUAWEI GRA-CL00
	private String uuid;//设备uuid
	private String loginIp;//登录ip
	private String result;//登录结果【  成功:T, 失败: F_密码不正确】
	
	public Integer getAppVersion() {
		return appVersion;
	}
	public void setAppVersion(Integer appVersion) {
		this.appVersion = appVersion;
	}
	
	public Integer getRid() {
		return rid;
	}
	public void setRid(Integer rid) {
		this.rid = rid;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public Date getGmtCreate() {
		return gmtCreate;
	}
	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}
	public String getPhoneType() {
		return phoneType;
	}
	public void setPhoneType(String phoneType) {
		this.phoneType = phoneType;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getLoginIp() {
		return loginIp;
	}
	public void setLoginIp(String loginIp) {
		this.loginIp = loginIp;
	}
	public String getOsType() {
		return osType;
	}
	public void setOsType(String osType) {
		this.osType = osType;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	
}
