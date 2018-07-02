package com.ald.fanbei.api.dal.domain;

import java.math.BigDecimal;

public class GoodsForDate {
	private Long doubleGoodsId;
	private int status;
	private Long goodsId;
	private int stockCount;
	private int count;
	private BigDecimal saleAmount;
	private BigDecimal realAmount;
	private BigDecimal rebateAmount;
	private String goodsName;
	private String goodsIcon;
	private String thumbnaillcon;
	private String goodsUrl;

	public Long getDoubleGoodsId() {
		return doubleGoodsId;
	}

	public void setDoubleGoodsId(Long doubleGoodsId) {
		this.doubleGoodsId = doubleGoodsId;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Long getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(Long goodsId) {
		this.goodsId = goodsId;
	}

	public int getStockCount() {
		return stockCount;
	}

	public void setStockCount(int stockCount) {
		this.stockCount = stockCount;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public BigDecimal getSaleAmount() {
		return saleAmount;
	}

	public void setSaleAmount(BigDecimal saleAmount) {
		this.saleAmount = saleAmount;
	}

	public BigDecimal getRealAmount() {
		return realAmount;
	}

	public void setRealAmount(BigDecimal realAmount) {
		this.realAmount = realAmount;
	}

	public BigDecimal getRebateAmount() {
		return rebateAmount;
	}

	public void setRebateAmount(BigDecimal rebateAmount) {
		this.rebateAmount = rebateAmount;
	}

	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	public String getGoodsIcon() {
		return goodsIcon;
	}

	public void setGoodsIcon(String goodsIcon) {
		this.goodsIcon = goodsIcon;
	}

	public String getThumbnaillcon() {
		return thumbnaillcon;
	}

	public void setThumbnaillcon(String thumbnaillcon) {
		this.thumbnaillcon = thumbnaillcon;
	}

	public String getGoodsUrl() {
		return goodsUrl;
	}

	public void setGoodsUrl(String goodsUrl) {
		this.goodsUrl = goodsUrl;
	}

	@Override
	public String toString() {
		return "GoodsForDate [doubleGoodsId=" + doubleGoodsId + ", status=" + status + ", goodsId=" + goodsId
				+ ", stockCount=" + stockCount + ", count=" + count + ", saleAmount=" + saleAmount + ", realAmount="
				+ realAmount + ", rebateAmount=" + rebateAmount + ", goodsName=" + goodsName + ", goodsIcon="
				+ goodsIcon + ", thumbnaillcon=" + thumbnaillcon + ", goodsUrl=" + goodsUrl + "]";
	}



}
