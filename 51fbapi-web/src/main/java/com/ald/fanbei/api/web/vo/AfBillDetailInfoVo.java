package com.ald.fanbei.api.web.vo;

import java.math.BigDecimal;
import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * 
 * @类描述：
 * @author hexin 2017年2月21日下午17:59:06
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfBillDetailInfoVo extends AbstractSerial{

	private static final long serialVersionUID = 8537808527358013071L;

	private Long billId;//账单id
	private BigDecimal billAmount;//还款金额
	private String billStatus;//还款状态 Y:已还款 ，N:未还款，F:冻结
	private String borrowType;//借款类型：【CASH:现金 ,CONSUME:消费分期,CONSUME_TEMP:消费分期待确认,TOCASH:消费转换成现金借款,TOCONSUME:消费待确认转化成消费】
	private String name;//借款名称
	private BigDecimal borrowAmount;//借款本金
	private BigDecimal poundageAmount;//手续费
	private BigDecimal overduePoundageAmount;//逾期手续费
	private Integer interestDay;//还款日内借款天数
	private BigDecimal interestAmount;//借款利息
	private Integer overdueDay;//逾期天数
	private BigDecimal overdueAmount;//逾期利息
	private Date gmtBorrow;//借款时间
	private Integer nper;//分期数
	private Integer billNper;//当前分期
	private String borrowNo;//借款编号
	public Long getBillId() {
		return billId;
	}
	public void setBillId(Long billId) {
		this.billId = billId;
	}
	public BigDecimal getBillAmount() {
		return billAmount;
	}
	public void setBillAmount(BigDecimal billAmount) {
		this.billAmount = billAmount;
	}
	public String getBillStatus() {
		return billStatus;
	}
	public void setBillStatus(String billStatus) {
		this.billStatus = billStatus;
	}
	public String getBorrowType() {
		return borrowType;
	}
	public void setBorrowType(String borrowType) {
		this.borrowType = borrowType;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public BigDecimal getBorrowAmount() {
		return borrowAmount;
	}
	public void setBorrowAmount(BigDecimal borrowAmount) {
		this.borrowAmount = borrowAmount;
	}
	public BigDecimal getPoundageAmount() {
		return poundageAmount;
	}
	public void setPoundageAmount(BigDecimal poundageAmount) {
		this.poundageAmount = poundageAmount;
	}
	public Integer getInterestDay() {
		return interestDay;
	}
	public void setInterestDay(Integer interestDay) {
		this.interestDay = interestDay;
	}
	public BigDecimal getInterestAmount() {
		return interestAmount;
	}
	public void setInterestAmount(BigDecimal interestAmount) {
		this.interestAmount = interestAmount;
	}
	public Integer getOverdueDay() {
		return overdueDay;
	}
	public void setOverdueDay(Integer overdueDay) {
		this.overdueDay = overdueDay;
	}
	public BigDecimal getOverdueAmount() {
		return overdueAmount;
	}
	public void setOverdueAmount(BigDecimal overdueAmount) {
		this.overdueAmount = overdueAmount;
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
	public String getBorrowNo() {
		return borrowNo;
	}
	public void setBorrowNo(String borrowNo) {
		this.borrowNo = borrowNo;
	}
	public BigDecimal getOverduePoundageAmount() {
		return overduePoundageAmount;
	}
	public void setOverduePoundageAmount(BigDecimal overduePoundageAmount) {
		this.overduePoundageAmount = overduePoundageAmount;
	}
}
