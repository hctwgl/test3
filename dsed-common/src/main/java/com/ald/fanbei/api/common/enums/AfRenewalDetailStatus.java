package com.ald.fanbei.api.common.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * @类描述：
 * @author fumeiai 2017年5月18日 15:45:21
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum AfRenewalDetailStatus {
	APPLY("A", "新建状态"), 
	PROCESS("P", "处理中"), 
	NO("N", "续期失败"), 
	YES("Y", "续期成功");

	private String code;
	private String name;

	private static Map<String, AfRenewalDetailStatus> codeRenewalTypeMap = null;

	AfRenewalDetailStatus(String code, String name) {
		this.code = code;
		this.name = name;
	}

	public static AfRenewalDetailStatus findRenewalTypeByCode(String code) {
		for (AfRenewalDetailStatus renewalType : AfRenewalDetailStatus.values()) {
			if (renewalType.getCode().equals(code)) {
				return renewalType;
			}
		}
		return null;
	}

	public static Map<String, AfRenewalDetailStatus> getCodeRenewalTypeMap() {
		if (codeRenewalTypeMap != null && codeRenewalTypeMap.size() > 0) {
			return codeRenewalTypeMap;
		}
		codeRenewalTypeMap = new HashMap<String, AfRenewalDetailStatus>();
		for (AfRenewalDetailStatus item : AfRenewalDetailStatus.values()) {
			codeRenewalTypeMap.put(item.getCode(), item);
		}
		return codeRenewalTypeMap;
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
