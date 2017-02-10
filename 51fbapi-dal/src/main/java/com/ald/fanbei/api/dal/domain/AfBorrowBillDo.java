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
	
	private Long userId;//用户id
	
	private Long borrowId;//借款Id
	
	private String name;//借款名称
	
	private Date gmtBorrow;//借款时间
	
	private String billMonth;//账单月，yyyy-MM
	
	private String billNper;//账单期数，如3期中的第一期则值为 1/3
	
	private BigDecimal billAmount;//账单金额
	
	private String status;//是否还款状态【Y:已还款 ，N:未还款】
	
	private Long repaymentId;//还款id
	
	private Date gmtRepayment;//实际还款时间
	
	private BigDecimal repaymentAmount;//实际还款金额
	
	private String overdueStatus;//是否逾期状态【Y:逾期 ，N:未逾期】
	
	private Integer overdueDays;//逾期天数,还款之后才计算逾期天数
	
	private BigDecimal overdueAmount;//逾期手续费

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

	public String getBillMonth() {
		return billMonth;
	}

	public void setBillMonth(String billMonth) {
		this.billMonth = billMonth;
	}

	public String getBillNper() {
		return billNper;
	}

	public void setBillNper(String billNper) {
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

	public Long getRepaymentId() {
		return repaymentId;
	}

	public void setRepaymentId(Long repaymentId) {
		this.repaymentId = repaymentId;
	}

	public Date getGmtRepayment() {
		return gmtRepayment;
	}

	public void setGmtRepayment(Date gmtRepayment) {
		this.gmtRepayment = gmtRepayment;
	}

	public BigDecimal getRepaymentAmount() {
		return repaymentAmount;
	}

	public void setRepaymentAmount(BigDecimal repaymentAmount) {
		this.repaymentAmount = repaymentAmount;
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

	public BigDecimal getOverdueAmount() {
		return overdueAmount;
	}

	public void setOverdueAmount(BigDecimal overdueAmount) {
		this.overdueAmount = overdueAmount;
	}
	
}
