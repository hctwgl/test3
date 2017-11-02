package com.ald.fanbei.api.common.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @类描述：
 * @author fmai 2017年6月7日 11:32:26
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum RiskStatus {

	A("A", "未审核"), 
	NO("N", "未通过审核"), 
	PROCESS("P", "审核中"), 
	SECTOR("S", "部分认证标识"), 
	YES("Y", "已通过审核");

	private String code;
	private String name;

	private static Map<String, RiskStatus> codeRiskStatusMap = null;

	RiskStatus(String code, String name) {
		this.code = code;
		this.name = name;
	}

	public static RiskStatus findRoleTypeByCode(String code) {
		for (RiskStatus roleType : RiskStatus.values()) {
			if (roleType.getCode().equals(code)) {
				return roleType;
			}
		}
		return null;
	}

	public static Map<String, RiskStatus> getCodeRoleTypeMap() {
		if (codeRiskStatusMap != null && codeRiskStatusMap.size() > 0) {
			return codeRiskStatusMap;
		}
		codeRiskStatusMap = new HashMap<String, RiskStatus>();
		for (RiskStatus item : RiskStatus.values()) {
			codeRiskStatusMap.put(item.getCode(), item);
		}
		return codeRiskStatusMap;
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
