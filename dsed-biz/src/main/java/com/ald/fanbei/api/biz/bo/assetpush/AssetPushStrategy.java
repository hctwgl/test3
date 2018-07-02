package com.ald.fanbei.api.biz.bo.assetpush;

import java.io.Serializable;
import java.util.List;

/**
 * 债权推送超时查询策略
 * @author wujun
 * @version 1.0.0 初始化
 * @date 2018年2月27日下午2:52:20
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AssetPushStrategy implements Serializable {

	private static final long serialVersionUID = 4347678991772430075L;
	Integer rePushInterval;
	Integer rePushCount;
	Integer timeOut;
	Integer queryInterval ;
	Integer queryCount ;
	
	public Integer getRePushInterval() {
		return rePushInterval;
	}
	public void setRePushInterval(Integer rePushInterval) {
		this.rePushInterval = rePushInterval;
	}
	public Integer getRePushCount() {
		return rePushCount;
	}
	public void setRePushCount(Integer rePushCount) {
		this.rePushCount = rePushCount;
	}
	public Integer getTimeOut() {
		return timeOut;
	}
	public void setTimeOut(Integer timeOut) {
		this.timeOut = timeOut;
	}
	public Integer getQueryInterval() {
		return queryInterval;
	}
	public void setQueryInterval(Integer queryInterval) {
		this.queryInterval = queryInterval;
	}
	public Integer getQueryCount() {
		return queryCount;
	}
	public void setQueryCount(Integer queryCount) {
		this.queryCount = queryCount;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}
