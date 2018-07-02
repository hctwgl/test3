/**
 * 
 */
package com.ald.fanbei.api.common.enums;

import org.apache.commons.lang.StringUtils;

/**
 * @类描述：
 * @author suweili 2017年3月25日上午10:30:21
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum SignRewardType {
	ZERO(0,"自己签到", "自己签到"),
	ONE(1, "朋友帮签(分享者)","朋友帮签(分享者)"),
	TWO(2, "补签成功(分享者)","补签成功(分享者)"),
	THREE(3, "补签失败","补签失败"),
	FOUR(4, "朋友帮签(好友)","朋友帮签(好友)"),
	FIVE(5, "补签成功(好友)","补签成功(好友)");


	private Integer code;
    private String name;
    private String dec;


    SignRewardType(Integer code, String name, String dec) {
        this.code = code;
        this.name = name;
        this.setDec(dec);

    }
    public static SignRewardType findRoleTypeByCode(Integer code) {
        for (SignRewardType roleType : SignRewardType.values()) {
            if (StringUtils.equals(code.toString(), roleType.getCode().toString())) {
                return roleType;
            }
        }
        return null;
    }
    public static SignRewardType findRoleTypeByName(String name) {
        for (SignRewardType roleType : SignRewardType.values()) {
            if (StringUtils.equals(name, roleType.getName())) {
                return roleType;
            }
        }
        return null;
    }
	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the dec
	 */
	public String getDec() {
		return dec;
	}

	/**
	 * @param dec the dec to set
	 */
	public void setDec(String dec) {
		this.dec = dec;
	}

}
