/**
 * 
 */
package com.ald.fanbei.api.common.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * @类描述：
 * @author suweili 2017年4月18日上午10:15:23
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum ContactRelationType {
	father("father", "父亲"), 
	mother("mother", "母亲"),
	spouse("spouse", "配偶"),
	child("child", "子女"),
	other_realative("other_realative", "其他亲属"),
	friend("friend", "朋友"),
	coworker("coworker", "同事"),
	others("others", "其他");
    
    private String code;
    private String name;

    private static Map<String,ContactRelationType> codeRoleTypeMap = null;

    ContactRelationType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static ContactRelationType findRoleTypeByCode(String code) {
        for (ContactRelationType roleType : ContactRelationType.values()) {
            if (roleType.getCode().equals(code)) {
                return roleType;
            }
        }
        return null;
    }
    public static ContactRelationType findRoleTypeByName(String name) {
        for (ContactRelationType roleType : ContactRelationType.values()) {
            if (roleType.getName().equals(name)) {
                return roleType;
            }
        }
        return null;
    }
    
    public static Map<String,ContactRelationType> getCodeRoleTypeMap(){
        if(codeRoleTypeMap != null && codeRoleTypeMap.size() > 0){
            return codeRoleTypeMap;
        }
        codeRoleTypeMap = new HashMap<String, ContactRelationType>();
        for(ContactRelationType item:ContactRelationType.values()){
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
