package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 贷款业务实体
 * 
 * @author Jiang Rongbo
 * @version 1.0.0 初始化
 * @date 2018-01-19 16:50:32
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfLoanPeriodsDo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    /**
     * 主键Rid
     */
    private Long rid;
    

    /**
     * 
     */
    private Date gmtCreate;

    /**
     * 
     */
    private Date gmtModified;

    /**
     * 用户编号
     */
    private Long userId;

    /**
     * 借款编号
     */
    private Long loanId;

    /**
     * 借钱流水
     */
    private String loanNo;

    /**
     * 贷款产品类型
     */
    private String prdType;

    /**
     * 计划还款日期
     */
    private Date gmtPlanRepay;

    /**
     * 借钱总期数
     */
    private Integer periods;

    /**
     * 期数
     */
    private Integer nper;

    /**
     * 是否还款状态【Y:已还款 ，P:部分还款，D:还款中 N:未还款，C:关闭】
     */
    private String status;

    /**
     * 
     */
    private String remark;

    /**
     * 是否逾期，Y表示逾期，N表示未逾期
     */
    private String overdueStatus;

    /**
     * 
     */
    private Integer overdueDays;

    /**
     * 还款Id
     */
    private Long repayId;

    /**
     * 利息费
     */
    private BigDecimal interestFee;

    /**
     * 手续费
     */
    private BigDecimal serviceFee;

    /**
     * 逾期费用
     */
    private BigDecimal overdueAmount;

    /**
     * 已还金额
     */
    private BigDecimal repayAmount;

    /**
     * 最后一次还款时间
     */
    private Date gmtLastRepay;

    /**
     * 优惠券Id
     */
    private Long couponId;

    /**
     * 优惠金额
     */
    private BigDecimal reducedAmount;

    /**
     * 使用余额还款金额
     */
    private BigDecimal rebatedAmount;

    /**
     * 还款手续费
     */
    private BigDecimal paidServiceFee;

    /**
     * 还款利息费
     */
    private BigDecimal paidInterestFee;

    /**
     * 已还逾期费
     */
    private BigDecimal paidOverdueAmount;

    /**
     * 当期本金
     */
    private BigDecimal amount;

    /**
     * 提前还款状态,"Y"表示提前还款，"N"表示非提前还款
     */
    private String preRepayStatus;


    /**
     * 获取主键Id
     *
     * @return rid
     */
    public Long getRid(){
      return rid;
    }

    /**
     * 设置主键Id
     * 
     * @param 要设置的主键Id
     */
    public void setRid(Long rid){
      this.rid = rid;
    }
    

    /**
     * 获取
     *
     * @return 
     */
    public Date getGmtCreate(){
      return gmtCreate;
    }

    /**
     * 设置
     * 
     * @param gmtCreate 要设置的
     */
    public void setGmtCreate(Date gmtCreate){
      this.gmtCreate = gmtCreate;
    }

    /**
     * 获取
     *
     * @return 
     */
    public Date getGmtModified(){
      return gmtModified;
    }

    /**
     * 设置
     * 
     * @param gmtModified 要设置的
     */
    public void setGmtModified(Date gmtModified){
      this.gmtModified = gmtModified;
    }

    /**
     * 获取用户编号
     *
     * @return 用户编号
     */
    public Long getUserId(){
      return userId;
    }

    /**
     * 设置用户编号
     * 
     * @param userId 要设置的用户编号
     */
    public void setUserId(Long userId){
      this.userId = userId;
    }

    /**
     * 获取借款编号
     *
     * @return 借款编号
     */
    public Long getLoanId(){
      return loanId;
    }

    /**
     * 设置借款编号
     * 
     * @param loanId 要设置的借款编号
     */
    public void setLoanId(Long loanId){
      this.loanId = loanId;
    }

    /**
     * 获取借钱流水
     *
     * @return 借钱流水
     */
    public String getLoanNo(){
      return loanNo;
    }

    /**
     * 设置借钱流水
     * 
     * @param loanNo 要设置的借钱流水
     */
    public void setLoanNo(String loanNo){
      this.loanNo = loanNo;
    }

    /**
     * 获取贷款产品类型
     *
     * @return 贷款产品类型
     */
    public String getPrdType(){
      return prdType;
    }

    /**
     * 设置贷款产品类型
     * 
     * @param prdType 要设置的贷款产品类型
     */
    public void setPrdType(String prdType){
      this.prdType = prdType;
    }

    /**
     * 获取计划还款日期
     *
     * @return 计划还款日期
     */
    public Date getGmtPlanRepay(){
      return gmtPlanRepay;
    }

    /**
     * 设置计划还款日期
     * 
     * @param gmtPlanRepay 要设置的计划还款日期
     */
    public void setGmtPlanRepay(Date gmtPlanRepay){
      this.gmtPlanRepay = gmtPlanRepay;
    }

    /**
     * 获取借钱总期数
     *
     * @return 借钱总期数
     */
    public Integer getPeriods(){
      return periods;
    }

    /**
     * 设置借钱总期数
     * 
     * @param periods 要设置的借钱总期数
     */
    public void setPeriods(Integer periods){
      this.periods = periods;
    }

    /**
     * 获取期数
     *
     * @return 期数
     */
    public Integer getNper(){
      return nper;
    }

    /**
     * 设置期数
     * 
     * @param nper 要设置的期数
     */
    public void setNper(Integer nper){
      this.nper = nper;
    }

    /**
     * 获取是否还款状态【Y:已还款 ，P:部分还款，D:还款中 N:未还款，C:关闭】
     *
     * @return 是否还款状态【Y:已还款 ，P:部分还款，D:还款中 N:未还款，C:关闭】
     */
    public String getStatus(){
      return status;
    }

    /**
     * 设置是否还款状态【Y:已还款 ，P:部分还款，D:还款中 N:未还款，C:关闭】
     * 
     * @param status 要设置的是否还款状态【Y:已还款 ，P:部分还款，D:还款中 N:未还款，C:关闭】
     */
    public void setStatus(String status){
      this.status = status;
    }

    /**
     * 获取
     *
     * @return 
     */
    public String getRemark(){
      return remark;
    }

    /**
     * 设置
     * 
     * @param remark 要设置的
     */
    public void setRemark(String remark){
      this.remark = remark;
    }

    /**
     * 获取是否逾期，Y表示逾期，N表示未逾期
     *
     * @return 是否逾期，Y表示逾期，N表示未逾期
     */
    public String getOverdueStatus(){
      return overdueStatus;
    }

    /**
     * 设置是否逾期，Y表示逾期，N表示未逾期
     * 
     * @param overdueStatus 要设置的是否逾期，Y表示逾期，N表示未逾期
     */
    public void setOverdueStatus(String overdueStatus){
      this.overdueStatus = overdueStatus;
    }

    /**
     * 获取
     *
     * @return 
     */
    public Integer getOverdueDays(){
      return overdueDays;
    }

    /**
     * 设置
     * 
     * @param overdueDays 要设置的
     */
    public void setOverdueDays(Integer overdueDays){
      this.overdueDays = overdueDays;
    }

    /**
     * 获取还款Id
     *
     * @return 还款Id
     */
    public Long getRepayId(){
      return repayId;
    }

    /**
     * 设置还款Id
     * 
     * @param repayId 要设置的还款Id
     */
    public void setRepayId(Long repayId){
      this.repayId = repayId;
    }

    /**
     * 获取利息费
     *
     * @return 利息费
     */
    public BigDecimal getInterestFee(){
      return interestFee;
    }

    /**
     * 设置利息费
     * 
     * @param interestFee 要设置的利息费
     */
    public void setInterestFee(BigDecimal interestFee){
      this.interestFee = interestFee;
    }

    /**
     * 获取手续费
     *
     * @return 手续费
     */
    public BigDecimal getServiceFee(){
      return serviceFee;
    }

    /**
     * 设置手续费
     * 
     * @param serviceFee 要设置的手续费
     */
    public void setServiceFee(BigDecimal serviceFee){
      this.serviceFee = serviceFee;
    }

    /**
     * 获取逾期费用
     *
     * @return 逾期费用
     */
    public BigDecimal getOverdueAmount(){
      return overdueAmount;
    }

    /**
     * 设置逾期费用
     * 
     * @param overdueAmount 要设置的逾期费用
     */
    public void setOverdueAmount(BigDecimal overdueAmount){
      this.overdueAmount = overdueAmount;
    }

    /**
     * 获取已还金额
     *
     * @return 已还金额
     */
    public BigDecimal getRepayAmount(){
      return repayAmount;
    }

    /**
     * 设置已还金额
     * 
     * @param repayAmount 要设置的已还金额
     */
    public void setRepayAmount(BigDecimal repayAmount){
      this.repayAmount = repayAmount;
    }

    /**
     * 获取最后一次还款时间
     *
     * @return 最后一次还款时间
     */
    public Date getGmtLastRepay(){
      return gmtLastRepay;
    }

    /**
     * 设置最后一次还款时间
     * 
     * @param gmtLastRepay 要设置的最后一次还款时间
     */
    public void setGmtLastRepay(Date gmtLastRepay){
      this.gmtLastRepay = gmtLastRepay;
    }

    /**
     * 获取优惠券Id
     *
     * @return 优惠券Id
     */
    public Long getCouponId(){
      return couponId;
    }

    /**
     * 设置优惠券Id
     * 
     * @param couponId 要设置的优惠券Id
     */
    public void setCouponId(Long couponId){
      this.couponId = couponId;
    }

    /**
     * 获取优惠金额
     *
     * @return 优惠金额
     */
    public BigDecimal getReducedAmount(){
      return reducedAmount;
    }

    /**
     * 设置优惠金额
     * 
     * @param reducedAmount 要设置的优惠金额
     */
    public void setReducedAmount(BigDecimal reducedAmount){
      this.reducedAmount = reducedAmount;
    }

    /**
     * 获取使用余额还款金额
     *
     * @return 使用余额还款金额
     */
    public BigDecimal getRebatedAmount(){
      return rebatedAmount;
    }

    /**
     * 设置使用余额还款金额
     * 
     * @param rebatedAmount 要设置的使用余额还款金额
     */
    public void setRebatedAmount(BigDecimal rebatedAmount){
      this.rebatedAmount = rebatedAmount;
    }

    /**
     * 获取还款手续费
     *
     * @return 还款手续费
     */
    public BigDecimal getPaidServiceFee(){
      return paidServiceFee;
    }

    /**
     * 设置还款手续费
     * 
     * @param paidServiceFee 要设置的还款手续费
     */
    public void setPaidServiceFee(BigDecimal paidServiceFee){
      this.paidServiceFee = paidServiceFee;
    }

    /**
     * 获取还款利息费
     *
     * @return 还款利息费
     */
    public BigDecimal getPaidInterestFee(){
      return paidInterestFee;
    }

    /**
     * 设置还款利息费
     * 
     * @param paidInterestFee 要设置的还款利息费
     */
    public void setPaidInterestFee(BigDecimal paidInterestFee){
      this.paidInterestFee = paidInterestFee;
    }

    /**
     * 获取已还逾期费
     *
     * @return 已还逾期费
     */
    public BigDecimal getPaidOverdueAmount(){
      return paidOverdueAmount;
    }

    /**
     * 设置已还逾期费
     * 
     * @param paidOverdueAmount 要设置的已还逾期费
     */
    public void setPaidOverdueAmount(BigDecimal paidOverdueAmount){
      this.paidOverdueAmount = paidOverdueAmount;
    }

    /**
     * 获取当期本金
     *
     * @return 当期本金
     */
    public BigDecimal getAmount(){
      return amount;
    }

    /**
     * 设置当期本金
     * 
     * @param amount 要设置的当期本金
     */
    public void setAmount(BigDecimal amount){
      this.amount = amount;
    }

    /**
     * 获取提前还款状态,"Y"表示提前还款，"N"表示非提前还款
     *
     * @return 提前还款状态,"Y"表示提前还款，"N"表示非提前还款
     */
    public String getPreRepayStatus(){
      return preRepayStatus;
    }

    /**
     * 设置提前还款状态,"Y"表示提前还款，"N"表示非提前还款
     * 
     * @param preRepayStatus 要设置的提前还款状态,"Y"表示提前还款，"N"表示非提前还款
     */
    public void setPreRepayStatus(String preRepayStatus){
      this.preRepayStatus = preRepayStatus;
    }

}