package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 实体
 * 
 * @author yanghailong
 * @version 1.0.0 初始化
 * @date 2018-09-10 13:22:35
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class JsdBorrowCashRenewalDo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    /**
     * 主键Rid
     */
    private Long rid;
    
    /**
     * 借款ID
     */
    private Long borrowId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 状态【A:新建状态，P:处理中,S:快捷支付短信发送成功, Y:续期成功 , N:续期失败,B续期退还】
     */
    private String status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 续期本金
     */
    private BigDecimal renewalAmount;

    /**
     * 上期利息
     */
    private BigDecimal priorInterest;

    /**
     * 上期滞纳金
     */
    private BigDecimal priorOverdue;

    /**
     * 上期手续费
     */
    private BigDecimal priorPoundage;

    /**
     * 本次续期所还本金
     */
    private BigDecimal capital;

    /**
     * 实付金额
     */
    private BigDecimal actualAmount;

    /**
     * 下期利息
     */
    private BigDecimal nextInterest;

    /**
     * 下期手续费
     */
    private BigDecimal nextPoundage;

    /**
     * 支付方式（卡名称）
     */
    private String cardName;

    /**
     * 卡号
     */
    private String cardNumber;

    /**
     * 续期编号（我方生成）
     */
    private String tradeNo;

    /**
     * 续期编号（西瓜提供）
     */
    private String tradeNoXgxy;

    /**
     * 第三方的交易流水号（UPS提供）
     */
    private String tradeNoUps;

    /**
     * 续期天数
     */
    private Long renewalDay;

    /**
     * 借钱手续费率（日）
     */
    private BigDecimal poundageRate;

    /**
     * 央行基准利率
     */
    private BigDecimal baseBankRate;

    /**
     * 逾期天数
     */
    private Integer overdueDay;

    /**
     * 逾期状态
     */
    private String overdueStatus;

    /**
     * 原预计还款时间
     */
    private Date gmtPlanRepayment;

    /**
     * 失败码
     */
    private String failCode;

    /**
     * 失败原因
     */
    private String failMsg;

    /**
     * 申请时间
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
     * 获取借款ID
     *
     * @return 借款ID
     */
    public Long getBorrowId(){
      return borrowId;
    }

    /**
     * 设置借款ID
     * 
     * @param borrowId 要设置的借款ID
     */
    public void setBorrowId(Long borrowId){
      this.borrowId = borrowId;
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
     * 获取状态【A:新建状态，P:处理中,S:快捷支付短信发送成功, Y:续期成功 , N:续期失败,B续期退还】
     *
     * @return 状态【A:新建状态，P:处理中,S:快捷支付短信发送成功, Y:续期成功 , N:续期失败,B续期退还】
     */
    public String getStatus(){
      return status;
    }

    /**
     * 设置状态【A:新建状态，P:处理中,S:快捷支付短信发送成功, Y:续期成功 , N:续期失败,B续期退还】
     * 
     * @param status 要设置的状态【A:新建状态，P:处理中,S:快捷支付短信发送成功, Y:续期成功 , N:续期失败,B续期退还】
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
     * 获取续期本金
     *
     * @return 续期本金
     */
    public BigDecimal getRenewalAmount(){
      return renewalAmount;
    }

    /**
     * 设置续期本金
     * 
     * @param renewalAmount 要设置的续期本金
     */
    public void setRenewalAmount(BigDecimal renewalAmount){
      this.renewalAmount = renewalAmount;
    }

    /**
     * 获取上期利息
     *
     * @return 上期利息
     */
    public BigDecimal getPriorInterest(){
      return priorInterest;
    }

    /**
     * 设置上期利息
     * 
     * @param priorInterest 要设置的上期利息
     */
    public void setPriorInterest(BigDecimal priorInterest){
      this.priorInterest = priorInterest;
    }

    /**
     * 获取上期滞纳金
     *
     * @return 上期滞纳金
     */
    public BigDecimal getPriorOverdue(){
      return priorOverdue;
    }

    /**
     * 设置上期滞纳金
     * 
     * @param priorOverdue 要设置的上期滞纳金
     */
    public void setPriorOverdue(BigDecimal priorOverdue){
      this.priorOverdue = priorOverdue;
    }

    /**
     * 获取上期手续费
     *
     * @return 上期手续费
     */
    public BigDecimal getPriorPoundage(){
      return priorPoundage;
    }

    /**
     * 设置上期手续费
     * 
     * @param priorPoundage 要设置的上期手续费
     */
    public void setPriorPoundage(BigDecimal priorPoundage){
      this.priorPoundage = priorPoundage;
    }

    /**
     * 获取本次续期所还本金
     *
     * @return 本次续期所还本金
     */
    public BigDecimal getCapital(){
      return capital;
    }

    /**
     * 设置本次续期所还本金
     * 
     * @param capital 要设置的本次续期所还本金
     */
    public void setCapital(BigDecimal capital){
      this.capital = capital;
    }

    /**
     * 获取实付金额
     *
     * @return 实付金额
     */
    public BigDecimal getActualAmount(){
      return actualAmount;
    }

    /**
     * 设置实付金额
     * 
     * @param actualAmount 要设置的实付金额
     */
    public void setActualAmount(BigDecimal actualAmount){
      this.actualAmount = actualAmount;
    }

    /**
     * 获取下期利息
     *
     * @return 下期利息
     */
    public BigDecimal getNextInterest(){
      return nextInterest;
    }

    /**
     * 设置下期利息
     * 
     * @param nextInterest 要设置的下期利息
     */
    public void setNextInterest(BigDecimal nextInterest){
      this.nextInterest = nextInterest;
    }

    /**
     * 获取下期手续费
     *
     * @return 下期手续费
     */
    public BigDecimal getNextPoundage(){
      return nextPoundage;
    }

    /**
     * 设置下期手续费
     * 
     * @param nextPoundage 要设置的下期手续费
     */
    public void setNextPoundage(BigDecimal nextPoundage){
      this.nextPoundage = nextPoundage;
    }

    /**
     * 获取支付方式（卡名称）
     *
     * @return 支付方式（卡名称）
     */
    public String getCardName(){
      return cardName;
    }

    /**
     * 设置支付方式（卡名称）
     * 
     * @param cardName 要设置的支付方式（卡名称）
     */
    public void setCardName(String cardName){
      this.cardName = cardName;
    }

    /**
     * 获取卡号
     *
     * @return 卡号
     */
    public String getCardNumber(){
      return cardNumber;
    }

    /**
     * 设置卡号
     * 
     * @param cardNumber 要设置的卡号
     */
    public void setCardNumber(String cardNumber){
      this.cardNumber = cardNumber;
    }

    /**
     * 获取续期编号（我方生成）
     *
     * @return 续期编号（我方生成）
     */
    public String getTradeNo(){
      return tradeNo;
    }

    /**
     * 设置续期编号（我方生成）
     * 
     * @param tradeNo 要设置的续期编号（我方生成）
     */
    public void setTradeNo(String tradeNo){
      this.tradeNo = tradeNo;
    }

    /**
     * 获取续期编号（西瓜提供）
     *
     * @return 续期编号（西瓜提供）
     */
    public String getTradeNoXgxy(){
      return tradeNoXgxy;
    }

    /**
     * 设置续期编号（西瓜提供）
     * 
     * @param tradeNoXgxy 要设置的续期编号（西瓜提供）
     */
    public void setTradeNoXgxy(String tradeNoXgxy){
      this.tradeNoXgxy = tradeNoXgxy;
    }

    /**
     * 获取第三方的交易流水号（UPS提供）
     *
     * @return 第三方的交易流水号（UPS提供）
     */
    public String getTradeNoUps(){
      return tradeNoUps;
    }

    /**
     * 设置第三方的交易流水号（UPS提供）
     * 
     * @param tradeNoUps 要设置的第三方的交易流水号（UPS提供）
     */
    public void setTradeNoUps(String tradeNoUps){
      this.tradeNoUps = tradeNoUps;
    }

    /**
     * 获取续期天数
     *
     * @return 续期天数
     */
    public Long getRenewalDay(){
      return renewalDay;
    }

    /**
     * 设置续期天数
     * 
     * @param renewalDay 要设置的续期天数
     */
    public void setRenewalDay(Long renewalDay){
      this.renewalDay = renewalDay;
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
     * 获取央行基准利率
     *
     * @return 央行基准利率
     */
    public BigDecimal getBaseBankRate(){
      return baseBankRate;
    }

    /**
     * 设置央行基准利率
     * 
     * @param baseBankRate 要设置的央行基准利率
     */
    public void setBaseBankRate(BigDecimal baseBankRate){
      this.baseBankRate = baseBankRate;
    }

    /**
     * 获取逾期天数
     *
     * @return 逾期天数
     */
    public Integer getOverdueDay(){
      return overdueDay;
    }

    /**
     * 设置逾期天数
     * 
     * @param overdueDay 要设置的逾期天数
     */
    public void setOverdueDay(Integer overdueDay){
      this.overdueDay = overdueDay;
    }

    /**
     * 获取逾期状态
     *
     * @return 逾期状态
     */
    public String getOverdueStatus(){
      return overdueStatus;
    }

    /**
     * 设置逾期状态
     * 
     * @param overdueStatus 要设置的逾期状态
     */
    public void setOverdueStatus(String overdueStatus){
      this.overdueStatus = overdueStatus;
    }

    /**
     * 获取原预计还款时间
     *
     * @return 原预计还款时间
     */
    public Date getGmtPlanRepayment(){
      return gmtPlanRepayment;
    }

    /**
     * 设置原预计还款时间
     * 
     * @param gmtPlanRepayment 要设置的原预计还款时间
     */
    public void setGmtPlanRepayment(Date gmtPlanRepayment){
      this.gmtPlanRepayment = gmtPlanRepayment;
    }

    /**
     * 获取失败码
     *
     * @return 失败码
     */
    public String getFailCode(){
      return failCode;
    }

    /**
     * 设置失败码
     * 
     * @param failCode 要设置的失败码
     */
    public void setFailCode(String failCode){
      this.failCode = failCode;
    }

    /**
     * 获取失败原因
     *
     * @return 失败原因
     */
    public String getFailMsg(){
      return failMsg;
    }

    /**
     * 设置失败原因
     * 
     * @param failMsg 要设置的失败原因
     */
    public void setFailMsg(String failMsg){
      this.failMsg = failMsg;
    }

    /**
     * 获取申请时间
     *
     * @return 申请时间
     */
    public Date getGmtCreate(){
      return gmtCreate;
    }

    /**
     * 设置申请时间
     * 
     * @param gmtCreate 要设置的申请时间
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