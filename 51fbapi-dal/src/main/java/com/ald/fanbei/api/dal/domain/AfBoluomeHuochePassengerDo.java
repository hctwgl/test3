package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;
import java.math.BigDecimal;

/**
 * 菠萝觅订单详情实体
 * 
 * @author gaojb
 * @version 1.0.0 初始化
 * @date 2018-02-02 16:34:02
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfBoluomeHuochePassengerDo extends AbstractSerial {

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
     * 第三方订单号
     */
    private String thirdOrderNo;

    /**
     * 生日
     */
    private String birthday;

    /**
     * 证件号码
     */
    private String credentialCode;

    /**
     * 证件类型
     */
    private String cardType;

    /**
     * 乘客姓名
     */
    private String name;

    /**
     * 购票类型 成人票、儿童票
     */
    private String passengerType;


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
     * 获取第三方订单号
     *
     * @return 第三方订单号
     */
    public String getThirdOrderNo(){
      return thirdOrderNo;
    }

    /**
     * 设置第三方订单号
     * 
     * @param thirdOrderNo 要设置的第三方订单号
     */
    public void setThirdOrderNo(String thirdOrderNo){
      this.thirdOrderNo = thirdOrderNo;
    }

    /**
     * 获取生日
     *
     * @return 生日
     */
    public String getBirthday(){
      return birthday;
    }

    /**
     * 设置生日
     * 
     * @param birthday 要设置的生日
     */
    public void setBirthday(String birthday){
      this.birthday = birthday;
    }

    /**
     * 获取证件号码
     *
     * @return 证件号码
     */
    public String getCredentialCode(){
      return credentialCode;
    }

    /**
     * 设置证件号码
     * 
     * @param credentialCode 要设置的证件号码
     */
    public void setCredentialCode(String credentialCode){
      this.credentialCode = credentialCode;
    }

    /**
     * 获取证件类型
     *
     * @return 证件类型
     */
    public String getCardType(){
      return cardType;
    }

    /**
     * 设置证件类型
     * 
     * @param cardType 要设置的证件类型
     */
    public void setCardType(String cardType){
      this.cardType = cardType;
    }

    /**
     * 获取乘客姓名
     *
     * @return 乘客姓名
     */
    public String getName(){
      return name;
    }

    /**
     * 设置乘客姓名
     * 
     * @param name 要设置的乘客姓名
     */
    public void setName(String name){
      this.name = name;
    }

    /**
     * 获取购票类型 成人票、儿童票
     *
     * @return 购票类型 成人票、儿童票
     */
    public String getPassengerType(){
      return passengerType;
    }

    /**
     * 设置购票类型 成人票、儿童票
     * 
     * @param passengerType 要设置的购票类型 成人票、儿童票
     */
    public void setPassengerType(String passengerType){
      this.passengerType = passengerType;
    }

}