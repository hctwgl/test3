/**
 * 
 */
package com.ald.fanbei.api.common.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @类描述：人脸识别类型
 * @author xiaotianjian 2017年7月30日下午10:40:05
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum FaceType {
	YITU("YITU", "依图"),
	FACE_PLUS("FACE_PLUS","face++");
    
    private String code;
    private String name;

    private static Map<String,FaceType> codeRoleTypeMap = null;

    FaceType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static FaceType findRoleTypeByCode(String code) {
        for (FaceType roleType : FaceType.values()) {
            if (roleType.getCode().equals(code)) {
                return roleType;
            }
        }
        return null;
    }

    
    public static Map<String,FaceType> getCodeRoleTypeMap(){
        if(codeRoleTypeMap != null && codeRoleTypeMap.size() > 0){
            return codeRoleTypeMap;
        }
        codeRoleTypeMap = new HashMap<String, FaceType>();
        for(FaceType item:FaceType.values()){
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
