package com.ald.fanbei.api.biz.bo;


/**
 *@类现描述：支付路由发送签约查询bo
 *@author chenjinhu 2017年2月19日 下午2:01:25
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class UpsQueryAuthSignReqBo  extends UpsReqBo{
	private static final long serialVersionUID = -6762573744930105400L;
	
	private String tradeNo		;   //签约订单号
	private String contractNo	;   //签约协议号
	private String realName		;   //真实姓名
	private String certNo		;   //证件号码
	private String cardNo		;   //卡号
	private String startDate	;   //开始日期
	private String endDate		;   //结束日期
	
	public String getTradeNo() {
		return tradeNo;
	}
	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
		this.put("tradeNo", tradeNo);
	}
	public String getContractNo() {
		return contractNo;
	}
	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
		this.put("contractNo", contractNo);
	}
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
		this.put("realName", realName);
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

	
	
	
}
