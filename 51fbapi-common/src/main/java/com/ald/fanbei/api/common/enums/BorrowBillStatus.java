package com.ald.fanbei.api.common.enums;

import java.util.HashMap;
import java.util.Map;


/**
 * 
 * @类描述：
 * @author hexin 2017年2月9日下午2:14:26
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum BorrowBillStatus {

	YES("Y", "已还款"), 
	NO("N", "未还款"),
	FORBIDDEN("F", "冻结"),
	PART("P", "部分还款"), 
	OVERDUE("O","逾期"),
	CLOSE("C","关闭(针对于退款)");
    
    private String code;
    private String name;

    private static Map<String,BorrowBillStatus> codeRoleTypeMap = null;

    BorrowBillStatus(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static BorrowBillStatus findRoleTypeByCode(String code) {
        for (BorrowBillStatus roleType : BorrowBillStatus.values()) {
            if (roleType.getCode().equals(code)) {
                return roleType;
            }
        }
        return null;
    }

    
    public static Map<String,BorrowBillStatus> getCodeRoleTypeMap(){
        if(codeRoleTypeMap != null && codeRoleTypeMap.size() > 0){
            return codeRoleTypeMap;
        }
        codeRoleTypeMap = new HashMap<String, BorrowBillStatus>();
        for(BorrowBillStatus item:BorrowBillStatus.values()){
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
