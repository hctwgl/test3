package com.ald.fanbei.api.dal.domain.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * 
* @ClassName: HomePageSecKillGoods 
* @Description: 首页秒杀
* @author gaojb
* @date 2018年4月12日 下午5:15:55 
*
 */
public class HomePageSecKillGoods extends AbstractSerial{

	private static final long serialVersionUID = 1L;
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
    private String source; // 商品的来源
    private Date activityStart;
    private Date activityEnd;
    private Long  activityId;

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public Date getActivityStart() {
        return activityStart;
    }

    public void setActivityStart(Date activityStart) {
        this.activityStart = activityStart;
    }

    public Date getActivityEnd() {
        return activityEnd;
    }

    public void setActivityEnd(Date activityEnd) {
        this.activityEnd = activityEnd;
    }

    public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

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
        return "HomePageSecKillGoods{" +
                "activityAmount=" + activityAmount +
                ", goodsId=" + goodsId +
                ", total=" + total +
                ", goodName='" + goodName + '\'' +
                ", goodsIcon='" + goodsIcon + '\'' +
                ", thumbnailIcon='" + thumbnailIcon + '\'' +
                ", goodsUrl='" + goodsUrl + '\'' +
                ", priceAmount=" + priceAmount +
                ", saleAmount=" + saleAmount +
                ", rebateAmount=" + rebateAmount +
                ", rebateRate=" + rebateRate +
                ", remark='" + remark + '\'' +
                ", volume=" + volume +
                ", subscribe=" + subscribe +
                ", interestFreeId=" + interestFreeId +
                ", source='" + source + '\'' +
                ", activityStart=" + activityStart +
                ", activityEnd=" + activityEnd +
                '}';
    }
}
