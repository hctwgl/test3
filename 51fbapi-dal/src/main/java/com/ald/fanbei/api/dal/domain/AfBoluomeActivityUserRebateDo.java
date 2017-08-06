package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;

import java.math.BigDecimal;
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
    private BigDecimal rebate;

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
    private BigDecimal inviteRebate;
    
 

	public Long getRid() {
		return rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public Date getGmtCreate() {
		return gmtCreate;
	}

	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}

	public Date getGmtModified() {
		return gmtModified;
	}

	public void setGmtModified(Date gmtModified) {
		this.gmtModified = gmtModified;
	}

	public Long getBoluomeActivityId() {
		return boluomeActivityId;
	}

	public void setBoluomeActivityId(Long boluomeActivityId) {
		this.boluomeActivityId = boluomeActivityId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public BigDecimal getRebate() {
		return rebate;
	}

	public void setRebate(BigDecimal rebate) {
		this.rebate = rebate;
	}

	public Long getRefOrderId() {
		return refOrderId;
	}

	public void setRefOrderId(Long refOrderId) {
		this.refOrderId = refOrderId;
	}

	public Long getRefUserId() {
		return refUserId;
	}

	public void setRefUserId(Long refUserId) {
		this.refUserId = refUserId;
	}

	public BigDecimal getInviteRebate() {
		return inviteRebate;
	}

	public void setInviteRebate(BigDecimal inviteRebate) {
		this.inviteRebate = inviteRebate;
	}



}