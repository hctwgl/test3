package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 活动预约信息表实体
 * 
 * @author hqj
 * @version 1.0.0 初始化
 * @date 2018-04-13 22:32:58
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfActivityUserSmsDo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    /**
     * 主键Rid
     */
    private Long rid;
    

    /**
     * 创建时间，预约时间
     */
    private Date gmtCreate;

    /**
     * 最后修改时间
     */
    private Date gmtModified;

    /**
     * 秒杀活动id
     */
    private Long activityId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 是否发送 1：已发送; 0：未发送
     */
    private Integer isSent;

    /**
     * 秒杀商品id
     */
    private Long goodsId;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 
     */
    private Date activityTime;

    /**
     * 
     */
    private Date sendTime;

    /**
     * 活动类型 1：秒杀;（可扩充）
     */
    private Integer type;


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
     * 获取创建时间，预约时间
     *
     * @return 创建时间，预约时间
     */
    public Date getGmtCreate(){
      return gmtCreate;
    }

    /**
     * 设置创建时间，预约时间
     * 
     * @param gmtCreate 要设置的创建时间，预约时间
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
     * 获取秒杀活动id
     *
     * @return 秒杀活动id
     */
    public Long getActivityId(){
      return activityId;
    }

    /**
     * 设置秒杀活动id
     * 
     * @param activityId 要设置的秒杀活动id
     */
    public void setActivityId(Long activityId){
      this.activityId = activityId;
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
     * 获取是否发送 1：已发送; 0：未发送
     *
     * @return 是否发送 1：已发送; 0：未发送
     */
    public Integer getIsSent(){
      return isSent;
    }

    /**
     * 设置是否发送 1：已发送; 0：未发送
     * 
     * @param isSent 要设置的是否发送 1：已发送; 0：未发送
     */
    public void setIsSent(Integer isSent){
      this.isSent = isSent;
    }

    /**
     * 获取秒杀商品id
     *
     * @return 秒杀商品id
     */
    public Long getGoodsId(){
      return goodsId;
    }

    /**
     * 设置秒杀商品id
     * 
     * @param goodsId 要设置的秒杀商品id
     */
    public void setGoodsId(Long goodsId){
      this.goodsId = goodsId;
    }

    /**
     * 获取商品名称
     *
     * @return 商品名称
     */
    public String getGoodsName(){
      return goodsName;
    }

    /**
     * 设置商品名称
     * 
     * @param goodsName 要设置的商品名称
     */
    public void setGoodsName(String goodsName){
      this.goodsName = goodsName;
    }

    /**
     * 获取
     *
     * @return 
     */
    public Date getActivityTime(){
      return activityTime;
    }

    /**
     * 设置
     * 
     * @param activityTime 要设置的
     */
    public void setActivityTime(Date activityTime){
      this.activityTime = activityTime;
    }

    /**
     * 获取
     *
     * @return 
     */
    public Date getSendTime(){
      return sendTime;
    }

    /**
     * 设置
     * 
     * @param sendTime 要设置的
     */
    public void setSendTime(Date sendTime){
      this.sendTime = sendTime;
    }

    /**
     * 获取活动类型 1：秒杀;（可扩充）
     *
     * @return 活动类型 1：秒杀;（可扩充）
     */
    public Integer getType(){
      return type;
    }

    /**
     * 设置活动类型 1：秒杀;（可扩充）
     * 
     * @param type 要设置的活动类型 1：秒杀;（可扩充）
     */
    public void setType(Integer type){
      this.type = type;
    }

}