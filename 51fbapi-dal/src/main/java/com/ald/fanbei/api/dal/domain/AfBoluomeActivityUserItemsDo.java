package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;

/**
 * '第三方-上树请求记录实体
 * 
 * @author maqiaopan-template
 * @version 1.0.0 初始化
 * @date 2017-08-01 10:38:47
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfBoluomeActivityUserItemsDo extends AbstractSerial {

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
     * 活动物件名称
     */
    private Long boluomeActivityId;

    /**
     * 用户id
     */
    private Long userId;

    private String userName;
  

	/**
     * 状态【NORMAL:正常状态 FROZEN:赠送中 SENDED:已经赠送】
     */
    private String status;

    /**
     * 来源人的id 主动领取为-1 若大于0 则为原来user_items_id
     */
    private Long sourceId;

    /**
     * 来源人id
     */
    private Long sourceUserId;

    /**
     * 赠送时间
     */
    private Date gmtSended;


    /**
     * 获取主键Id
     *
     * @return id
     */

    private Long itemsId;

    public Long getItemsId() {
		return itemsId;
	}

	public void setItemsId(Long itemsId) {
		this.itemsId = itemsId;
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
     * 获取活动物件名称
     *
     * @return 活动物件名称
     */
    public Long getBoluomeActivityId(){
      return boluomeActivityId;
    }

    /**
     * 设置活动物件名称
     * 
     * @param boluomeActivityId 要设置的活动物件名称
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
     * 获取状态【NORMAL:正常状态 FROZEN:赠送中 SENDED:已经赠送】
     *
     * @return 状态【NORMAL:正常状态 FROZEN:赠送中 SENDED:已经赠送】
     */
    public String getStatus(){
      return status;
    }

    /**
     * 设置状态【NORMAL:正常状态 FROZEN:赠送中 SENDED:已经赠送】
     * 
     * @param status 要设置的状态【NORMAL:正常状态 FROZEN:赠送中 SENDED:已经赠送】
     */
    public void setStatus(String status){
      this.status = status;
    }

    /**
     * 获取来源人的id 主动领取为-1 若大于0 则为原来user_items_id
     *
     * @return 来源人的id 主动领取为-1 若大于0 则为原来user_items_id
     */
    public Long getSourceId(){
      return sourceId;
    }

    /**
     * 设置来源人的id 主动领取为-1 若大于0 则为原来user_items_id
     * 
     * @param sourceId 要设置的来源人的id 主动领取为-1 若大于0 则为原来user_items_id
     */
    public void setSourceId(Long sourceId){
      this.sourceId = sourceId;
    }

    /**
     * 获取来源人id
     *
     * @return 来源人id
     */
    public Long getSourceUserId(){
      return sourceUserId;
    }

    /**
     * 设置来源人id
     * 
     * @param sourceUserId 要设置的来源人id
     */
    public void setSourceUserId(Long sourceUserId){
      this.sourceUserId = sourceUserId;
    }

    /**
     * 获取赠送时间
     *
     * @return 赠送时间
     */
    public Date getGmtSended(){
      return gmtSended;
    }

    /**
     * 设置赠送时间
     * 
     * @param gmtSended 要设置的赠送时间
     */
    public void setGmtSended(Date gmtSended){
      this.gmtSended = gmtSended;
    }
    public String getUserName() {
 		return userName;
 	}

 	public void setUserName(String userName) {
 		this.userName = userName;
 	}
}