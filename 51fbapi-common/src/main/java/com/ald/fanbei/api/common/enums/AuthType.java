package com.ald.fanbei.api.common.enums;

/**
 * 
 * @类描述：
 * @author xiaotianjian 2017年2月7日下午2:34:01
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum AuthType {

	FUND("fund","公积金"),
	ALIPAY("alipay","支付宝"),
	INSURANCE("insurance","社保"),
	ZHENGXIN("zhengxin","人行征信"),
	BANK("bank","网银"),
	CARDEMAIL("cardEmail","信用卡邮箱"),
	CHSI("chsi","学信"),
	BUBBLE("bubble","冒泡");
    private String code;
    private String name;
    
	private AuthType(String code, String name) {
		this.code = code;
		this.name = name;
	}
	
	public static AuthType findByCode(String code) {
		for (AuthType roleType : AuthType.values()) {
			if (roleType.getCode().equals(code)) {
				return roleType;
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}
