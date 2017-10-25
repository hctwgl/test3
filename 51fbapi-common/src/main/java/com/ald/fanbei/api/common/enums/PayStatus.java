package com.ald.fanbei.api.common.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @类描述：
 * @author xiaotianjian 2017年3月24日下午9:19:19
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum PayStatus {
	
	NOTPAY("N", "未支付"),
	DEALING("D", "支付中"),
	PAYED("P", "已支付"),
	REFUND("R", "退款");
	
    private String code;
    private String name;

    private static Map<String,PayStatus> codeRoleTypeMap = null;

    PayStatus(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static PayStatus findRoleTypeByCode(String code) {
        for (PayStatus roleType : PayStatus.values()) {
            if (roleType.getCode().equals(code)) {
                return roleType;
            }
        }
        return null;
    }

    
    public static Map<String,PayStatus> getCodeRoleTypeMap(){
        if(codeRoleTypeMap != null && codeRoleTypeMap.size() > 0){
            return codeRoleTypeMap;
        }
        codeRoleTypeMap = new HashMap<String, PayStatus>();
        for(PayStatus item:PayStatus.values()){
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
