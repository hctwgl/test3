package com.ald.fanbei.api.dal.domain;

import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;

public class AfUserDo extends AbstractSerial{
	private static final long serialVersionUID = -6353150283853474562L;
	private Date birthday;
	private Date gmtModified;
	private String password;
	private Long rid;
	private String userName;
	private Date gmtCreate;
	private String nick;
	private String avata;
	private String name;
	private Integer gender;
	private Integer errorCount;
	private String mobile;
	private String salt;
	private String undisturbedStartTime;
	private String undisturbedEndTime;
	private Integer isUndisturbed;
	private String inviteCode;
	private Long invitor;
	private Date gmtInvite;
	private String openid;
	private String unionid;
	private String isSubscribe;
	
	public Date getBirthday() {
		return birthday;
	}
	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}
	public Date getGmtModified() {
		return gmtModified;
	}
	public void setGmtModified(Date gmtModified) {
		this.gmtModified = gmtModified;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
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
	public String getNick() {
		return nick;
	}
	public void setNick(String nick) {
		this.nick = nick;
	}
	public String getAvata() {
		return avata;
	}
	public void setAvata(String avata) {
		this.avata = avata;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getGender() {
		return gender;
	}
	public void setGender(Integer gender) {
		this.gender = gender;
	}
	public Integer getErrorCount() {
		return errorCount;
	}
	public void setErrorCount(Integer errorCount) {
		this.errorCount = errorCount;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getSalt() {
		return salt;
	}
	public void setSalt(String salt) {
		this.salt = salt;
	}
	public Integer getIsUndisturbed() {
		return isUndisturbed;
	}
	public void setIsUndisturbed(Integer isUndisturbed) {
		this.isUndisturbed = isUndisturbed;
	}
	public Long getRid() {
		return rid;
	}
	public void setRid(Long rid) {
		this.rid = rid;
	}
	public String getUndisturbedStartTime() {
		return undisturbedStartTime;
	}
	public void setUndisturbedStartTime(String undisturbedStartTime) {
		this.undisturbedStartTime = undisturbedStartTime;
	}
	public String getUndisturbedEndTime() {
		return undisturbedEndTime;
	}
	public void setUndisturbedEndTime(String undisturbedEndTime) {
		this.undisturbedEndTime = undisturbedEndTime;
	}
	public String getInviteCode() {
		return inviteCode;
	}
	public void setInviteCode(String inviteCode) {
		this.inviteCode = inviteCode;
	}
	public void setInvitor(Long invitor) {
		this.invitor = invitor;
	}
	public Long getInvitor() {
		return invitor;
	}
	public Date getGmtInvite() {
		return gmtInvite;
	}
	public void setGmtInvite(Date gmtInvite) {
		this.gmtInvite = gmtInvite;
	}
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	public String getUnionid() {
		return unionid;
	}
	public void setUnionid(String unionid) {
		this.unionid = unionid;
	}
	public String getIsSubscribe() {
		return isSubscribe;
	}
	public void setIsSubscribe(String isSubscribe) {
		this.isSubscribe = isSubscribe;
	}
}
