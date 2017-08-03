package com.ald.fanbei.api.common.enums;


/**
 * 
 * @类描述：业务访问记录类型
 * @author chengkang 2017-07-19 16:26:32
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum AfBusinessAccessRecordsRefType {

	LOANSUPERMARKET("LOANSUPERMARKET", "借款超市访问");
    
    private String code;
    private String name;

    AfBusinessAccessRecordsRefType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static AfBusinessAccessRecordsRefType findRoleTypeByCode(String code) {
        for (AfBusinessAccessRecordsRefType roleType : AfBusinessAccessRecordsRefType.values()) {
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
