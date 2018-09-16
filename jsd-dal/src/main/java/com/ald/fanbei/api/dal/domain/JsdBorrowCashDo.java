package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 实体
 * 
 * @author CodeGenerate
 * @version 1.0.0 初始化
 * @date 2018-09-16 11:51:40
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class JsdBorrowCashDo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    /**
     * 主键Rid
     */
    private Long rid;
    
    /**
     * 用户id
     */
    private Long userId;

    /**
     * 该值为西瓜信用为机构产品生成的一个id，标识机构产品，对接时将会告知机构
     */
    private String productNo;

    /**
     * 借款编号
     */
    private String borrowNo;

    /**
     * 
     */
    private String tradeNoXgxy;

    /**
     * 
     */
    private String tradeNoUps;

    /**
     * 借款天数
     */
    private String type;

    /**
     * 借款类型:SELL:赊账,BEHEAD:搭售
     */
    private String version;

    /**
     * 借款状态【APPLY:申请/未审核，WAITTRANSED:待打款，TRANSEDFAIL:打款失败,TRANSEDING:打款中 , TRANSED:已经打款/待还款,CLOSED:关闭,FINSH:已结清】
     */
    private String status;

    /**
     * 审批状态：PASS-审核通过，WAIT-待审批，REFUSE - 拒绝
     */
    private String reviewStatus;

    /**
     * 审批备注
     */
    private String reviewRemark;

    /**
     * 申请金额
     */
    private BigDecimal amount;

    /**
     * 到账金额
     */
    private BigDecimal arrivalAmount;

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
     * 逾期费
     */
    private BigDecimal overdueAmount;

    /**
     * 手续费
     */
    private BigDecimal poundageAmount;

    /**
     * 利息
     */
    private BigDecimal interestAmount;

    /**
     * 已还本金
     */
    private BigDecimal repayPrinciple;

    /**
     * 已还款金额
     */
    private BigDecimal repayAmount;

    /**
     * 累计利息
     */
    private BigDecimal sumRepaidInterest;

    /**
     * 累计续期手续费
     */
    private BigDecimal sumRepaidPoundage;

    /**
     * 累计逾期金
     */
    private BigDecimal sumRepaidOverdue;

    /**
     * 累计续期次数
     */
    private Long renewalNum;

    /**
     * 
     */
    private BigDecimal interestRate;

    /**
     * 借钱手续费率（日）
     */
    private BigDecimal poundageRate;

    /**
     * 
     */
    private BigDecimal overdueRate;

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
     * 订单为FINSH时记录该时间
     */
    private String finishDate;

    /**
     * 备注
     */
    private String remark;

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
     * 获取该值为西瓜信用为机构产品生成的一个id，标识机构产品，对接时将会告知机构
     *
     * @return 该值为西瓜信用为机构产品生成的一个id，标识机构产品，对接时将会告知机构
     */
    public String getProductNo(){
      return productNo;
    }

    /**
     * 设置该值为西瓜信用为机构产品生成的一个id，标识机构产品，对接时将会告知机构
     * 
     * @param productNo 要设置的该值为西瓜信用为机构产品生成的一个id，标识机构产品，对接时将会告知机构
     */
    public void setProductNo(String productNo){
      this.productNo = productNo;
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
     * 获取
     *
     * @return 
     */
    public String getTradeNoXgxy(){
      return tradeNoXgxy;
    }

    /**
     * 设置
     * 
     * @param tradeNoXgxy 要设置的
     */
    public void setTradeNoXgxy(String tradeNoXgxy){
      this.tradeNoXgxy = tradeNoXgxy;
    }

    /**
     * 获取
     *
     * @return 
     */
    public String getTradeNoUps(){
      return tradeNoUps;
    }

    /**
     * 设置
     * 
     * @param tradeNoUps 要设置的
     */
    public void setTradeNoUps(String tradeNoUps){
      this.tradeNoUps = tradeNoUps;
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
     * 获取借款类型:SELL:赊账,BEHEAD:搭售
     *
     * @return 借款类型:SELL:赊账,BEHEAD:搭售
     */
    public String getVersion(){
      return version;
    }

    /**
     * 设置借款类型:SELL:赊账,BEHEAD:搭售
     * 
     * @param version 要设置的借款类型:SELL:赊账,BEHEAD:搭售
     */
    public void setVersion(String version){
      this.version = version;
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
     * 获取审批状态：PASS-审核通过，WAIT-待审批，REFUSE - 拒绝
     *
     * @return 审批状态：PASS-审核通过，WAIT-待审批，REFUSE - 拒绝
     */
    public String getReviewStatus(){
      return reviewStatus;
    }

    /**
     * 设置审批状态：PASS-审核通过，WAIT-待审批，REFUSE - 拒绝
     * 
     * @param reviewStatus 要设置的审批状态：PASS-审核通过，WAIT-待审批，REFUSE - 拒绝
     */
    public void setReviewStatus(String reviewStatus){
      this.reviewStatus = reviewStatus;
    }

    /**
     * 获取审批备注
     *
     * @return 审批备注
     */
    public String getReviewRemark(){
      return reviewRemark;
    }

    /**
     * 设置审批备注
     * 
     * @param reviewRemark 要设置的审批备注
     */
    public void setReviewRemark(String reviewRemark){
      this.reviewRemark = reviewRemark;
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
     * 获取利息
     *
     * @return 利息
     */
    public BigDecimal getInterestAmount(){
      return interestAmount;
    }

    /**
     * 设置利息
     * 
     * @param interestAmount 要设置的利息
     */
    public void setInterestAmount(BigDecimal interestAmount){
      this.interestAmount = interestAmount;
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
     * 获取累计利息
     *
     * @return 累计利息
     */
    public BigDecimal getSumRepaidInterest(){
      return sumRepaidInterest;
    }

    /**
     * 设置累计利息
     * 
     * @param sumRepaidInterest 要设置的累计利息
     */
    public void setSumRepaidInterest(BigDecimal sumRepaidInterest){
      this.sumRepaidInterest = sumRepaidInterest;
    }

    /**
     * 获取累计续期手续费
     *
     * @return 累计续期手续费
     */
    public BigDecimal getSumRepaidPoundage(){
      return sumRepaidPoundage;
    }

    /**
     * 设置累计续期手续费
     * 
     * @param sumRepaidPoundage 要设置的累计续期手续费
     */
    public void setSumRepaidPoundage(BigDecimal sumRepaidPoundage){
      this.sumRepaidPoundage = sumRepaidPoundage;
    }

    /**
     * 获取累计逾期金
     *
     * @return 累计逾期金
     */
    public BigDecimal getSumRepaidOverdue(){
      return sumRepaidOverdue;
    }

    /**
     * 设置累计逾期金
     * 
     * @param sumRepaidOverdue 要设置的累计逾期金
     */
    public void setSumRepaidOverdue(BigDecimal sumRepaidOverdue){
      this.sumRepaidOverdue = sumRepaidOverdue;
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
     * 获取
     *
     * @return 
     */
    public BigDecimal getInterestRate(){
      return interestRate;
    }

    /**
     * 设置
     * 
     * @param interestRate 要设置的
     */
    public void setInterestRate(BigDecimal interestRate){
      this.interestRate = interestRate;
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
     * 获取
     *
     * @return 
     */
    public BigDecimal getOverdueRate(){
      return overdueRate;
    }

    /**
     * 设置
     * 
     * @param overdueRate 要设置的
     */
    public void setOverdueRate(BigDecimal overdueRate){
      this.overdueRate = overdueRate;
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


}