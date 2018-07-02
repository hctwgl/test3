package com.ald.fanbei.api.common.enums;

import java.util.HashMap;
import java.util.Map;


/**
 * 
 * @类描述：
 * @author hexin 2017年2月28日下午4:14:26
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum BankcardStatus {

	NEW("N", "新建状态"), 
	BIND("B", "绑定状态"),
	UNBIND("U", "审核关闭");
    
    private String code;
    private String name;

    private static Map<String,BankcardStatus> codeRoleTypeMap = null;

    BankcardStatus(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static BankcardStatus findRoleTypeByCode(String code) {
        for (BankcardStatus roleType : BankcardStatus.values()) {
            if (roleType.getCode().equals(code)) {
                return roleType;
            }
        }
        return null;
    }

    
    public static Map<String,BankcardStatus> getCodeRoleTypeMap(){
        if(codeRoleTypeMap != null && codeRoleTypeMap.size() > 0){
            return codeRoleTypeMap;
        }
        codeRoleTypeMap = new HashMap<String, BankcardStatus>();
        for(BankcardStatus item:BankcardStatus.values()){
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
