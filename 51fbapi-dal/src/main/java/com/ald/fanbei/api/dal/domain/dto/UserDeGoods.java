package com.ald.fanbei.api.dal.domain.dto;

import java.math.BigDecimal;
import java.util.List;

public class UserDeGoods {

    public Long getGoodsPriceId() {
	return goodsPriceId;
    }

    public void setGoodsPriceId(Long goodsPriceId) {
	this.goodsPriceId = goodsPriceId;
    }

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

    public Integer getType() {
	return type;
    }

    public void setType(Integer type) {
	this.type = type;
    }

    public Integer getShare() {
	return share;
    }

    public void setShare(Integer share) {
	this.share = share;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public List<UserDeGoodsCoupon> getCouponList() {
	return couponList;
    }

    public void setCouponList(List<UserDeGoodsCoupon> couponList) {
	this.couponList = couponList;
    }

    public BigDecimal getLowestPrice() {
	return lowestPrice;
    }

    public void setLowestPrice(BigDecimal lowestPrice) {
	this.lowestPrice = lowestPrice;
    }

    public String getGoodsId() {
	return goodsId;
    }

    public void setGoodsId(String goodsId) {
	this.goodsId = goodsId;
    }

    public String getThumbnailIcon() {
	return thumbnailIcon;
    }

    public void setThumbnailIcon(String thumbnailIcon) {
	this.thumbnailIcon = thumbnailIcon;
    }

    public Integer getCutCount() {
	return cutCount;
    }

    public void setCutCount(Integer cutCount) {
	this.cutCount = cutCount;
    }

    private Long goodsPriceId;
    private String image;
    private String thumbnailIcon;
    private BigDecimal originalPrice;
    private BigDecimal cutPrice;
    private BigDecimal lowestPrice;
    private Integer type;
    private Integer share;
    private String name;
    private String goodsId;
    private Integer cutCount;
    private List<UserDeGoodsCoupon> couponList;

    @Override
    public String toString() {
	return "UserDeGoods [goodsPriceId=" + goodsPriceId + ", image=" + image + ", thumbnailIcon=" + thumbnailIcon + ", originalPrice=" + originalPrice + ", cutPrice=" + cutPrice + ", lowestPrice=" + lowestPrice + ", type=" + type + ", share=" + share + ", name=" + name + ", goodsId=" + goodsId + ", cutCount=" + cutCount + ", couponList=" + couponList + "]";
    }

}
