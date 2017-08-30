package com.ald.fanbei.api.common.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @类描述：魔蝎返回code
 * @author chengkang 2017年3月4日上午11:08:57
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum MoXieResCodeType {
	
	DONOTHING("-1", "无操作退回"), 
	ZERO("0", "失败"), 
	ONE("1", "成功"), 
	TWO("2", "认证中"), 
	FIFTY("50", "认证失败");

	private String code;

	private String description;

	private static Map<String, MoXieResCodeType> codeMap = null;

	MoXieResCodeType(String code, String description) {
		this.code = code;
		this.description = description;
	}

	public static MoXieResCodeType findEnumByCode(String code) {
		for (MoXieResCodeType goodSource : MoXieResCodeType.values()) {
			if (goodSource.getCode().equals(code)) {
				return goodSource;
			}
		}
		return null;
	}

	public static Map<String, MoXieResCodeType> getCodeEnumMap() {
		if (codeMap != null && codeMap.size() > 0) {
			return codeMap;
		}
		codeMap = new HashMap<String, MoXieResCodeType>();
		for (MoXieResCodeType item : MoXieResCodeType.values()) {
			codeMap.put(item.getCode(), item);
		}
		return codeMap;
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
