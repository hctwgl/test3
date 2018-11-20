package com.ald.fanbei.api.common.enums;


/**
 * @类描述：资产包业务类型枚举
 * @author wujun 
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum AfAssetPackageBusiType {
	BORROWCASH(0, "现金贷"), 
	BORROW(1, "消费分期"),
	LOAN(2, "白领贷"),
	XGJSD(4, "西瓜极速贷");
	
	private Integer code;

	private String description;

	AfAssetPackageBusiType(Integer code, String description) {
		this.code = code;
		this.description = description;
	}

	public static AfAssetPackageBusiType findEnumByCode(Integer code) {
		for (AfAssetPackageBusiType goodSource : AfAssetPackageBusiType.values()) {
			if (goodSource.getCode().equals(code)) {
				return goodSource;
			}
		}
		return null;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
