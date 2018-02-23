package com.ald.fanbei.api.biz.bo;

public class AuthCallbackBo {
	
	private String orderNo;
	
	private String consumerNo;
	
	private String authItem;
	
	private String code;
	
	private String result;
	
	

	public AuthCallbackBo(String orderNo, String consumerNo, String authItem, String result) {
		
		this.orderNo = orderNo;
		this.consumerNo = consumerNo;
		this.authItem = authItem;
		this.result = result;
	}
	
	

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getConsumerNo() {
		return consumerNo;
	}

	public void setConsumerNo(String consumerNo) {
		this.consumerNo = consumerNo;
	}

	public String getAuthItem() {
		return authItem;
	}

	public void setAuthItem(String authItem) {
		this.authItem = authItem;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	
}
