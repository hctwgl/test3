package com.ald.fanbei.api.common.enums;

public enum JsdRepayType {
    WITHHOLD( "代扣还款","WITHHOLD"),
    INITIATIVE( "主动还款","INITIATIVE"),
    OFFLINE( "管理员线下还款","OFFLINE"),
    COLLECTION( "催收逾期还款","COLLECTION"),
    REVIEW_COLLECTION( "管理员审批催收还款","REVIEW_COLLECTION"),
    SETTLE_SYSTEM("清结算系统还款","SETTLESYSTEM");

    private String code;
    private String xgxyCode;


    JsdRepayType(String code, String xgxyCode) {
        this.code = code;
        this.xgxyCode = xgxyCode;
    }

    public static JsdRepayType findRoleTypeByXgxyCode(String value) {
        for (JsdRepayType roleType : JsdRepayType.values()) {
            if (roleType.getXgxyCode().equals(value)) {
                return roleType;
            }
        }
        return null;
    }
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getXgxyCode() {
        return xgxyCode;
    }

    public void setXgxyCode(String xgxyCode) {
        this.xgxyCode = xgxyCode;
    }
}
