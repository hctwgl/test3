package com.ald.fanbei.api.common.enums;

import java.util.HashMap;
import java.util.Map;


/**
 * 
 * @类描述：
 * @author xiaotianjian 2017年3月25日下午3:38:45
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum ShopPlantFormType {

	BOLUOME("BOLUOME", "菠萝觅");
    
    private String code;
    private String name;

    private static Map<String,ShopPlantFormType> codeRoleTypeMap = null;

    ShopPlantFormType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static ShopPlantFormType findRoleTypeByCode(String code) {
        for (ShopPlantFormType roleType : ShopPlantFormType.values()) {
            if (roleType.getCode().equals(code)) {
                return roleType;
            }
        }
        return null;
    }

    
    public static Map<String,ShopPlantFormType> getCodeRoleTypeMap(){
        if(codeRoleTypeMap != null && codeRoleTypeMap.size() > 0){
            return codeRoleTypeMap;
        }
        codeRoleTypeMap = new HashMap<String, ShopPlantFormType>();
        for(ShopPlantFormType item:ShopPlantFormType.values()){
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
