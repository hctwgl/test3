package com.ald.fanbei.api.common.enums;

/**
 * 
 * @类描述：
 * @author Jiang Rongbo 2017年6月7日 11:32:26
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum RiskRaiseResult {

	PASS("10", "通过"), 
	REFUSE("20", "拒绝");

	private String code;
	private String name;

	RiskRaiseResult(String code, String name) {
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
