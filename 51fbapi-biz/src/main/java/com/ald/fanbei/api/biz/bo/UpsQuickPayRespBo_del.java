package com.ald.fanbei.api.biz.bo;

/**
 *@类现描述：
 *@author chenqiwei 2018年3月30日 下午13:52:19
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class UpsQuickPayRespBo_del extends UpsRespBo {
	private String amount			;   //交易金额
	private String userNo			;   //用户唯一标识
	private String realName			;	//真实姓名
	private String cardNo			;	//卡号
	private String certType			;	//证件类型
	private String certNo			;	//证件号
	private String notifyUrl		;   //异步通知地址
	private String tradeDate		;   //交易时间
	private String tradeState	    ;   //交易状态
	private String tradeDesc		;   //交易状态描述
	private String tradeNo			;   //交易号
	//private String purpose		;   //用途
	//private String remark			;   //备注
	//private String returnUrl		;   //同步通知地址
	//private String phone			;   //手机号码
	//private String bankCode		;   //银行编号

	
	public String getUserNo() {
		return userNo;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
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
	public String getNotifyUrl() {
		return notifyUrl;
	}
	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
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
	public String getTradeNo() {
		return tradeNo;
	}
	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}
	
	
	
}
