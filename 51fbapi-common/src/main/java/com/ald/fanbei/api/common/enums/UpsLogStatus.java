package com.ald.fanbei.api.common.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @类描述：
 * @author xiaotianjian 2017年3月31日下午4:07:26
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum UpsLogStatus {

	NEW("NEW", "新建"),
	SUCCESS("SUCCESS", "成功"),
	FAIL("FAIL", "失败");

    private String code;
    private String name;
    
    private static Map<String,UpsLogStatus> codeRoleTypeMap = null;

    UpsLogStatus(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static UpsLogStatus findRoleTypeByCode(String code) {
        for (UpsLogStatus roleType : UpsLogStatus.values()) {
            if (roleType.getCode().equals(code)) {
                return roleType;
            }
        }
        return null;
    }

    
    public static Map<String,UpsLogStatus> getCodeRoleTypeMap(){
        if(codeRoleTypeMap != null && codeRoleTypeMap.size() > 0){
            return codeRoleTypeMap;
        }
        codeRoleTypeMap = new HashMap<String, UpsLogStatus>();
        for(UpsLogStatus item:UpsLogStatus.values()){
            codeRoleTypeMap.put(item.getCode(), item);
        }
        return codeRoleTypeMap;
    }
    
    public static UpsLogStatus getByCode(String code){
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
