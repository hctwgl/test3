package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.math.BigDecimal;

/**
 * 秒杀活动管理(商品)实体
 * 
 * @author hqj
 * @version 1.0.0 初始化
 * @date 2018-03-07 10:25:58
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Getter
@Setter
 public class AfSeckillActivityGoodsDo extends AbstractSerial {

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
     * 限购数量
     */
    private Integer limitCount;

    /**
     * 商品id
     */
    private Long goodsId;

    /**
     * 商品id
     */
    private Long priceId;

    /**
     * 价格类型：1商品 2规格
     */
    private Integer type;

    /**
     * 活动id
     */
    private Long activityId;

    /**
     * 添加者
     */
    private String creator;

    /**
     * 修改者
     */
    private String modifier;


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
     * 获取限购数量
     *
     * @return 限购数量
     */
    public Integer getLimitCount(){
      return limitCount;
    }

    /**
     * 设置限购数量
     * 
     * @param limitCount 要设置的限购数量
     */
    public void setLimitCount(Integer limitCount){
      this.limitCount = limitCount;
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
     * 获取价格类型：1商品 2规格
     *
     * @return 价格类型：1商品 2规格
     */
    public Integer getType(){
      return type;
    }

    /**
     * 设置价格类型：1商品 2规格
     * 
     * @param type 要设置的价格类型：1商品 2规格
     */
    public void setType(Integer type){
      this.type = type;
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
     * 获取添加者
     *
     * @return 添加者
     */
    public String getCreator(){
      return creator;
    }

    /**
     * 设置添加者
     * 
     * @param creator 要设置的添加者
     */
    public void setCreator(String creator){
      this.creator = creator;
    }

    /**
     * 获取修改者
     *
     * @return 修改者
     */
    public String getModifier(){
      return modifier;
    }

    /**
     * 设置修改者
     * 
     * @param modifier 要设置的修改者
     */
    public void setModifier(String modifier){
      this.modifier = modifier;
    }

    public Long getPriceId() {
        return priceId;
    }

    public void setPriceId(Long priceId) {
        this.priceId = priceId;
    }
}