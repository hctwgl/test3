package com.ald.fanbei.api.common.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @类描述：
 * @author xiaotianjian 2017年5月2日上午10:24:17
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum RefundSource {

	USER("U", "用户主动退款"),
	PLANT_FORM("P", "平台退款");

    private String code;
    private String name;
    
    private static Map<String,RefundSource> codeRoleTypeMap = null;

    RefundSource(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static RefundSource findRoleTypeByCode(String code) {
        for (RefundSource roleType : RefundSource.values()) {
            if (roleType.getCode().equals(code)) {
                return roleType;
            }
        }
        return null;
    }

    
    public static Map<String,RefundSource> getCodeRoleTypeMap(){
        if(codeRoleTypeMap != null && codeRoleTypeMap.size() > 0){
            return codeRoleTypeMap;
        }
        codeRoleTypeMap = new HashMap<String, RefundSource>();
        for(RefundSource item:RefundSource.values()){
            codeRoleTypeMap.put(item.getCode(), item);
        }
        return codeRoleTypeMap;
    }
    
    public static RefundSource getByCode(String code){
    	return codeRoleTypeMap.get(code);
    }


	public String getName() {
		return name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setName(String name) {
		this.name = name;
	}


}
