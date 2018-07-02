package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * '联合注册成功日志表实体
 * 
 * @author hqj
 * @version 1.0.0 初始化
 * @date 2017-10-05 10:18:05
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfUnionThirdRegisterDo extends AbstractSerial {

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
     * 注册渠道
     */
    private String channelCode;

    /**
     * 用户名
     */
    private String phone;

    /**
     * 默认密码，不加密
     */
    private String defaultPassword;

    /**
     * 请求信息
     */
    private String requestInfo;

    /**
     * 用户状态：1新用户 2老用户 3其他渠道老用户
     */
    private Integer userState;

    /**
     * 返回链接
     */
    private String returnUrl;


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
     * 获取注册渠道
     *
     * @return 注册渠道
     */
    public String getChannelCode(){
      return channelCode;
    }

    /**
     * 设置注册渠道
     * 
     * @param channelCode 要设置的注册渠道
     */
    public void setChannelCode(String channelCode){
      this.channelCode = channelCode;
    }

    /**
     * 获取用户名
     *
     * @return 用户名
     */
    public String getPhone(){
      return phone;
    }

    /**
     * 设置用户名
     * 
     * @param phone 要设置的用户名
     */
    public void setPhone(String phone){
      this.phone = phone;
    }

    /**
     * 获取默认密码，不加密
     *
     * @return 默认密码，不加密
     */
    public String getDefaultPassword(){
      return defaultPassword;
    }

    /**
     * 设置默认密码，不加密
     * 
     * @param defaultPassword 要设置的默认密码，不加密
     */
    public void setDefaultPassword(String defaultPassword){
      this.defaultPassword = defaultPassword;
    }

    /**
     * 获取请求信息
     *
     * @return 请求信息
     */
    public String getRequestInfo(){
      return requestInfo;
    }

    /**
     * 设置请求信息
     * 
     * @param requestInfo 要设置的请求信息
     */
    public void setRequestInfo(String requestInfo){
      this.requestInfo = requestInfo;
    }

    /**
     * 获取用户状态：1新用户 2老用户 3其他渠道老用户
     *
     * @return 用户状态：1新用户 2老用户 3其他渠道老用户
     */
    public Integer getUserState(){
      return userState;
    }

    /**
     * 设置用户状态：1新用户 2老用户 3其他渠道老用户
     * 
     * @param userState 要设置的用户状态：1新用户 2老用户 3其他渠道老用户
     */
    public void setUserState(Integer userState){
      this.userState = userState;
    }

    /**
     * 获取返回链接
     *
     * @return 返回链接
     */
    public String getReturnUrl(){
      return returnUrl;
    }

    /**
     * 设置返回链接
     * 
     * @param returnUrl 要设置的返回链接
     */
    public void setReturnUrl(String returnUrl){
      this.returnUrl = returnUrl;
    }

}