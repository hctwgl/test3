package com.ald.fanbei.api.common.enums;

/**
 * 重发机制下和资产方同步更新债权信息类型
 * @author wujun
 */
public enum DsedRepeatAssetSideType {
	
	PREFINISH_NOTICE_ASSETSIDE("PREFINISH_NOTICE_ASSETSIDE", "提前结清通知资金方");
	
    private String code;
    private String name;

    DsedRepeatAssetSideType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static DsedRepeatAssetSideType findRoleTypeByCode(String code) {
        for (DsedRepeatAssetSideType roleType : DsedRepeatAssetSideType.values()) {
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
