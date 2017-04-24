package com.ald.fanbei.api.common.enums;

import java.util.HashMap;
import java.util.Map;


/**
 * 
 * @类描述：
 * @author hexin 2017年2月16日下午20:53:26
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum MobileStatus {

	SUCCESS("10000000", "交易成功"),
	YES("Y","已认证"),
	NO("N","未认证"),
	WAIT("W","认证中");
    
    private String code;
    private String name;

    private static Map<String,MobileStatus> codeRoleTypeMap = null;

    MobileStatus(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static MobileStatus findRoleTypeByCode(String code) {
        for (MobileStatus roleType : MobileStatus.values()) {
            if (roleType.getCode().equals(code)) {
                return roleType;
            }
        }
        return null;
    }

    
    public static Map<String,MobileStatus> getCodeRoleTypeMap(){
        if(codeRoleTypeMap != null && codeRoleTypeMap.size() > 0){
            return codeRoleTypeMap;
        }
        codeRoleTypeMap = new HashMap<String, MobileStatus>();
        for(MobileStatus item:MobileStatus.values()){
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
