package com.ald.fanbei.api.common.enums;

import java.util.HashMap;
import java.util.Map;


/**
 * 
 * @类描述：计息方式
 * @author xiaotianjian 2017年6月7日下午2:07:35
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum BorrowCalculateMethod {

	DENG_E_BEN_XI("DEBX", "等额本息"), 
	DENG_BEN_DENG_XI("DBDX", "等本等息");
    
    private String code;
    private String name;

    private static Map<String,BorrowCalculateMethod> codeRoleTypeMap = null;

    BorrowCalculateMethod(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static BorrowCalculateMethod findRoleTypeByCode(String code) {
        for (BorrowCalculateMethod roleType : BorrowCalculateMethod.values()) {
            if (roleType.getCode().equals(code)) {
                return roleType;
            }
        }
        return null;
    }

    
    public static Map<String,BorrowCalculateMethod> getCodeRoleTypeMap(){
        if(codeRoleTypeMap != null && codeRoleTypeMap.size() > 0){
            return codeRoleTypeMap;
        }
        codeRoleTypeMap = new HashMap<String, BorrowCalculateMethod>();
        for(BorrowCalculateMethod item:BorrowCalculateMethod.values()){
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