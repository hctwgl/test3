package com.ald.fanbei.api.dal.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * 
 * @类描述：借款详情表
 * @author hexin 2017年2月09日上午11:37:20
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfBorrowBillDo extends AbstractSerial{

	private static final long serialVersionUID = -4099172521665179067L;

	private Long rid;
	private Date gmtCreate;
	private Date gmtModified;
	private Long userId;
	private Long borrowId;
	private String borrowNo;
	private String name;
	private Date gmtBorrow;
	private int  billYear;
	private int  billMonth;
	private Integer nper;
	private Integer billNper;
	private BigDecimal billAmount;
	private String status;
	private String overdueStatus;
	private Integer overdueDays;
	private Long repaymentId;
	private BigDecimal principleAmount;
	private BigDecimal interestAmount;
	private BigDecimal overdueInterestAmount;
	private BigDecimal poundageAmount;
	private BigDecimal overduePoundageAmount;
	public Long getRid() {
		return rid;
	}
	public void setRid(Long rid) {
		this.rid = rid;
	}
	public Date getGmtCreate() {
		return gmtCreate;
	}
	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}
	public Date getGmtModified() {
		return gmtModified;
	}
	public void setGmtModified(Date gmtModified) {
		this.gmtModified = gmtModified;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getBorrowId() {
		return borrowId;
	}
	public void setBorrowId(Long borrowId) {
		this.borrowId = borrowId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getGmtBorrow() {
		return gmtBorrow;
	}
	public void setGmtBorrow(Date gmtBorrow) {
		this.gmtBorrow = gmtBorrow;
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
	public String getOverdueStatus() {
		return overdueStatus;
	}
	public void setOverdueStatus(String overdueStatus) {
		this.overdueStatus = overdueStatus;
	}
	public Integer getOverdueDays() {
		return overdueDays;
	}
	public void setOverdueDays(Integer overdueDays) {
		this.overdueDays = overdueDays;
	}
	public Long getRepaymentId() {
		return repaymentId;
	}
	public void setRepaymentId(Long repaymentId) {
		this.repaymentId = repaymentId;
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
	public String getBorrowNo() {
		return borrowNo;
	}
	public void setBorrowNo(String borrowNo) {
		this.borrowNo = borrowNo;
	}

}
