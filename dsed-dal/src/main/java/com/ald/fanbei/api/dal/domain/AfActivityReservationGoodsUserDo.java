package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 用户预定预售商品表实体
 * 
 * @author chenqingsong
 * @version 1.0.0 初始化
 * @date 2018-05-22 09:12:12
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfActivityReservationGoodsUserDo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    /**
     * 主键Rid
     */
    private Long rid;
    
    /**
     * 预约商品ID
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
     * 优惠券ID
     */
    private Long couponId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 预定商品数量
     */
    private int goodsCount;

    private Integer limitCount; //限购数量
    private String goodsName;  //商品名称
    private Double activityAmount;//活动金额
    private String endAmount;//到手价
    private String saleAmount;//售价（）
    private Double couponPrice;//优惠券金额

    private String goodsIcon;//商品图标
    private String thumbnailIcon;//商品缩略图
    private String goodsUrl;//商品链接

    private Long userReservationId;

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
     */
    public void setRid(Long rid){
      this.rid = rid;
    }
    
    /**
     * 获取预约商品ID
     *
     * @return 预约商品ID
     */
    public Long getGoodsId(){
      return goodsId;
    }

    /**
     * 设置预约商品ID
     * 
     * @param goodsId 要设置的预约商品ID
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
     * 获取优惠券ID
     *
     * @return 优惠券ID
     */
    public Long getCouponId(){
      return couponId;
    }

    /**
     * 设置优惠券ID
     * 
     * @param couponId 要设置的优惠券ID
     */
    public void setCouponId(Long couponId){
      this.couponId = couponId;
    }

    /**
     * 获取用户ID
     *
     * @return 用户ID
     */
    public Long getUserId(){
      return userId;
    }

    /**
     * 设置用户ID
     * 
     * @param userId 要设置的用户ID
     */
    public void setUserId(Long userId){
      this.userId = userId;
    }

    public int getGoodsCount() {
        return goodsCount;
    }

    public void setGoodsCount(int goodsCount) {
        this.goodsCount = goodsCount;
    }

    public Integer getLimitCount() {
        return limitCount;
    }

    public void setLimitCount(Integer limitCount) {
        this.limitCount = limitCount;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public Double getActivityAmount() {
        return activityAmount;
    }

    public void setActivityAmount(Double activityAmount) {
        this.activityAmount = activityAmount;
    }

    public String getEndAmount() {
        return endAmount;
    }

    public void setEndAmount(String endAmount) {
        this.endAmount = endAmount;
    }

    public String getSaleAmount() {
        return saleAmount;
    }

    public void setSaleAmount(String saleAmount) {
        this.saleAmount = saleAmount;
    }

    public Double getCouponPrice() {
        return couponPrice;
    }

    public void setCouponPrice(Double couponPrice) {
        this.couponPrice = couponPrice;
    }

    public String getGoodsIcon() {
        return goodsIcon;
    }

    public void setGoodsIcon(String goodsIcon) {
        this.goodsIcon = goodsIcon;
    }

    public String getThumbnailIcon() {
        return thumbnailIcon;
    }

    public void setThumbnailIcon(String thumbnailIcon) {
        this.thumbnailIcon = thumbnailIcon;
    }

    public String getGoodsUrl() {
        return goodsUrl;
    }

    public void setGoodsUrl(String goodsUrl) {
        this.goodsUrl = goodsUrl;
    }

    public Long getUserReservationId() {
        return userReservationId;
    }

    public void setUserReservationId(Long userReservationId) {
        this.userReservationId = userReservationId;
    }
}