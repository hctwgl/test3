package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 都市E贷用户通讯录信息表实体
 * 
 * @author guoshuaiqiang
 * @version 1.0.0 初始化
 * @date 2018-06-19 10:52:15
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class DsedUserContactsDo extends AbstractSerial {

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
     * 用户名
     */
    private String userId;

    /**
     * 通讯录关联用户手机号
     */
    private String contactsMobile;


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
     * 获取用户名
     *
     * @return 用户名
     */
    public String getUserId(){
      return userId;
    }

    /**
     * 设置用户名
     * 
     * @param userId 要设置的用户名
     */
    public void setUserId(String userId){
      this.userId = userId;
    }

    /**
     * 获取通讯录关联用户手机号
     *
     * @return 通讯录关联用户手机号
     */
    public String getContactsMobile(){
      return contactsMobile;
    }

    /**
     * 设置通讯录关联用户手机号
     * 
     * @param contactsMobile 要设置的通讯录关联用户手机号
     */
    public void setContactsMobile(String contactsMobile){
      this.contactsMobile = contactsMobile;
    }

}