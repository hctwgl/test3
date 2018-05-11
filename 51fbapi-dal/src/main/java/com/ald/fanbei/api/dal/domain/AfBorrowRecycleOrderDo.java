package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 回收商品记录表实体
 * 
 * @author guoshuaiqiang
 * @version 1.0.0 初始化
 * @date 2018-05-01 11:29:38
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfBorrowRecycleOrderDo extends AbstractSerial {

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
     * 原借款Id
     */
    private Long borrowId;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 商品属性组合，json字符串
     */
    private String propertyValue;

    /**
     * 商品图标
     */
    private String goodsImg;

    private Long userId;

    private BigDecimal overdueRate;

    public BigDecimal getOverdueRate() {
        return overdueRate;
    }

    public void setOverdueRate(BigDecimal overdueRate) {
        this.overdueRate = overdueRate;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

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
     * 获取原借款Id
     *
     * @return 原借款Id
     */
    public Long getBorrowId(){
      return borrowId;
    }

    /**
     * 设置原借款Id
     * 
     * @param borrowId 要设置的原借款Id
     */
    public void setBorrowId(Long borrowId){
      this.borrowId = borrowId;
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
     * 获取商品属性组合，json字符串
     *
     * @return 商品属性组合，json字符串
     */
    public String getPropertyValue(){
      return propertyValue;
    }

    /**
     * 设置商品属性组合，json字符串
     * 
     * @param propertyValue 要设置的商品属性组合，json字符串
     */
    public void setPropertyValue(String propertyValue){
      this.propertyValue = propertyValue;
    }

    /**
     * 获取商品图标
     *
     * @return 商品图标
     */
    public String getGoodsImg(){
      return goodsImg;
    }

    /**
     * 设置商品图标
     * 
     * @param goodsImg 要设置的商品图标
     */
    public void setGoodsImg(String goodsImg){
      this.goodsImg = goodsImg;
    }

}