package com.ald.fanbei.api.biz.bo.ups;

/**
 *@类现描述：支付路由  快捷支付bo
 *@author r chenqiwei 2018年3月30日 下午13:52:19
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class UpsProtocolPayReqBo extends UpsReqBo {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String amount			;   //交易金额
	private String userNo			;   //用户唯一标识
	private String realName			;	//真实姓名
	private String phone			;   //手机号码
	private String cardNo			;	//卡号
	private String certType			;	//证件类型
	private String certNo			;	//证件号
	private String notifyUrl		;   //异步通知地址
	private String expiredTime      ;   //交易过期时间
	private String productName      ;   //商品名称
	private String smsFlag;				//是否发送短信验证码（0：不发送短信1：发送短信）---1.7版本
	private String bankCode;			//银行简码
	private String purpose;				//分业务
	private String returnUrl;			//同步返回地址


	private String cvv2;
	private String validDate;

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

	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
		this.put("amount", amount);
	}
	public String getUserNo() {
		return userNo;
	}
	public void setUserNo(String userNo) {
		this.userNo = userNo;
		this.put("userNo", userNo);
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
	public String getCardNo() {
		return cardNo;
	}
	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
		this.put("cardNo", cardNo);
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
	public String getNotifyUrl() {
		return notifyUrl;
	}
	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
		this.put("notifyUrl", notifyUrl);
	}
	public String getExpiredTime() {
		return expiredTime;
	}
	public void setExpiredTime(String expiredTime) {
		this.expiredTime = expiredTime;
		this.put("expiredTime", expiredTime);
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
		this.put("productName", productName);
	}

	public String getSmsFlag() {
		return smsFlag;
	}
	public void setSmsFlag(String smsFlag) {
		this.smsFlag = smsFlag;
		this.put("smsFlag", smsFlag);
	}

	public String getBankCode() {
		return bankCode;
	}
	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
		this.put("bankCode", bankCode);
	}

	public String getPurpose() {
		return purpose;
	}
	public void setPurpose(String purpose) {
		this.purpose = purpose;
		this.put("purpose", purpose);
	}

	public String getReturnUrl() {
		return returnUrl;
	}
	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
		this.put("returnUrl", returnUrl);
	}

	
	
}
