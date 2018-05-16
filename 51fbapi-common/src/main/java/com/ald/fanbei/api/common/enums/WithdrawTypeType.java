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
public enum WithdrawTypeType {
	//zero 10代表金额 500 是优惠券id
	ZERO("0","10", "500"),
	ONE("1", "30","提现金额"),
	TWO("2", "50","提现金额"),
	THREE("3", "100","提现金额");


	private String code;
    private String name;
    private String dec;


    WithdrawTypeType(String code, String name, String dec) {
        this.code = code;
        this.name = name;
        this.setDec(dec);

    }
    public static WithdrawTypeType findRoleTypeByCode(String code) {
        for (WithdrawTypeType roleType : WithdrawTypeType.values()) {
            if (StringUtils.equals(code, roleType.getCode())) {
                return roleType;
            }
        }
        return null;
    }
    public static WithdrawTypeType findRoleTypeByName(String name) {
        for (WithdrawTypeType roleType : WithdrawTypeType.values()) {
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
