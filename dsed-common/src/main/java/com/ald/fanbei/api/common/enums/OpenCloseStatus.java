package com.ald.fanbei.api.common.enums;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;


/**
 * 
 *@类描述：支付类型枚举
 *@author 陈金虎 2017年1月16日 下午11:48:42
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum OpenCloseStatus {

	 O("O", "开启"), 
	 C("C", "关闭");
    
    private String code;
    private String name;


    OpenCloseStatus(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static OpenCloseStatus findRoleTypeByCode(String code) {
        for (OpenCloseStatus roleType : OpenCloseStatus.values()) {
            if (roleType.getCode().equals(code)) {
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
