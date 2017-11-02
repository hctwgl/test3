package com.ald.fanbei.api.common.enums;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;


/**
 * 
 * @类描述：虚拟商品类目代码
 * @author xiaotianjian 2017年7月6日下午4:03:25
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum VirtualGoodsCateogy {
	//category给菠萝觅使用
	TELEPHONE_CHARGE("100","HUAFEI", "话费充值"),
	LIU_LIANG("101","LIULIANG", "流量充值"),
	FUEL_CARD("102","JIAYOUKA", "加油卡"),
	Q_Q_COINS("200","", "Q币"),
	SOFTWARE("201","", "软件"),
	ACCOUNT_NUMBER("202","", "点卡账号");
	

    private String code;
    private String category;
    private String name;


    
    private static Map<String,VirtualGoodsCateogy> codeRoleTypeMap = null;

    VirtualGoodsCateogy(String code,String category, String name) {
        this.code = code;
        this.category = category;
        this.name = name;
    }

    public static VirtualGoodsCateogy findRoleTypeByCode(String code) {
        for (VirtualGoodsCateogy roleType : VirtualGoodsCateogy.values()) {
            if (StringUtils.equals(code, roleType.getCode())) {
                return roleType;
            }
        }
        return null;
    }
    
    public static VirtualGoodsCateogy findRoleTypeByCategory(String category) {
        for (VirtualGoodsCateogy roleType : VirtualGoodsCateogy.values()) {
            if (StringUtils.equals(category, roleType.getCategory())) {
                return roleType;
            }
        }
        return null;
    }

    
    public static Map<String,VirtualGoodsCateogy> getCodeRoleTypeMap(){
        if(codeRoleTypeMap != null && codeRoleTypeMap.size() > 0){
            return codeRoleTypeMap;
        }
        codeRoleTypeMap = new HashMap<String, VirtualGoodsCateogy>();
        for(VirtualGoodsCateogy item:VirtualGoodsCateogy.values()){
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

	/**
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * @param category the category to set
	 */
	public void setCategory(String category) {
		this.category = category;
	}

}
