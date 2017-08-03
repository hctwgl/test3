package com.ald.fanbei.api.biz.bo;

import java.math.BigDecimal;

import com.ald.fanbei.api.common.AbstractSerial;


/**
 * 
 * @类描述：
 * @author xiaotianjian 2017年5月18日下午3:50:56
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class TaobaoSkuInfoBo extends AbstractSerial{
	
	private static final long serialVersionUID = 3843808496171212253L;
	private BigDecimal price;    //原价
	private String sku_id;   //skuId
	private BigDecimal promotion_price;   //促销价
	private Integer quantity;   //库存
	/**
	 * @return the price
	 */
	public BigDecimal getPrice() {
		return price;
	}
	/**
	 * @param price the price to set
	 */
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	/**
	 * @return the sku_id
	 */
	public String getSku_id() {
		return sku_id;
	}
	/**
	 * @param sku_id the sku_id to set
	 */
	public void setSku_id(String sku_id) {
		this.sku_id = sku_id;
	}
	/**
	 * @return the promotion_price
	 */
	public BigDecimal getPromotion_price() {
		return promotion_price;
	}
	/**
	 * @param promotion_price the promotion_price to set
	 */
	public void setPromotion_price(BigDecimal promotion_price) {
		this.promotion_price = promotion_price;
	}
	/**
	 * @return the quantity
	 */
	public Integer getQuantity() {
		return quantity;
	}
	/**
	 * @param quantity the quantity to set
	 */
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	
	
}
	
	


