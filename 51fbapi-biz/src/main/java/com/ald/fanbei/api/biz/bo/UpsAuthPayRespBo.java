package com.ald.fanbei.api.biz.bo;


/**
 *@类现描述：支付路由认证支付bo
 *@author chenjinhu 2017年2月19日 下午1:49:59
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class UpsAuthPayRespBo extends UpsReqBo{
	
	private static final long serialVersionUID = -7894585752688339269L;
	private String amount			;   //交易金额
	private String userCustNo	;   //第三方账户号
	private String realName		;   //真实姓名
	private String cardNo			;   //银行卡号
	private String certType		;   //证件类型
	private String certNo			;   //证件号码
	private String tradeNo			;   //交易订单号
	private String tradeState	;   //交易状态
	private String tradeDesc		;   //交易描述
	private String notifyUrl		;   //异步通知地址
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getUserCustNo() {
		return userCustNo;
	}
	public void setUserCustNo(String userCustNo) {
		this.userCustNo = userCustNo;
	}
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
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
	public String getTradeNo() {
		return tradeNo;
	}
	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}
	public String getTradeState() {
		return tradeState;
	}
	public void setTradeState(String tradeState) {
		this.tradeState = tradeState;
	}
	public String getTradeDesc() {
		return tradeDesc;
	}
	public void setTradeDesc(String tradeDesc) {
		this.tradeDesc = tradeDesc;
	}
	public String getNotifyUrl() {
		return notifyUrl;
	}
	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}

}
