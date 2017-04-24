package com.ald.fanbei.api.common.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @类描述：
 * @author xiaotianjian 2017年3月30日下午2:26:47
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum OrderRefundStatus {
	
	REFUNDING("REFUNDING", "退款中"),
	FAIL("FAIL", "退款失败"),
	FINISH("FINISH", "退款成功");
	
    private String code;
    private String name;

    private static Map<String,OrderRefundStatus> codeRoleTypeMap = null;

    OrderRefundStatus(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static OrderRefundStatus findRoleTypeByCode(String code) {
        for (OrderRefundStatus roleType : OrderRefundStatus.values()) {
            if (roleType.getCode().equals(code)) {
                return roleType;
            }
        }
        return null;
    }

    
    public static Map<String,OrderRefundStatus> getCodeRoleTypeMap(){
        if(codeRoleTypeMap != null && codeRoleTypeMap.size() > 0){
            return codeRoleTypeMap;
        }
        codeRoleTypeMap = new HashMap<String, OrderRefundStatus>();
        for(OrderRefundStatus item:OrderRefundStatus.values()){
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
