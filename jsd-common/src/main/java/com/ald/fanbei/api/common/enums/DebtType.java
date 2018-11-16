package com.ald.fanbei.api.common.enums;


/**
 * @类描述：债权类型
 * @author wujun  
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */

public enum DebtType {
	
	BORROWCASH("BORROWCASH", "极速贷"), 
	BORROW("BORROW", "分期"),
	LOAN("LOAN", "白领贷"),
	XGJSD("XGJSD", "西瓜极速贷2.0");

	private String code;

	private String description;

	DebtType(String code, String description) {
		this.code = code;
		this.description = description;
	}

	public static DebtType findEnumByCode(String code) {
		for (DebtType goodSource : DebtType.values()) {
			if (goodSource.getCode().equals(code)) {
				return goodSource;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
