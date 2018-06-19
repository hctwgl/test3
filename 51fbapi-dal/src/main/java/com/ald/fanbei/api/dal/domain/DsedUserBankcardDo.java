package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 都市E贷用户绑定的银行卡实体
 * 
 * @author guoshuaiqiang
 * @version 1.0.0 初始化
 * @date 2018-06-19 10:51:50
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class DsedUserBankcardDo extends AbstractSerial {

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
    private String cardNumber;

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

}