package com.ald.fanbei.api.web.vo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * 
 * @类描述：
 * @author hexin 2017年2月21日上午10:41:38
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfBillHomeVo extends AbstractSerial{

	/**
	 * 
	 */
	private static final long serialVersionUID = 990327740589938087L;

	private String billYear;//账单年
	private String billMonth;//账单月
	private BigDecimal repayAmount;//应还金额
	private BigDecimal haspayAmount;//已还款总额
	private String repayStatus;//还款状态 Y:已还款 ，N:未还款	 
	private Integer billCount;//账单笔数
	private Integer pageNo;//当前页	 
	private Date  repayDay;//还款日
	private List<AfBillHomeListVo> billList;
	public String getBillYear() {
		return billYear;
	}
	public void setBillYear(String billYear) {
		this.billYear = billYear;
	}
	public String getBillMonth() {
		return billMonth;
	}
	public void setBillMonth(String billMonth) {
		this.billMonth = billMonth;
	}
	public BigDecimal getRepayAmount() {
		return repayAmount;
	}
	public void setRepayAmount(BigDecimal repayAmount) {
		this.repayAmount = repayAmount;
	}
	public String getRepayStatus() {
		return repayStatus;
	}
	public void setRepayStatus(String repayStatus) {
		this.repayStatus = repayStatus;
	}
	public Integer getBillCount() {
		return billCount;
	}
	public void setBillCount(Integer billCount) {
		this.billCount = billCount;
	}
	public Integer getPageNo() {
		return pageNo;
	}
	public void setPageNo(Integer pageNo) {
		this.pageNo = pageNo;
	}
	public Date getRepayDay() {
		return repayDay;
	}
	public void setRepayDay(Date repayDay) {
		this.repayDay = repayDay;
	}
	public List<AfBillHomeListVo> getBillList() {
		return billList;
	}
	public void setBillList(List<AfBillHomeListVo> billList) {
		this.billList = billList;
	}
	public BigDecimal getHaspayAmount() {
		return haspayAmount;
	}
	public void setHaspayAmount(BigDecimal haspayAmount) {
		this.haspayAmount = haspayAmount;
	}
}
