package com.ald.fanbei.admin.common.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @类描述：优惠券场景
 * @author xiaotianjian 2017年2月25日上午10:10:15
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum CouponScene {

	REGIST("REGIST", "regist","注册红包"), SIGN_IN("SIGNIN", "signin","签到优惠券"),
	AUTH_NAME("AUTHNAME", "authname","注册红包"),
	CREDIT_AUTH("CREDITAUTH", "creditauth","信用认证");

	private String type;
	private String key;
	private String description;

	private static Map<String, CouponScene> codeRoleTypeMap = null;


	private CouponScene(String type, String key, String description) {
		this.type = type;
		this.key = key;
		this.description = description;
	}

	public static CouponScene findEnumByCode(String type) {
		for (CouponScene goodSource : CouponScene.values()) {
			if (goodSource.getType().equals(type)) {
				return goodSource;
			}
		}
		return null;
	}

	public static Map<String, CouponScene> getCodeEnumMap() {
		if (codeRoleTypeMap != null && codeRoleTypeMap.size() > 0) {
			return codeRoleTypeMap;
		}
		codeRoleTypeMap = new HashMap<String, CouponScene>();
		for (CouponScene item : CouponScene.values()) {
			codeRoleTypeMap.put(item.getType(), item);
		}
		return codeRoleTypeMap;
	}

	public String getType() {
		return type;
	}

	public String getKey() {
		return key;
	}

	public String getDescription() {
		return description;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}
