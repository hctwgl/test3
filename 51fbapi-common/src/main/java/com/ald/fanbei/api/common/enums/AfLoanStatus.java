/**
 * 
 */
package com.ald.fanbei.api.common.enums;

/**
 * @类描述：
 * @author suweili 2017年3月25日下午5:15:14
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum AfLoanStatus {
	APPLY("申请/未审核", "REVIEWING"),
	TRANSFERING("打款中", "TRANSFERING"),
	TRANSFERRED("已经打款/待还款", "TRANSFERRED"),
	REVIEWFAIL("审核不通过", "REVIEWFAIL"),
	REPAYING("还款中", "REPAYING"),
	FINISHED("已结清", "FINISHED"),
	CLOSED("关闭", "CLOSED");
	
    public String desz;
    public String referBorrowCashCode;

    AfLoanStatus(String desz,String referBorrowCashCode) {
    	this.desz = desz;
    	this.referBorrowCashCode = referBorrowCashCode;
    }
}
