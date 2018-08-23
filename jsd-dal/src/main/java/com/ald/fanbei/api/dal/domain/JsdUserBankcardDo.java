package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 极速贷用户银行卡信息实体
 * 
 * @author yanghailong
 * @version 1.0.0 初始化
 * @date 2018-08-23 09:40:59
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class JsdUserBankcardDo extends AbstractSerial {

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
     * 用户id
     */
    private Long userId;

    /**
     * 银行预留手机号
     */
    private String mobile;

    /**
     * 银行编号
     */
    private String bankCode;

    /**
     * 银行卡号
     */
    private String bankCardNumber;

    /**
     * 是否为主卡【Y:主卡,N:非主卡】
     */
    private String isMain;

    /**
     * 银行名称
     */
    private String bankName;

    /**
     * 绑定状态【N:new新建,B:bind绑定,U:unbind解绑】
     */
    private String status;

    /**
     * 
     */
    private String validDate;

    /**
     * 
     */
    private String safeCode;

    /**
     * 西瓜信用的绑卡唯一编号
     */
    private String xgxyBindNo;


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
     * 获取银行预留手机号
     *
     * @return 银行预留手机号
     */
    public String getMobile(){
      return mobile;
    }

    /**
     * 设置银行预留手机号
     * 
     * @param mobile 要设置的银行预留手机号
     */
    public void setMobile(String mobile){
      this.mobile = mobile;
    }

    /**
     * 获取银行编号
     *
     * @return 银行编号
     */
    public String getBankCode(){
      return bankCode;
    }

    /**
     * 设置银行编号
     * 
     * @param bankCode 要设置的银行编号
     */
    public void setBankCode(String bankCode){
      this.bankCode = bankCode;
    }

    /**
     * 获取银行卡号
     *
     * @return 银行卡号
     */
    public String getBankCardNumber(){
      return bankCardNumber;
    }

    /**
     * 设置银行卡号
     * 
     * @param bankCardNumber 要设置的银行卡号
     */
    public void setBankCardNumber(String bankCardNumber){
      this.bankCardNumber = bankCardNumber;
    }

    /**
     * 获取是否为主卡【Y:主卡,N:非主卡】
     *
     * @return 是否为主卡【Y:主卡,N:非主卡】
     */
    public String getIsMain(){
      return isMain;
    }

    /**
     * 设置是否为主卡【Y:主卡,N:非主卡】
     * 
     * @param isMain 要设置的是否为主卡【Y:主卡,N:非主卡】
     */
    public void setIsMain(String isMain){
      this.isMain = isMain;
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
     * 获取绑定状态【N:new新建,B:bind绑定,U:unbind解绑】
     *
     * @return 绑定状态【N:new新建,B:bind绑定,U:unbind解绑】
     */
    public String getStatus(){
      return status;
    }

    /**
     * 设置绑定状态【N:new新建,B:bind绑定,U:unbind解绑】
     * 
     * @param status 要设置的绑定状态【N:new新建,B:bind绑定,U:unbind解绑】
     */
    public void setStatus(String status){
      this.status = status;
    }

    /**
     * 获取
     *
     * @return 
     */
    public String getValidDate(){
      return validDate;
    }

    /**
     * 设置
     * 
     * @param validDate 要设置的
     */
    public void setValidDate(String validDate){
      this.validDate = validDate;
    }

    /**
     * 获取
     *
     * @return 
     */
    public String getSafeCode(){
      return safeCode;
    }

    /**
     * 设置
     * 
     * @param safeCode 要设置的
     */
    public void setSafeCode(String safeCode){
      this.safeCode = safeCode;
    }

    /**
     * 获取西瓜信用的绑卡唯一编号
     *
     * @return 西瓜信用的绑卡唯一编号
     */
    public String getXgxyBindNo(){
      return xgxyBindNo;
    }

    /**
     * 设置西瓜信用的绑卡唯一编号
     * 
     * @param xgxyBindNo 要设置的西瓜信用的绑卡唯一编号
     */
    public void setXgxyBindNo(String xgxyBindNo){
      this.xgxyBindNo = xgxyBindNo;
    }

}