/**
 * 
 */
package com.ald.fanbei.api.common.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * @类描述：
 * @author suweili 2017年3月30日下午2:49:21
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum AfBorrowCashRepmentStatus {
	APPLY("A", "新建状态"), 
	PROCESS("P","处理中"),
	NO("N", "还款失败"),
	YES("Y", "还款成功");
    
    private String code;
    private String name;

    private static Map<String,AfBorrowCashRepmentStatus> codeRoleTypeMap = null;

    AfBorrowCashRepmentStatus(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static AfBorrowCashRepmentStatus findRoleTypeByCode(String code) {
        for (AfBorrowCashRepmentStatus roleType : AfBorrowCashRepmentStatus.values()) {
            if (roleType.getCode().equals(code)) {
                return roleType;
            }
        }
        return null;
    }

    
    public static Map<String,AfBorrowCashRepmentStatus> getCodeRoleTypeMap(){
        if(codeRoleTypeMap != null && codeRoleTypeMap.size() > 0){
            return codeRoleTypeMap;
        }
        codeRoleTypeMap = new HashMap<String, AfBorrowCashRepmentStatus>();
        for(AfBorrowCashRepmentStatus item:AfBorrowCashRepmentStatus.values()){
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
