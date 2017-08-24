package com.ald.fanbei.api.biz.bo;

/**
 *@类现描述：支付路由签约响应bo
 *@author chenjinhu 2017年2月19日 下午4:24:50
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class UpsQueryAuthSignRespBo extends UpsRespBo {
	private String tradeNo			;   //签约订单号
	private String contractNo	;   //签约协议号
	private String realName		;   //真实姓名
	private String certNo			;   //证件号码
	private String cardNo			;   //卡号
	private String tradeState		;   //开始日期
	private String tradeDesc			;   //结束日期
	
	public String getTradeNo() {
		return tradeNo;
	}
	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}
	public String getContractNo() {
		return contractNo;
	}
	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	public String getCertNo() {
		return certNo;
	}
	public void setCertNo(String certNo) {
		this.certNo = certNo;
	}
	public String getCardNo() {
		return cardNo;
	}
	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
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
