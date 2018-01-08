package com.ald.fanbei.api.common.enums;


/**
 * @类描述：资产包状态枚举
 * @author chengkang 2017年11月27日下午5:57:51
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum AfAssetPackageStatus {
	
	TOGENERATE("togenerate", "待匹配"), 
	TOUPLOAD("toupload", "待上传"),
	TOSEND("tosend", "待发送"),
	SENDED("sended", "已发送"),
	CANCELED("canceled", "已撤销"),
	RETURNED("returned", "已回款");

	private String code;

	private String description;

	AfAssetPackageStatus(String code, String description) {
		this.code = code;
		this.description = description;
	}

	public static AfAssetPackageStatus findEnumByCode(String code) {
		for (AfAssetPackageStatus goodSource : AfAssetPackageStatus.values()) {
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
