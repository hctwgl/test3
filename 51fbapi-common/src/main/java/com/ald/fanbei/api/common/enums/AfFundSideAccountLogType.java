package com.ald.fanbei.api.common.enums;

import org.apache.commons.lang.StringUtils;

/**
 * @author chengkang
 *资金方账户变动类型
 */
public enum AfFundSideAccountLogType {
	
	RECHARGE("recharge","充值"), 
	WITHDRAW("withdraw", "提现"), 
	LOAN("loan", "放款"), 
	COLLECT("collect", "回款");
	
	
	private String code;
    private String dec;


    AfFundSideAccountLogType(String code, String dec) {
        this.code = code;
        this.dec = dec;
    }
    
    public static AfFundSideAccountLogType findTypeByCode(String code) {
        for (AfFundSideAccountLogType roleType : AfFundSideAccountLogType.values()) {
            if (StringUtils.equals(code, roleType.getCode())) {
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
