package com.ald.fanbei.api.biz.bo.assetpush;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;
/**
 * 债权推送的开关配置
 * @author wujun
 * @version 1.0.0 初始化
 * @date 2018年2月27日下午2:52:20
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AssetPushSwitchConf implements Serializable {

	private static final long serialVersionUID = 4347678991772430075L;
	String rePush;
	String pushFail;
	String reviewFail;
	String payFail;
	String pushWhite;
	
	public String getRePush() {
		return rePush;
	}
	public void setRePush(String rePush) {
		this.rePush = rePush;
	}
	public String getPushFail() {
		return pushFail;
	}
	public void setPushFail(String pushFail) {
		this.pushFail = pushFail;
	}
	public String getReviewFail() {
		return reviewFail;
	}
	public void setReviewFail(String reviewFail) {
		this.reviewFail = reviewFail;
	}
	public String getPayFail() {
		return payFail;
	}
	public void setPayFail(String payFail) {
		this.payFail = payFail;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getPushWhite() {
		return pushWhite;
	}
	public void setPushWhite(String pushWhite) {
		this.pushWhite = pushWhite;
	}
	
}
