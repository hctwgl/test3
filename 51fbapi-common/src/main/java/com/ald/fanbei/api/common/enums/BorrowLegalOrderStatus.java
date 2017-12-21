package com.ald.fanbei.api.common.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @类描述：
 * @author hexin 2017年2月17日下午15:31:23
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum BorrowLegalOrderStatus {
	
	UNPAID("UNPAID", "UNPAID"),
	AWAIT_DELIVER("AWAIT_DELIVER", "待发货"),
	DELIVERED("DELIVERED", "已发货"),
	CONFIRM_RECEIVED("CONFIRM_RECEIVED", "已确认收货"),
	CLOSED("CLOSED", "订单关闭");
	
    private String code;
    private String name;

    private static Map<String,BorrowLegalOrderStatus> codeRoleTypeMap = null;

    BorrowLegalOrderStatus(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static BorrowLegalOrderStatus findRoleTypeByCode(String code) {
        for (BorrowLegalOrderStatus roleType : BorrowLegalOrderStatus.values()) {
            if (roleType.getCode().equals(code)) {
                return roleType;
            }
        }
        return null;
    }

    
    public static Map<String,BorrowLegalOrderStatus> getCodeRoleTypeMap(){
        if(codeRoleTypeMap != null && codeRoleTypeMap.size() > 0){
            return codeRoleTypeMap;
        }
        codeRoleTypeMap = new HashMap<String, BorrowLegalOrderStatus>();
        for(BorrowLegalOrderStatus item:BorrowLegalOrderStatus.values()){
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
