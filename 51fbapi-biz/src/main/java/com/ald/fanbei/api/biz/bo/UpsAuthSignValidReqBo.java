package com.ald.fanbei.api.biz.bo;


/**
 *@类现描述：支付路由发送签约短信验证bo
 *@author chenjinhu 2017年2月19日 下午2:01:25
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class UpsAuthSignValidReqBo extends UpsReqBo{
	private static final long serialVersionUID = -6762573744930105400L;
	
	private String tradeNo		;//签约时的订单号
	private String smsCode		;//短信验证码
	private String tradeDate	;//订单日期
	private String notifyUrl	;//异步通知地址
	

	public String getTradeNo() {
		return tradeNo;
	}
	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
		this.put("tradeNo", tradeNo);
	}
	public String getSmsCode() {
		return smsCode;
	}
	public void setSmsCode(String smsCode) {
		this.smsCode = smsCode;
		this.put("smsCode", smsCode);
	}
	public String getTradeDate() {
		return tradeDate;
	}
	public void setTradeDate(String tradeDate) {
		this.tradeDate = tradeDate;
		this.put("tradeDate", tradeDate);
	}
	public String getNotifyUrl() {
		return notifyUrl;
	}
	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
		this.put("notifyUrl", notifyUrl);
	}
	
	
}
