package com.ald.jsd.mgr.web.dto.req;

import java.math.BigDecimal;
import java.util.Date;

public class ReviewLoanDetailsReq extends BaseReq {
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
     * 审核状态 PASS-审核通过，WAIT-待审批，REFUSE - 拒绝
     */
    private String reviewStatus;

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
     * 申请时间
     */
    private Date applyDate;

    /**
     * 授信额度
     */
    private BigDecimal riskAmount;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 商品价格
     */
    private BigDecimal goodsPrice;

    /**
     * 商品手续费
     */
    private BigDecimal goodsPoundageAmount;

    /**
     * 商品利息
     */
    private BigDecimal goodsInterestAmount;

    /**
     * 收货地址
     */
    private String address;

    /**
     * 联系电话
     */
    private String mobile;

    /**
     * 身份证号
     */
    private String idNumber;

    /**
     * 性别
     */
    private String gender;

    /**
     * 年龄
     */
    private String age;

    /**
     * 借款人
     */
    private String realName;

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

    public String getReviewStatus() {
        return reviewStatus;
    }

    public void setReviewStatus(String reviewStatus) {
        this.reviewStatus = reviewStatus;
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

    public Date getApplyDate() {
        return applyDate;
    }

    public void setApplyDate(Date applyDate) {
        this.applyDate = applyDate;
    }

    public BigDecimal getRiskAmount() {
        return riskAmount;
    }

    public void setRiskAmount(BigDecimal riskAmount) {
        this.riskAmount = riskAmount;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public BigDecimal getGoodsPrice() {
        return goodsPrice;
    }

    public void setGoodsPrice(BigDecimal goodsPrice) {
        this.goodsPrice = goodsPrice;
    }

    public BigDecimal getGoodsPoundageAmount() {
        return goodsPoundageAmount;
    }

    public void setGoodsPoundageAmount(BigDecimal goodsPoundageAmount) {
        this.goodsPoundageAmount = goodsPoundageAmount;
    }

    public BigDecimal getGoodsInterestAmount() {
        return goodsInterestAmount;
    }

    public void setGoodsInterestAmount(BigDecimal goodsInterestAmount) {
        this.goodsInterestAmount = goodsInterestAmount;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }
}
