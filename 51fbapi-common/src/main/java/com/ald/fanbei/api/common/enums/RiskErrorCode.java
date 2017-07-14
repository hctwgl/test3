package com.ald.fanbei.api.common.enums;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;


/**
 * 
 * @类描述：弱风控失败原因
 * @author xiaotianjian 2017年7月6日下午4:03:25
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum RiskErrorCode {

	AUTH_AMOUNT_LIMIT("101", "额度超过限制"),
	OVERDUE_BORROW_CASH("102", "有逾期借款"),
	OVERDUE_BORROW("103", "有逾期消费分期"),
	OTHER_RULE("104", "其他规则");
	

    private String    code;

    private String name;


    
    private static Map<String,RiskErrorCode> codeRoleTypeMap = null;

    RiskErrorCode(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static RiskErrorCode findRoleTypeByCode(String code) {
        for (RiskErrorCode roleType : RiskErrorCode.values()) {
            if (StringUtils.equals(code, roleType.getCode())) {
                return roleType;
            }
        }
        return null;
    }

    
    public static Map<String,RiskErrorCode> getCodeRoleTypeMap(){
        if(codeRoleTypeMap != null && codeRoleTypeMap.size() > 0){
            return codeRoleTypeMap;
        }
        codeRoleTypeMap = new HashMap<String, RiskErrorCode>();
        for(RiskErrorCode item:RiskErrorCode.values()){
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
