package com.ald.fanbei.api.biz.bo;


/**
 *@类现描述：支付路由签约bo
 *@author chenjinhu 2017年2月19日 下午1:49:59
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class UpsAuthSignReqBo extends UpsReqBo{
	private static final long serialVersionUID = 8310455391344267354L;
	
	private String userNo		;//用户在商户的唯一标识
	private String bankCode	    ;//银行代码
	private String realName			;//真实姓名
	private String phone					;//手机号码
	private String certType			;//证件类型
	private String certNo				;//证件号码
	private String cardNo				;//卡号
	private String returnUrl			;//同步通知地址
	private String notifyUrl			;//异步通知地址

	private String cardType;
	private String cvv2;
	private String validDate;

	public String getCardType() {
		return cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
		this.put("cardType", cardType);
	}

	public String getCvv2() {
		return cvv2;
	}

	public void setCvv2(String cvv2) {
		this.cvv2 = cvv2;
		this.put("cvv2", cvv2);
	}

	public String getValidDate() {
		return validDate;
	}

	public void setValidDate(String validDate) {
		this.validDate = validDate;
		this.put("validDate", validDate);
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
	public String getReturnUrl() {
		return returnUrl;
	}
	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
		this.put("returnUrl", returnUrl);
	}
	public String getNotifyUrl() {
		return notifyUrl;
	}
	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
		this.put("notifyUrl", notifyUrl);
	}
	public String getUserNo() {
		return userNo;
	}
	public void setUserNo(String userNo) {
		this.userNo = userNo;
		this.put("userNo", userNo);
	}

}
