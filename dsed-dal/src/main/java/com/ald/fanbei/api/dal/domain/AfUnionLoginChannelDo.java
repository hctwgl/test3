package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * '联合登录渠道信息表实体
 * 
 * @author maqiaopan-template
 * @version 1.0.0 初始化
 * @date 2017-09-20 15:38:24
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfUnionLoginChannelDo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    /**
     * 主键Rid
     */
    private Long rid;
    
    /**
     * 联合登录商户code
     */
    private String code;

    /**
     * 秘钥，常用于加解密与验签。可以由渠道方提供也可以由爱上街提供
     */
    private String secretKey;

    /**
     * 扩展值
     */
    private String value1;

    /**
     * 扩展值
     */
    private String value2;

    /**
     * 扩展值
     */
    private String value3;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 修改时间
     */
    private Date gmtModified;


    /**
     * 商户名称
     */
    private String name;

    /**
     * 类型 :1.fanbei借贷超市商户 2.外部推广渠道
     */
    private String btype;

    /**
     * 第三方的渠道码，目前无具体作用
     */
    private String thirdChannelCode;


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
     * 获取联合登录商户code
     *
     * @return 联合登录商户code
     */
    public String getCode(){
      return code;
    }

    /**
     * 设置联合登录商户code
     * 
     * @param code 要设置的联合登录商户code
     */
    public void setCode(String code){
      this.code = code;
    }

    /**
     * 获取秘钥，常用于加解密与验签。可以由渠道方提供也可以由爱上街提供
     *
     * @return 秘钥，常用于加解密与验签。可以由渠道方提供也可以由爱上街提供
     */
    public String getSecretKey(){
      return secretKey;
    }

    /**
     * 设置秘钥，常用于加解密与验签。可以由渠道方提供也可以由爱上街提供
     * 
     * @param secretKey 要设置的秘钥，常用于加解密与验签。可以由渠道方提供也可以由爱上街提供
     */
    public void setSecretKey(String secretKey){
      this.secretKey = secretKey;
    }

    /**
     * 获取扩展值
     *
     * @return 扩展值
     */
    public String getValue1(){
      return value1;
    }

    /**
     * 设置扩展值
     * 
     * @param value1 要设置的扩展值
     */
    public void setValue1(String value1){
      this.value1 = value1;
    }

    /**
     * 获取扩展值
     *
     * @return 扩展值
     */
    public String getValue2(){
      return value2;
    }

    /**
     * 设置扩展值
     * 
     * @param value2 要设置的扩展值
     */
    public void setValue2(String value2){
      this.value2 = value2;
    }

    /**
     * 获取扩展值
     *
     * @return 扩展值
     */
    public String getValue3(){
      return value3;
    }

    /**
     * 设置扩展值
     * 
     * @param value3 要设置的扩展值
     */
    public void setValue3(String value3){
      this.value3 = value3;
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
     * 获取修改时间
     *
     * @return 修改时间
     */
    public Date getGmtModified(){
      return gmtModified;
    }

    /**
     * 设置修改时间
     * 
     * @param gmtModified 要设置的修改时间
     */
    public void setGmtModified(Date gmtModified){
      this.gmtModified = gmtModified;
    }


    /**
     * 获取商户名称
     *
     * @return 商户名称
     */
    public String getName(){
      return name;
    }

    /**
     * 设置商户名称
     * 
     * @param name 要设置的商户名称
     */
    public void setName(String name){
      this.name = name;
    }

    /**
     * 获取类型 :1.fanbei借贷超市商户 2.外部推广渠道
     *
     * @return 类型 :1.fanbei借贷超市商户 2.外部推广渠道
     */
    public String getBtype(){
      return btype;
    }

    /**
     * 设置类型 :1.fanbei借贷超市商户 2.外部推广渠道
     * 
     * @param btype 要设置的类型 :1.fanbei借贷超市商户 2.外部推广渠道
     */
    public void setBtype(String btype){
      this.btype = btype;
    }

    /**
     * 获取第三方的渠道码，目前无具体作用
     *
     * @return 第三方的渠道码，目前无具体作用
     */
    public String getThirdChannelCode(){
      return thirdChannelCode;
    }

    /**
     * 设置第三方的渠道码，目前无具体作用
     * 
     * @param thirdChannelCode 要设置的第三方的渠道码，目前无具体作用
     */
    public void setThirdChannelCode(String thirdChannelCode){
      this.thirdChannelCode = thirdChannelCode;
    }

}