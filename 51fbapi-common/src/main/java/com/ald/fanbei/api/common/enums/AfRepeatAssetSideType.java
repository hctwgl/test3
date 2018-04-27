package com.ald.fanbei.api.common.enums;

/**
 * 重发机制下和资产方同步更新债权信息类型
 * @author chengkang
 */
public enum AfRepeatAssetSideType {
	REFUND_ASSETSIDE("refundNoticeAssetside", "admin退货通知资产方");
    
    private String code;
    private String name;

    AfRepeatAssetSideType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static AfRepeatAssetSideType findRoleTypeByCode(String code) {
        for (AfRepeatAssetSideType roleType : AfRepeatAssetSideType.values()) {
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
