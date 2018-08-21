package com.ald.fanbei.api.common.enums;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * 重试事件类型
 * @author wujun
 * @version 1.0.0 初始化
 * @date 2018年3月2日下午1:55:41
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum RetryEventType {

    PUSH("pushEdspayRetry", "重推事件"), QUERY("queryEdspayRetry", "查询事件");

    private String    code;

    private String name;


    
    private static Map<String,RetryEventType> codeRoleTypeMap = null;

    RetryEventType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static RetryEventType findRoleTypeByCode(String code) {
        for (RetryEventType roleType : RetryEventType.values()) {
            if (StringUtils.equals(code, roleType.getCode())) {
                return roleType;
            }
        }
        return null;
    }

    
    public static Map<String,RetryEventType> getCodeRoleTypeMap(){
        if(codeRoleTypeMap != null && codeRoleTypeMap.size() > 0){
            return codeRoleTypeMap;
        }
        codeRoleTypeMap = new HashMap<String, RetryEventType>();
        for(RetryEventType item:RetryEventType.values()){
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
