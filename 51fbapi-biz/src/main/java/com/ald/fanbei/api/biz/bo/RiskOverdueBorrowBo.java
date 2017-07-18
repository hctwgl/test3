package com.ald.fanbei.api.biz.bo;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * 
 * @类描述：
 * @author xiaotianjian 2017年7月10日下午6:53:48
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class RiskOverdueBorrowBo extends AbstractSerial{

	private static final long serialVersionUID = -6100157119514768019L;
	
	private String borrowNo;//借款编号
	private Integer overdueDays;//逾期天数
	private Integer overdueTimes;//逾期次数
	
	/**
	 * @return the borrowNo
	 */
	public String getBorrowNo() {
		return borrowNo;
	}
	/**
	 * @param borrowNo the borrowNo to set
	 */
	public void setBorrowNo(String borrowNo) {
		this.borrowNo = borrowNo;
	}
	/**
	 * @return the overdueDay
	 */
	public Integer getOverdueDays() {
		return overdueDays;
	}
	/**
	 * @param overdueDay the overdueDay to set
	 */
	public void setOverdueDays(Integer overdueDays) {
		this.overdueDays = overdueDays;
	}
	/**
	 * @return the overdueTimes
	 */
	public Integer getOverdueTimes() {
		return overdueTimes;
	}
	/**
	 * @param overdueTimes the overdueTimes to set
	 */
	public void setOverdueTimes(Integer overdueTimes) {
		this.overdueTimes = overdueTimes;
	}
	
	
	
}
