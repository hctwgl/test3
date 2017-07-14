/**
 * 
 */
package com.ald.fanbei.api.common.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * @类描述：
 * @author suweili 2017年4月24日下午4:32:25
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum AfAgentOrderStatus {
	NEW("NEW", "新建"),
	ASSIGN("ASSIGN","已分配"),
	BUY("BUY","已购买");
    
    private String code;
    private String name;

    private static Map<String,AfAgentOrderStatus> codeRoleTypeMap = null;

    AfAgentOrderStatus(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static AfAgentOrderStatus findRoleTypeByCode(String code) {
        for (AfAgentOrderStatus roleType : AfAgentOrderStatus.values()) {
            if (roleType.getCode().equals(code)) {
                return roleType;
            }
        }
        return null;
    }

    
    public static Map<String,AfAgentOrderStatus> getCodeRoleTypeMap(){
        if(codeRoleTypeMap != null && codeRoleTypeMap.size() > 0){
            return codeRoleTypeMap;
        }
        codeRoleTypeMap = new HashMap<String, AfAgentOrderStatus>();
        for(AfAgentOrderStatus item:AfAgentOrderStatus.values()){
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
