/**
 * 
 */
package com.ald.fanbei.api.common.enums;

/**
 * @类描述：
 * @author suweili 2017年3月25日下午5:15:14
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum AfLoanPeriodStatus {
	AWAIT_REPAY("待还款"),
	REPAYING("划款中"),
	PART_REPAY("部分还款"),
	FINISHED("已结清"),
	CLOSED("已结清");
	
    public String desz;

    AfLoanPeriodStatus(String desz) {
    	this.desz = desz;
    }
}
