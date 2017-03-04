package com.ald.fanbei.api.biz.bo;


/**
 *@类现描述：支付路由认证支付确认bo
 *@author chenjinhu 2017年2月19日 下午1:49:59
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class UpsAuthPayConfirmReqBo extends UpsReqBo{
	
	
	private static final long serialVersionUID = 437740174293716763L;
	private String smsCode          ;   //短信验证码
	private String cardNo			;	//银行卡号
	private String userNo			;	//用户在商户的唯一标识
	private String tradeNo			;   //原认证支付交易订单号
	private String notifyUrl		;   //异步通知地址
	
	public String getSmsCode() {
		return smsCode;
	}
	public void setSmsCode(String smsCode) {
		this.smsCode = smsCode;
		this.put("smsCode", smsCode);
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
	public String getTradeNo() {
		return tradeNo;
	}
	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
		this.put("tradeNo", tradeNo);
	}
	public String getNotifyUrl() {
		return notifyUrl;
	}
	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
		this.put("notifyUrl", notifyUrl);
	}

}
