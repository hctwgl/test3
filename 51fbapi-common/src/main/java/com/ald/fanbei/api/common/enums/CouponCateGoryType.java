package com.ald.fanbei.api.common.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @类描述：
 * @author chenqiwei 2018年1月23日下午8:34:01
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum CouponCateGoryType {

    _FIRST_SINGLE_("_FIRST_SINGLE_","新用户专享首单"),
    _NEW_USER_BOLUOME_COUPON_("_NEW_USER_BOLUOME_COUPON_","新用户菠萝觅优惠券"),
    _EXCLUSIVE_CREDIT_("_EXCLUSIVE_CREDIT_","新用户信用专享"),
    _FIRST_SHOPPING_("_FIRST_SHOPPING_","新用户信用专享"),
    _FIRST_LOAN_("_FIRST_LOAN_","新用户首次借钱");
    

    private String code;
    private String name;
    
    private static Map<String,CouponCateGoryType> codeRoleTypeMap = null;

    CouponCateGoryType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static CouponCateGoryType findRoleTypeByCode(String code) {
        for (CouponCateGoryType roleType : CouponCateGoryType.values()) {
            if (roleType.getCode().equals(code)) {
                return roleType;
            }
        }
        return null;
    }

    
    public static Map<String,CouponCateGoryType> getCodeRoleTypeMap(){
        if(codeRoleTypeMap != null && codeRoleTypeMap.size() > 0){
            return codeRoleTypeMap;
        }
        codeRoleTypeMap = new HashMap<String, CouponCateGoryType>();
        for(CouponCateGoryType item:CouponCateGoryType.values()){
            codeRoleTypeMap.put(item.getCode(), item);
        }
        return codeRoleTypeMap;
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
