package com.ald.fanbei.api.biz.bo.barlyClearance;

import java.math.BigDecimal;

/**
 * @author honghzengpei 2017/11/28 16:49
 * @类描述：订单支付
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AllBarlyClearanceDetailBo {
    private Long billId;
    private int nper;  //期数
    private BigDecimal amount; //金额
    private BigDecimal poundAmount; //手续费
    private BigDecimal interest;    //预期利息
    private int status;             //1己出帐  ,0未出帐
    private int overdue;            //1 逾期，0正常
    private Boolean isFree;         //是否勉手续费;

    public int getNper() {
        return nper;
    }

    public void setNper(int nper) {
        this.nper = nper;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getPoundAmount() {
        return poundAmount;
    }

    public void setPoundAmount(BigDecimal poundAmount) {
        this.poundAmount = poundAmount;
    }

    public BigDecimal getInterest() {
        return interest;
    }

    public void setInterest(BigDecimal interest) {
        this.interest = interest;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Boolean getFree() {
        return isFree;
    }

    public void setFree(Boolean free) {
        isFree = free;
    }

    public int getOverdue() {
        return overdue;
    }

    public void setOverdue(int overdue) {
        this.overdue = overdue;
    }

    public Long getBillId() {
        return billId;
    }

    public void setBillId(Long billId) {
        this.billId = billId;
    }
}
