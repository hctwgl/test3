package com.ald.fanbei.api.common.enums;

import java.util.HashMap;
import java.util.Map;


/**
 * 
 * @类描述：
 * @author hexin 2017年2月28日下午9:52:26
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum RepaymentStatus {

	NEW("N", "新建状态"), 
	PROCESS("P","处理中"),
	YES("Y", "还款成功");
    
    private String code;
    private String name;

    private static Map<String,RepaymentStatus> codeRoleTypeMap = null;

    RepaymentStatus(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static RepaymentStatus findRoleTypeByCode(String code) {
        for (RepaymentStatus roleType : RepaymentStatus.values()) {
            if (roleType.getCode().equals(code)) {
                return roleType;
            }
        }
        return null;
    }

    
    public static Map<String,RepaymentStatus> getCodeRoleTypeMap(){
        if(codeRoleTypeMap != null && codeRoleTypeMap.size() > 0){
            return codeRoleTypeMap;
        }
        codeRoleTypeMap = new HashMap<String, RepaymentStatus>();
        for(RepaymentStatus item:RepaymentStatus.values()){
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
