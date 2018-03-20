package com.ald.fanbei.api.common.enums;

/**
 * 
 * @类描述：
 * @author fmai 2017年6月7日 11:32:26
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum RiskSceneType {

	BLDANDXJD("1", "白领贷与现金贷"), 
	XJD("2", "现金贷"),
	BLD("3", "白领贷"),
	ONLINE("4", "消费分期");

	private String code;
	private String name;

	RiskSceneType(String code, String name) {
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
