package com.ald.fanbei.api.dal.domain.dto;

import com.ald.fanbei.api.dal.domain.DsedLoanPeriodsDo;

import java.math.BigDecimal;
import java.sql.Date;

public class DsedLoanPeriodsDto extends DsedLoanPeriodsDo {

    private static final long serialVersionUID = -3162832676757115210L;
    private BigDecimal monthlyPayment;
    private Integer repayNum;
    private String userName;
    private String realName;
    private Date gmtPlanRepayment;
    private BigDecimal serviceFee;
    private BigDecimal interestFee;
    private BigDecimal repaidServiceFee;
    private BigDecimal repaidInterestFee;
    private BigDecimal periodsAmount;
    private Date gmtPlanRepay;
    private Integer days;
    private BigDecimal interestRate;
    private BigDecimal serviceRate;
    private BigDecimal overdueRate;
    private Date gmtArrival;
    private Integer overdueDays;
    private long id;
    private Long rid;
    private Integer arrivalAmount;

    @Override
    public Long getRid() {
        return rid;
    }

    @Override
    public void setRid(Long rid) {
        this.rid = rid;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public Integer getOverdueDays() {
        return overdueDays;
    }

    @Override
    public void setOverdueDays(Integer overdueDays) {
        this.overdueDays = overdueDays;
    }

    public Date getGmtArrival() {
        return gmtArrival;
    }

    public void setGmtArrival(Date gmtArrival) {
        this.gmtArrival = gmtArrival;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public BigDecimal getServiceRate() {
        return serviceRate;
    }

    public void setServiceRate(BigDecimal serviceRate) {
        this.serviceRate = serviceRate;
    }

    public BigDecimal getOverdueRate() {
        return overdueRate;
    }

    public void setOverdueRate(BigDecimal overdueRate) {
        this.overdueRate = overdueRate;
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
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

    public BigDecimal getRepaidServiceFee() {
        return repaidServiceFee;
    }

    public void setRepaidServiceFee(BigDecimal repaidServiceFee) {
        this.repaidServiceFee = repaidServiceFee;
    }

    public BigDecimal getRepaidInterestFee() {
        return repaidInterestFee;
    }

    public Integer getArrivalAmount() {
        return arrivalAmount;
    }

    public void setArrivalAmount(Integer arrivalAmount) {
        this.arrivalAmount = arrivalAmount;
    }

    public void setRepaidInterestFee(BigDecimal repaidInterestFee) {
        this.repaidInterestFee = repaidInterestFee;
    }

    public BigDecimal getPeriodsAmount() {
        return periodsAmount;
    }

    public void setPeriodsAmount(BigDecimal periodsAmount) {
        this.periodsAmount = periodsAmount;
    }

    public Date getGmtPlanRepay() {
        return gmtPlanRepay;
    }

    public void setGmtPlanRepay(Date gmtPlanRepay) {
        this.gmtPlanRepay = gmtPlanRepay;
    }

    public BigDecimal getMonthlyPayment() {
        return monthlyPayment;
    }

    public void setMonthlyPayment(BigDecimal monthlyPayment) {
        this.monthlyPayment = monthlyPayment;
    }

    public Integer getRepayNum() {
        return repayNum;
    }

    public void setRepayNum(Integer repayNum) {
        this.repayNum = repayNum;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public Date getGmtPlanRepayment() {
        return gmtPlanRepayment;
    }

    public void setGmtPlanRepayment(Date gmtPlanRepayment) {
        this.gmtPlanRepayment = gmtPlanRepayment;
    }
}

