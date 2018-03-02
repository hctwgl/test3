package com.ald.fanbei.api.biz.third.util.baiqishi;

import java.io.Serializable;
import java.util.Date;

/**
 * 白骑士数据存储实体
 */
 public class DataBaiQiShi implements Serializable {

    private static final long serialVersionUID = 1L;



    /**
     * 应用编号
     */
    private String appId;

    /**
     * 事件类型
     */
    private String eventType;

    /**
     * 电话号码
     */
    private String mobile;

    /**
     * 身份证号
     */
    private String certNo;

    /**
     * 姓名
     */
    private String name;

    /**
     * 指纹标识
     */
    private String tokenKey;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 银行卡号
     */
    private String bankCardNo;

    /**
     * 芝麻id
     */
    private String zmOpenId;

    /**
     * 返回结果
     */
    private String result;



    /**
     * 获取应用编号
     *
     * @return 应用编号
     */
    public String getAppId(){
      return appId;
    }

    /**
     * 设置应用编号
     * 
     * @param appId 要设置的应用编号
     */
    public void setAppId(String appId){
      this.appId = appId;
    }

    /**
     * 获取事件类型
     *
     * @return 事件类型
     */
    public String getEventType(){
      return eventType;
    }

    /**
     * 设置事件类型
     * 
     * @param eventType 要设置的事件类型
     */
    public void setEventType(String eventType){
      this.eventType = eventType;
    }

    /**
     * 获取电话号码
     *
     * @return 电话号码
     */
    public String getMobile(){
      return mobile;
    }

    /**
     * 设置电话号码
     * 
     * @param mobile 要设置的电话号码
     */
    public void setMobile(String mobile){
      this.mobile = mobile;
    }

    /**
     * 获取身份证号
     *
     * @return 身份证号
     */
    public String getCertNo(){
      return certNo;
    }

    /**
     * 设置身份证号
     * 
     * @param certNo 要设置的身份证号
     */
    public void setCertNo(String certNo){
      this.certNo = certNo;
    }

    /**
     * 获取姓名
     *
     * @return 姓名
     */
    public String getName(){
      return name;
    }

    /**
     * 设置姓名
     * 
     * @param name 要设置的姓名
     */
    public void setName(String name){
      this.name = name;
    }

    /**
     * 获取指纹标识
     *
     * @return 指纹标识
     */
    public String getTokenKey(){
      return tokenKey;
    }

    /**
     * 设置指纹标识
     * 
     * @param tokenKey 要设置的指纹标识
     */
    public void setTokenKey(String tokenKey){
      this.tokenKey = tokenKey;
    }

    /**
     * 获取创建时间
     *
     * @return 创建时间
     */
    public Date getCreateTime(){
      return createTime;
    }

    /**
     * 设置创建时间
     * 
     * @param createTime 要设置的创建时间
     */
    public void setCreateTime(Date createTime){
      this.createTime = createTime;
    }

    /**
     * 获取银行卡号
     *
     * @return 银行卡号
     */
    public String getBankCardNo(){
      return bankCardNo;
    }

    /**
     * 设置银行卡号
     * 
     * @param bankCardNo 要设置的银行卡号
     */
    public void setBankCardNo(String bankCardNo){
      this.bankCardNo = bankCardNo;
    }

    /**
     * 获取芝麻id
     *
     * @return 芝麻id
     */
    public String getZmOpenId(){
      return zmOpenId;
    }

    /**
     * 设置芝麻id
     * 
     * @param zmOpenId 要设置的芝麻id
     */
    public void setZmOpenId(String zmOpenId){
      this.zmOpenId = zmOpenId;
    }

    /**
     * 获取返回结果
     *
     * @return 返回结果
     */
    public String getResult(){
      return result;
    }

    /**
     * 设置返回结果
     * 
     * @param result 要设置的返回结果
     */
    public void setResult(String result){
      this.result = result;
    }

}