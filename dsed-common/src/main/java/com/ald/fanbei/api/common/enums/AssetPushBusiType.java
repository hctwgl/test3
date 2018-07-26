package com.ald.fanbei.api.common.enums;


/**
 * @类描述：资产包业务类型枚举
 * @author wujun 
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum AssetPushBusiType {
	
	DSED(3, "都市e贷");
	
	private Integer code;

	private String description;

	AssetPushBusiType(Integer code, String description) {
		this.code = code;
		this.description = description;
	}

	public static AssetPushBusiType findEnumByCode(Integer code) {
		for (AssetPushBusiType goodSource : AssetPushBusiType.values()) {
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
