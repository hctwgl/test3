package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 秒杀活动管理实体
 * 
 * @author hqj
 * @version 1.0.0 初始化
 * @date 2018-03-06 16:58:37
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfSeckillActivityDo extends AbstractSerial {

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
     * 修改时间
     */
    private Date gmtModified;

    /**
     * 活动名称
     */
    private String name;

    /**
     * 活动类型，1表示特惠类型，2表示秒杀类型
     */
    private Integer type;

    /**
     * 活动图片URL地址
     */
    private String iconUrl;

    /**
     * 活动开始时间
     */
    private Date gmtStart;

    /**
     * 活动结束时间
     */
    private Date gmtEnd;

    /**
     * 背景颜色
     */
    private String bgColor;

    /**
     * 商品数量
     */
    private Integer goodsCount;

    /**
     * 活动状态 1：启用，0：禁用
     */
    private Integer status;

    /**
     * 是否删除，0表示启用，1表示禁用
     */
    private Integer isDisable;

    /**
     * 折扣类型，1表示折扣，2表示立减
     */
    private Integer discountType;

    /**
     * 折扣
     */
    private BigDecimal discount;

    /**
     * 立减价格
     */
    private BigDecimal subtractPrice;

    /**
     * 添加者
     */
    private String creator;

    /**
     * 修改者
     */
    private String modifier;

    /**
     * 活动开始时间
     */
    private Date gmtPStart;

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
     * @param 
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
     * 获取活动名称
     *
     * @return 活动名称
     */
    public String getName(){
      return name;
    }

    /**
     * 设置活动名称
     * 
     * @param name 要设置的活动名称
     */
    public void setName(String name){
      this.name = name;
    }

    /**
     * 获取活动类型，1表示特惠类型，2表示秒杀类型
     *
     * @return 活动类型，1表示特惠类型，2表示秒杀类型
     */
    public Integer getType(){
      return type;
    }

    /**
     * 设置活动类型，1表示特惠类型，2表示秒杀类型
     * 
     * @param type 要设置的活动类型，1表示特惠类型，2表示秒杀类型
     */
    public void setType(Integer type){
      this.type = type;
    }

    /**
     * 获取活动图片URL地址
     *
     * @return 活动图片URL地址
     */
    public String getIconUrl(){
      return iconUrl;
    }

    /**
     * 设置活动图片URL地址
     * 
     * @param iconUrl 要设置的活动图片URL地址
     */
    public void setIconUrl(String iconUrl){
      this.iconUrl = iconUrl;
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

    /**
     * 获取背景颜色
     *
     * @return 背景颜色
     */
    public String getBgColor(){
      return bgColor;
    }

    /**
     * 设置背景颜色
     * 
     * @param bgColor 要设置的背景颜色
     */
    public void setBgColor(String bgColor){
      this.bgColor = bgColor;
    }

    /**
     * 获取商品数量
     *
     * @return 商品数量
     */
    public Integer getGoodsCount(){
      return goodsCount;
    }

    /**
     * 设置商品数量
     * 
     * @param goodsCount 要设置的商品数量
     */
    public void setGoodsCount(Integer goodsCount){
      this.goodsCount = goodsCount;
    }

    /**
     * 获取活动状态 1：启用，0：禁用
     *
     * @return 活动状态 1：启用，0：禁用
     */
    public Integer getStatus(){
      return status;
    }

    /**
     * 设置活动状态 1：启用，0：禁用
     * 
     * @param status 要设置的活动状态 1：启用，0：禁用
     */
    public void setStatus(Integer status){
      this.status = status;
    }

    /**
     * 获取是否删除，0表示启用，1表示禁用
     *
     * @return 是否删除，0表示启用，1表示禁用
     */
    public Integer getIsDisable(){
      return isDisable;
    }

    /**
     * 设置是否删除，0表示启用，1表示禁用
     * 
     * @param isDisable 要设置的是否删除，0表示启用，1表示禁用
     */
    public void setIsDisable(Integer isDisable){
      this.isDisable = isDisable;
    }

    /**
     * 获取折扣类型，1表示折扣，2表示立减
     *
     * @return 折扣类型，1表示折扣，2表示立减
     */
    public Integer getDiscountType(){
      return discountType;
    }

    /**
     * 设置折扣类型，1表示折扣，2表示立减
     * 
     * @param discountType 要设置的折扣类型，1表示折扣，2表示立减
     */
    public void setDiscountType(Integer discountType){
      this.discountType = discountType;
    }

    /**
     * 获取折扣
     *
     * @return 折扣
     */
    public BigDecimal getDiscount(){
      return discount;
    }

    /**
     * 设置折扣
     * 
     * @param discount 要设置的折扣
     */
    public void setDiscount(BigDecimal discount){
      this.discount = discount;
    }

    /**
     * 获取立减价格
     *
     * @return 立减价格
     */
    public BigDecimal getSubtractPrice(){
      return subtractPrice;
    }

    /**
     * 设置立减价格
     * 
     * @param subtractPrice 要设置的立减价格
     */
    public void setSubtractPrice(BigDecimal subtractPrice){
      this.subtractPrice = subtractPrice;
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

    public Date getGmtPStart() {
        return gmtPStart;
    }

    public void setGmtPStart(Date gmtPStart) {
        this.gmtPStart = gmtPStart;
    }
}