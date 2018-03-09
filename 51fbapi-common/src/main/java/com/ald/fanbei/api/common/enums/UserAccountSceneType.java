package com.ald.fanbei.api.common.enums;

public enum UserAccountSceneType {
    ONLINE("ONLINE", "线上分期"), 
    TRAIN("TRAIN", "培训分期"), 
    
    /** 借贷相关 */
    CASH("CASH", "现金贷"),
    BLD_LOAN("BLD_LOAN", "白领贷"),
	LOAN_TOTAL("LOAN_TOTAL", "借贷总额度");

    private String code;
    private String name;

    UserAccountSceneType(String code, String name) {
	this.code = code;
	this.name = name;
    }

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

}
