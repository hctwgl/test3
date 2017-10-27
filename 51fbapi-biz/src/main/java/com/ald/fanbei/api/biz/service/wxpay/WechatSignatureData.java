package com.ald.fanbei.api.biz.service.wxpay;

import java.security.Timestamp;

import com.ald.fanbei.api.common.AbstractSerial;
/**
 * @date:2017-9-27 16:04:10
 * @author qiaopan
 * @description 微信验签
 *
 */
public class WechatSignatureData extends AbstractSerial{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2817646359900392346L;
	private String appId;//appid
	private String nonceStr;//uuid
	private String sign;//sha1加密之后
	private Long timestamp;//时间戳
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getNonceStr() {
		return nonceStr;
	}
	public void setNonceStr(String nonceStr) {
		this.nonceStr = nonceStr;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	public Long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	
}
