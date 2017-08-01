package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;

/**
 * '第三方-上树请求记录实体
 * 
 * @author maqiaopan-template
 * @version 1.0.0 初始化
 * @date 2017-08-01 10:39:15
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfBoluomeActivityUserRebateDo extends AbstractSerial {

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
     * 订单id
     */
    private Long orderId;

    /**
     * 自己下单返利
     */
    private Double rebate;

    /**
     * 邀请用户下单订单id
     */
    private Long refOrderId;

    /**
     * 邀请人id
     */
    private Long refUserId;

    /**
     * 邀请用户返利
     */
    private Double inviteRebate;


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
     * 获取订单id
     *
     * @return 订单id
     */
    public Long getOrderId(){
      return orderId;
    }

    /**
     * 设置订单id
     * 
     * @param orderId 要设置的订单id
     */
    public void setOrderId(Long orderId){
      this.orderId = orderId;
    }

    /**
     * 获取自己下单返利
     *
     * @return 自己下单返利
     */
    public Double getRebate(){
      return rebate;
    }

    /**
     * 设置自己下单返利
     * 
     * @param rebate 要设置的自己下单返利
     */
    public void setRebate(Double rebate){
      this.rebate = rebate;
    }

    /**
     * 获取邀请用户下单订单id
     *
     * @return 邀请用户下单订单id
     */
    public Long getRefOrderId(){
      return refOrderId;
    }

    /**
     * 设置邀请用户下单订单id
     * 
     * @param refOrderId 要设置的邀请用户下单订单id
     */
    public void setRefOrderId(Long refOrderId){
      this.refOrderId = refOrderId;
    }

    /**
     * 获取邀请人id
     *
     * @return 邀请人id
     */
    public Long getRefUserId(){
      return refUserId;
    }

    /**
     * 设置邀请人id
     * 
     * @param refUserId 要设置的邀请人id
     */
    public void setRefUserId(Long refUserId){
      this.refUserId = refUserId;
    }

    /**
     * 获取邀请用户返利
     *
     * @return 邀请用户返利
     */
    public Double getInviteRebate(){
      return inviteRebate;
    }

    /**
     * 设置邀请用户返利
     * 
     * @param inviteRebate 要设置的邀请用户返利
     */
    public void setInviteRebate(Double inviteRebate){
      this.inviteRebate = inviteRebate;
    }

}