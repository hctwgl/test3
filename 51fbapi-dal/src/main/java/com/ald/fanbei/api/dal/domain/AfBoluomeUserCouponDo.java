package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 点亮活动新版实体
 * 
 * @author gaojibin_temple
 * @version 1.0.0 初始化
 * @date 2017-11-13 17:28:33
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfBoluomeUserCouponDo extends AbstractSerial {

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
     * 用户id
     */
    private Long userId;

    /**
     * 优惠卷id
     */
    private Long couponId;

    /**
     * 1 可使用
            2 不可用
     */
    private Integer status;

    /**
     * REGISTER 注册
            PICK 点击连接获取
            RECOMMEND 推荐人
     */
    private String channel;

    /**
     * 关联对象id
     */
    private Long refId;


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
     * 获取优惠卷id
     *
     * @return 优惠卷id
     */
    public Long getCouponId(){
      return couponId;
    }

    /**
     * 设置优惠卷id
     * 
     * @param couponId 要设置的优惠卷id
     */
    public void setCouponId(Long couponId){
      this.couponId = couponId;
    }

    /**
     * 获取1 可使用
            2 不可用
     *
     * @return 1 可使用
            2 不可用
     */
    public Integer getStatus(){
      return status;
    }

    /**
     * 设置1 可使用
            2 不可用
     * 
     * @param status 要设置的1 可使用
            2 不可用
     */
    public void setStatus(Integer status){
      this.status = status;
    }

    /**
     * 获取REGISTER 注册
            PICK 点击连接获取
            RECOMMEND 推荐人
     *
     * @return REGISTER 注册
            PICK 点击连接获取
            RECOMMEND 推荐人
     */
    public String getChannel(){
      return channel;
    }

    /**
     * 设置REGISTER 注册
            PICK 点击连接获取
            RECOMMEND 推荐人
     * 
     * @param channel 要设置的REGISTER 注册
            PICK 点击连接获取
            RECOMMEND 推荐人
     */
    public void setChannel(String channel){
      this.channel = channel;
    }

    /**
     * 获取关联对象id
     *
     * @return 关联对象id
     */
    public Long getRefId(){
      return refId;
    }

    /**
     * 设置关联对象id
     * 
     * @param refId 要设置的关联对象id
     */
    public void setRefId(Long refId){
      this.refId = refId;
    }

	@Override
	public String toString() {
		return "AfBoluomeUserCouponDo [rid=" + rid + ", gmtCreate=" + gmtCreate + ", gmtModified=" + gmtModified
				+ ", userId=" + userId + ", couponId=" + couponId + ", status=" + status + ", channel=" + channel
				+ ", refId=" + refId + "]";
	}

}