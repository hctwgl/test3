package com.ald.fanbei.api.common.enums;

/**
 * 还款通知催收平台类型
 * @author chengkang
 */
public enum AfRepayCollectionType {
	 APP("10", "app线上还款"),
	 OFFLINE("20", "线下还款");
    
    private String code;
    private String name;

    AfRepayCollectionType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static AfRepayCollectionType findRoleTypeByCode(String code) {
        for (AfRepayCollectionType roleType : AfRepayCollectionType.values()) {
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
