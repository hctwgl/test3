package com.ald.fanbei.api.web.vo;

import com.ald.fanbei.api.common.AbstractSerial;
import com.ald.fanbei.api.dal.domain.DsedLoanDo;

import java.math.BigDecimal;
import java.util.List;

/**  
 * @Description: 都市e贷借钱信息vo
 * @author gsq
 * @date 2018年1月25日
 */
public class DsedLoanDetailVo extends AbstractSerial {
	
	private static final long serialVersionUID = 1L;

	/**
	 * 手续费率
	 */
	private BigDecimal serviceRate;
	private BigDecimal interestRate;
	private BigDecimal totalFee;
	private BigDecimal totalServiceFee;
	private BigDecimal overdueRate;
	private BigDecimal totalInterestFee;
	private BigDecimal [] billTotalAmount;
	private BigDecimal arrivalAmount;
	public static DsedLoanDetailVo gen(int periods,
								 BigDecimal serviceRate, BigDecimal interestRate, BigDecimal overdueRate, BigDecimal arrivalAmount,
								 BigDecimal totalServiceFee, BigDecimal totalInterestFee) {
		DsedLoanDetailVo l = new DsedLoanDetailVo();
		l.serviceRate = serviceRate;//服务费率
		l.interestRate = interestRate;//借款利率
		l.totalFee = totalServiceFee.add(totalInterestFee);
		l.totalServiceFee = totalServiceFee;
		l.overdueRate = overdueRate;
		l.totalInterestFee = totalInterestFee;
		BigDecimal [] billTotalAmounts =new BigDecimal[periods];
        for (int i = 0; i < periods; i++) {
            billTotalAmounts[i] = totalServiceFee.add(totalInterestFee).add(arrivalAmount).divide(BigDecimal.valueOf(periods),2);
        }
		l.billTotalAmount = billTotalAmounts;
		l.arrivalAmount = arrivalAmount;
		return l;
	}

	public BigDecimal getServiceRate() {
		return serviceRate;
	}

	public void setServiceRate(BigDecimal serviceRate) {
		this.serviceRate = serviceRate;
	}

	public BigDecimal getInterestRate() {
		return interestRate;
	}

	public void setInterestRate(BigDecimal interestRate) {
		this.interestRate = interestRate;
	}

	public BigDecimal getTotalFee() {
		return totalFee;
	}

	public void setTotalFee(BigDecimal totalFee) {
		this.totalFee = totalFee;
	}

	public BigDecimal getTotalServiceFee() {
		return totalServiceFee;
	}

	public void setTotalServiceFee(BigDecimal totalServiceFee) {
		this.totalServiceFee = totalServiceFee;
	}

	public BigDecimal getOverdueRate() {
		return overdueRate;
	}

	public void setOverdueRate(BigDecimal overdueRate) {
		this.overdueRate = overdueRate;
	}

	public BigDecimal getTotalInterestFee() {
		return totalInterestFee;
	}

	public void setTotalInterestFee(BigDecimal totalInterestFee) {
		this.totalInterestFee = totalInterestFee;
	}

    public BigDecimal[] getBillTotalAmount() {
        return billTotalAmount;
    }

    public void setBillTotalAmount(BigDecimal[] billTotalAmount) {
        this.billTotalAmount = billTotalAmount;
    }

    public BigDecimal getArrivalAmount() {
		return arrivalAmount;
	}

	public void setArrivalAmount(BigDecimal arrivalAmount) {
		this.arrivalAmount = arrivalAmount;
	}
}
