package com.ald.fanbei.api.web.vo;

import java.math.BigDecimal;
import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;

/**  
 * @Description: 白领贷借钱分期信息vo
 * @author yhl
 * @date 2018年1月25日
 */
public class AfLoanPeriodsVo extends AbstractSerial {
	
	private static final long serialVersionUID = 1L;
	
	private Long loanPeriodsId;		// 借款期数id
	private int nper;		// 期数
	private String status;	// 状态(已还款：Y；已逾期：O；还款中：D；未还款：N)
	private Date gmtPlanRepay;	// 最后还款时间
	private BigDecimal serviceFee;	// 手续费
	private BigDecimal interestFee;	// 利息
	private BigDecimal overdueAmount;	// 逾期费
	private BigDecimal perAmount;	// 每期还款金额
	private BigDecimal restAmount;	// 每期剩余应还金额
	
	public Long getLoanPeriodsId() {
		return loanPeriodsId;
	}
	public void setLoanPeriodsId(Long loanPeriodsId) {
		this.loanPeriodsId = loanPeriodsId;
	}
	public int getNper() {
		return nper;
	}
	public void setNper(int nper) {
		this.nper = nper;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Date getGmtPlanRepay() {
		return gmtPlanRepay;
	}
	public void setGmtPlanRepay(Date gmtPlanRepay) {
		this.gmtPlanRepay = gmtPlanRepay;
	}
	public BigDecimal getServiceFee() {
		return serviceFee;
	}
	public void setServiceFee(BigDecimal serviceFee) {
		this.serviceFee = serviceFee;
	}
	public BigDecimal getInterestFee() {
		return interestFee;
	}
	public void setInterestFee(BigDecimal interestFee) {
		this.interestFee = interestFee;
	}
	public BigDecimal getOverdueAmount() {
		return overdueAmount;
	}
	public void setOverdueAmount(BigDecimal overdueAmount) {
		this.overdueAmount = overdueAmount;
	}
	public BigDecimal getPerAmount() {
		return perAmount;
	}
	public void setPerAmount(BigDecimal perAmount) {
		this.perAmount = perAmount;
	}
	public BigDecimal getRestAmount() {
		return restAmount;
	}
	public void setRestAmount(BigDecimal restAmount) {
		this.restAmount = restAmount;
	}
}
