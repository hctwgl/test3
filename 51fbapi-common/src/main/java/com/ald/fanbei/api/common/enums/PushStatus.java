package com.ald.fanbei.api.common.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @类描述：
 * @author xiaotianjian 2017年2月15日下午2:34:51
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum PushStatus {

	REFUND_SUC("REFUND_SUC", "退款成功"), REFUND_FAIL("REFUND_FAIL", "退款失败"),
	PAY_SUC("PAY_SUC", "支付成功");

	private String code;

	private String description;

	private static Map<String, PushStatus> codeRoleTypeMap = null;

	PushStatus(String code, String description) {
		this.code = code;
		this.description = description;
	}

	public static PushStatus findEnumByCode(String code) {
		for (PushStatus goodSource : PushStatus.values()) {
			if (goodSource.getCode().equals(code)) {
				return goodSource;
			}
		}
		return null;
	}

	public static Map<String, PushStatus> getCodeRoleTypeMap() {
		if (codeRoleTypeMap != null && codeRoleTypeMap.size() > 0) {
			return codeRoleTypeMap;
		}
		codeRoleTypeMap = new HashMap<String, PushStatus>();
		for (PushStatus item : PushStatus.values()) {
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
