package com.ald.fanbei.api.common.enums;

public enum TradeOrderStatus {

	NEW("NEW", "新建"), REFUNDING("REFUNDING", "退款中"), REFUND("REFUND", "退款"), EXTRACT("EXTRACT", "提现"), EXTRACTING("EXTRACTING", "提现中");

	private String code;
	private String name;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	private TradeOrderStatus(String code, String name) {
		this.code = code;
		this.name = name;
	}

}
