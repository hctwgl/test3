/**
 * 
 */
package com.ald.fanbei.api.common.enums;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * @类描述：
 * @author suweili 2017年3月11日下午12:44:34
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum UserStatus {
	NORMAL("NORMAL", "正常"), FROZEN("FROZEN", "冻结");

    private String    code;

    private String name;


    

    UserStatus(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static PayType findRoleTypeByCode(String code) {
        for (PayType roleType : PayType.values()) {
            if (StringUtils.equals(code, roleType.getCode())) {
                return roleType;
            }
        }
        return null;
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
