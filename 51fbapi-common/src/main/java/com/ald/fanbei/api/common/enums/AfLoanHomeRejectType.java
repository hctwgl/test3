package com.ald.fanbei.api.common.enums;

public enum AfLoanHomeRejectType {
	PASS("通过"), 
	NO_AUTHZ("未认证"),
	QUOTA_TOO_SMALL("额度小于最快借款额"),
	NO_PASS_STRO_RISK("未通过强风控审核"),
	NO_PASS_WEAK_RISK("未通过弱风控审核");
    
    public String name;

    AfLoanHomeRejectType(String name) {
        this.name = name;
    }
}
