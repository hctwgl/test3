package com.ald.fanbei.api.common.enums;

/**
 * 
 * 
 * @类描述：接口数据统计类型
 * 
 * @author huyang 2017年4月26日下午3:35:30
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum ApiCallType {
	TONGDUN("TONGDUN", "同盾实名认证"), YOUDUN("YOUDUN", "有盾实名认证"), YITU_CARD("YITU_CARD", "依图身份证认证"), YITU_FACE("YITU_FACE",
			"依图人脸证认证");

	private String code;
	private String name;

	public static ApiCallType findRoleTypeByCode(String code) {
		for (ApiCallType roleType : ApiCallType.values()) {
			if (roleType.getCode().equals(code)) {
				return roleType;
			}
		}
		return null;
	}

	private ApiCallType(String code, String name) {
		this.code = code;
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
