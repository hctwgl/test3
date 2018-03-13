package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 秒杀活动管理(商品)实体
 * 
 * @author hqj
 * @version 1.0.0 初始化
 * @date 2018-03-08 21:35:18
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfSeckillActivityOrderDo extends AbstractSerial {

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
     * 特惠价格
     */
    private BigDecimal specialPrice;

    /**
     * 商品数量
     */
    private Long orderId;

    /**
     * 商品id
     */
    private Long goodsId;

    /**
     * 活动id
     */
    private Long activityId;

    /**
     * 活动开始时间
     */
    private Date gmtStart;

    /**
     * 活动结束时间
     */
    private Date gmtEnd;


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
     * 获取特惠价格
     *
     * @return 特惠价格
     */
    public BigDecimal getSpecialPrice(){
      return specialPrice;
    }

    /**
     * 设置特惠价格
     * 
     * @param specialPrice 要设置的特惠价格
     */
    public void setSpecialPrice(BigDecimal specialPrice){
      this.specialPrice = specialPrice;
    }

    /**
     * 获取商品数量
     *
     * @return 商品数量
     */
    public Long getOrderId(){
      return orderId;
    }

    /**
     * 设置商品数量
     * 
     * @param orderId 要设置的商品数量
     */
    public void setOrderId(Long orderId){
      this.orderId = orderId;
    }

    /**
     * 获取商品id
     *
     * @return 商品id
     */
    public Long getGoodsId(){
      return goodsId;
    }

    /**
     * 设置商品id
     * 
     * @param goodsId 要设置的商品id
     */
    public void setGoodsId(Long goodsId){
      this.goodsId = goodsId;
    }

    /**
     * 获取活动id
     *
     * @return 活动id
     */
    public Long getActivityId(){
      return activityId;
    }

    /**
     * 设置活动id
     * 
     * @param activityId 要设置的活动id
     */
    public void setActivityId(Long activityId){
      this.activityId = activityId;
    }

    /**
     * 获取活动开始时间
     *
     * @return 活动开始时间
     */
    public Date getGmtStart(){
      return gmtStart;
    }

    /**
     * 设置活动开始时间
     * 
     * @param gmtStart 要设置的活动开始时间
     */
    public void setGmtStart(Date gmtStart){
      this.gmtStart = gmtStart;
    }

    /**
     * 获取活动结束时间
     *
     * @return 活动结束时间
     */
    public Date getGmtEnd(){
      return gmtEnd;
    }

    /**
     * 设置活动结束时间
     * 
     * @param gmtEnd 要设置的活动结束时间
     */
    public void setGmtEnd(Date gmtEnd){
      this.gmtEnd = gmtEnd;
    }

}