package com.ald.fanbei.api.biz.bo;

/**
 *@类现描述：
 *@author chenjinhu 2017年2月20日 下午1:52:19
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class UpsCollectRespBo extends UpsRespBo {
	private String amount			;  //交易金额
	private String phone				;  //手机号码
	private String purpose			;  //用途
	private String remark			;  //备注
	private String returnUrl		;  //同步通知地址
	private String notifyUrl		;  //异步通知地址
	private String tradeState	;  //交易状态
	private String tradeDesc		;  //交易状态描述
	private String contractNo	;  //用户协议号
	
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getPurpose() {
		return purpose;
	}
	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getReturnUrl() {
		return returnUrl;
	}
	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
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
	public String getContractNo() {
		return contractNo;
	}
	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}
	
}
