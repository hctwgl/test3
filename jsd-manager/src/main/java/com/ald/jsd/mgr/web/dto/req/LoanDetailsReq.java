package com.ald.jsd.mgr.web.dto.req;

import com.ald.fanbei.api.dal.domain.JsdBorrowCashRenewalDo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class LoanDetailsReq {
    /**
     * 西瓜借款编号
     */
    private String tradeNoXgxy;
    /**
     * 借款金额
     */
    private BigDecimal amount;
    /**
     * 到手金额
     */
    private BigDecimal arrivalAmount;

    /**
     * 状态
     */
    private String status;

    /**
     * 借款利率
     */
    private BigDecimal interestRate;

    /**
     * 手续费率
     */
    private BigDecimal poundageRate;

    /**
     * 逾期费率
     */
    private BigDecimal overdueRate;

    /**
     * 手续费
     */
    private BigDecimal poundageAmount;

    /**
     * 利息
     */
    private BigDecimal interestAmount;

    /**
     * 借款期限
     */
    private String term;

    /**
     * 借款用途
     */
    private String loanRemark;

    /**
     * 还款来源
     */
    private String repayRemark;

    /**
     * 申请时间
     */
    private Date applyDate;

    /**
     * 联系电话
     */
    private String mobile;

    /**
     * 身份证号
     */
    private String idNumber;

    /**
     * 借款人
     */
    private String realName;

    /**
     * 放款时间
     */
    private Date gmtArrival;

    /**
     * 银行名称
     */
    private String cardName;

    /**
     * 银行卡号
     */
    private String cardNumber;

    /**
     * 已还金额
     */
    private BigDecimal repayAmount;

    /**
     * 剩余未还总金额
     */
    private BigDecimal unrepayAmount;

    /**
     * 剩余未还利息费
     */
    private BigDecimal unrepayInterestAmount;

    /**
     * 剩余未逾期费
     */
    private BigDecimal unrepayOverdueAmount;

    /**
     * 剩余未还手续费
     */
    private BigDecimal unrepayServiceAmount;

    /**
     * 是否入催
     */
    private String isCollection;

    /**
     * 续期记录
     */
    private List<JsdBorrowCashRenewalDo> renewal;

    public String getTradeNoXgxy() {
        return tradeNoXgxy;
    }

    public void setTradeNoXgxy(String tradeNoXgxy) {
        this.tradeNoXgxy = tradeNoXgxy;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getArrivalAmount() {
        return arrivalAmount;
    }

    public void setArrivalAmount(BigDecimal arrivalAmount) {
        this.arrivalAmount = arrivalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public BigDecimal getPoundageRate() {
        return poundageRate;
    }

    public void setPoundageRate(BigDecimal poundageRate) {
        this.poundageRate = poundageRate;
    }

    public BigDecimal getOverdueRate() {
        return overdueRate;
    }

    public void setOverdueRate(BigDecimal overdueRate) {
        this.overdueRate = overdueRate;
    }

    public BigDecimal getPoundageAmount() {
        return poundageAmount;
    }

    public void setPoundageAmount(BigDecimal poundageAmount) {
        this.poundageAmount = poundageAmount;
    }

    public BigDecimal getInterestAmount() {
        return interestAmount;
    }

    public void setInterestAmount(BigDecimal interestAmount) {
        this.interestAmount = interestAmount;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getLoanRemark() {
        return loanRemark;
    }

    public void setLoanRemark(String loanRemark) {
        this.loanRemark = loanRemark;
    }

    public String getRepayRemark() {
        return repayRemark;
    }

    public void setRepayRemark(String repayRemark) {
        this.repayRemark = repayRemark;
    }

    public Date getApplyDate() {
        return applyDate;
    }

    public void setApplyDate(Date applyDate) {
        this.applyDate = applyDate;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public Date getGmtArrival() {
        return gmtArrival;
    }

    public void setGmtArrival(Date gmtArrival) {
        this.gmtArrival = gmtArrival;
    }

    public BigDecimal getRepayAmount() {
        return repayAmount;
    }

    public void setRepayAmount(BigDecimal repayAmount) {
        this.repayAmount = repayAmount;
    }

    public BigDecimal getUnrepayAmount() {
        return unrepayAmount;
    }

    public void setUnrepayAmount(BigDecimal unrepayAmount) {
        this.unrepayAmount = unrepayAmount;
    }

    public BigDecimal getUnrepayInterestAmount() {
        return unrepayInterestAmount;
    }

    public void setUnrepayInterestAmount(BigDecimal unrepayInterestAmount) {
        this.unrepayInterestAmount = unrepayInterestAmount;
    }

    public BigDecimal getUnrepayOverdueAmount() {
        return unrepayOverdueAmount;
    }

    public void setUnrepayOverdueAmount(BigDecimal unrepayOverdueAmount) {
        this.unrepayOverdueAmount = unrepayOverdueAmount;
    }

    public BigDecimal getUnrepayServiceAmount() {
        return unrepayServiceAmount;
    }

    public void setUnrepayServiceAmount(BigDecimal unrepayServiceAmount) {
        this.unrepayServiceAmount = unrepayServiceAmount;
    }

    public String getIsCollection() {
        return isCollection;
    }

    public void setIsCollection(String isCollection) {
        this.isCollection = isCollection;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public List<JsdBorrowCashRenewalDo> getRenewal() {
        return renewal;
    }

    public void setRenewal(List<JsdBorrowCashRenewalDo> renewal) {
        this.renewal = renewal;
    }
}
