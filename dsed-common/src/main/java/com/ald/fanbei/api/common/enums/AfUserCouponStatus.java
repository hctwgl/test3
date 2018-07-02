package com.ald.fanbei.api.common.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * @author luoxiao @date 2018/4/17 17:45
 * @类描述：用户优惠券使用状态
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum AfUserCouponStatus {
    EXPIRE("EXPIRE", "过期"),
    NOUSE("NOUSE", "未使用"),
    USED("USED", "已使");

    private String code;
    private String name;

    private static Map<String,AfUserCouponStatus> codeRoleTypeMap = null;

    AfUserCouponStatus(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static AfUserCouponStatus findRoleTypeByCode(String code) {
        for (AfUserCouponStatus roleType : AfUserCouponStatus.values()) {
            if (roleType.getCode().equals(code)) {
                return roleType;
            }
        }
        return null;
    }


    public static Map<String,AfUserCouponStatus> getCodeRoleTypeMap(){
        if(codeRoleTypeMap != null && codeRoleTypeMap.size() > 0){
            return codeRoleTypeMap;
        }
        codeRoleTypeMap = new HashMap<String, AfUserCouponStatus>();
        for(AfUserCouponStatus item:AfUserCouponStatus.values()){
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
