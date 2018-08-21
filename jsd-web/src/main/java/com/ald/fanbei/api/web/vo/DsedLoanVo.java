package com.ald.fanbei.api.web.vo;

import com.ald.fanbei.api.common.AbstractSerial;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**  
 * @Description: 都市e贷借钱信息vo
 * @author gsq
 * @date 2018年1月25日
 */
public class DsedLoanVo extends AbstractSerial {
	
	private static final long serialVersionUID = 1L;
	private String borrowNo;		// 借款编号
	private String isPeriod;		// 是否为分期产品,Y 分期，N 不分期
	private String status;		// 借款状态（APPLY:申请/未审核， TRANSFERING:打款中 ， TRANSFERRED:已经打款/待还款 TRANSFAILED:打款失败，CLOSED:关闭，FINISHED:已结清）
	private BigDecimal interestRate;
	private BigDecimal totalInterestFee;
	private BigDecimal serviceRate;
	private BigDecimal totalServiceFee;
	private BigDecimal arrivalAmount;
	private List<DsedLoanPeriodsVo> borrowBillDetails;

	public BigDecimal getInterestRate() {
		return interestRate;
	}

	public void setInterestRate(BigDecimal interestRate) {
		this.interestRate = interestRate;
	}

	public BigDecimal getTotalInterestFee() {
		return totalInterestFee;
	}

	public void setTotalInterestFee(BigDecimal totalInterestFee) {
		this.totalInterestFee = totalInterestFee;
	}

	public BigDecimal getServiceRate() {
		return serviceRate;
	}

	public void setServiceRate(BigDecimal serviceRate) {
		this.serviceRate = serviceRate;
	}

	public BigDecimal getTotalServiceFee() {
		return totalServiceFee;
	}

	public void setTotalServiceFee(BigDecimal totalServiceFee) {
		this.totalServiceFee = totalServiceFee;
	}

	public BigDecimal getArrivalAmount() {
		return arrivalAmount;
	}

	public void setArrivalAmount(BigDecimal arrivalAmount) {
		this.arrivalAmount = arrivalAmount;
	}

	public String getBorrowNo() {
		return borrowNo;
	}

	public void setBorrowNo(String borrowNo) {
		this.borrowNo = borrowNo;
	}

	public String getIsPeriod() {
		return isPeriod;
	}

	public void setIsPeriod(String isPeriod) {
		this.isPeriod = isPeriod;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<DsedLoanPeriodsVo> getBorrowBillDetails() {
		return borrowBillDetails;
	}

	public void setBorrowBillDetails(List<DsedLoanPeriodsVo> borrowBillDetails) {
		this.borrowBillDetails = borrowBillDetails;
	}
}
