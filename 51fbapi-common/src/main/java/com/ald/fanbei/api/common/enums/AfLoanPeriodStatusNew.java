/**
 * 
 */
package com.ald.fanbei.api.common.enums;

/**
 *
 *
 * @author wangli
 * @date 2018/4/14 13:49
 */
public enum AfLoanPeriodStatusNew {
	AWAIT_REPAY("AWAIT_REPAY", "待还款"),
	REPAYING("REPAYING", "还款中"),
	PART_REPAY("PART_REPAY", "部分还款"),
	FINISHED("FINISHED", "已结清");

	private String code;

	private String name;

    AfLoanPeriodStatusNew(String code, String name) {
    	this.code = code;
    	this.name = name;
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}
}
