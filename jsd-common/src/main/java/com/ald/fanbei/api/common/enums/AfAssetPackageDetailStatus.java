package com.ald.fanbei.api.common.enums;


/**
 * @类描述：资产包j明细状态枚举
 * @author chengkang 2017年11月27日下午5:57:51
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum AfAssetPackageDetailStatus {
	VALID("Y", "初始有效"), 
	INVALID("N", "标记无效"),
	REDISTRIBUTE("R", "已重新分配");

	private String code;

	private String description;

	AfAssetPackageDetailStatus(String code, String description) {
		this.code = code;
		this.description = description;
	}

	public static AfAssetPackageDetailStatus findEnumByCode(String code) {
		for (AfAssetPackageDetailStatus goodSource : AfAssetPackageDetailStatus.values()) {
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
