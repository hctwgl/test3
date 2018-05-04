package com.ald.fanbei.api.web.vo;

import java.math.BigDecimal;

/**
 * @类描述 爱上街品牌商品VO值对象
 * @author liutengyuan 
 * @date 2018年4月9日
 */
public class AfGoodsVo1 extends AfGoodsVo {

	
	private static final long serialVersionUID = 1L;
	private Long categoryId;
	private Long brandId;
	private Integer saleCount;
	private String source;
	private BigDecimal rebateRate;
	private String tags;//免息活动标签
	private String stockCount;//库存
	private String goodsPic1;
	private String goodsPic2;
	private String goodsPic3;
	private String goodsPic4;
	private String goodsDetail;
	
	
	public Long getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}
	public Long getBrandId() {
		return brandId;
	}
	public void setBrandId(Long brandId) {
		this.brandId = brandId;
	}
	public Integer getSaleCount() {
		return saleCount;
	}
	public void setSaleCount(Integer saleCount) {
		this.saleCount = saleCount;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	
	public BigDecimal getRebateRate() {
		return rebateRate;
	}
	public void setRebateRate(BigDecimal rebateRate) {
		this.rebateRate = rebateRate;
	}
	public String getTags() {
		return tags;
	}
	public void setTags(String tags) {
		this.tags = tags;
	}
	public String getStockCount() {
		return stockCount;
	}
	public void setStockCount(String stockCount) {
		this.stockCount = stockCount;
	}
	public String getGoodsPic1() {
		return goodsPic1;
	}
	public void setGoodsPic1(String goodsPic1) {
		this.goodsPic1 = goodsPic1;
	}
	public String getGoodsPic2() {
		return goodsPic2;
	}
	public void setGoodsPic2(String goodsPic2) {
		this.goodsPic2 = goodsPic2;
	}
	public String getGoodsPic3() {
		return goodsPic3;
	}
	public void setGoodsPic3(String goodsPic3) {
		this.goodsPic3 = goodsPic3;
	}
	public String getGoodsPic4() {
		return goodsPic4;
	}
	public void setGoodsPic4(String goodsPic4) {
		this.goodsPic4 = goodsPic4;
	}
	public String getGoodsDetail() {
		return goodsDetail;
	}
	public void setGoodsDetail(String goodsDetail) {
		this.goodsDetail = goodsDetail;
	}
	@Override
	public String toString() {
		return "AfGoodsVo1 [categoryId=" + categoryId + ", brandId=" + brandId
				+ ", saleCount=" + saleCount + ", source=" + source
				+ ", rebateRate=" + rebateRate + ", tags=" + tags
				+ ", stockCount=" + stockCount + ", goodsPic1=" + goodsPic1
				+ ", goodsPic2=" + goodsPic2 + ", goodsPic3=" + goodsPic3
				+ ", goodsPic4=" + goodsPic4 + ", goodsDetail=" + goodsDetail
				+ "]";
	}
	
	
}
