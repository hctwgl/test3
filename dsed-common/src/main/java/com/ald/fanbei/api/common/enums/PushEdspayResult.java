package com.ald.fanbei.api.common.enums;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * 债权推送钱包的最终处理结果
 * @author wujun
 * @version 1.0.0 初始化
 * @date 2018年3月1日下午5:53:57
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum PushEdspayResult {
	

	PUSHFAIL("pushfail", "推送钱包失败"), 
	REVIEWFAIL("reviewFail", "浙商审核失败"), 
	PAYFAIL("payFail", "浙商打款失败"), 
	PAYSUCCESS("paySuccess", "浙商打款成功");

    private String    code;

    private String name;


    
    private static Map<String,PushEdspayResult> codeMap = null;

    PushEdspayResult(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static PushEdspayResult findEnumByCode(String code) {
        for (PushEdspayResult roleType : PushEdspayResult.values()) {
            if (StringUtils.equals(code, roleType.getCode())) {
                return roleType;
            }
        }
        return null;
    }

    
    public static Map<String,PushEdspayResult> getCodeEnumMap(){
        if(codeMap != null && codeMap.size() > 0){
            return codeMap;
        }
        codeMap = new HashMap<String, PushEdspayResult>();
        for(PushEdspayResult item:PushEdspayResult.values()){
            codeMap.put(item.getCode(), item);
        }
        return codeMap;
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
