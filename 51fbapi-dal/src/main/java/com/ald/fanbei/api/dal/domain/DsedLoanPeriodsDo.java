package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 都市易贷借款期数表实体
 * 
 * @author gaojibin
 * @version 1.0.0 初始化
 * @date 2018-06-19 10:44:06
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class DsedLoanPeriodsDo extends AbstractSerial {

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
     * 当期本金
     */
    private BigDecimal amount;

    /**
     * 借钱总期数
     */
    private Integer periods;

    /**
     * 当期期数
     */
    private Integer nper;

    /**
     * 当期状态【FINISHED:已还清 ，REPAYING:还款中，AWAIT_REPAY :未还款，PART_REPAY:部分还款】
     */
    private String status;

    /**
     * 对应的还款Id
     */
    private Long repayId;

    /**
     * 备注信息
     */
    private String remark;

    /**
     * 是否逾期，Y表示逾期，N表示未逾期，还清后也不重置
     */
    private String overdueStatus;

    /**
     * 逾期天数
     */
    private Integer overdueDays;

    /**
     * 未还利息费
     */
    private BigDecimal interestFee;

    /**
     * 未还手续费
     */
    private BigDecimal serviceFee;

    /**
     * 未还逾期费用
     */
    private BigDecimal overdueAmount;

    /**
     * 已还金额
     */
    private BigDecimal repayAmount;

    /**
     * 提前还款状态,"Y"表示提前还款，"N"表示非提前还款
     */
    private String preRepayStatus;

    /**
     * 还款手续费
     */
    private BigDecimal repaidServiceFee;

    /**
     * 还款利息费
     */
    private BigDecimal repaidInterestFee;

    /**
     * 已还逾期费
     */
    private BigDecimal repaidOverdueAmount;

    /**
     * 最后一次还款时间
     */
    private Date gmtLastRepay;

    /**
     * 计划还款日期
     */
    private Date gmtPlanRepay;


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
     * 获取当期期数
     *
     * @return 当期期数
     */
    public Integer getNper(){
      return nper;
    }

    /**
     * 设置当期期数
     * 
     * @param nper 要设置的当期期数
     */
    public void setNper(Integer nper){
      this.nper = nper;
    }

    /**
     * 获取当期状态【FINISHED:已还清 ，REPAYING:还款中，AWAIT_REPAY :未还款，PART_REPAY:部分还款】
     *
     * @return 当期状态【FINISHED:已还清 ，REPAYING:还款中，AWAIT_REPAY :未还款，PART_REPAY:部分还款】
     */
    public String getStatus(){
      return status;
    }

    /**
     * 设置当期状态【FINISHED:已还清 ，REPAYING:还款中，AWAIT_REPAY :未还款，PART_REPAY:部分还款】
     * 
     * @param status 要设置的当期状态【FINISHED:已还清 ，REPAYING:还款中，AWAIT_REPAY :未还款，PART_REPAY:部分还款】
     */
    public void setStatus(String status){
      this.status = status;
    }

    /**
     * 获取对应的还款Id
     *
     * @return 对应的还款Id
     */
    public Long getRepayId(){
      return repayId;
    }

    /**
     * 设置对应的还款Id
     * 
     * @param repayId 要设置的对应的还款Id
     */
    public void setRepayId(Long repayId){
      this.repayId = repayId;
    }

    /**
     * 获取备注信息
     *
     * @return 备注信息
     */
    public String getRemark(){
      return remark;
    }

    /**
     * 设置备注信息
     * 
     * @param remark 要设置的备注信息
     */
    public void setRemark(String remark){
      this.remark = remark;
    }

    /**
     * 获取是否逾期，Y表示逾期，N表示未逾期，还清后也不重置
     *
     * @return 是否逾期，Y表示逾期，N表示未逾期，还清后也不重置
     */
    public String getOverdueStatus(){
      return overdueStatus;
    }

    /**
     * 设置是否逾期，Y表示逾期，N表示未逾期，还清后也不重置
     * 
     * @param overdueStatus 要设置的是否逾期，Y表示逾期，N表示未逾期，还清后也不重置
     */
    public void setOverdueStatus(String overdueStatus){
      this.overdueStatus = overdueStatus;
    }

    /**
     * 获取逾期天数
     *
     * @return 逾期天数
     */
    public Integer getOverdueDays(){
      return overdueDays;
    }

    /**
     * 设置逾期天数
     * 
     * @param overdueDays 要设置的逾期天数
     */
    public void setOverdueDays(Integer overdueDays){
      this.overdueDays = overdueDays;
    }

    /**
     * 获取未还利息费
     *
     * @return 未还利息费
     */
    public BigDecimal getInterestFee(){
      return interestFee;
    }

    /**
     * 设置未还利息费
     * 
     * @param interestFee 要设置的未还利息费
     */
    public void setInterestFee(BigDecimal interestFee){
      this.interestFee = interestFee;
    }

    /**
     * 获取未还手续费
     *
     * @return 未还手续费
     */
    public BigDecimal getServiceFee(){
      return serviceFee;
    }

    /**
     * 设置未还手续费
     * 
     * @param serviceFee 要设置的未还手续费
     */
    public void setServiceFee(BigDecimal serviceFee){
      this.serviceFee = serviceFee;
    }

    /**
     * 获取未还逾期费用
     *
     * @return 未还逾期费用
     */
    public BigDecimal getOverdueAmount(){
      return overdueAmount;
    }

    /**
     * 设置未还逾期费用
     * 
     * @param overdueAmount 要设置的未还逾期费用
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

    /**
     * 获取还款手续费
     *
     * @return 还款手续费
     */
    public BigDecimal getRepaidServiceFee(){
      return repaidServiceFee;
    }

    /**
     * 设置还款手续费
     * 
     * @param repaidServiceFee 要设置的还款手续费
     */
    public void setRepaidServiceFee(BigDecimal repaidServiceFee){
      this.repaidServiceFee = repaidServiceFee;
    }

    /**
     * 获取还款利息费
     *
     * @return 还款利息费
     */
    public BigDecimal getRepaidInterestFee(){
      return repaidInterestFee;
    }

    /**
     * 设置还款利息费
     * 
     * @param repaidInterestFee 要设置的还款利息费
     */
    public void setRepaidInterestFee(BigDecimal repaidInterestFee){
      this.repaidInterestFee = repaidInterestFee;
    }

    /**
     * 获取已还逾期费
     *
     * @return 已还逾期费
     */
    public BigDecimal getRepaidOverdueAmount(){
      return repaidOverdueAmount;
    }

    /**
     * 设置已还逾期费
     * 
     * @param repaidOverdueAmount 要设置的已还逾期费
     */
    public void setRepaidOverdueAmount(BigDecimal repaidOverdueAmount){
      this.repaidOverdueAmount = repaidOverdueAmount;
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

}