package com.ald.fanbei.api.common.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @类描述：
 * @author hexin 2017年2月16日下午16:26:26
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum OrderType {

	MOBILE("MOBILE", "话费充值","MB"), 
	TAOBAO("TAOBAO", "淘宝订单","TB"),
	TMALL("TMALL", "天猫订单","TM"),
	AGENTBUY("AGENTBUY", "代买订单","DM"),
	SELFSUPPORT("SELFSUPPORT", "自营商品订单","SG"),

	BOLUOME("BOLUOME", "菠萝觅","BL"),

    TRADE("TRADE", "商圈订单", "TR");
    
    private String code;
    private String name;
    private String shortName;

    private static Map<String,InterestType> codeRoleTypeMap = null;
    private static List<String> needRecordPayFailCodes = null;

    OrderType(String code, String name,String shortName) {
        this.code = code;
        this.name = name;
        this.shortName = shortName;
    }

    public static InterestType findRoleTypeByCode(String code) {
        for (InterestType roleType : InterestType.values()) {
            if (roleType.getCode().equals(code)) {
                return roleType;
            }
        }
        return null;
    }

    public static List<String> getNeedRecordPayFailCodes(){
        if(needRecordPayFailCodes != null && needRecordPayFailCodes.size() > 0){
            return needRecordPayFailCodes;
        }
        needRecordPayFailCodes = new ArrayList<String>();
        for(InterestType item:InterestType.values()){
        	needRecordPayFailCodes.add(item.getCode());
        }
        return needRecordPayFailCodes;
    }
    
    
    public static Map<String,InterestType> getCodeRoleTypeMap(){
        if(codeRoleTypeMap != null && codeRoleTypeMap.size() > 0){
            return codeRoleTypeMap;
        }
        codeRoleTypeMap = new HashMap<String, InterestType>();
        for(InterestType item:InterestType.values()){
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

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	
}
