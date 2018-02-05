package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 一元活动用户注册记录表实体
 * 
 * @author chenqiwei
 * @version 1.0.0 初始化
 * @date 2018-01-26 20:04:31
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfBoluomeOneYuanRegisterDo extends AbstractSerial {

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
     * 注册手机号
     */
    private String mobile;

    /**
     * 注册来源
     */
    private String typeFrom;

    /**
     * 来源点位
     */
    private String typeFromNum;

    private String inviter;

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
     * 获取注册手机号
     *
     * @return 注册手机号
     */
    public String getMobile(){
      return mobile;
    }

    /**
     * 设置注册手机号
     * 
     * @param mobile 要设置的注册手机号
     */
    public void setMobile(String mobile){
      this.mobile = mobile;
    }

    /**
     * 获取注册来源
     *
     * @return 注册来源
     */
    public String getTypeFrom(){
      return typeFrom;
    }

    /**
     * 设置注册来源
     * 
     * @param typeFrom 要设置的注册来源
     */
    public void setTypeFrom(String typeFrom){
      this.typeFrom = typeFrom;
    }

    /**
     * 获取来源点位
     *
     * @return 来源点位
     */
    public String getTypeFromNum(){
      return typeFromNum;
    }

    /**
     * 设置来源点位
     * 
     * @param typeFromNum 要设置的来源点位
     */
    public void setTypeFromNum(String typeFromNum){
      this.typeFromNum = typeFromNum;
    }

    public String getInviter() {
        return inviter;
    }

    public void setInviter(String inviter) {
        this.inviter = inviter;
    }

}