package com.ald.fanbei.api.biz.bo.jsd;

public class JsdApplyBorrowCashBo {

    private String openId;

    private String productNo;

    private String borrowNo;

    private String amount;

    private String term;

    private String unit;

    private String loanRemark;

    private String repayRemark;

    private String bankNo;

    private String isTying;

    private String tyingType;

    private JsdGoodsInfoBo JsdApplyBorrowCashBo;

    private Long userId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getProductNo() {
        return productNo;
    }

    public void setProductNo(String productNo) {
        this.productNo = productNo;
    }

    public String getBorrowNo() {
        return borrowNo;
    }

    public void setBorrowNo(String borrowNo) {
        this.borrowNo = borrowNo;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
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

    public String getBankNo() {
        return bankNo;
    }

    public void setBankNo(String bankNo) {
        this.bankNo = bankNo;
    }

    public String getIsTying() {
        return isTying;
    }

    public void setIsTying(String isTying) {
        this.isTying = isTying;
    }

    public String getTyingType() {
        return tyingType;
    }

    public void setTyingType(String tyingType) {
        this.tyingType = tyingType;
    }

    public JsdGoodsInfoBo getJsdApplyBorrowCashBo() {
        return JsdApplyBorrowCashBo;
    }

    public void setJsdApplyBorrowCashBo(JsdGoodsInfoBo jsdApplyBorrowCashBo) {
        JsdApplyBorrowCashBo = jsdApplyBorrowCashBo;
    }
}
