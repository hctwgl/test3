package com.ald.fanbei.api.common.enums;


/**
 * @类描述：客户端类型枚举
 * @author chenkang 2017年8月9日下午17:48:57
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum ClientTypeEnum {
	
	ANDROID("Andriod", "安卓"), 
	IOS("IOS", "苹果");

	private String code;
	private String description;

	ClientTypeEnum(String code, String description) {
		this.code = code;
		this.description = description;
	}

	public static ClientTypeEnum findEnumByCode(String code) {
		for (ClientTypeEnum clientType : ClientTypeEnum.values()) {
			if (clientType.getCode().equals(code)) {
				return clientType;
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
