package com.ald.fanbei.api.dal.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;
import com.alibaba.fastjson.annotation.JSONField;

/**
 * 商圈商户提现记录表实体
 * 
 * @author huyang
 * @version 1.0.0 初始化
 * @date 2017-07-14 16:46:58
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfTradeWithdrawRecordDo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    /**
     * 主键Id
     */
    private Long id;
    

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 最后修改时间
     */
    private Date gmtModified;

    /**
     * 状态【APPLY:申请/未审核 , AGREE:同意/审核通过 ,REFUSE:拒绝 , TRANSED:已经打款 ,TRANSEDING 打款中， CLOSED:关闭】
     */
    private String status;

    /**
     * 商户id
     */
    private Long businessId;

    /**
     * 提现金额
     */
    private BigDecimal amount;

    /**
     * 类型【CASH：cash 现金提现】
     */
    private String type;

    /**
     * 银行卡号
     */
    private String cardNumber;

    /**
     * 银行名称
     */
    private String cardName;

    /**
     * 银行代码
     */
    private String cardCode;


    /**
     * 获取主键Id
     *
     * @return id
     */
    public Long getId(){
      return id;
    }

    /**
     * 设置主键Id
     * 
     * @param 要设置的主键Id
     */
    public void setId(Long id){
      this.id = id;
    }
    

    /**
     * 获取创建时间
     *
     * @return 创建时间
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
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
     * 获取最后修改时间
     *
     * @return 最后修改时间
     */
    public Date getGmtModified(){
      return gmtModified;
    }

    /**
     * 设置最后修改时间
     * 
     * @param gmtModified 要设置的最后修改时间
     */
    public void setGmtModified(Date gmtModified){
      this.gmtModified = gmtModified;
    }

    /**
     * 获取状态【APPLY:申请/未审核 , AGREE:同意/审核通过 ,REFUSE:拒绝 , TRANSED:已经打款 ,TRANSEDING 打款中， CLOSED:关闭】
     *
     * @return 状态【APPLY:申请/未审核 , AGREE:同意/审核通过 ,REFUSE:拒绝 , TRANSED:已经打款 ,TRANSEDING 打款中， CLOSED:关闭】
     */
    public String getStatus(){
      return status;
    }

    /**
     * 设置状态【APPLY:申请/未审核 , AGREE:同意/审核通过 ,REFUSE:拒绝 , TRANSED:已经打款 ,TRANSEDING 打款中， CLOSED:关闭】
     * 
     * @param status 要设置的状态【APPLY:申请/未审核 , AGREE:同意/审核通过 ,REFUSE:拒绝 , TRANSED:已经打款 ,TRANSEDING 打款中， CLOSED:关闭】
     */
    public void setStatus(String status){
      this.status = status;
    }

    /**
     * 获取商户id
     *
     * @return 商户id
     */
    public Long getBusinessId(){
      return businessId;
    }

    /**
     * 设置商户id
     * 
     * @param businessId 要设置的商户id
     */
    public void setBusinessId(Long businessId){
      this.businessId = businessId;
    }

    /**
     * 获取提现金额
     *
     * @return 提现金额
     */
    public BigDecimal getAmount(){
      return amount;
    }

    /**
     * 设置提现金额
     * 
     * @param amount 要设置的提现金额
     */
    public void setAmount(BigDecimal amount){
      this.amount = amount;
    }

    /**
     * 获取类型【CASH：cash 现金提现】
     *
     * @return 类型【CASH：cash 现金提现】
     */
    public String getType(){
      return type;
    }

    /**
     * 设置类型【CASH：cash 现金提现】
     * 
     * @param type 要设置的类型【CASH：cash 现金提现】
     */
    public void setType(String type){
      this.type = type;
    }

    /**
     * 获取银行卡号
     *
     * @return 银行卡号
     */
    public String getCardNumber(){
      return cardNumber;
    }

    /**
     * 设置银行卡号
     * 
     * @param cardNumber 要设置的银行卡号
     */
    public void setCardNumber(String cardNumber){
      this.cardNumber = cardNumber;
    }

    /**
     * 获取银行名称
     *
     * @return 银行名称
     */
    public String getCardName(){
      return cardName;
    }

    /**
     * 设置银行名称
     * 
     * @param cardName 要设置的银行名称
     */
    public void setCardName(String cardName){
      this.cardName = cardName;
    }

    /**
     * 获取银行代码
     *
     * @return 银行代码
     */
    public String getCardCode(){
      return cardCode;
    }

    /**
     * 设置银行代码
     * 
     * @param cardCode 要设置的银行代码
     */
    public void setCardCode(String cardCode){
      this.cardCode = cardCode;
    }

}