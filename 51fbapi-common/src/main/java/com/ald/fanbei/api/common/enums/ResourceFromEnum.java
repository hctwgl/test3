package com.ald.fanbei.api.common.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @类描述： 客户端 访问的资源来源
 * @author weiqingeng
 * @date 2018年3月15日上午10:15:58
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum ResourceFromEnum {

	BANNER("BANNER", "轮播"),
	SPECIAL("SPECIAL", "专场"),

	;
	private String code;
	private String description;

	private static Map<String, ResourceFromEnum> codeRoleTypeMap = null;

	ResourceFromEnum(String code, String description) {
		this.code = code;
		this.description = description;
	}

	public static ResourceFromEnum findEnumByCode(String code) {
		for (ResourceFromEnum goodSource : ResourceFromEnum.values()) {
			if (goodSource.getCode().equals(code)) {
				return goodSource;
			}
		}
		return null;
	}

	public static Map<String, ResourceFromEnum> getCodeRoleTypeMap() {
		if (codeRoleTypeMap != null && codeRoleTypeMap.size() > 0) {
			return codeRoleTypeMap;
		}
		codeRoleTypeMap = new HashMap<String, ResourceFromEnum>();
		for (ResourceFromEnum item : ResourceFromEnum.values()) {
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
