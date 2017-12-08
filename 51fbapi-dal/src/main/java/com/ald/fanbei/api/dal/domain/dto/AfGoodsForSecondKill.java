package com.ald.fanbei.api.dal.domain.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * @Title: AfGoodsForSecondKill.java
 * @Package com.ald.fanbei.api.dal.domain.dto
 * @Description: TODO(用一句话描述该文件做什么)
 * @Copyright (c) 浙江阿拉丁电子商务股份有限公司 All Rights Reserved.
 * @author qiao
 * @date 2017年12月8日 上午10:34:28
 * @version V1.0
 */
public class AfGoodsForSecondKill extends AbstractSerial {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4040529492366462846L;
	private Date serviceDate;
	private List<AfGoodsBuffer> goodsList;

	public Date getServiceDate() {
		return serviceDate;
	}

	public void setServiceDate(Date serviceDate) {
		this.serviceDate = serviceDate;
	}

	public List<AfGoodsBuffer> getGoodsList() {
		return goodsList;
	}

	public void setGoodsList(List<AfGoodsBuffer> goodsList) {
		this.goodsList = goodsList;
	}

	@Override
	public String toString() {
		return "AfGoodsForSecondKill [serviceDate=" + serviceDate + ", goodsList=" + goodsList + "]";
	}

	public class AfGoodsBuffer {
		private Date startTime;

		private List<GoodsForDate> goodsListForDate;

		public Date getStartTime() {
			return startTime;
		}

		public void setStartTime(Date startTime) {
			this.startTime = startTime;
		}

		public List<GoodsForDate> getGoodsListForDate() {
			return goodsListForDate;
		}

		public void setGoodsListForDate(List<GoodsForDate> goodsListForDate) {
			this.goodsListForDate = goodsListForDate;
		}

		@Override
		public String toString() {
			return "AfGoodsBuffer [startTime=" + startTime + ", goodsListForDate=" + goodsListForDate + "]";
		}

	}

	public class GoodsForDate {
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
			return "GoodsForDate [status=" + status + ", goodsId=" + goodsId + ", stockCount=" + stockCount + ", count="
					+ count + ", saleAmount=" + saleAmount + ", realAmount=" + realAmount + ", rebateAmount="
					+ rebateAmount + ", goodsName=" + goodsName + ", goodsIcon=" + goodsIcon + ", thumbnaillcon="
					+ thumbnaillcon + ", goodsUrl=" + goodsUrl + "]";
		}

	}

}
