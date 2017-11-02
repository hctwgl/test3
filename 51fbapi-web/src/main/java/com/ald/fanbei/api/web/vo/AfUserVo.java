package com.ald.fanbei.api.web.vo;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * 
 * @类描述：
 * @author Xiaotianjian 2017年1月19日下午7:52:02
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfUserVo extends AbstractSerial {

	private static final long serialVersionUID = 1L;
	private Long userId;
	private String avatar;
	private String mobile;
	private String nick;
	private String userName;
	private String realName;
	private String email;
	private String province;
	private String city;
	private String county;
	private String address;
	private Integer failCount;
	private String bindCard;

	private String alipayAccount;
	private String realnameStatus; // 是否实名状态
	private String idNumber; //省份证号
	private String isUploadImage; // 是否上传图片了
	private String bankCardStatus;
	private String faceStatus;
	private String riskStatus;
	
	public String getRealnameStatus() {
		return realnameStatus;
	}

	public void setRealnameStatus(String realnameStatus) {
		this.realnameStatus = realnameStatus;
	}

	public String getIdNumber() {
		return idNumber;
	}

	public void setIdNumber(String idNumber) {
		this.idNumber = idNumber;
	}

	public String getIsUploadImage() {
		return isUploadImage;
	}

	public void setIsUploadImage(String isUploadImage) {
		this.isUploadImage = isUploadImage;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public AfUserVo() {
		
	}
	
	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getNick() {
		return nick;
	}
	public void setNick(String nick) {
		this.nick = nick;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}


	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAlipayAccount() {
		return alipayAccount;
	}

	public void setAlipayAccount(String alipayAccount) {
		this.alipayAccount = alipayAccount;
	}

	/**
	 * @return the bindCard
	 */
	public String getBindCard() {
		return bindCard;
	}

	/**
	 * @param bindCard the bindCard to set
	 */
	public void setBindCard(String bindCard) {
		this.bindCard = bindCard;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCounty() {
		return county;
	}

	public void setCounty(String county) {
		this.county = county;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Integer getFailCount() {
		return failCount;
	}

	public void setFailCount(Integer failCount) {
		this.failCount = failCount;
	}

	/**
	 * @return the realName
	 */
	public String getRealName() {
		return realName;
	}

	/**
	 * @param realName the realName to set
	 */
	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getBankCardStatus() {
		return bankCardStatus;
	}

	public void setBankCardStatus(String bankCardStatus) {
		this.bankCardStatus = bankCardStatus;
	}

	public String getFaceStatus() {
		return faceStatus;
	}

	public void setFaceStatus(String faceStatus) {
		this.faceStatus = faceStatus;
	}

	public String getRiskStatus() {
		return riskStatus;
	}

	public void setRiskStatus(String riskStatus) {
		this.riskStatus = riskStatus;
	}

}
