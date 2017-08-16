package com.ald.fanbei.api.common.enums;

public enum TradeWithdrawRecordStatus {
	APPLY("APPLY","申请/未审核"), TRANSED("TRANSED","已经打款"), TRANSEDING("TRANSEDING","打款中"),CLOSED("CLOSED","关闭");

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

	private TradeWithdrawRecordStatus(String code, String name) {
		this.code = code;
		this.name = name;
	}

}
