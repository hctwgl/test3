package com.ald.fanbei.api.common.enums;

import java.util.HashMap;
import java.util.Map;


/**
 * 
 * @类描述：菠萝觅支付类型
 * @author xiaotianjian 2017年8月10日下午7:36:30
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum BoluomePayType {

	WECHAT("WECHAT", "微信"), 
	DEBITCARD("DEBITCARD", "借记卡");
    
    private String code;
    private String name;

    private static Map<String,BoluomePayType> codeRoleTypeMap = null;

    BoluomePayType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static BoluomePayType findRoleTypeByCode(String code) {
        for (BoluomePayType roleType : BoluomePayType.values()) {
            if (roleType.getCode().equals(code)) {
                return roleType;
            }
        }
        return null;
    }

    
    public static Map<String,BoluomePayType> getCodeRoleTypeMap(){
        if(codeRoleTypeMap != null && codeRoleTypeMap.size() > 0){
            return codeRoleTypeMap;
        }
        codeRoleTypeMap = new HashMap<String, BoluomePayType>();
        for(BoluomePayType item:BoluomePayType.values()){
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
