package com.ald.fanbei.api.common.enums;

import java.util.HashMap;
import java.util.Map;


/**
 * 
 * @类描述：
 * @author hexin 2017年2月16日下午20:53:26
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum SupplyCertifyStatus {

	SUCCESS("10000000", "交易成功"),
	A("A","未认证"),
	YES("Y","已认证"),
	NO("N","认证未通过"),
	WAIT("W","认证中");
    
    private String code;
    private String name;

    private static Map<String,SupplyCertifyStatus> codeRoleTypeMap = null;

    SupplyCertifyStatus(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static SupplyCertifyStatus findRoleTypeByCode(String code) {
        for (SupplyCertifyStatus roleType : SupplyCertifyStatus.values()) {
            if (roleType.getCode().equals(code)) {
                return roleType;
            }
        }
        return null;
    }

    
    public static Map<String,SupplyCertifyStatus> getCodeRoleTypeMap(){
        if(codeRoleTypeMap != null && codeRoleTypeMap.size() > 0){
            return codeRoleTypeMap;
        }
        codeRoleTypeMap = new HashMap<String, SupplyCertifyStatus>();
        for(SupplyCertifyStatus item:SupplyCertifyStatus.values()){
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
