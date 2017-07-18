package com.ald.fanbei.api.common.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @类描述：
 * @author xiaotianjian 2017年2月7日下午2:20:43
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum UserCouponSource {

	
	REGIST("REGIST", "注册"), 
    INVITE("INVITE", "邀请"),
    SIGNIN("SIGNIN", "签到"),

    SPECIAL("SPECIAL", "邀请");
	
    private String code;
    private String name;

    private static Map<String,UserCouponSource> codeRoleTypeMap = null;

    UserCouponSource(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static UserCouponSource findRoleTypeByCode(String code) {
        for (UserCouponSource roleType : UserCouponSource.values()) {
            if (roleType.getCode().equals(code)) {
                return roleType;
            }
        }
        return null;
    }

    
    public static Map<String,UserCouponSource> getCodeRoleTypeMap(){
        if(codeRoleTypeMap != null && codeRoleTypeMap.size() > 0){
            return codeRoleTypeMap;
        }
        codeRoleTypeMap = new HashMap<String, UserCouponSource>();
        for(UserCouponSource item:UserCouponSource.values()){
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
