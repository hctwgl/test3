package com.ald.fanbei.api.common.enums;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;


/**
 * 
 * @类描述：
 * @author chefeipeng 2018年6月19日下午5:00:34
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum GenderType {

    M("M", "男"), F("F", "女"),U("U","未知");
    private String    code;

    private String name;

    private static Map<String,GenderType> codeRoleTypeMap = null;

    GenderType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static GenderType findRoleTypeByCode(String code) {
        for (GenderType roleType : GenderType.values()) {
            if (StringUtils.equals(code, roleType.getCode())) {
                return roleType;
            }
        }
        return null;
    }

    
    public static Map<String,GenderType> getCodeRoleTypeMap(){
        if(codeRoleTypeMap != null && codeRoleTypeMap.size() > 0){
            return codeRoleTypeMap;
        }
        codeRoleTypeMap = new HashMap<String, GenderType>();
        for(GenderType item: GenderType.values()){
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
