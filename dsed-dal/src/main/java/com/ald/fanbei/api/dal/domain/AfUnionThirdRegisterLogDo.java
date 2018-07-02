package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * '联合注册接口日志表实体
 * 
 * @author hqj
 * @version 1.0.0 初始化
 * @date 2017-10-05 15:56:53
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfUnionThirdRegisterLogDo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    /**
     * 主键Rid
     */
    private Long rid;
    
    /**
     * 登录时间
     */
    private Date gmtModified;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 渠道code
     */
    private String channelCode;


    /**
     * 返回信息
     */
    private String returnInfo;

    /**
     * 第三方返回信息
     */
    private String returnThirdInfo;

    /**
     * 接口访问时间
     */
    private Long  requestTime;



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

    public String getReturnInfo() {
        return returnInfo;
    }

    public void setReturnInfo(String returnInfo) {
        this.returnInfo = returnInfo;
    }

    public String getReturnThirdInfo() {
        return returnThirdInfo;
    }

    public void setReturnThirdInfo(String returnThirdInfo) {
        this.returnThirdInfo = returnThirdInfo;
    }

    public Long getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(Long requestTime) {
        this.requestTime = requestTime;
    }

}