/**
 * 
 */
package com.ald.fanbei.api.common.enums;

/**
 * @author ZJF
 */
public enum AfLoanRepaymentChannel {
	ONLINE("线上主动还款"),
	COLLECT("催收平台还款"),
	ADMIN("管理后台财务还款"),
	AUTO_REPAY("自动还款（代扣）");
	
    public String desz;

    AfLoanRepaymentChannel(String desz) {
    	this.desz = desz;
    }
}
