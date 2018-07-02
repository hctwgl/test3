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
 * @date 2018-02-02 16:34:01
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfBoluomeJipiaoPassengerDo extends AbstractSerial {

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
     * 机场建设费
     */
    private BigDecimal airportFee;

    /**
     * 生日
     */
    private Date birthday;

    /**
     * 证件号码
     */
    private String credentialCode;

    /**
     * 证件类型
     */
    private String credentialType;

    /**
     * 机票价格
     */
    private BigDecimal facePrice;

    /**
     * 乘客姓名
     */
    private String passengerName;


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
     * 获取机场建设费
     *
     * @return 机场建设费
     */
    public BigDecimal getAirportFee(){
      return airportFee;
    }

    /**
     * 设置机场建设费
     * 
     * @param airportFee 要设置的机场建设费
     */
    public void setAirportFee(BigDecimal airportFee){
      this.airportFee = airportFee;
    }

    /**
     * 获取生日
     *
     * @return 生日
     */
    public Date getBirthday(){
      return birthday;
    }

    /**
     * 设置生日
     * 
     * @param birthday 要设置的生日
     */
    public void setBirthday(Date birthday){
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
    public String getCredentialType(){
      return credentialType;
    }

    /**
     * 设置证件类型
     * 
     * @param credentialType 要设置的证件类型
     */
    public void setCredentialType(String credentialType){
      this.credentialType = credentialType;
    }

    /**
     * 获取机票价格
     *
     * @return 机票价格
     */
    public BigDecimal getFacePrice(){
      return facePrice;
    }

    /**
     * 设置机票价格
     * 
     * @param facePrice 要设置的机票价格
     */
    public void setFacePrice(BigDecimal facePrice){
      this.facePrice = facePrice;
    }

    /**
     * 获取乘客姓名
     *
     * @return 乘客姓名
     */
    @JSONField(name="name")
    public String getPassengerName(){
      return passengerName;
    }

    /**
     * 设置乘客姓名
     * 
     * @param name 要设置的乘客姓名
     */
    @JSONField(name="name")
    public void setPassengerName(String passengerName){
      this.passengerName = passengerName;
    }

}