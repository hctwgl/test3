package com.ald.fanbei.api.biz.bo;

/**
 *@类现描述：支付路由代扣请求bo
 *@author chenjinhu 2017年2月20日 下午1:42:33
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class UpsCollectReqBo extends UpsReqBo {
	private static final long serialVersionUID = 7977070544204337468L;
	private String amount			;   //交易金额
	private String userNo			;   //用户唯一标识
	private String realName			;	//真实姓名
	private String phone				;   //手机号码
	private String bankCode			;   //银行编号
	private String cardNo			;	//卡号
	private String certType			;	//证件类型
	private String certNo			;	//证件号
	private String purpose			;   //用途
	private String remark			;   //备注
	private String returnUrl		;   //同步通知地址
	private String notifyUrl		;   //异步通知地址
	
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
		this.put("amount", amount);
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
		this.put("phone", phone);
	}
	public String getPurpose() {
		return purpose;
	}
	public void setPurpose(String purpose) {
		this.purpose = purpose;
		this.put("purpose", purpose);
	}
	
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
		this.put("remark", remark);
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
	}
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	public String getBankCode() {
		return bankCode;
	}
	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}
	public String getCardNo() {
		return cardNo;
	}
	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}
	public String getCertType() {
		return certType;
	}
	public void setCertType(String certType) {
		this.certType = certType;
	}
	public String getCertNo() {
		return certNo;
	}
	public void setCertNo(String certNo) {
		this.certNo = certNo;
	}
}
