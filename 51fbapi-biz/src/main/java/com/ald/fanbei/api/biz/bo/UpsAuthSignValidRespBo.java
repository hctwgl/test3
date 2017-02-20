package com.ald.fanbei.api.biz.bo;

/**
 *@类现描述：
 *@author chenjinhu 2017年2月19日 下午4:29:15
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class UpsAuthSignValidRespBo extends UpsRespBo {
	private String tradeNo			;   //签约时的订单号
	private String smsCode			;   //短信验证码
	private String tradeDate		;   //订单日期
	private String contractNo	;   //签约标识
	private String tradeState	;   //交易状态
	private String tradeDesc		;   //交易状态描述
	private String notifyUrl		;   //异步通知地址
	public String getTradeNo() {
		return tradeNo;
	}
	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}
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
	public String getContractNo() {
		return contractNo;
	}
	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
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
