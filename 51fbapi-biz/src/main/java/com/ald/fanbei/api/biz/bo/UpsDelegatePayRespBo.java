package com.ald.fanbei.api.biz.bo;

/**
 *@类现描述：支付路由单笔代付响应bo
 *@author chenjinhu 2017年2月19日 下午4:18:08
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class UpsDelegatePayRespBo extends UpsRespBo{
	private String amount			;   //交易金额
	private String realName		;   //收款人名称
	private String cardNo			;   //银行卡号
	private String purpose			;   //用途
	private String tradeState;      //交易状态
	private String tradeDesc;       //交易描述
	private String notifyUrl		;   //异步通知地址
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
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
	public String getPurpose() {
		return purpose;
	}
	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}
	public String getNotifyUrl() {
		return notifyUrl;
	}
	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
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
	
	
}
