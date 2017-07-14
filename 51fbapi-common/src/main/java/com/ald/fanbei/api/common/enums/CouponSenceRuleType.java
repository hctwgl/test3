package com.ald.fanbei.api.common.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @类描述：
 * @author xiaotianjian 2017年2月7日下午2:34:01
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum CouponSenceRuleType {

    REGIST("REGIST", "注册"),
    SIGNIN("SIGNIN", "签到"),
    AUTHNAME("AUTHNAME", "实名认证"),
    PICK("PICK", "领取"),
    INVITE("INVITE", "邀请"),
    CREDITAUTH("CREDITAUTH", "信用认证");
    

    private String code;
    private String name;
    
    private static Map<String,CouponSenceRuleType> codeRoleTypeMap = null;

    CouponSenceRuleType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static CouponSenceRuleType findRoleTypeByCode(String code) {
        for (CouponSenceRuleType roleType : CouponSenceRuleType.values()) {
            if (roleType.getCode().equals(code)) {
                return roleType;
            }
        }
        return null;
    }

    
    public static Map<String,CouponSenceRuleType> getCodeRoleTypeMap(){
        if(codeRoleTypeMap != null && codeRoleTypeMap.size() > 0){
            return codeRoleTypeMap;
        }
        codeRoleTypeMap = new HashMap<String, CouponSenceRuleType>();
        for(CouponSenceRuleType item:CouponSenceRuleType.values()){
            codeRoleTypeMap.put(item.getCode(), item);
        }
        return codeRoleTypeMap;
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
