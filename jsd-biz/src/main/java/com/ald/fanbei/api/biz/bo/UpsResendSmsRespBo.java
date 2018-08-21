package com.ald.fanbei.api.biz.bo;

/**
 *@类现描述：
 *@author chenqiwei 2018年3月29日 下午16:52:19
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class UpsResendSmsRespBo extends UpsRespBo {

	private String tradeState	;  //交易状态
	private String tradeDesc		;  //交易状态描述
	private String tradeNo			;//交易号
	
	
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
