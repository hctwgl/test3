package com.ald.fanbei.api.biz.bo.assetside;

import java.io.Serializable;

/**
 *@类现描述：资产方平台请求实体
 *@author chengkang 2017年11月29日 14:29:12
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AssetSideReqBo implements Serializable{

	private static final long serialVersionUID = 1923132956548193237L;
	private String data;
	private Long sendTime;
	private String sign;
	private String appId;
	
	public AssetSideReqBo() {
		super();
	}
	
	
	public AssetSideReqBo(String data, Long sendTime, String sign, String appId) {
		super();
		this.data = data;
		this.sendTime = sendTime;
		this.sign = sign;
		this.appId = appId;
	}

	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public Long getSendTime() {
		return sendTime;
	}
	public void setSendTime(Long sendTime) {
		this.sendTime = sendTime;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	
}
