package com.ald.fanbei.api.dal.domain.dto;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 双十一砍价实体
 * 
 * @author gaojibin_temple
 * @version 1.0.0 初始化
 * @date 2017-10-17 11:40:20
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfDeUserGoodsInfoDto extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    /**
     * 商品图片
     */
    private String image;

    /**
     * 原价
     */
    private BigDecimal  originalPrice;

    /**
     * 砍价金额
     */
    private BigDecimal cutPrice;

    /**
     * 商品详情
     */
    private String goodsDetail;
    /**
     * 结束时间
     */
    private long endTime;
    
    /**
     * 商品id
     */
    private long  goodsId;
    
    
    
    /**
     * 商品名称
     */
    private String name;
    
    private long totalCount;
    
    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }
    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }
    public void setOriginalPrice(BigDecimal originalPrice) {
        this.originalPrice = originalPrice;
    }
    public BigDecimal getCutPrice() {
        return cutPrice;
    }
    public void setCutPrice(BigDecimal cutPrice) {
        this.cutPrice = cutPrice;
    }
    public String getGoodsDetail() {
        return goodsDetail;
    }
    public void setGoodsDetail(String goodsDetail) {
        this.goodsDetail = goodsDetail;
    }
   
    public long getEndTime() {
        return endTime;
    }
    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public long getTotalCount() {
        return totalCount;
    }
    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }
    public long getGoodsId() {
        return goodsId;
    }
    public void setGoodsId(long goodsId) {
        this.goodsId = goodsId;
    }
  
}