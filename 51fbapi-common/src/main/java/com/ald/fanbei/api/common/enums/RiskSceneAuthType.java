package com.ald.fanbei.api.common.enums;

/**
 * 
 * @类描述：
 * @author Jiang Rongbo 2018年2月7日下午2:34:01
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum RiskSceneAuthType {

	MAP_ALIPY_PASS("80", AuthType.FUND),
	MAP_ALIPY_UNPASS("81", AuthType.FUND),
	MAP_FUND_PASS("93", AuthType.ALIPAY),
	MAP_FUND_UNPASS("94", AuthType.ALIPAY),
	MAP_INSURANCE_PASS("70", AuthType.INSURANCE),
	MAP_INSURANCE_UNPASS("71", AuthType.INSURANCE),
	MAP_CARDMAIL_PASS("60", AuthType.CARDEMAIL),
	MAP_ZHENGXIN_PASS("110", AuthType.ZHENGXIN),
	MAP_ZHENGXIN_UNPASS("111", AuthType.ZHENGXIN),
	MAP_BANK_PASS("155", AuthType.BANK);

    private String code;
    private AuthType name;


	RiskSceneAuthType(String code, AuthType name) {
		this.code = code;
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public static AuthType findByCode(String code) {
		for (RiskSceneAuthType riskScene : RiskSceneAuthType.values()) {
			if (riskScene.getCode().equals(code)) {
				return riskScene.name;
			}
		}
		return null;
	}
	public void setCode(String code) {
		this.code = code;
	}

	public AuthType getName() {
		return name;
	}

	public void setName(AuthType name) {
		this.name = name;
	}
}
