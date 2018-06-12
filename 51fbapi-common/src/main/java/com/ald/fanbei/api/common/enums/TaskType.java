/**
 * 
 */
package com.ald.fanbei.api.common.enums;

import org.apache.commons.lang.StringUtils;

/**
 * @类描述：
 * @author cfp 2017年3月25日上午10:30:21
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum TaskType {
	browse("browse","浏览", ""),
	shopping("shopping", "购物",""),
	share("share", "分享",""),
	verified("verified", "实名",""),
	strong_risk("strong_risk", "分期强风控通过",""),
	push("push","打开手机推送权限",""),
	loan_market_access("loan_market_access","借贷超市注册","");


	private String code;
    private String name;
    private String dec;


    TaskType(String code, String name, String dec) {
        this.code = code;
        this.name = name;
        this.setDec(dec);

    }
    public static TaskType findRoleTypeByCode(String code) {
        for (TaskType roleType : TaskType.values()) {
            if (StringUtils.equals(code, roleType.getCode())) {
                return roleType;
            }
        }
        return null;
    }
    public static TaskType findRoleTypeByName(String name) {
        for (TaskType roleType : TaskType.values()) {
            if (StringUtils.equals(name, roleType.getName())) {
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
