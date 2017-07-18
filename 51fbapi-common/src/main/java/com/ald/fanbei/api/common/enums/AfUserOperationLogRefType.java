package com.ald.fanbei.api.common.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * @类现描述：用户特殊操作日志关联业务类型
 * @author chengkang 2017年6月4日 下午4:27:00
 * @version
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum AfUserOperationLogRefType {

	AFBORROWCASH("af_borrow_cash", "现金借款");

    private String code;
    private String name;
    
    private static Map<String,AfUserOperationLogRefType> codeRoleTypeMap = null;

    AfUserOperationLogRefType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static AfUserOperationLogRefType findRoleTypeByCode(String code) {
        for (AfUserOperationLogRefType roleType : AfUserOperationLogRefType.values()) {
            if (roleType.getCode().equals(code)) {
                return roleType;
            }
        }
        return null;
    }

    
    public static Map<String,AfUserOperationLogRefType> getCodeRoleTypeMap(){
        if(codeRoleTypeMap != null && codeRoleTypeMap.size() > 0){
            return codeRoleTypeMap;
        }
        codeRoleTypeMap = new HashMap<String, AfUserOperationLogRefType>();
        for(AfUserOperationLogRefType item:AfUserOperationLogRefType.values()){
            codeRoleTypeMap.put(item.getCode(), item);
        }
        return codeRoleTypeMap;
    }
    
    public static AfUserOperationLogRefType getByCode(String code){
    	return codeRoleTypeMap.get(code);
    }


	public String getName() {
		return name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setName(String name) {
		this.name = name;
	}


}
