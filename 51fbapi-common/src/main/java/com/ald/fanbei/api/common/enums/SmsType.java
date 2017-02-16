package com.ald.fanbei.api.common.enums;

import java.util.HashMap;
import java.util.Map;


/**
 * 
 * @类描述：
 * @author Xiaotianjian 2017年1月19日下午4:48:07
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum SmsType {

    REGIST("R", "注册短信"), 
    FORGET_PASS("F", "忘记密码验证短信"),
    MOBILE_BIND("M", "手机绑定"),
    COMMON("C","通用");

    private String code;

    private String name;
    
    private static Map<String,SmsType> codeRoleTypeMap = null;

    SmsType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static SmsType findRoleTypeByCode(String code) {
        for (SmsType roleType : SmsType.values()) {
            if (roleType.getCode().equalsIgnoreCase(code)) {
                return roleType;
            }
        }
        return null;
    }

    
    public static Map<String,SmsType> getCodeRoleTypeMap(){
        if(codeRoleTypeMap != null && codeRoleTypeMap.size() > 0){
            return codeRoleTypeMap;
        }
        codeRoleTypeMap = new HashMap<String, SmsType>();
        for(SmsType item:SmsType.values()){
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
