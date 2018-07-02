package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * '联合登录用户登录日志表实体
 * 
 * @author maqiaopan-template
 * @version 1.0.0 初始化
 * @date 2017-09-19 15:57:41
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfUnionLoginRegisterDo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    /**
     * 主键Rid
     */
    private Long rid;
    
    /**
     * 注册渠道
     */
    private String channelCode;

    /**
     * 手机号码
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
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 用户id
     */
    private Long userId;


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
     * 获取手机号码
     *
     * @return 手机号码
     */
    public String getPhone(){
      return phone;
    }

    /**
     * 设置手机号码
     * 
     * @param phone 要设置的手机号码
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

}