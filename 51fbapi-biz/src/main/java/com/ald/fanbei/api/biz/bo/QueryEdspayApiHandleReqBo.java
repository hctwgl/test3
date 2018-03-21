package com.ald.fanbei.api.biz.bo;

import java.util.HashMap;

/**
 * 
 * @author wujun
 * @version 1.0.0 初始化
 * @date 2018年3月1日上午11:31:04
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class QueryEdspayApiHandleReqBo extends HashMap<String, String>{

	private static final long serialVersionUID = 4608466453298742175L;
	private String sign;
	private String timestamp;
	private String data;
	
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
		this.put("data", data);
	}
	
}
