package com.ald.fanbei.api.common.enums;


/**
 * @类描述：资产方操作类型枚举
 * @author chengkang 2017年11月27日下午5:57:51
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum AfAssetOperaLogChangeType {

	GET_ASSET("getasset", "获取债权资产"), 
	GIVE_BACK("giveback", "退回债权资产");

	private String code;
	private String description;

	AfAssetOperaLogChangeType(String code, String description) {
		this.code = code;
		this.description = description;
	}

	public static AfAssetOperaLogChangeType findEnumByCode(String code) {
		for (AfAssetOperaLogChangeType goodSource : AfAssetOperaLogChangeType.values()) {
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
