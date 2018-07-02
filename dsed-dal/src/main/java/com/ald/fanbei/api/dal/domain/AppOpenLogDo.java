package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 *  首页打开日志表实体
 * 
 * @author hqj
 * @version 1.0.0 初始化
 * @date 2017-10-17 20:04:20
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AppOpenLogDo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    /**
     * 主键Rid
     */
    private Long rid;
    
    /**
     * 手机类型
     */
    private String phoneType;

    /**
     * app版本
     */
    private String appVersion;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 设备指纹
     */
    private String uuid;
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
     * @param rid
     */
    public void setRid(Long rid){
      this.rid = rid;
    }
    
    /**
     * 获取手机类型
     *
     * @return 手机类型
     */
    public String getPhoneType(){
      return phoneType;
    }

    /**
     * 设置手机类型
     * 
     * @param phoneType 要设置的手机类型
     */
    public void setPhoneType(String phoneType){
      this.phoneType = phoneType;
    }

    /**
     * 获取app版本
     *
     * @return app版本
     */
    public String getAppVersion(){
      return appVersion;
    }

    /**
     * 设置app版本
     * 
     * @param appVersion 要设置的app版本
     */
    public void setAppVersion(String appVersion){
      this.appVersion = appVersion;
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
     * 获取用户名
     *
     * @return 用户名
     */
    public String getUserName(){
      return userName;
    }

    /**
     * 设置用户名
     * 
     * @param userName 要设置的用户名
     */
    public void setUserName(String userName){
      this.userName = userName;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}