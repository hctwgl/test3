package com.ald.fanbei.api.biz.bo;

/**
 *@类现描述：
 *@author chenjinhu 2017年2月19日 下午4:29:15
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class UpsAuthSignValidRespBo extends UpsRespBo {
	private String userNo			;   //用户编号
	private String cardNo			;	//卡号	
	private String smsCode			;   //短信验证码
	private String tradeDate		;   //订单日期
	private String tradeState	;   //交易状态
	private String tradeDesc		;   //交易状态描述
	private String notifyUrl		;   //异步通知地址
	public String getSmsCode() {
		return smsCode;
	}
	public void setSmsCode(String smsCode) {
		this.smsCode = smsCode;
	}
	public String getTradeDate() {
		return tradeDate;
	}
	public void setTradeDate(String tradeDate) {
		this.tradeDate = tradeDate;
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
	public String getUserNo() {
		return userNo;
	}
	public void setUserNo(String userNo) {
		this.userNo = userNo;
	}
	public String getCardNo() {
		return cardNo;
	}
	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}
	
}
