package com.ald.fanbei.api.biz.third.util.common;

public enum RiskResultCode {
	PASS("10", "审核通过"), REFUSE("30", "审核拒绝"), HUM_PASS("20", "人审通过");
	
	public String code;
	public String desc;
	
	RiskResultCode(String code, String desc){
		this.code = code;
		this.desc = desc;
	}
	
}
