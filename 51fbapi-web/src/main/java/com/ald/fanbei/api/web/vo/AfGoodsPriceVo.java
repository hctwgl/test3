package com.ald.fanbei.api.web.vo;

import java.math.BigDecimal;

import com.ald.fanbei.api.common.AbstractSerial;

public class AfGoodsPriceVo extends AbstractSerial {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1893140295561540377L;

	private Long priceId;
	/**
	 * 商品属性组合,属性值id从小到大排序，多个逗号隔开,开始和结尾都需要带上逗号，如“,1,5,3,”
	 */
	private String propertyValueIds;

	public Long getPriceId() {
		return priceId;
	}

	public void setPriceId(Long priceId) {
		this.priceId = priceId;
	}

	public String getPropertyValueIds() {
		return propertyValueIds;
	}

	public void setPropertyValueIds(String propertyValueIds) {
		this.propertyValueIds = propertyValueIds;
	}

	public String getPropertyValueNames() {
		return propertyValueNames;
	}

	public void setPropertyValueNames(String propertyValueNames) {
		this.propertyValueNames = propertyValueNames;
	}

	public BigDecimal getPriceAmount() {
		return priceAmount;
	}

	public void setPriceAmount(BigDecimal priceAmount) {
		this.priceAmount = priceAmount;
	}

	public BigDecimal getActualAmount() {
		return actualAmount;
	}

	public void setActualAmount(BigDecimal actualAmount) {
		this.actualAmount = actualAmount;
	}

	public String getIsSale() {
		return isSale;
	}

	public void setIsSale(String isSale) {
		this.isSale = isSale;
	}

	/**
	 * 各个属性的组合,以逗号分隔
	 */
	private String propertyValueNames;

	/**
	 * 市场价格
	 */
	private BigDecimal priceAmount;

	/**
	 * 销售价格
	 */
	private BigDecimal actualAmount;
	private String isSale;

}
