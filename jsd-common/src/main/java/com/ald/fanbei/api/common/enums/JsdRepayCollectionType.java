package com.ald.fanbei.api.common.enums;

/**
 * 还款通知催收平台类型
 * @author chengkang
 */
public enum JsdRepayCollectionType {
	 APP("10", "app线上还款"),
	 OFFLINE("20", "线下还款");

    private String code;
    private String name;

    JsdRepayCollectionType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static JsdRepayCollectionType findRoleTypeByCode(String code) {
        for (JsdRepayCollectionType roleType : JsdRepayCollectionType.values()) {
            if (roleType.getCode().equals(code)) {
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
