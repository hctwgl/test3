package com.ald.fanbei.api.common.enums;

import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;

public enum AfLoanRejectType {
	PASS("通过", null), 
	NO_AUTHZ("未认证", FanbeiExceptionCode.LOAN_NO_AUTHZ),
	AUTHING("认证中", FanbeiExceptionCode.AUTHING),
	GO_BLD_AUTH("跳转白领贷认证",FanbeiExceptionCode.GO_BLD_AUTH),
	QUOTA_TOO_SMALL("额度小于最底借款额", FanbeiExceptionCode.LOAN_QUOTA_TOO_SMALL),
	NO_PASS_STRO_RISK("未通过强风控审核", FanbeiExceptionCode.LOAN_NO_PASS_STRO_RISK),
	NO_PASS_WEAK_RISK("未通过弱风控审核", FanbeiExceptionCode.LOAN_NO_PASS_WEAK_RISK),
	SWITCH_OFF("贷款开关关闭", FanbeiExceptionCode.LOAN_SWITCH_OFF);
    
    public String desz;
    public FanbeiExceptionCode exceptionCode;

    AfLoanRejectType(String desz, FanbeiExceptionCode exceptionCode) {
        this.desz = desz;
        this.exceptionCode = exceptionCode;
    }
}
