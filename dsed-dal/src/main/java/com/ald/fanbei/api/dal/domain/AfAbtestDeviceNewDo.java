package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 用户设备号记录表实体
 * 
 * @author chenqiwei
 * @version 1.0.0 初始化
 * @date 2018-03-06 19:59:03
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfAbtestDeviceNewDo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    /**
     * 主键Rid
     */
    private Long rid;
    

    /**
     * 创建时间
     */
    private Date gmtModified;

    /**
     * 最后更新时间
     */
    private Date gmtCreate;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 设备尾号
     */
    private String deviceNum;

    /**
     * 登录日期
     */
    private Integer loginDate;


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
    public Date getGmtModified(){
      return gmtModified;
    }

    /**
     * 设置创建时间
     * 
     * @param gmtModified 要设置的创建时间
     */
    public void setGmtModified(Date gmtModified){
      this.gmtModified = gmtModified;
    }

    /**
     * 获取最后更新时间
     *
     * @return 最后更新时间
     */
    public Date getGmtCreate(){
      return gmtCreate;
    }

    /**
     * 设置最后更新时间
     * 
     * @param gmtCreate 要设置的最后更新时间
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

    /**
     * 获取设备尾号
     *
     * @return 设备尾号
     */
    public String getDeviceNum(){
      return deviceNum;
    }

    /**
     * 设置设备尾号
     * 
     * @param deviceNum 要设置的设备尾号
     */
    public void setDeviceNum(String deviceNum){
      this.deviceNum = deviceNum;
    }

    public Integer getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(Integer loginDate) {
        this.loginDate = loginDate;
    }

    
}