package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 资金方账户基本信息表实体
 * 
 * @author chegnkang
 * @version 1.0.0 初始化
 * @date 2017-09-29 13:53:40
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfFundSideInfoDo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    /**
     * 主键Rid
     */
    private Long rid;
    
    /**
     * 资金方名称
     */
    private String name;

    /**
     * 类别
     */
    private String category;

    /**
     * 联系人名称
     */
    private String contactsName;

    /**
     * 联系人手机号
     */
    private String contactsPhone;

    /**
     * 登录名
     */
    private String loginName;

    /**
     * 登录密码
     */
    private String loginPassword;

    /**
     * 加盐
     */
    private String loginSalt;

    /**
     * 注册时间
     */
    private Date gmtRegister;

    /**
     * 账户修改时间
     */
    private Date gmtModify;

    /**
     * 年华利率
     */
    private BigDecimal annualRate;

    /**
     * Y:启用 N:禁用
     */
    private String status;

    /**
     * 银行卡代码
     */
    private String cardCode;

    /**
     * 银行名称
     */
    private String cardBank;

    /**
     * 银行卡号
     */
    private String cardNo;

    /**
     * 持卡人手机号
     */
    private String cardPhone;

    /**
     * 持卡人身份证
     */
    private String cardUserIdnumber;

    /**
     * 银行卡持卡人姓名
     */
    private String cardUserName;


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
     * 获取资金方名称
     *
     * @return 资金方名称
     */
    public String getName(){
      return name;
    }

    /**
     * 设置资金方名称
     * 
     * @param name 要设置的资金方名称
     */
    public void setName(String name){
      this.name = name;
    }

    /**
     * 获取类别
     *
     * @return 类别
     */
    public String getCategory(){
      return category;
    }

    /**
     * 设置类别
     * 
     * @param category 要设置的类别
     */
    public void setCategory(String category){
      this.category = category;
    }

    /**
     * 获取联系人名称
     *
     * @return 联系人名称
     */
    public String getContactsName(){
      return contactsName;
    }

    /**
     * 设置联系人名称
     * 
     * @param contactsName 要设置的联系人名称
     */
    public void setContactsName(String contactsName){
      this.contactsName = contactsName;
    }

    /**
     * 获取联系人手机号
     *
     * @return 联系人手机号
     */
    public String getContactsPhone(){
      return contactsPhone;
    }

    /**
     * 设置联系人手机号
     * 
     * @param contactsPhone 要设置的联系人手机号
     */
    public void setContactsPhone(String contactsPhone){
      this.contactsPhone = contactsPhone;
    }

    /**
     * 获取登录名
     *
     * @return 登录名
     */
    public String getLoginName(){
      return loginName;
    }

    /**
     * 设置登录名
     * 
     * @param loginName 要设置的登录名
     */
    public void setLoginName(String loginName){
      this.loginName = loginName;
    }

    /**
     * 获取登录密码
     *
     * @return 登录密码
     */
    public String getLoginPassword(){
      return loginPassword;
    }

    /**
     * 设置登录密码
     * 
     * @param loginPassword 要设置的登录密码
     */
    public void setLoginPassword(String loginPassword){
      this.loginPassword = loginPassword;
    }

    /**
     * 获取加盐
     *
     * @return 加盐
     */
    public String getLoginSalt(){
      return loginSalt;
    }

    /**
     * 设置加盐
     * 
     * @param loginSalt 要设置的加盐
     */
    public void setLoginSalt(String loginSalt){
      this.loginSalt = loginSalt;
    }

    /**
     * 获取注册时间
     *
     * @return 注册时间
     */
    public Date getGmtRegister(){
      return gmtRegister;
    }

    /**
     * 设置注册时间
     * 
     * @param gmtRegister 要设置的注册时间
     */
    public void setGmtRegister(Date gmtRegister){
      this.gmtRegister = gmtRegister;
    }

    /**
     * 获取账户修改时间
     *
     * @return 账户修改时间
     */
    public Date getGmtModify(){
      return gmtModify;
    }

    /**
     * 设置账户修改时间
     * 
     * @param gmtModify 要设置的账户修改时间
     */
    public void setGmtModify(Date gmtModify){
      this.gmtModify = gmtModify;
    }

    /**
     * 获取年华利率
     *
     * @return 年华利率
     */
    public BigDecimal getAnnualRate(){
      return annualRate;
    }

    /**
     * 设置年华利率
     * 
     * @param annualRate 要设置的年华利率
     */
    public void setAnnualRate(BigDecimal annualRate){
      this.annualRate = annualRate;
    }

    /**
     * 获取Y:启用 N:禁用
     *
     * @return Y:启用 N:禁用
     */
    public String getStatus(){
      return status;
    }

    /**
     * 设置Y:启用 N:禁用
     * 
     * @param status 要设置的Y:启用 N:禁用
     */
    public void setStatus(String status){
      this.status = status;
    }

    /**
     * 获取银行卡代码
     *
     * @return 银行卡代码
     */
    public String getCardCode(){
      return cardCode;
    }

    /**
     * 设置银行卡代码
     * 
     * @param cardCode 要设置的银行卡代码
     */
    public void setCardCode(String cardCode){
      this.cardCode = cardCode;
    }

    /**
     * 获取银行名称
     *
     * @return 银行名称
     */
    public String getCardBank(){
      return cardBank;
    }

    /**
     * 设置银行名称
     * 
     * @param cardBank 要设置的银行名称
     */
    public void setCardBank(String cardBank){
      this.cardBank = cardBank;
    }

    /**
     * 获取银行卡号
     *
     * @return 银行卡号
     */
    public String getCardNo(){
      return cardNo;
    }

    /**
     * 设置银行卡号
     * 
     * @param cardNo 要设置的银行卡号
     */
    public void setCardNo(String cardNo){
      this.cardNo = cardNo;
    }

    /**
     * 获取持卡人手机号
     *
     * @return 持卡人手机号
     */
    public String getCardPhone(){
      return cardPhone;
    }

    /**
     * 设置持卡人手机号
     * 
     * @param cardPhone 要设置的持卡人手机号
     */
    public void setCardPhone(String cardPhone){
      this.cardPhone = cardPhone;
    }

    /**
     * 获取持卡人身份证
     *
     * @return 持卡人身份证
     */
    public String getCardUserIdnumber(){
      return cardUserIdnumber;
    }

    /**
     * 设置持卡人身份证
     * 
     * @param cardUserIdnumber 要设置的持卡人身份证
     */
    public void setCardUserIdnumber(String cardUserIdnumber){
      this.cardUserIdnumber = cardUserIdnumber;
    }

    /**
     * 获取银行卡持卡人姓名
     *
     * @return 银行卡持卡人姓名
     */
    public String getCardUserName(){
      return cardUserName;
    }

    /**
     * 设置银行卡持卡人姓名
     * 
     * @param cardUserName 要设置的银行卡持卡人姓名
     */
    public void setCardUserName(String cardUserName){
      this.cardUserName = cardUserName;
    }

}