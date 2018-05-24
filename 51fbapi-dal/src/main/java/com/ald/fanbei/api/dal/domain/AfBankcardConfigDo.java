package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 信用卡绑定及订单支付实体
 * 
 * @author gaojibin
 * @version 1.0.0 初始化
 * @date 2018-05-09 10:01:47
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfBankcardConfigDo extends AbstractSerial {

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
     * 最后修改时间
     */
    private Date gmtModified;

    /**
     * 银行名称
     */
    private String bankName;

    /**
     * 银行编码
     */
    private String bankCode;

    /**
     * 银行卡名称
     */
    private String cardName;

    /**
     * 银行卡卡号长度
     */
    private Integer cardnoLength;

    /**
     * 
     */
    private String cardnoHeader;

    /**
     * 
     */
    private Integer headerLength;

    /**
     * 0 其它
            1 借记卡
            2 信用卡
     */
    private Integer cardType;

    /**
     * 
     */
    private String cardTypeName;


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
     * 获取银行名称
     *
     * @return 银行名称
     */
    public String getBankName(){
      return bankName;
    }

    /**
     * 设置银行名称
     * 
     * @param bankName 要设置的银行名称
     */
    public void setBankName(String bankName){
      this.bankName = bankName;
    }

    /**
     * 获取银行编码
     *
     * @return 银行编码
     */
    public String getBankCode(){
      return bankCode;
    }

    /**
     * 设置银行编码
     * 
     * @param bankCode 要设置的银行编码
     */
    public void setBankCode(String bankCode){
      this.bankCode = bankCode;
    }

    /**
     * 获取银行卡名称
     *
     * @return 银行卡名称
     */
    public String getCardName(){
      return cardName;
    }

    /**
     * 设置银行卡名称
     * 
     * @param cardName 要设置的银行卡名称
     */
    public void setCardName(String cardName){
      this.cardName = cardName;
    }

    /**
     * 获取银行卡卡号长度
     *
     * @return 银行卡卡号长度
     */
    public Integer getCardnoLength(){
      return cardnoLength;
    }

    /**
     * 设置银行卡卡号长度
     * 
     * @param cardnoLength 要设置的银行卡卡号长度
     */
    public void setCardnoLength(Integer cardnoLength){
      this.cardnoLength = cardnoLength;
    }

    /**
     * 获取
     *
     * @return 
     */
    public String getCardnoHeader(){
      return cardnoHeader;
    }

    /**
     * 设置
     * 
     * @param cardnoHeader 要设置的
     */
    public void setCardnoHeader(String cardnoHeader){
      this.cardnoHeader = cardnoHeader;
    }

    /**
     * 获取
     *
     * @return 
     */
    public Integer getHeaderLength(){
      return headerLength;
    }

    /**
     * 设置
     * 
     * @param headerLength 要设置的
     */
    public void setHeaderLength(Integer headerLength){
      this.headerLength = headerLength;
    }

    /**
     * 获取0 其它
            1 借记卡
            2 信用卡
     *
     * @return 0 其它
            1 借记卡
            2 信用卡
     */
    public Integer getCardType(){
      return cardType;
    }

    /**
     * 设置0 其它
            1 借记卡
            2 信用卡
     * 
     * @param cardType 要设置的0 其它
            1 借记卡
            2 信用卡
     */
    public void setCardType(Integer cardType){
      this.cardType = cardType;
    }

    /**
     * 获取
     *
     * @return 
     */
    public String getCardTypeName(){
      return cardTypeName;
    }

    /**
     * 设置
     * 
     * @param cardTypeName 要设置的
     */
    public void setCardTypeName(String cardTypeName){
      this.cardTypeName = cardTypeName;
    }

}