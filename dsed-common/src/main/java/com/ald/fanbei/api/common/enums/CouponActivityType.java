package com.ald.fanbei.api.common.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @类描述：
 * @author chenqiwei 2018年1月23日下午8:34:01
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum CouponActivityType {

    FIRST_SINGLE("FIRST_SINGLE","新用户专享首单"),
    EXCLUSIVE_CREDIT("EXCLUSIVE_CREDIT","新用户信用专享"),
    FIRST_LOAN("FIRST_LOAN","新用户首次借钱");
	
    private String code;
    private String name;
    
    private static Map<String,CouponActivityType> codeRoleTypeMap = null;

    CouponActivityType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static CouponActivityType findRoleTypeByCode(String code) {
        for (CouponActivityType roleType : CouponActivityType.values()) {
            if (roleType.getCode().equals(code)) {
                return roleType;
            }
        }
        return null;
    }

    
    public static Map<String,CouponActivityType> getCodeRoleTypeMap(){
        if(codeRoleTypeMap != null && codeRoleTypeMap.size() > 0){
            return codeRoleTypeMap;
        }
        codeRoleTypeMap = new HashMap<String, CouponActivityType>();
        for(CouponActivityType item:CouponActivityType.values()){
            codeRoleTypeMap.put(item.getCode(), item);
        }
        return codeRoleTypeMap;
    }
    
    public static CouponActivityType getByCode(String code){
    	return getCodeRoleTypeMap().get(code);
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
