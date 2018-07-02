package com.ald.fanbei.api.common.enums;

/**
 * 重发机制下和催收平台同步业务类型
 * @author chengkang
 */
public enum AfRepeatCollectionType {
	 APP_REPAYMENT("repaymentCollection", "app线上还款"), 
	 APP_RENEWAL("renewalCollection", "app线上续期"), 
	 ADMIN_OFFLINE_REPAYMENT("repayOffCollection", "admin线下还款");
    
    private String code;
    private String name;

    AfRepeatCollectionType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static AfRepeatCollectionType findRoleTypeByCode(String code) {
        for (AfRepeatCollectionType roleType : AfRepeatCollectionType.values()) {
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
