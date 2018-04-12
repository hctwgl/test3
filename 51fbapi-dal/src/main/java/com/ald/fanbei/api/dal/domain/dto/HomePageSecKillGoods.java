package com.ald.fanbei.api.dal.domain.dto;

import java.math.BigDecimal;

/**
 * 
* @ClassName: HomePageSecKillGoods 
* @Description: 首页秒杀
* @author gaojb
* @date 2018年4月12日 下午5:15:55 
*
 */
public class HomePageSecKillGoods {

    private BigDecimal activityAmount;
    private Long goodsId;
    private Integer total;
    private String goodName;
    private String goodsIcon;
    private String thumbnailIcon;
    private String goodsUrl;
    private BigDecimal priceAmount;
    private BigDecimal saleAmount;
    private BigDecimal rebateAmount;
    private Double rebateRate;
    private String remark;
    private Integer volume;
    private Integer subscribe;
    private Integer interestFreeId;

    public BigDecimal getActivityAmount() {
	return activityAmount;
    }

    public void setActivityAmount(BigDecimal activityAmount) {
	this.activityAmount = activityAmount;
    }

    public Long getGoodsId() {
	return goodsId;
    }

    public void setGoodsId(Long goodsId) {
	this.goodsId = goodsId;
    }

    public Integer getTotal() {
	return total;
    }

    public void setTotal(Integer total) {
	this.total = total;
    }

    public String getGoodName() {
	return goodName;
    }

    public void setGoodName(String goodName) {
	this.goodName = goodName;
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

    public BigDecimal getPriceAmount() {
	return priceAmount;
    }

    public void setPriceAmount(BigDecimal priceAmount) {
	this.priceAmount = priceAmount;
    }

    public BigDecimal getSaleAmount() {
	return saleAmount;
    }

    public void setSaleAmount(BigDecimal saleAmount) {
	this.saleAmount = saleAmount;
    }

    public BigDecimal getRebateAmount() {
	return rebateAmount;
    }

    public void setRebateAmount(BigDecimal rebateAmount) {
	this.rebateAmount = rebateAmount;
    }

    public Double getRebateRate() {
	return rebateRate;
    }

    public void setRebateRate(Double rebate_rate) {
	this.rebateRate = rebate_rate;
    }

    public String getRemark() {
	return remark;
    }

    public void setRemark(String remark) {
	this.remark = remark;
    }

    public Integer getVolume() {
	return volume;
    }

    public void setVolume(Integer volume) {
	this.volume = volume;
    }

    public Integer getSubscribe() {
	return subscribe;
    }

    public void setSubscribe(Integer subscribe) {
	this.subscribe = subscribe;
    }

    public Integer getInterestFreeId() {
	return interestFreeId;
    }

    public void setInterestFreeId(Integer interestFreeId) {
	this.interestFreeId = interestFreeId;
    }

    @Override
    public String toString() {
	return "HomePageSecKillGoods [activityAmount=" + activityAmount + ", goodsId=" + goodsId + ", total=" + total + ", goodName=" + goodName + ", goodsIcon=" + goodsIcon + ", thumbnailIcon=" + thumbnailIcon + ", goodsUrl=" + goodsUrl + ", priceAmount=" + priceAmount + ", saleAmount=" + saleAmount + ", rebateAmount=" + rebateAmount + ", rebate_rate=" + rebateRate + ", remark=" + remark + ", volume=" + volume + ", subscribe=" + subscribe + ", interestFreeId=" + interestFreeId + "]";
    }

}
