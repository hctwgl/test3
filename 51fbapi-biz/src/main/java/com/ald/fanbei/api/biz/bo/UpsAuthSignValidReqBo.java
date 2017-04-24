package com.ald.fanbei.api.biz.bo;


/**
 *@类现描述：支付路由发送签约短信验证bo
 *@author chenjinhu 2017年2月19日 下午2:01:25
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class UpsAuthSignValidReqBo extends UpsReqBo{
	private static final long serialVersionUID = -6762573744930105400L;
	
	private String userNo		;//用户唯一标识
	private String smsCode		;//短信验证码
	private String cardNo		;//卡号
	private String notifyUrl	;//异步通知地址
	
	public String getSmsCode() {
		return smsCode;
	}
	public void setSmsCode(String smsCode) {
		this.smsCode = smsCode;
		this.put("smsCode", smsCode);
	}
	public String getNotifyUrl() {
		return notifyUrl;
	}
	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
		this.put("notifyUrl", notifyUrl);
	}
	public String getUserNo() {
		return userNo;
	}
	public void setUserNo(String userNo) {
		this.userNo = userNo;
		this.put("userNo", userNo);
	}
	public String getCardNo() {
		return cardNo;
	}
	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
		this.put("cardNo", cardNo);
	}
	
}
