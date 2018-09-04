package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 极速贷实体
 * 
 * @author yanghailong
 * @version 1.0.0 初始化
 * @date 2018-08-22 16:18:07
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class JsdBorrowLegalOrderCashDo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    /**
     * 主键Rid
     */
    private Long rid;
    

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 
     */
    private Date gmtModified;

    /**
     * 
     */
    private Long userId;

    /**
     * 原借款Id
     */
    private Long borrowId;

    /**
     * 对应借款附带订单id
     */
    private Long borrowLegalOrderId;

    /**
     * 借款流水号
     */
    private String cashNo;

    /**
     * 借款天数
     */
    private String type;

    /**
     * 借款金额
     */
    private BigDecimal amount;

    /**
     * 借款状态【APPLYING:审核中，AWAIT_REPAY:待还款,PART_REPAID: 部分还款, FINISHED:已经结清,CLOSED:借款关闭】
     */
    private String status;

    /**
     * 
     */
    private String remark;

    /**
     * 借款用途
     */
    private String borrowRemark;

    /**
     * 还款来源
     */
    private String refundRemark;

    /**
     * 逾期天数
     */
    private short overdueDay;

    /**
     * 逾期状态：Y：逾期；N：未逾期
     */
    private String overdueStatus;

    /**
     * 逾期产生的逾期额
     */
    private BigDecimal overdueAmount;

    /**
     * 累计已还逾期额
     */
    private BigDecimal sumRepaidOverdue;

    /**
     * 已还款金额
     */
    private BigDecimal repaidAmount;

    /**
     * 手续费
     */
    private BigDecimal poundageAmount;

    /**
     * 累计已还手续费
     */
    private BigDecimal sumRepaidPoundage;

    /**
     * 利息费
     */
    private BigDecimal interestAmount;

    /**
     * 累计已还利息费
     */
    private BigDecimal sumRepaidInterest;

    /**
     * 手续费费率
     */
    private BigDecimal poundageRate;

    /**
     * 利率
     */
    private BigDecimal interestRate;

    /**
     * 
     */
    private Integer planRepayDays;

    /**
     * 应还日期
     */
    private Date gmtPlanRepay;

    /**
     * 关闭时间
     */
    private Date gmtClose;

    /**
     * 最近一次还款的时间
     */
    private Date gmtLastRepayment;

    /**
     * 借款结清时间
     */
    private Date gmtFinish;


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
     * 获取创建时间
     *
     * @return 创建时间
     */
    public Date getGmtCreate(){
      return gmtCreate;
    }

    /**
     * 设置创建时间
     * 
     * @param gmtCreate 要设置的创建时间
     */
    public void setGmtCreate(Date gmtCreate){
      this.gmtCreate = gmtCreate;
    }

    /**
     * 获取
     *
     * @return 
     */
    public Long getUserId(){
      return userId;
    }

    /**
     * 设置
     * 
     * @param userId 要设置的
     */
    public void setUserId(Long userId){
      this.userId = userId;
    }

    /**
     * 获取原借款Id
     *
     * @return 原借款Id
     */
    public Long getBorrowId(){
      return borrowId;
    }

    /**
     * 设置原借款Id
     * 
     * @param borrowId 要设置的原借款Id
     */
    public void setBorrowId(Long borrowId){
      this.borrowId = borrowId;
    }

    /**
     * 获取对应借款附带订单id
     *
     * @return 对应借款附带订单id
     */
    public Long getBorrowLegalOrderId(){
      return borrowLegalOrderId;
    }

    /**
     * 设置对应借款附带订单id
     * 
     * @param borrowLegalOrderId 要设置的对应借款附带订单id
     */
    public void setBorrowLegalOrderId(Long borrowLegalOrderId){
      this.borrowLegalOrderId = borrowLegalOrderId;
    }

    /**
     * 获取借款流水号
     *
     * @return 借款流水号
     */
    public String getCashNo(){
      return cashNo;
    }

    /**
     * 设置借款流水号
     * 
     * @param cashNo 要设置的借款流水号
     */
    public void setCashNo(String cashNo){
      this.cashNo = cashNo;
    }

    /**
     * 获取借款天数
     *
     * @return 借款天数
     */
    public String getType(){
      return type;
    }

    /**
     * 设置借款天数
     * 
     * @param type 要设置的借款天数
     */
    public void setType(String type){
      this.type = type;
    }

    /**
     * 获取借款金额
     *
     * @return 借款金额
     */
    public BigDecimal getAmount(){
      return amount;
    }

    /**
     * 设置借款金额
     * 
     * @param amount 要设置的借款金额
     */
    public void setAmount(BigDecimal amount){
      this.amount = amount;
    }

    /**
     * 获取借款状态【APPLYING:审核中，AWAIT_REPAY:待还款,PART_REPAID: 部分还款, FINISHED:已经结清,CLOSED:借款关闭】
     *
     * @return 借款状态【APPLYING:审核中，AWAIT_REPAY:待还款,PART_REPAID: 部分还款, FINISHED:已经结清,CLOSED:借款关闭】
     */
    public String getStatus(){
      return status;
    }

    /**
     * 设置借款状态【APPLYING:审核中，AWAIT_REPAY:待还款,PART_REPAID: 部分还款, FINISHED:已经结清,CLOSED:借款关闭】
     * 
     * @param status 要设置的借款状态【APPLYING:审核中，AWAIT_REPAY:待还款,PART_REPAID: 部分还款, FINISHED:已经结清,CLOSED:借款关闭】
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
     * 获取借款用途
     *
     * @return 借款用途
     */
    public String getBorrowRemark(){
      return borrowRemark;
    }

    /**
     * 设置借款用途
     * 
     * @param borrowRemark 要设置的借款用途
     */
    public void setBorrowRemark(String borrowRemark){
      this.borrowRemark = borrowRemark;
    }

    /**
     * 获取还款来源
     *
     * @return 还款来源
     */
    public String getRefundRemark(){
      return refundRemark;
    }

    /**
     * 设置还款来源
     * 
     * @param refundRemark 要设置的还款来源
     */
    public void setRefundRemark(String refundRemark){
      this.refundRemark = refundRemark;
    }

    /**
     * 获取逾期天数
     *
     * @return 逾期天数
     */
    public short getOverdueDay(){
      return overdueDay;
    }

    /**
     * 设置逾期天数
     * 
     * @param overdueDay 要设置的逾期天数
     */
    public void setOverdueDay(short overdueDay){
      this.overdueDay = overdueDay;
    }

    /**
     * 获取逾期状态：Y：逾期；N：未逾期
     *
     * @return 逾期状态：Y：逾期；N：未逾期
     */
    public String getOverdueStatus(){
      return overdueStatus;
    }

    /**
     * 设置逾期状态：Y：逾期；N：未逾期
     * 
     * @param overdueStatus 要设置的逾期状态：Y：逾期；N：未逾期
     */
    public void setOverdueStatus(String overdueStatus){
      this.overdueStatus = overdueStatus;
    }

    /**
     * 获取逾期产生的逾期额
     *
     * @return 逾期产生的逾期额
     */
    public BigDecimal getOverdueAmount(){
      return overdueAmount;
    }

    /**
     * 设置逾期产生的逾期额
     * 
     * @param overdueAmount 要设置的逾期产生的逾期额
     */
    public void setOverdueAmount(BigDecimal overdueAmount){
      this.overdueAmount = overdueAmount;
    }

    /**
     * 获取累计已还逾期额
     *
     * @return 累计已还逾期额
     */
    public BigDecimal getSumRepaidOverdue(){
      return sumRepaidOverdue;
    }

    /**
     * 设置累计已还逾期额
     * 
     * @param sumRepaidOverdue 要设置的累计已还逾期额
     */
    public void setSumRepaidOverdue(BigDecimal sumRepaidOverdue){
      this.sumRepaidOverdue = sumRepaidOverdue;
    }

    /**
     * 获取已还款金额
     *
     * @return 已还款金额
     */
    public BigDecimal getRepaidAmount(){
      return repaidAmount;
    }

    /**
     * 设置已还款金额
     * 
     * @param repaidAmount 要设置的已还款金额
     */
    public void setRepaidAmount(BigDecimal repaidAmount){
      this.repaidAmount = repaidAmount;
    }

    /**
     * 获取手续费
     *
     * @return 手续费
     */
    public BigDecimal getPoundageAmount(){
      return poundageAmount;
    }

    /**
     * 设置手续费
     * 
     * @param poundageAmount 要设置的手续费
     */
    public void setPoundageAmount(BigDecimal poundageAmount){
      this.poundageAmount = poundageAmount;
    }

    /**
     * 获取累计已还手续费
     *
     * @return 累计已还手续费
     */
    public BigDecimal getSumRepaidPoundage(){
      return sumRepaidPoundage;
    }

    /**
     * 设置累计已还手续费
     * 
     * @param sumRepaidPoundage 要设置的累计已还手续费
     */
    public void setSumRepaidPoundage(BigDecimal sumRepaidPoundage){
      this.sumRepaidPoundage = sumRepaidPoundage;
    }

    /**
     * 获取利息费
     *
     * @return 利息费
     */
    public BigDecimal getInterestAmount(){
      return interestAmount;
    }

    /**
     * 设置利息费
     * 
     * @param interestAmount 要设置的利息费
     */
    public void setInterestAmount(BigDecimal interestAmount){
      this.interestAmount = interestAmount;
    }

    /**
     * 获取累计已还利息费
     *
     * @return 累计已还利息费
     */
    public BigDecimal getSumRepaidInterest(){
      return sumRepaidInterest;
    }

    /**
     * 设置累计已还利息费
     * 
     * @param sumRepaidInterest 要设置的累计已还利息费
     */
    public void setSumRepaidInterest(BigDecimal sumRepaidInterest){
      this.sumRepaidInterest = sumRepaidInterest;
    }

    /**
     * 获取手续费费率
     *
     * @return 手续费费率
     */
    public BigDecimal getPoundageRate(){
      return poundageRate;
    }

    /**
     * 设置手续费费率
     * 
     * @param poundageRate 要设置的手续费费率
     */
    public void setPoundageRate(BigDecimal poundageRate){
      this.poundageRate = poundageRate;
    }

    /**
     * 获取利率
     *
     * @return 利率
     */
    public BigDecimal getInterestRate(){
      return interestRate;
    }

    /**
     * 设置利率
     * 
     * @param interestRate 要设置的利率
     */
    public void setInterestRate(BigDecimal interestRate){
      this.interestRate = interestRate;
    }

    /**
     * 获取
     *
     * @return 
     */
    public Integer getPlanRepayDays(){
      return planRepayDays;
    }

    /**
     * 设置
     * 
     * @param planRepayDays 要设置的
     */
    public void setPlanRepayDays(Integer planRepayDays){
      this.planRepayDays = planRepayDays;
    }

    /**
     * 获取应还日期
     *
     * @return 应还日期
     */
    public Date getGmtPlanRepay(){
      return gmtPlanRepay;
    }

    /**
     * 设置应还日期
     * 
     * @param gmtPlanRepay 要设置的应还日期
     */
    public void setGmtPlanRepay(Date gmtPlanRepay){
      this.gmtPlanRepay = gmtPlanRepay;
    }

    /**
     * 获取关闭时间
     *
     * @return 关闭时间
     */
    public Date getGmtClose(){
      return gmtClose;
    }

    /**
     * 设置关闭时间
     * 
     * @param gmtClose 要设置的关闭时间
     */
    public void setGmtClose(Date gmtClose){
      this.gmtClose = gmtClose;
    }

    /**
     * 获取最近一次还款的时间
     *
     * @return 最近一次还款的时间
     */
    public Date getGmtLastRepayment(){
      return gmtLastRepayment;
    }

    /**
     * 设置最近一次还款的时间
     * 
     * @param gmtLastRepayment 要设置的最近一次还款的时间
     */
    public void setGmtLastRepayment(Date gmtLastRepayment){
      this.gmtLastRepayment = gmtLastRepayment;
    }

    /**
     * 获取借款结清时间
     *
     * @return 借款结清时间
     */
    public Date getGmtFinish(){
      return gmtFinish;
    }

    /**
     * 设置借款结清时间
     * 
     * @param gmtFinish 要设置的借款结清时间
     */
    public void setGmtFinish(Date gmtFinish){
      this.gmtFinish = gmtFinish;
    }

	public Date getGmtModified() {
		return gmtModified;
	}

	public void setGmtModified(Date gmtModified) {
		this.gmtModified = gmtModified;
	}

}