package com.ald.fanbei.api.common.enums;

public enum AfBorrowCashRejectType {
	PASS("通过"),
	NO_AUTHZ("未认证"),
	QUOTA_TOO_SMALL("额度小于最小借款额"),
	NO_PASS_STRO_RISK("未通过强风控审核"),
	NO_PASS_WEAK_RISK("未通过弱风控审核"),
	SWITCH_OFF("借钱开关关闭");
    
    public String desz;

    AfBorrowCashRejectType(String desz) {
        this.desz = desz;
    }
}
