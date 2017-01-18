package com.ald.fanbei.common.enums;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;


/**
 * 
 *@类描述：支付类型枚举
 *@author 陈金虎 2017年1月16日 下午11:48:42
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum PayType {

    ALIPAY("ALIPAY", "支付宝"), WECHAT("WECHAT", "微信");

    private String    code;

    private String name;


    
    private static Map<String,PayType> codeRoleTypeMap = null;

    PayType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static PayType findRoleTypeByCode(String code) {
        for (PayType roleType : PayType.values()) {
            if (StringUtils.equals(code, roleType.getCode())) {
                return roleType;
            }
        }
        return null;
    }

    
    public static Map<String,PayType> getCodeRoleTypeMap(){
        if(codeRoleTypeMap != null && codeRoleTypeMap.size() > 0){
            return codeRoleTypeMap;
        }
        codeRoleTypeMap = new HashMap<String, PayType>();
        for(PayType item:PayType.values()){
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
