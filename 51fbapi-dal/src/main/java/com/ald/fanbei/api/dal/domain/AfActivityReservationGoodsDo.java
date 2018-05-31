package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 活动预约商品表实体
 * 
 * @author chenqingsong
 * @version 1.0.0 初始化
 * @date 2018-05-22 10:23:02
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfActivityReservationGoodsDo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    /**
     * 主键Rid
     */
    private Long rid;
    
    /**
     * 商品ID
     */
    private Long goodsId;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 最后修改时间
     */
    private Date gmtModified;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 限购数量
     */
    private String limitCount;

    /**
     * 优惠券ID
     */
    private Integer couponId;

    /**
     * 排序
     */
    private Integer sort;



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
     * 获取商品ID
     *
     * @return 商品ID
     */
    public Long getGoodsId(){
      return goodsId;
    }

    /**
     * 设置商品ID
     * 
     * @param goodsId 要设置的商品ID
     */
    public void setGoodsId(Long goodsId){
      this.goodsId = goodsId;
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
     * 获取开始时间
     *
     * @return 开始时间
     */
    public Date getStartTime(){
      return startTime;
    }

    /**
     * 设置开始时间
     * 
     * @param startTime 要设置的开始时间
     */
    public void setStartTime(Date startTime){
      this.startTime = startTime;
    }

    /**
     * 获取结束时间
     *
     * @return 结束时间
     */
    public Date getEndTime(){
      return endTime;
    }

    /**
     * 设置结束时间
     * 
     * @param endTime 要设置的结束时间
     */
    public void setEndTime(Date endTime){
      this.endTime = endTime;
    }

    /**
     * 获取限购数量
     *
     * @return 限购数量
     */
    public String getLimitCount(){
      return limitCount;
    }

    /**
     * 设置限购数量
     * 
     * @param limitCount 要设置的限购数量
     */
    public void setLimitCount(String limitCount){
      this.limitCount = limitCount;
    }

    /**
     * 获取优惠券ID
     *
     * @return 优惠券ID
     */
    public Integer getCouponId(){
      return couponId;
    }

    /**
     * 设置优惠券ID
     * 
     * @param couponId 要设置的优惠券ID
     */
    public void setCouponId(Integer couponId){
      this.couponId = couponId;
    }

    /**
     * 获取排序
     *
     * @return 排序
     */
    public Integer getSort(){
      return sort;
    }

    /**
     * 设置排序
     * 
     * @param sort 要设置的排序
     */
    public void setSort(Integer sort){
      this.sort = sort;
    }


}