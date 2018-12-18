package com.ald.fanbei.api.common.enums;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;


/**
 * 
 * @类描述：
 * @author chefeipeng 2018年6月19日下午5:00:34
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum RepayType {

    WITHHOLD("WITHHOLD", "代扣"), KUAIJIE("KUAIJIE", "快捷支付(有短信)"),XIEYI("XIEYI", "协议支付");
    private String    code;

    private String name;

    private static Map<String,RepayType> codeRoleTypeMap = null;

    RepayType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static RepayType findRoleTypeByCode(String code) {
        for (RepayType roleType : RepayType.values()) {
            if (StringUtils.equals(code, roleType.getCode())) {
                return roleType;
            }
        }
        return null;
    }

    
    public static Map<String,RepayType> getCodeRoleTypeMap(){
        if(codeRoleTypeMap != null && codeRoleTypeMap.size() > 0){
            return codeRoleTypeMap;
        }
        codeRoleTypeMap = new HashMap<String, RepayType>();
        for(RepayType item: RepayType.values()){
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
