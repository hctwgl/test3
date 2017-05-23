package com.ald.fanbei.api.biz.bo;


/**
 *@类现描述：支付路由解约bo
 *@author hexin 2017年3月12日 上午9:59:29
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class UpsSignReleaseReqBo extends UpsReqBo{

	/**
	 * 
	 */
	private static final long serialVersionUID = 9186048723261287133L;
	
	private String userNo;//用户唯一标识
	private String bankCode;//银行编号
	private String realName;//真实姓名
	private String phone;//手机号
	private String certType;//证件类型
	private String certNo;//证件号码
	private String cardNo;//卡号
	private String notifyUrl;//异步url
	public String getUserNo() {
		return userNo;
	}
	public void setUserNo(String userNo) {
		this.userNo = userNo;
		this.put("userNo", userNo);
	}
	public String getBankCode() {
		return bankCode;
	}
	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
		this.put("bankCode", bankCode);
	}
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
		this.put("realName", realName);
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
		this.put("phone", phone);
	}
	public String getCertType() {
		return certType;
	}
	public void setCertType(String certType) {
		this.certType = certType;
		this.put("certType", certType);
	}
	public String getCertNo() {
		return certNo;
	}
	public void setCertNo(String certNo) {
		this.certNo = certNo;
		this.put("certNo", certNo);
	}
	public String getCardNo() {
		return cardNo;
	}
	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
		this.put("cardNo", cardNo);
	}
	public String getNotifyUrl() {
		return notifyUrl;
	}
	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
		this.put("notifyUrl", notifyUrl);
	}
}
