package com.ald.fanbei.api.biz.bo;

import java.util.HashMap;

/**
 * 51公积金信息通知风控
 * @author wujun
 * @version 1.0.0 初始化
 * @date 2018年3月22日下午3:20:41
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class newFundNotifyReqBo extends HashMap<String, String>{

	private static final long serialVersionUID = 4608466453298742175L;

	private String consumerNo;
	private String details;
	private String orderNo;
	private String orderSn;
	private String signInfo;
	
	public String getConsumerNo() {
		return consumerNo;
	}
	public void setConsumerNo(String consumerNo) {
		this.consumerNo = consumerNo;
		this.put("consumerNo", consumerNo);
	}
	public String getDetails() {
		return details;
	}
	public void setDetails(String details) {
		this.details = details;
		this.put("details", details);
	}
	public String getSignInfo() {
		return signInfo;
	}
	public void setSignInfo(String signInfo) {
		this.signInfo = signInfo;
		this.put("signInfo", signInfo);
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
		this.put("orderNo", orderNo);
	}
	/**
	 * @return the orderSn
	 */
	public String getOrderSn() {
		return orderSn;
	}
	/**
	 * @param orderSn the orderSn to set
	 */
	public void setOrderSn(String orderSn) {
		this.orderSn = orderSn;
		this.put("orderSn", orderSn);
	}

}
