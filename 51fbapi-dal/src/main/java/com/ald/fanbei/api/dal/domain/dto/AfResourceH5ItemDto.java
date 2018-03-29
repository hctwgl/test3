package com.ald.fanbei.api.dal.domain.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.ald.fanbei.api.dal.domain.AfResourceH5ItemDo;


/**
 * @author Jingru
 * @version 创建时间：2018年3月21日 下午5:41:09
 * @Description 类描述
 */
public class AfResourceH5ItemDto extends AfResourceH5ItemDo {
	private static final long serialVersionUID = 4612251113399624959L;

	private Long goodsCount;
	private String categoryName;
	private BigDecimal saleAmount;
	private String goodsName;

	/**
	 * @return the goodsCount
	 */
	public Long getGoodsCount() {
		return goodsCount;
	}

	/**
	 * @param goodsCount the goodsCount to set
	 */
	public void setGoodsCount(Long goodsCount) {
		this.goodsCount = goodsCount;
	}


	/**
	 * @return the saleAmount
	 */
	public BigDecimal getSaleAmount() {
		return saleAmount;
	}

	/**
	 * @param saleAmount the saleAmount to set
	 */
	public void setSaleAmount(BigDecimal saleAmount) {
		this.saleAmount = saleAmount;
	}

	/**
	 * @return the categoryName
	 */
	public String getCategoryName() {
		return categoryName;
	}

	/**
	 * @param categoryName the categoryName to set
	 */
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	/**
	 * @return the goodsName
	 */
	public String getGoodsName() {
		return goodsName;
	}

	/**
	 * @param goodsName the goodsName to set
	 */
	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	public void setGoodsList(List<Map<String, Object>> goodsList) {
		// TODO Auto-generated method stub
		
	}
}
