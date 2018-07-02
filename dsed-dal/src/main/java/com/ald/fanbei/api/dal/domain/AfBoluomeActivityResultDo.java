package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;

/**
 * '第三方-上树请求记录实体
 * 
 * @author maqiaopan-template
 * @version 1.0.0 初始化
 * @date 2017-08-01 10:38:33
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfBoluomeActivityResultDo extends AbstractSerial {

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
     * 修改时间
     */
    private Date gmtModified;

    /**
     * 活动id
     */
    private Long boluomeActivityId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户账号
     */
    private String userName;

    /**
     * 返回结果 优惠券id
     */
    private Long result;


    /**
     * 获取主键Id
     *
     * @return id
     */

    

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

    /**
     * 获取活动id
     *
     * @return 活动id
     */
    public Long getBoluomeActivityId(){
      return boluomeActivityId;
    }

    /**
     * 设置活动id
     * 
     * @param boluomeActivityId 要设置的活动id
     */
    public void setBoluomeActivityId(Long boluomeActivityId){
      this.boluomeActivityId = boluomeActivityId;
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
     * 获取返回结果 优惠券id
     *
     * @return 返回结果 优惠券id
     */
    public Long getResult(){
      return result;
    }

    /**
     * 设置返回结果 优惠券id
     * 
     * @param result 要设置的返回结果 优惠券id
     */
    public void setResult(Long result){
      this.result = result;
    }

}