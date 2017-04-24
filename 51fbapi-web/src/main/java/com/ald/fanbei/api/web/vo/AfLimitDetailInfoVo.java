package com.ald.fanbei.api.web.vo;

import java.math.BigDecimal;
import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * 
 * @类描述：
 * @author hexin 2017年2月23日下午17:21:49
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfLimitDetailInfoVo extends AbstractSerial{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2742237559303878678L;
	private String cardName;//卡名称
	private String cardNo;//卡尾号
	private String name;//借还款名称
	private BigDecimal amount;//借还款金额
	private BigDecimal couponAmount;//优惠券金额
	private BigDecimal rebateAmount;//返现金额
	private BigDecimal actualAmount;//现金支付金额
	private Integer nper;//分期
	private Integer nperRepayment;//已还期数
	private BigDecimal repayPrinAmount;//已还本金
	private BigDecimal perAmount;//每期还款金额
	private Date gmtCreate;//创建时间
	private String number;//借还款编号
	private String status;//借还款状态
	private String borrowDetail;//借款明细
	private Integer borrowDay;//借款天数
	private BigDecimal payAmount;//当前需还
	public String getCardName() {
		return cardName;
	}
	public void setCardName(String cardName) {
		this.cardName = cardName;
	}
	public String getCardNo() {
		return cardNo;
	}
	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public BigDecimal getCouponAmount() {
		return couponAmount;
	}
	public void setCouponAmount(BigDecimal couponAmount) {
		this.couponAmount = couponAmount;
	}
	public BigDecimal getRebateAmount() {
		return rebateAmount;
	}
	public void setRebateAmount(BigDecimal rebateAmount) {
		this.rebateAmount = rebateAmount;
	}
	public BigDecimal getActualAmount() {
		return actualAmount;
	}
	public void setActualAmount(BigDecimal actualAmount) {
		this.actualAmount = actualAmount;
	}
	public Integer getNper() {
		return nper;
	}
	public void setNper(Integer nper) {
		this.nper = nper;
	}
	public Integer getNperRepayment() {
		return nperRepayment;
	}
	public void setNperRepayment(Integer nperRepayment) {
		this.nperRepayment = nperRepayment;
	}
	public BigDecimal getRepayPrinAmount() {
		return repayPrinAmount;
	}
	public void setRepayPrinAmount(BigDecimal repayPrinAmount) {
		this.repayPrinAmount = repayPrinAmount;
	}
	public BigDecimal getPerAmount() {
		return perAmount;
	}
	public void setPerAmount(BigDecimal perAmount) {
		this.perAmount = perAmount;
	}
	public Date getGmtCreate() {
		return gmtCreate;
	}
	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getBorrowDetail() {
		return borrowDetail;
	}
	public void setBorrowDetail(String borrowDetail) {
		this.borrowDetail = borrowDetail;
	}
	public Integer getBorrowDay() {
		return borrowDay;
	}
	public void setBorrowDay(Integer borrowDay) {
		this.borrowDay = borrowDay;
	}
	public BigDecimal getPayAmount() {
		return payAmount;
	}
	public void setPayAmount(BigDecimal payAmount) {
		this.payAmount = payAmount;
	}
}
