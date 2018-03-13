package com.ald.fanbei.api.common.enums;

/**
 * 
 * @类描述：
 * @author fmai 2017年6月7日 11:32:26
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum RaiseStatus {

	Y("Y", "提额成功"), 
	N("N", "未提额"),
	F("F", "提额失败");

	private String code;
	private String name;

	RaiseStatus(String code, String name) {
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
