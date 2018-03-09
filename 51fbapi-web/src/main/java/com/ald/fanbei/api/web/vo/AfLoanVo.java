package com.ald.fanbei.api.web.vo;

import java.math.BigDecimal;
import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;

/**  
 * @Description: 白领贷借钱信息vo
 * @author yhl
 * @date 2018年1月25日
 */
public class AfLoanVo extends AbstractSerial {
	
	private static final long serialVersionUID = 1L;
	private String loanPeriodsIds;		// 借款期数id(多期用英文逗号隔开)
	private int nper;		// 当前期数	
	private BigDecimal currentPeriodAmount;		// 本月待还金额
	private Date gmtPlanRepay;		// 本月还款时间
	private BigDecimal amount;		// 借款金额	
	private BigDecimal totalServiceFee;		// 总手续费
	private BigDecimal totalInterestFee;	// 总利息
	private int periods;		// 还款期限
	private String repayDay;	// 每月还款时间
	private String loanProduct;	// 借钱产品
	private String cardNumber;	// 银行卡号
	private String cardName;	// 银行卡名称
	private Date gmtCreate;		// 申请时间
	private Date gmtArrival;	// 打款时间
	private String status;		// 贷款状态
	private String overdueStatus;		// 逾期状态
	private BigDecimal perPeriodAmount;		// 每期还款金额
	private BigDecimal rebateAmount;		// 用户余额

	
	public BigDecimal getRebateAmount() {
		return rebateAmount;
	}
	public void setRebateAmount(BigDecimal rebateAmount) {
		this.rebateAmount = rebateAmount;
	}
	public String getOverdueStatus() {
		return overdueStatus;
	}
	public void setOverdueStatus(String overdueStatus) {
		this.overdueStatus = overdueStatus;
	}
	
	
	public BigDecimal getPerPeriodAmount() {
		return perPeriodAmount;
	}
	public void setPerPeriodAmount(BigDecimal perPeriodAmount) {
		this.perPeriodAmount = perPeriodAmount;
	}
	public BigDecimal getCurrentPeriodAmount() {
		return currentPeriodAmount;
	}
	public void setCurrentPeriodAmount(BigDecimal currentPeriodAmount) {
		this.currentPeriodAmount = currentPeriodAmount;
	}
	public Date getGmtPlanRepay() {
		return gmtPlanRepay;
	}
	public void setGmtPlanRepay(Date gmtPlanRepay) {
		this.gmtPlanRepay = gmtPlanRepay;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public BigDecimal getTotalServiceFee() {
		return totalServiceFee;
	}
	public void setTotalServiceFee(BigDecimal totalServiceFee) {
		this.totalServiceFee = totalServiceFee;
	}
	public BigDecimal getTotalInterestFee() {
		return totalInterestFee;
	}
	public void setTotalInterestFee(BigDecimal totalInterestFee) {
		this.totalInterestFee = totalInterestFee;
	}
	public int getPeriods() {
		return periods;
	}
	public void setPeriods(int periods) {
		this.periods = periods;
	}
	public String getRepayDay() {
		return repayDay;
	}
	public void setRepayDay(String repayDay) {
		this.repayDay = repayDay;
	}
	public String getLoanProduct() {
		return loanProduct;
	}
	public void setLoanProduct(String loanProduct) {
		this.loanProduct = loanProduct;
	}
	public String getCardNumber() {
		return cardNumber;
	}
	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}
	public String getCardName() {
		return cardName;
	}
	public void setCardName(String cardName) {
		this.cardName = cardName;
	}
	public Date getGmtCreate() {
		return gmtCreate;
	}
	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}
	public Date getGmtArrival() {
		return gmtArrival;
	}
	public void setGmtArrival(Date gmtArrival) {
		this.gmtArrival = gmtArrival;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getLoanPeriodsIds() {
		return loanPeriodsIds;
	}
	public void setLoanPeriodsIds(String loanPeriodsIds) {
		this.loanPeriodsIds = loanPeriodsIds;
	}
	public int getNper() {
		return nper;
	}
	public void setNper(int nper) {
		this.nper = nper;
	}
	
}
