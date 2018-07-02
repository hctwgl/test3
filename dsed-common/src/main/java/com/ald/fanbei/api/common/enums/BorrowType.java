package com.ald.fanbei.api.common.enums;

import java.util.HashMap;
import java.util.Map;


/**
 * 
 * @类描述：
 * @author hexin 2017年2月9日下午2:14:26
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum BorrowType {

	CASH("CASH", "现金"), 
	CONSUME("CONSUME", "消费分期"),
	CONSUME_TEMP("CONSUME_TEMP", "消费分期待确认"),
	TOCASH("TOCASH", "消费转换成现金借款"),
	TOCONSUME("TOCONSUME", "消费待确认转化成消费"),
    HOME_CONSUME("HOME_CONSUME", "租房分期"),
    LEASE("LEASE", "租赁分期");
    
    private String code;
    private String name;

    private static Map<String,BorrowType> codeRoleTypeMap = null;

    BorrowType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static BorrowType findRoleTypeByCode(String code) {
        for (BorrowType roleType : BorrowType.values()) {
            if (roleType.getCode().equals(code)) {
                return roleType;
            }
        }
        return null;
    }

    
    public static Map<String,BorrowType> getCodeRoleTypeMap(){
        if(codeRoleTypeMap != null && codeRoleTypeMap.size() > 0){
            return codeRoleTypeMap;
        }
        codeRoleTypeMap = new HashMap<String, BorrowType>();
        for(BorrowType item:BorrowType.values()){
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
