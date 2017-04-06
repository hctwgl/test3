package com.ald.fanbei.api.common.enums;

import java.util.HashMap;
import java.util.Map;


/**
 * 
 * @类描述：
 * @author xiaotianjian 2017年2月7日下午2:20:43
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum CouponStatus {

	 EXPIRE("EXPIRE", "过期"), 
	 NOUSE("NOUSE", "未使用"),
	 USED("USED", "已经使用");
    
    private String code;
    private String name;

    private static Map<String,CouponStatus> codeRoleTypeMap = null;

    CouponStatus(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static CouponStatus findRoleTypeByCode(String code) {
        for (CouponStatus roleType : CouponStatus.values()) {
            if (roleType.getCode().equals(code)) {
                return roleType;
            }
        }
        return null;
    }

    
    public static Map<String,CouponStatus> getCodeRoleTypeMap(){
        if(codeRoleTypeMap != null && codeRoleTypeMap.size() > 0){
            return codeRoleTypeMap;
        }
        codeRoleTypeMap = new HashMap<String, CouponStatus>();
        for(CouponStatus item:CouponStatus.values()){
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
