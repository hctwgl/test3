package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 用户代扣信息实体
 * 
 * @author hqj
 * @version 1.0.0 初始化
 * @date 2017-11-07 20:35:59
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfUserWithholdDo extends AbstractSerial {

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
     * 用户名
     */
    private String userName;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 银行卡1
     */
    private String cardNumber1;

    /**
     * 银行卡2
     */
    private String cardNumber2;

    /**
     * 银行卡3
     */
    private String cardNumber3;

    /**
     * 银行卡4
     */
    private String cardNumber4;

    /**
     * 银行卡5
     */
    private String cardNumber5;

    /**
     * 是否开启代扣，1：是，0：否
     */
    private Integer isWithhold;

    /**
     * 是否使用余额，1：是，0：否
     */
    private Integer usebalance;

    /**
     * 最后一次开启时间
     */
    private Date lastopenTime;

    /**
     * 银行卡id1
     */
    private Long cardId1;

    /**
     * 银行卡id2
     */
    private Long cardId2;

    /**
     * 银行卡id3
     */
    private Long cardId3;

    /**
     * 银行卡id4
     */
    private Long cardId4;

    /**
     * 银行卡id5
     */
    private Long cardId5;


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
     * 获取用户名
     *
     * @return 用户名
     */
    public String getUserName(){
      return userName;
    }

    /**
     * 设置用户名
     * 
     * @param userName 要设置的用户名
     */
    public void setUserName(String userName){
      this.userName = userName;
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
     * 获取银行卡1
     *
     * @return 银行卡1
     */
    public String getCardNumber1(){
      return cardNumber1;
    }

    /**
     * 设置银行卡1
     * 
     * @param cardNumber1 要设置的银行卡1
     */
    public void setCardNumber1(String cardNumber1){
      this.cardNumber1 = cardNumber1;
    }

    /**
     * 获取银行卡2
     *
     * @return 银行卡2
     */
    public String getCardNumber2(){
      return cardNumber2;
    }

    /**
     * 设置银行卡2
     * 
     * @param cardNumber2 要设置的银行卡2
     */
    public void setCardNumber2(String cardNumber2){
      this.cardNumber2 = cardNumber2;
    }

    /**
     * 获取银行卡3
     *
     * @return 银行卡3
     */
    public String getCardNumber3(){
      return cardNumber3;
    }

    /**
     * 设置银行卡3
     * 
     * @param cardNumber3 要设置的银行卡3
     */
    public void setCardNumber3(String cardNumber3){
      this.cardNumber3 = cardNumber3;
    }

    /**
     * 获取银行卡4
     *
     * @return 银行卡4
     */
    public String getCardNumber4(){
      return cardNumber4;
    }

    /**
     * 设置银行卡4
     * 
     * @param cardNumber4 要设置的银行卡4
     */
    public void setCardNumber4(String cardNumber4){
      this.cardNumber4 = cardNumber4;
    }

    /**
     * 获取银行卡5
     *
     * @return 银行卡5
     */
    public String getCardNumber5(){
      return cardNumber5;
    }

    /**
     * 设置银行卡5
     * 
     * @param cardNumber5 要设置的银行卡5
     */
    public void setCardNumber5(String cardNumber5){
      this.cardNumber5 = cardNumber5;
    }

    /**
     * 获取是否开启代扣，1：是，0：否
     *
     * @return 是否开启代扣，1：是，0：否
     */
    public Integer getIsWithhold(){
      return isWithhold;
    }

    /**
     * 设置是否开启代扣，1：是，0：否
     * 
     * @param isWithhold 要设置的是否开启代扣，1：是，0：否
     */
    public void setIsWithhold(Integer isWithhold){
      this.isWithhold = isWithhold;
    }

    /**
     * 获取是否使用余额，1：是，0：否
     *
     * @return 是否使用余额，1：是，0：否
     */
    public Integer getUsebalance(){
      return usebalance;
    }

    /**
     * 设置是否使用余额，1：是，0：否
     * 
     * @param usebalance 要设置的是否使用余额，1：是，0：否
     */
    public void setUsebalance(Integer usebalance){
      this.usebalance = usebalance;
    }

    /**
     * 获取最后一次开启时间
     *
     * @return 最后一次开启时间
     */
    public Date getLastopenTime(){
      return lastopenTime;
    }

    /**
     * 设置最后一次开启时间
     * 
     * @param lastopenTime 要设置的最后一次开启时间
     */
    public void setLastopenTime(Date lastopenTime){
      this.lastopenTime = lastopenTime;
    }

    /**
     * 获取银行卡id1
     *
     * @return 银行卡id1
     */
    public Long getCardId1(){
      return cardId1;
    }

    /**
     * 设置银行卡id1
     * 
     * @param cardId1 要设置的银行卡id1
     */
    public void setCardId1(Long cardId1){
      this.cardId1 = cardId1;
    }

    /**
     * 获取银行卡id2
     *
     * @return 银行卡id2
     */
    public Long getCardId2(){
      return cardId2;
    }

    /**
     * 设置银行卡id2
     * 
     * @param cardId2 要设置的银行卡id2
     */
    public void setCardId2(Long cardId2){
      this.cardId2 = cardId2;
    }

    /**
     * 获取银行卡id3
     *
     * @return 银行卡id3
     */
    public Long getCardId3(){
      return cardId3;
    }

    /**
     * 设置银行卡id3
     * 
     * @param cardId3 要设置的银行卡id3
     */
    public void setCardId3(Long cardId3){
      this.cardId3 = cardId3;
    }

    /**
     * 获取银行卡id4
     *
     * @return 银行卡id4
     */
    public Long getCardId4(){
      return cardId4;
    }

    /**
     * 设置银行卡id4
     * 
     * @param cardId4 要设置的银行卡id4
     */
    public void setCardId4(Long cardId4){
      this.cardId4 = cardId4;
    }

    /**
     * 获取银行卡id5
     *
     * @return 银行卡id5
     */
    public Long getCardId5(){
      return cardId5;
    }

    /**
     * 设置银行卡id5
     * 
     * @param cardId5 要设置的银行卡id5
     */
    public void setCardId5(Long cardId5){
      this.cardId5 = cardId5;
    }

}