package com.ald.fanbei.api.biz.bo;


/**
 *@类现描述：支付路由签约bo
 *@author chenjinhu 2017年2月19日 下午1:49:59
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class UpsAuthSignReqBo extends UpsReqBo{
	private static final long serialVersionUID = 8310455391344267354L;
	
	
	private String bankCode	    ;//银行代码
	private String realName			;//真实姓名
	private String phone					;//手机号码
	private String certType			;//证件类型
	private String certNo				;//证件号码
	private String cardNo				;//卡号
	private String bankCardType	;//卡类型
	private String tradeDate			;//订单日期
	private String startDate			;//协议开始时间
	private String endDate				;//协议结束时间
	private String returnUrl			;//同步通知地址
	private String notifyUrl			;//异步通知地址
	
	
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
	public String getBankCardType() {
		return bankCardType;
	}
	public void setBankCardType(String bankCardType) {
		this.bankCardType = bankCardType;
		this.put("bankCardType", bankCardType);
	}
	public String getTradeDate() {
		return tradeDate;
	}
	public void setTradeDate(String tradeDate) {
		this.tradeDate = tradeDate;
		this.put("tradeDate", tradeDate);
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
		this.put("startDate", startDate);
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
		this.put("endDate", endDate);
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
	
	

}
