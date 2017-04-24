package com.ald.fanbei.api.biz.bo;


/**
 *@类现描述：支付路由认证确认支付bo
 *@author chenjinhu 2017年2月19日 下午1:49:59
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class UpsDelegatePayReqBo extends UpsReqBo{
	private static final long serialVersionUID = 8310455391344267354L;
	
	private String amount		;   //交易金额
	private String realName	;   //收款人名称
	private String cardNo		;   //银行卡号
	private String userNo		; //唯一标识
	private String phone		; //手机号
	private String bankName		; //银行名称
	private String bankCode		; //银行代码
	private String purpose		;   //用途
	private String notifyUrl	;   //异步通知地址
	
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
		this.put("amount", amount);
	}
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
		this.put("realName", realName);
	}
	public String getCardNo() {
		return cardNo;
	}
	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
		this.put("cardNo", cardNo);
	}
	public String getUserNo() {
		return userNo;
	}
	public void setUserNo(String userNo) {
		this.userNo = userNo;
		this.put("userNo", userNo);
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
		this.put("phone", phone);
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
		this.put("bankName", bankName);
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
	public String getNotifyUrl() {
		return notifyUrl;
	}
	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
		this.put("notifyUrl", notifyUrl);
	}
	
	

}
