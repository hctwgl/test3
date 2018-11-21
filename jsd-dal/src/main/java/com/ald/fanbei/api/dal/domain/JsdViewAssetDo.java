package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 实体
 * 
 * @author CodeGenerate
 * @version 1.0.0 初始化
 * @date 2018-11-15 14:22:31
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class JsdViewAssetDo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    private Long borrowCashId;

    /**
     * 主键，自增id
     */
    private Long userId;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 借款编号
     */
    private String borrowNo;

    /**
     * 申请金额
     */
    private BigDecimal amount;

    /**
     * 到账金额
     */
    private BigDecimal arrivalAmount;

    /**
     * 借钱手续费率（日）
     */
    private BigDecimal poundageRate;

    /**
     * 借款天数
     */
    private String type;

    /**
     * 
     */
    private Date gmtCreate;

    /**
     * 打款时间
     */
    private Date gmtArrival;

    /**
     * 身份证号
     */
    private String idNumber;

    /**
     * 绑定手机号
     */
    private String mobile;

    /**
     * 卡名称,或者支付宝
     */
    private String cardName;

    /**
     * 卡号或者支付宝账号
     */
    private String cardNumber;

    /**
     * 借款用途
     */
    private String borrowRemark;

    /**
     * 还款来源
     */
    private String repayRemark;


    /**
     * 获取
     *
     * @return 
     */
    public Long getBorrowCashId(){
      return borrowCashId;
    }

    /**
     * 设置
     * 
     * @param borrowCashId 要设置的
     */
    public void setBorrowCashId(Long borrowCashId){
      this.borrowCashId = borrowCashId;
    }

    /**
     * 获取主键，自增id
     *
     * @return 主键，自增id
     */
    public Long getUserId(){
      return userId;
    }

    /**
     * 设置主键，自增id
     * 
     * @param userId 要设置的主键，自增id
     */
    public void setUserId(Long userId){
      this.userId = userId;
    }

    /**
     * 获取真实姓名
     *
     * @return 真实姓名
     */
    public String getRealName(){
      return realName;
    }

    /**
     * 设置真实姓名
     * 
     * @param realName 要设置的真实姓名
     */
    public void setRealName(String realName){
      this.realName = realName;
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
     * 获取身份证号
     *
     * @return 身份证号
     */
    public String getIdNumber(){
      return idNumber;
    }

    /**
     * 设置身份证号
     * 
     * @param idNumber 要设置的身份证号
     */
    public void setIdNumber(String idNumber){
      this.idNumber = idNumber;
    }

    /**
     * 获取绑定手机号
     *
     * @return 绑定手机号
     */
    public String getMobile(){
      return mobile;
    }

    /**
     * 设置绑定手机号
     * 
     * @param mobile 要设置的绑定手机号
     */
    public void setMobile(String mobile){
      this.mobile = mobile;
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
    public String getRepayRemark(){
      return repayRemark;
    }

    /**
     * 设置还款来源
     * 
     * @param repayRemark 要设置的还款来源
     */
    public void setRepayRemark(String repayRemark){
      this.repayRemark = repayRemark;
    }

}