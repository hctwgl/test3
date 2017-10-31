package com.ald.fanbei.api.common.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @类描述：
 * @author xiaotianjian 2017年2月7日下午2:34:01
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum AccountLogType {

	CASH("CASH", "取现"),
	AU_SCORE("AU_SCORE", "授权分数"),
	AU_AMOUNT("AU_AMOUNT", "授权金额"),
	FREEZE("FREEZE", "冻结金额"),
	REBATE("REBATE", "返利"),
	SIGNIN("SIGNIN", "签到"),
	REGIST("REGIST", "新注册款");

    private String code;
    private String name;
    
    private static Map<String,AccountLogType> codeRoleTypeMap = null;

    AccountLogType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static AccountLogType findRoleTypeByCode(String code) {
        for (AccountLogType roleType : AccountLogType.values()) {
            if (roleType.getCode().equals(code)) {
                return roleType;
            }
        }
        return null;
    }

    
    public static Map<String,AccountLogType> getCodeRoleTypeMap(){
        if(codeRoleTypeMap != null && codeRoleTypeMap.size() > 0){
            return codeRoleTypeMap;
        }
        codeRoleTypeMap = new HashMap<String, AccountLogType>();
        for(AccountLogType item:AccountLogType.values()){
            codeRoleTypeMap.put(item.getCode(), item);
        }
        return codeRoleTypeMap;
    }
    
    public static AccountLogType getByCode(String code){
    	return codeRoleTypeMap.get(code);
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
