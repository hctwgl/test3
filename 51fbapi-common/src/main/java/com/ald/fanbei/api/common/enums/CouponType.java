package com.ald.fanbei.api.common.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @类描述：
 * @author xiaotianjian 2017年2月7日下午2:34:01
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum CouponType {

	COMMON("COMMON", "通用"),
	MOBILE("MOBILE", "手机充值"),
	REPAYMENT("REPAYMENT", "还款"),
	FULLVOUCHER("FULLVOUCHER", "满减券"),
	CASH("CASH", "现金"),
	REBATE("REBATE", "返现(签到、注册)"),
	ACTIVITY("ACTIVITY", "会场券"),
	FREEINTEREST("FREEINTEREST","借款免息券");
    private String code;
    private String name;
    
    private static Map<String,CouponType> codeRoleTypeMap = null;

    CouponType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static CouponType findRoleTypeByCode(String code) {
        for (CouponType roleType : CouponType.values()) {
            if (roleType.getCode().equals(code)) {
                return roleType;
            }
        }
        return null;
    }

    
    public static Map<String,CouponType> getCodeRoleTypeMap(){
        if(codeRoleTypeMap != null && codeRoleTypeMap.size() > 0){
            return codeRoleTypeMap;
        }
        codeRoleTypeMap = new HashMap<String, CouponType>();
        for(CouponType item:CouponType.values()){
            codeRoleTypeMap.put(item.getCode(), item);
        }
        return codeRoleTypeMap;
    }
    
    public static CouponType getByCode(String code){
    	return getCodeRoleTypeMap().get(code);
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
