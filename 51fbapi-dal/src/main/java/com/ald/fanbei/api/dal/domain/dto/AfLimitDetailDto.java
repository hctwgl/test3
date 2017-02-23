package com.ald.fanbei.api.dal.domain.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * 
 *@类描述：AfLimitDetailDto
 *@author 何鑫 2017年2月23日  14:50:24
 *@注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfLimitDetailDto extends AbstractSerial{

	private static final long serialVersionUID = 8829303631319630999L;

	private Long limitId;//明细id
	private String type;//明细类型
	private Date gmtCreate;
	private String borrowName;//借款名称
	private BigDecimal borrowAmount;//借款金额
	private String borrowNo;//借款编号
	private String repaymentName;//还款名称
	private BigDecimal repaymentAmount;//还款金额
	private String repaymentNo;//还款编号
	private Long refId;//明细id
	public Long getLimitId() {
		return limitId;
	}
	public void setLimitId(Long limitId) {
		this.limitId = limitId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Date getGmtCreate() {
		return gmtCreate;
	}
	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}
	public String getBorrowName() {
		return borrowName;
	}
	public void setBorrowName(String borrowName) {
		this.borrowName = borrowName;
	}
	public BigDecimal getBorrowAmount() {
		return borrowAmount;
	}
	public void setBorrowAmount(BigDecimal borrowAmount) {
		this.borrowAmount = borrowAmount;
	}
	public String getBorrowNo() {
		return borrowNo;
	}
	public void setBorrowNo(String borrowNo) {
		this.borrowNo = borrowNo;
	}
	public String getRepaymentName() {
		return repaymentName;
	}
	public void setRepaymentName(String repaymentName) {
		this.repaymentName = repaymentName;
	}
	public BigDecimal getRepaymentAmount() {
		return repaymentAmount;
	}
	public void setRepaymentAmount(BigDecimal repaymentAmount) {
		this.repaymentAmount = repaymentAmount;
	}
	public String getRepaymentNo() {
		return repaymentNo;
	}
	public void setRepaymentNo(String repaymentNo) {
		this.repaymentNo = repaymentNo;
	}
	public Long getRefId() {
		return refId;
	}
	public void setRefId(Long refId) {
		this.refId = refId;
	}
}
