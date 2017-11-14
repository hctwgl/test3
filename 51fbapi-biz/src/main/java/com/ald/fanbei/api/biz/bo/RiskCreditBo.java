package com.ald.fanbei.api.biz.bo;

import java.math.BigDecimal;

/**
 *  信用支付提供给风控的数据
 */
public class RiskCreditBo {
    private Integer loanPayOffCount;
    private Integer loanOverdueDay;
    private BigDecimal consumeBorrowAmount;
    private Integer consumePayOffCount;
    private Integer consumeOverdueDay;


    public Integer getLoanPayOffCount() {
        return loanPayOffCount;
    }

    public void setLoanPayOffCount(Integer loanPayOffCount) {
        this.loanPayOffCount = loanPayOffCount;
    }

    public Integer getLoanOverdueDay() {
        return loanOverdueDay;
    }

    public void setLoanOverdueDay(Integer loanOverdueDay) {
        this.loanOverdueDay = loanOverdueDay;
    }

    public BigDecimal getConsumeBorrowAmount() {
        return consumeBorrowAmount;
    }

    public void setConsumeBorrowAmount(BigDecimal consumeBorrowAmount) {
        this.consumeBorrowAmount = consumeBorrowAmount;
    }

    public Integer getConsumePayOffCount() {
        return consumePayOffCount;
    }

    public void setConsumePayOffCount(Integer consumePayOffCount) {
        this.consumePayOffCount = consumePayOffCount;
    }

    public Integer getConsumeOverdueDay() {
        return consumeOverdueDay;
    }

    public void setConsumeOverdueDay(Integer consumeOverdueDay) {
        this.consumeOverdueDay = consumeOverdueDay;
    }
}
