package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 极速贷实体
 * 
 * @author yanghailong
 * @version 1.0.0 初始化
 * @date 2018-08-22 16:18:06
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class JsdBorrowCashDo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    /**
     * 主键Rid
     */
    private Long rid;
    
    /**
     * 借款编号
     */
    private String borrowNo;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 借款天数
     */
    private String type;

    /**
     * 申请金额
     */
    private BigDecimal amount;

    /**
     * 借款状态【APPLY:申请/未审核，WAITTRANSED:待打款，TRANSEDFAIL:打款失败,TRANSEDING:打款中 , TRANSED:已经打款/待还款,CLOSED:关闭,FINSH:已结清】
     */
    private String status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 卡号或者支付宝账号
     */
    private String cardNumber;

    /**
     * 卡名称,或者支付宝
     */
    private String cardName;

    /**
     * 逾期状态：Y：逾期；N：未逾期
     */
    private String overdueStatus;

    /**
     * 逾期天数
     */
    private Long overdueDay;

    /**
     * 到账金额
     */
    private BigDecimal arrivalAmount;

    /**
     * 手续费
     */
    private BigDecimal poundage;
    /**
     * 利息
     */
    private BigDecimal rateAmount;

    public BigDecimal getRateAmount() {
		return rateAmount;
	}

	public void setRateAmount(BigDecimal rateAmount) {
		this.rateAmount = rateAmount;
	}

	/**
     * 已还本金
     */
    private BigDecimal repayPrinciple;

    /**
     * 已还款金额
     */
    private BigDecimal repayAmount;

    /**
     * 逾期费
     */
    private BigDecimal overdueAmount;

    /**
     * 累计利息
     */
    private BigDecimal sumRate;

    /**
     * 累计逾期金
     */
    private BigDecimal sumOverdue;

    /**
     * 累计续期手续费
     */
    private BigDecimal sumRenewalPoundage;

    /**
     * 累计续期次数
     */
    private Long renewalNum;

    /**
     * 借钱手续费率（日）
     */
    private BigDecimal poundageRate;

    /**
     * 用户分层日利率
     */
    private BigDecimal riskDailyRate;

    /**
     * 预计还款时间
     */
    private Date gmtPlanRepayment;

    /**
     * 打款时间
     */
    private Date gmtArrival;

    /**
     * 关闭时间
     */
    private Date gmtClose;

    /**
     * 
     */
    private Date gmtCreate;

    /**
     * 
     */
    private Date gmtModified;


    /**
     * 订单为FINSH时记录该时间
     */
    private String finishDate;

    /**
     * 续借前的逾期状态
     */
    private String rdBeforeOverdueStatus;

    /**
     * 借款类型:v1:赊账,v2:搭售
     */
    private String version;


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
     * 获取借款编号
     *
     * @return 借款编号
     */
    public String getBorrowNo(){
      return borrowNo;
    }

    /**
     * 设置借款编号
     * 
     * @param borrowNo 要设置的借款编号
     */
    public void setBorrowNo(String borrowNo){
      this.borrowNo = borrowNo;
    }

    /**
     * 获取用户id
     *
     * @return 用户id
     */
    public Long getUserId(){
      return userId;
    }

    /**
     * 设置用户id
     * 
     * @param userId 要设置的用户id
     */
    public void setUserId(Long userId){
      this.userId = userId;
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
     * 获取申请金额
     *
     * @return 申请金额
     */
    public BigDecimal getAmount(){
      return amount;
    }

    /**
     * 设置申请金额
     * 
     * @param amount 要设置的申请金额
     */
    public void setAmount(BigDecimal amount){
      this.amount = amount;
    }

    /**
     * 获取借款状态【APPLY:申请/未审核，WAITTRANSED:待打款，TRANSEDFAIL:打款失败,TRANSEDING:打款中 , TRANSED:已经打款/待还款,CLOSED:关闭,FINSH:已结清】
     *
     * @return 借款状态【APPLY:申请/未审核，WAITTRANSED:待打款，TRANSEDFAIL:打款失败,TRANSEDING:打款中 , TRANSED:已经打款/待还款,CLOSED:关闭,FINSH:已结清】
     */
    public String getStatus(){
      return status;
    }

    /**
     * 设置借款状态【APPLY:申请/未审核，WAITTRANSED:待打款，TRANSEDFAIL:打款失败,TRANSEDING:打款中 , TRANSED:已经打款/待还款,CLOSED:关闭,FINSH:已结清】
     * 
     * @param status 要设置的借款状态【APPLY:申请/未审核，WAITTRANSED:待打款，TRANSEDFAIL:打款失败,TRANSEDING:打款中 , TRANSED:已经打款/待还款,CLOSED:关闭,FINSH:已结清】
     */
    public void setStatus(String status){
      this.status = status;
    }

    /**
     * 获取备注
     *
     * @return 备注
     */
    public String getRemark(){
      return remark;
    }

    /**
     * 设置备注
     * 
     * @param remark 要设置的备注
     */
    public void setRemark(String remark){
      this.remark = remark;
    }

    /**
     * 获取卡号或者支付宝账号
     *
     * @return 卡号或者支付宝账号
     */
    public String getCardNumber(){
      return cardNumber;
    }

    /**
     * 设置卡号或者支付宝账号
     * 
     * @param cardNumber 要设置的卡号或者支付宝账号
     */
    public void setCardNumber(String cardNumber){
      this.cardNumber = cardNumber;
    }

    /**
     * 获取卡名称,或者支付宝
     *
     * @return 卡名称,或者支付宝
     */
    public String getCardName(){
      return cardName;
    }

    /**
     * 设置卡名称,或者支付宝
     * 
     * @param cardName 要设置的卡名称,或者支付宝
     */
    public void setCardName(String cardName){
      this.cardName = cardName;
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
     * 获取逾期天数
     *
     * @return 逾期天数
     */
    public Long getOverdueDay(){
      return overdueDay;
    }

    /**
     * 设置逾期天数
     * 
     * @param overdueDay 要设置的逾期天数
     */
    public void setOverdueDay(Long overdueDay){
      this.overdueDay = overdueDay;
    }

    /**
     * 获取到账金额
     *
     * @return 到账金额
     */
    public BigDecimal getArrivalAmount(){
      return arrivalAmount;
    }

    /**
     * 设置到账金额
     * 
     * @param arrivalAmount 要设置的到账金额
     */
    public void setArrivalAmount(BigDecimal arrivalAmount){
      this.arrivalAmount = arrivalAmount;
    }

    /**
     * 获取手续费
     *
     * @return 手续费
     */
    public BigDecimal getPoundage(){
      return poundage;
    }

    /**
     * 设置手续费
     * 
     * @param poundage 要设置的手续费
     */
    public void setPoundage(BigDecimal poundage){
      this.poundage = poundage;
    }

    /**
     * 获取已还本金
     *
     * @return 已还本金
     */
    public BigDecimal getRepayPrinciple(){
      return repayPrinciple;
    }

    /**
     * 设置已还本金
     * 
     * @param repayPrinciple 要设置的已还本金
     */
    public void setRepayPrinciple(BigDecimal repayPrinciple){
      this.repayPrinciple = repayPrinciple;
    }

    /**
     * 获取已还款金额
     *
     * @return 已还款金额
     */
    public BigDecimal getRepayAmount(){
      return repayAmount;
    }

    /**
     * 设置已还款金额
     * 
     * @param repayAmount 要设置的已还款金额
     */
    public void setRepayAmount(BigDecimal repayAmount){
      this.repayAmount = repayAmount;
    }

    /**
     * 获取逾期费
     *
     * @return 逾期费
     */
    public BigDecimal getOverdueAmount(){
      return overdueAmount;
    }

    /**
     * 设置逾期费
     * 
     * @param overdueAmount 要设置的逾期费
     */
    public void setOverdueAmount(BigDecimal overdueAmount){
      this.overdueAmount = overdueAmount;
    }

    /**
     * 获取累计利息
     *
     * @return 累计利息
     */
    public BigDecimal getSumRate(){
      return sumRate;
    }

    /**
     * 设置累计利息
     * 
     * @param sumRate 要设置的累计利息
     */
    public void setSumRate(BigDecimal sumRate){
      this.sumRate = sumRate;
    }

    /**
     * 获取累计逾期金
     *
     * @return 累计逾期金
     */
    public BigDecimal getSumOverdue(){
      return sumOverdue;
    }

    /**
     * 设置累计逾期金
     * 
     * @param sumOverdue 要设置的累计逾期金
     */
    public void setSumOverdue(BigDecimal sumOverdue){
      this.sumOverdue = sumOverdue;
    }

    /**
     * 获取累计续期手续费
     *
     * @return 累计续期手续费
     */
    public BigDecimal getSumRenewalPoundage(){
      return sumRenewalPoundage;
    }

    /**
     * 设置累计续期手续费
     * 
     * @param sumRenewalPoundage 要设置的累计续期手续费
     */
    public void setSumRenewalPoundage(BigDecimal sumRenewalPoundage){
      this.sumRenewalPoundage = sumRenewalPoundage;
    }

    /**
     * 获取累计续期次数
     *
     * @return 累计续期次数
     */
    public Long getRenewalNum(){
      return renewalNum;
    }

    /**
     * 设置累计续期次数
     * 
     * @param renewalNum 要设置的累计续期次数
     */
    public void setRenewalNum(Long renewalNum){
      this.renewalNum = renewalNum;
    }

    /**
     * 获取借钱手续费率（日）
     *
     * @return 借钱手续费率（日）
     */
    public BigDecimal getPoundageRate(){
      return poundageRate;
    }

    /**
     * 设置借钱手续费率（日）
     * 
     * @param poundageRate 要设置的借钱手续费率（日）
     */
    public void setPoundageRate(BigDecimal poundageRate){
      this.poundageRate = poundageRate;
    }

    /**
     * 获取用户分层日利率
     *
     * @return 用户分层日利率
     */
    public BigDecimal getRiskDailyRate(){
      return riskDailyRate;
    }

    /**
     * 设置用户分层日利率
     * 
     * @param riskDailyRate 要设置的用户分层日利率
     */
    public void setRiskDailyRate(BigDecimal riskDailyRate){
      this.riskDailyRate = riskDailyRate;
    }

    /**
     * 获取预计还款时间
     *
     * @return 预计还款时间
     */
    public Date getGmtPlanRepayment(){
      return gmtPlanRepayment;
    }

    /**
     * 设置预计还款时间
     * 
     * @param gmtPlanRepayment 要设置的预计还款时间
     */
    public void setGmtPlanRepayment(Date gmtPlanRepayment){
      this.gmtPlanRepayment = gmtPlanRepayment;
    }

    /**
     * 获取打款时间
     *
     * @return 打款时间
     */
    public Date getGmtArrival(){
      return gmtArrival;
    }

    /**
     * 设置打款时间
     * 
     * @param gmtArrival 要设置的打款时间
     */
    public void setGmtArrival(Date gmtArrival){
      this.gmtArrival = gmtArrival;
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
     * 获取订单为FINSH时记录该时间
     *
     * @return 订单为FINSH时记录该时间
     */
    public String getFinishDate(){
      return finishDate;
    }

    /**
     * 设置订单为FINSH时记录该时间
     * 
     * @param finishDate 要设置的订单为FINSH时记录该时间
     */
    public void setFinishDate(String finishDate){
      this.finishDate = finishDate;
    }

    /**
     * 获取续借前的逾期状态
     *
     * @return 续借前的逾期状态
     */
    public String getRdBeforeOverdueStatus(){
      return rdBeforeOverdueStatus;
    }

    /**
     * 设置续借前的逾期状态
     * 
     * @param rdBeforeOverdueStatus 要设置的续借前的逾期状态
     */
    public void setRdBeforeOverdueStatus(String rdBeforeOverdueStatus){
      this.rdBeforeOverdueStatus = rdBeforeOverdueStatus;
    }

    /**
     * 获取借款类型:v1:赊账,v2:搭售
     *
     * @return 借款类型:v1:赊账,v2:搭售
     */
    public String getVersion(){
      return version;
    }

    /**
     * 设置借款类型:v1:赊账,v2:搭售
     * 
     * @param version 要设置的借款类型:v1:赊账,v2:搭售
     */
    public void setVersion(String version){
      this.version = version;
    }

}