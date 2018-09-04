package com.ald.fanbei.api.biz.bo.jsd;

import java.math.BigDecimal;

public class ApplyBorrowCashBo {

    private String openId;

    private String productNo;

    private String borrowNo;

    private BigDecimal amount;

    private String term;

    private String unit;

    private String loanRemark;

    private String repayRemark;

    private String bankNo;

    private String isTying;

    private String tyingType;

    private JsdGoodsInfoBo JsdGoodsInfoBo;

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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
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

    public JsdGoodsInfoBo getJsdGoodsInfoBo() {
        return JsdGoodsInfoBo;
    }

    public void setJsdGoodsInfoBo(JsdGoodsInfoBo JsdGoodsInfoBo) {
    	this.JsdGoodsInfoBo = JsdGoodsInfoBo;
    }
    
    public static class JsdGoodsInfoBo {

        private String goodsName;

        private String goodsPrice;

        private String goodsImage;

        public String getGoodsName() {
            return goodsName;
        }

        public void setGoodsName(String goodsName) {
            this.goodsName = goodsName;
        }

        public String getGoodsPrice() {
            return goodsPrice;
        }

        public void setGoodsPrice(String goodsPrice) {
            this.goodsPrice = goodsPrice;
        }

        public String getGoodsImage() {
            return goodsImage;
        }

        public void setGoodsImage(String goodsImage) {
            this.goodsImage = goodsImage;
        }
    }
    
}
