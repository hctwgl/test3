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

	private String token;//公积金方的token
	private String data;//用户具体的公积金信息
	private String userId;//用户id
	private String orderSn;//公积金方的订单号
	private String orderNo;//风控单号
	private String signInfo;//加签
	
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
		this.put("token", token);
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
		this.put("data", data);
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
		this.put("userId", userId);
	}
	public String getOrderSn() {
		return orderSn;
	}
	public void setOrderSn(String orderSn) {
		this.orderSn = orderSn;
		this.put("orderSn", orderSn);
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
		this.put("orderNo", orderNo);
	}
	public String getSignInfo() {
		return signInfo;
	}
	public void setSignInfo(String signInfo) {
		this.signInfo = signInfo;
		this.put("signInfo", signInfo);
	}
	@Override
	public String toString() {
		return "RiskNotifyReqBo [token=" + token + ", data=" + data
				+ ", userId=" + userId + ", orderSn=" + orderSn + ", orderNo="
				+ orderNo + ", signInfo=" + signInfo + "]";
	}
	
}
