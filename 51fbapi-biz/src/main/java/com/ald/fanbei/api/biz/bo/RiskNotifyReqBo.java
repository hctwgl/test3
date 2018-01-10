package com.ald.fanbei.api.biz.bo;

import java.util.HashMap;

/**
 * 51公积金异步回调bo
 * @author wujun
 * @version 1.0.0 初始化
 * @date 2018年1月10日下午2:29:52
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class RiskNotifyReqBo extends HashMap<String, String>{

	private static final long serialVersionUID = 4608466453298742175L;

	private String token;
	private String data;
	private String userId;
	private String orderSn;
	private String orderNo;
	private String signInfo;
	
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getOrderSn() {
		return orderSn;
	}
	public void setOrderSn(String orderSn) {
		this.orderSn = orderSn;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getSignInfo() {
		return signInfo;
	}
	public void setSignInfo(String signInfo) {
		this.signInfo = signInfo;
	}
	
}
