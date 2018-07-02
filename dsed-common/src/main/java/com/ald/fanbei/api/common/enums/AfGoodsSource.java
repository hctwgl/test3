package com.ald.fanbei.api.common.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @类描述：商品来源枚举
 * @author chengkang 2017年6月16日下午5:57:51
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum AfGoodsSource {

	TAOBAO("TAOBAO", "淘宝"), TMALL("TMALL", "天猫"), SELFSUPPORT("SELFSUPPORT", "自营");

	private String code;

	private String description;

	private static Map<String, AfGoodsSource> codeRoleTypeMap = null;

	AfGoodsSource(String code, String description) {
		this.code = code;
		this.description = description;
	}

	public static AfGoodsSource findEnumByCode(String code) {
		for (AfGoodsSource goodSource : AfGoodsSource.values()) {
			if (goodSource.getCode().equals(code)) {
				return goodSource;
			}
		}
		return null;
	}

	public static Map<String, AfGoodsSource> getCodeRoleTypeMap() {
		if (codeRoleTypeMap != null && codeRoleTypeMap.size() > 0) {
			return codeRoleTypeMap;
		}
		codeRoleTypeMap = new HashMap<String, AfGoodsSource>();
		for (AfGoodsSource item : AfGoodsSource.values()) {
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
