package com.ald.fanbei.api.common.enums;


/**
 * @类描述：资产包类型枚举
 * @author chengkang 2017年11月28日下午5:57:51
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum AfAssetPackageType {
	FB_PUSH(0, "爱上街主动推送"),
	ASSET_REQ(1, "合作方主动请求");

	private Integer code;

	private String description;

	AfAssetPackageType(Integer code, String description) {
		this.code = code;
		this.description = description;
	}

	public static AfAssetPackageType findEnumByCode(Integer code) {
		for (AfAssetPackageType goodSource : AfAssetPackageType.values()) {
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
