package com.ald.fanbei.api.dal.domain.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.ald.fanbei.api.dal.domain.AfBorrowBillDo;


/**
 * 
* @ClassName: AfOverdueBillDto 
* 记录所有消费分期的逾期bill数据，向风控推送
* @author yuyue
* @date 2017年9月6日 下午2:23:02 
*
 */
public class AfOverdueBillDto extends AfBorrowBillDo{

	private static final long serialVersionUID = -5204951419377574076L;
	
	private Long billId;
	
	private Long borrowId;
	
	private String borrowNo;
	
	private Date gmtBorrow;
	
	private int billYear;
	
	private int billMonth;
	
	private Integer nper;
	
	private Integer billNper;
	
	private BigDecimal billAmount;
	
	private String status;
	
	private String remark;
	
	private Integer overdueDays;
	
	private BigDecimal principleAmount;
	
	private BigDecimal interestAmount;
	
	private BigDecimal overdueInterestAmount;
	
	private BigDecimal poundageAmount;
	
	private BigDecimal overduePoundageAmount;
	
	private String repayNo;
	
	private Date repayDate;
	
	private BigDecimal repayAmount;
	
	private BigDecimal salePercent;
	
	private BigDecimal saleAmount;
	
	private Date lastRepayDay;

	public Date getLastRepayDay() {
		return lastRepayDay;
	}

	public void setLastRepayDay(Date lastRepayDay) {
		this.lastRepayDay = lastRepayDay;
	}

	public Long getBorrowId() {
		return borrowId;
	}

	public void setBorrowId(Long borrowId) {
		this.borrowId = borrowId;
	}

	public String getBorrowNo() {
		return borrowNo;
	}

	public void setBorrowNo(String borrowNo) {
		this.borrowNo = borrowNo;
	}

	public Date getGmtBorrow() {
		return gmtBorrow;
	}

	public void setGmtBorrow(Date gmtBorrow) {
		this.gmtBorrow = gmtBorrow;
	}

	public Integer getNper() {
		return nper;
	}

	public void setNper(Integer nper) {
		this.nper = nper;
	}

	public Integer getBillNper() {
		return billNper;
	}

	public void setBillNper(Integer billNper) {
		this.billNper = billNper;
	}

	public BigDecimal getBillAmount() {
		return billAmount;
	}

	public void setBillAmount(BigDecimal billAmount) {
		this.billAmount = billAmount;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getOverdueDays() {
		return overdueDays;
	}

	public void setOverdueDays(Integer overdueDays) {
		this.overdueDays = overdueDays;
	}

	public BigDecimal getPrincipleAmount() {
		return principleAmount;
	}

	public void setPrincipleAmount(BigDecimal principleAmount) {
		this.principleAmount = principleAmount;
	}

	public BigDecimal getInterestAmount() {
		return interestAmount;
	}

	public void setInterestAmount(BigDecimal interestAmount) {
		this.interestAmount = interestAmount;
	}

	public BigDecimal getOverdueInterestAmount() {
		return overdueInterestAmount;
	}

	public void setOverdueInterestAmount(BigDecimal overdueInterestAmount) {
		this.overdueInterestAmount = overdueInterestAmount;
	}

	public BigDecimal getPoundageAmount() {
		return poundageAmount;
	}

	public void setPoundageAmount(BigDecimal poundageAmount) {
		this.poundageAmount = poundageAmount;
	}

	public BigDecimal getOverduePoundageAmount() {
		return overduePoundageAmount;
	}

	public void setOverduePoundageAmount(BigDecimal overduePoundageAmount) {
		this.overduePoundageAmount = overduePoundageAmount;
	}

	public Date getRepayDate() {
		return repayDate;
	}

	public void setRepayDate(Date repayDate) {
		this.repayDate = repayDate;
	}

	public BigDecimal getRepayAmount() {
		return repayAmount;
	}

	public void setRepayAmount(BigDecimal repayAmount) {
		this.repayAmount = repayAmount;
	}

	public BigDecimal getSalePercent() {
		return salePercent;
	}

	public void setSalePercent(BigDecimal salePercent) {
		this.salePercent = salePercent;
	}

	public BigDecimal getSaleAmount() {
		return saleAmount;
	}

	public void setSaleAmount(BigDecimal saleAmount) {
		this.saleAmount = saleAmount;
	}

	public String getRepayNo() {
		return repayNo;
	}

	public void setRepayNo(String repayNo) {
		this.repayNo = repayNo;
	}

	public int getBillYear() {
		return billYear;
	}

	public void setBillYear(int billYear) {
		this.billYear = billYear;
	}

	public int getBillMonth() {
		return billMonth;
	}

	public void setBillMonth(int billMonth) {
		this.billMonth = billMonth;
	}

	public Long getBillId() {
		return billId;
	}

	public void setBillId(Long billId) {
		this.billId = billId;
	}
	
}

