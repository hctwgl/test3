package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * '联合登录用户登录日志表实体
 * 
 * @author maqiaopan-template
 * @version 1.0.0 初始化
 * @date 2017-09-19 15:33:48
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfUnionLoginLogDo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    /**
     * 主键Rid
     */
    private Long rid;
    
    /**
     * 手机号
     */
    private String phone;

    /**
     * 渠道code
     */
    private String channelCode;

    /**
     * 登录时间
     */
    private Date gmtModified;

    /**
     * 请求信息
     */
    private String requestInfo;


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
     * 获取手机号
     *
     * @return 手机号
     */
    public String getPhone(){
      return phone;
    }

    /**
     * 设置手机号
     * 
     * @param phone 要设置的手机号
     */
    public void setPhone(String phone){
      this.phone = phone;
    }

    /**
     * 获取渠道code
     *
     * @return 渠道code
     */
    public String getChannelCode(){
      return channelCode;
    }

    /**
     * 设置渠道code
     * 
     * @param channelCode 要设置的渠道code
     */
    public void setChannelCode(String channelCode){
      this.channelCode = channelCode;
    }

    /**
     * 获取登录时间
     *
     * @return 登录时间
     */
    public Date getGmtModified(){
      return gmtModified;
    }

    /**
     * 设置登录时间
     * 
     * @param gmtModified 要设置的登录时间
     */
    public void setGmtModified(Date gmtModified){
      this.gmtModified = gmtModified;
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

}