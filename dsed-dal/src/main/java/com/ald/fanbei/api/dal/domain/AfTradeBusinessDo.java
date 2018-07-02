package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;

import java.util.Date;

/**
 * 商圈商户表实体
 * 
 * @author huyang
 * @version 1.0.0 初始化
 * @date 2017-07-14 16:40:39
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfTradeBusinessDo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    /**
     * 主键Id
     */
    private Long id;
    
    /**
     * 登录名
     */
    private String name;

    /**
     * 登录密码
     */
    private String password;

    /**
     * 登录时间
     */
    private Date loginTime;

    /**
     * 登出时间
     */
    private Date logoutTime;

    /**
     * 状态 1:正常 2:失效
     */
    private Integer status;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 修改人
     */
    private String modifier;

    /**
     * 修改时间
     */
    private Date gmtModified;



    /**
     * 获取主键Id
     *
     * @return id
     */
    public Long getId(){
      return id;
    }

    /**
     * 设置主键Id
     * 
     * @param 要设置的主键Id
     */
    public void setId(Long id){
      this.id = id;
    }
    
    /**
     * 获取登录名
     *
     * @return 登录名
     */
    public String getName(){
      return name;
    }

    /**
     * 设置登录名
     * 
     * @param name 要设置的登录名
     */
    public void setName(String name){
      this.name = name;
    }

    /**
     * 获取登录密码
     *
     * @return 登录密码
     */
    public String getPassword(){
      return password;
    }

    /**
     * 设置登录密码
     * 
     * @param password 要设置的登录密码
     */
    public void setPassword(String password){
      this.password = password;
    }

    /**
     * 获取登录时间
     *
     * @return 登录时间
     */
    public Date getLoginTime(){
      return loginTime;
    }

    /**
     * 设置登录时间
     * 
     * @param loginTime 要设置的登录时间
     */
    public void setLoginTime(Date loginTime){
      this.loginTime = loginTime;
    }

    /**
     * 获取登出时间
     *
     * @return 登出时间
     */
    public Date getLogoutTime(){
      return logoutTime;
    }

    /**
     * 设置登出时间
     * 
     * @param logoutTime 要设置的登出时间
     */
    public void setLogoutTime(Date logoutTime){
      this.logoutTime = logoutTime;
    }

    /**
     * 获取状态 1:正常 2:失效
     *
     * @return 状态 1:正常 2:失效
     */
    public Integer getStatus(){
      return status;
    }

    /**
     * 设置状态 1:正常 2:失效
     * 
     * @param status 要设置的状态 1:正常 2:失效
     */
    public void setStatus(Integer status){
      this.status = status;
    }

    /**
     * 获取创建人
     *
     * @return 创建人
     */
    public String getCreator(){
      return creator;
    }

    /**
     * 设置创建人
     * 
     * @param creator 要设置的创建人
     */
    public void setCreator(String creator){
      this.creator = creator;
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
     * 获取修改人
     *
     * @return 修改人
     */
    public String getModifier(){
      return modifier;
    }

    /**
     * 设置修改人
     * 
     * @param modifier 要设置的修改人
     */
    public void setModifier(String modifier){
      this.modifier = modifier;
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


}