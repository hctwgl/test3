package com.ald.fanbei.api.common.enums;

import java.util.HashMap;
import java.util.Map;


/**
 * 
 * @类描述：
 * @author xiaotianjian 2017年3月25日下午4:53:49
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum OrderSecType {

	JIU_DIAN("JIUDIAN", "酒店"), 
	WAI_MAI("WAIMAI", "外卖"),
	DAI_JIA("DAIJIA", "代驾"),
	DIAN_YING("DIANYING", "电影"),
	PAO_TUI("PAOTUI", "随意购"),
	JIA_DIAN_QING_XI("JIADIANQINGXI", "家电清洗"),
	JIA_DIAN_WEI_XIU("JIADIANWEIXIU", "家电维修");
    
    private String code;
    private String name;

    private static Map<String,OrderSecType> codeRoleTypeMap = null;

    OrderSecType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static OrderSecType findRoleTypeByCode(String code) {
        for (OrderSecType roleType : OrderSecType.values()) {
            if (roleType.getCode().equals(code)) {
                return roleType;
            }
        }
        return null;
    }

    
    public static Map<String,OrderSecType> getCodeRoleTypeMap(){
        if(codeRoleTypeMap != null && codeRoleTypeMap.size() > 0){
            return codeRoleTypeMap;
        }
        codeRoleTypeMap = new HashMap<String, OrderSecType>();
        for(OrderSecType item:OrderSecType.values()){
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
