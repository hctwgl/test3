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

    REGIST(1, "注册短信"), MODIFIED_PASS(2, "找回密码");

    private int    code;

    private String name;


    
    private static Map<Integer,SmsType> codeRoleTypeMap = null;

    SmsType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static SmsType findRoleTypeByCode(int code) {
        for (SmsType roleType : SmsType.values()) {
            if (roleType.getCode() == code) {
                return roleType;
            }
        }
        return null;
    }

    
    public static Map<Integer,SmsType> getCodeRoleTypeMap(){
        if(codeRoleTypeMap != null && codeRoleTypeMap.size() > 0){
            return codeRoleTypeMap;
        }
        codeRoleTypeMap = new HashMap<Integer, SmsType>();
        for(SmsType item:SmsType.values()){
            codeRoleTypeMap.put(item.getCode(), item);
        }
        return codeRoleTypeMap;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


}
