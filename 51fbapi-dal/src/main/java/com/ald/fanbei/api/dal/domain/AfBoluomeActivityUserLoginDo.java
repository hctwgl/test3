package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;

/**
 * '第三方-上树请求记录实体
 * 
 * @author maqiaopan-template
 * @version 1.0.0 初始化
 * @date 2017-08-01 10:39:02
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfBoluomeActivityUserLoginDo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    /**
     * 主键Id
     */
    private Long rid;
    

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户账号
     */
    private String userName;

    /**
     * 邀请人用户id
     */
    private Long refUserId;

    /**
     * 邀请人用户账号
     */
    private String refUserName;


    /**
     * 获取主键Id
     *
     * @return id
     */

    private Long boluomeActivityId;
    

    public Long getBoluomeActivityId() {
		return boluomeActivityId;
	}

	public void setBoluomeActivityId(Long boluomeActivityId) {
		this.boluomeActivityId = boluomeActivityId;
	}

	/**
     * 获取创建时间
     *
     * @return 创建时间
     */
    public Date getGmtCreate(){
      return gmtCreate;
    }

    public Long getRid() {
		return rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
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

    /**
     * 获取用户账号
     *
     * @return 用户账号
     */
    public String getUserName(){
      return userName;
    }

    /**
     * 设置用户账号
     * 
     * @param userName 要设置的用户账号
     */
    public void setUserName(String userName){
      this.userName = userName;
    }

    /**
     * 获取邀请人用户id
     *
     * @return 邀请人用户id
     */
    public Long getRefUserId(){
      return refUserId;
    }

    /**
     * 设置邀请人用户id
     * 
     * @param refUserId 要设置的邀请人用户id
     */
    public void setRefUserId(Long refUserId){
      this.refUserId = refUserId;
    }

    /**
     * 获取邀请人用户账号
     *
     * @return 邀请人用户账号
     */
    public String getRefUserName(){
      return refUserName;
    }

    /**
     * 设置邀请人用户账号
     * 
     * @param refUserName 要设置的邀请人用户账号
     */
    public void setRefUserName(String refUserName){
      this.refUserName = refUserName;
    }

}