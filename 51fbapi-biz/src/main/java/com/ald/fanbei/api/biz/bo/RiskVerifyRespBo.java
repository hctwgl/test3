package com.ald.fanbei.api.biz.bo;

public class RiskVerifyRespBo extends RiskRespBo {

	private String result;

	private String data;

	private String orderNo;// 我们自己生成的订单号

	private String consumerNo;// 用户编号

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
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

}
