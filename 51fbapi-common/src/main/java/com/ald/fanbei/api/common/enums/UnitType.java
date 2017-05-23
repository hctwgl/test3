package com.ald.fanbei.api.common.enums;

import java.util.HashMap;
import java.util.Map;


/**
 * 
 * @类描述：
 * @author xiaotianjian 2017年3月25日下午3:55:03
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum UnitType {

	RMB("RMB", "人民币"),
	PERCENTAGE("PERCENTAGE", "百分比");
    
    private String code;
    private String name;

    private static Map<String,UnitType> codeRoleTypeMap = null;

    UnitType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static UnitType findRoleTypeByCode(String code) {
        for (UnitType roleType : UnitType.values()) {
            if (roleType.getCode().equals(code)) {
                return roleType;
            }
        }
        return null;
    }

    
    public static Map<String,UnitType> getCodeRoleTypeMap(){
        if(codeRoleTypeMap != null && codeRoleTypeMap.size() > 0){
            return codeRoleTypeMap;
        }
        codeRoleTypeMap = new HashMap<String, UnitType>();
        for(UnitType item:UnitType.values()){
            codeRoleTypeMap.put(item.getCode(), item);
        }
        return codeRoleTypeMap;
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
