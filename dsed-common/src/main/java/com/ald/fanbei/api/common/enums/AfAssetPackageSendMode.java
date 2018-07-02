package com.ald.fanbei.api.common.enums;


/**
 * @类描述：资产包对接方式枚举
 * @author chengkang 2017年11月28日下午5:57:51
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum AfAssetPackageSendMode {
	EMAIL(1, "邮箱发送"), 
	INTERFACE(2, "接口对接"),
	OFFLINE(3, "线下发送");

	private Integer code;

	private String description;

	AfAssetPackageSendMode(Integer code, String description) {
		this.code = code;
		this.description = description;
	}

	public static AfAssetPackageSendMode findEnumByCode(Integer code) {
		for (AfAssetPackageSendMode goodSource : AfAssetPackageSendMode.values()) {
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
