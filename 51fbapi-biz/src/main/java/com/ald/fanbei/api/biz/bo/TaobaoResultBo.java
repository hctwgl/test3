package com.ald.fanbei.api.biz.bo;

import java.math.BigDecimal;
import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;


/**
 * 
 * @类描述：淘宝商品实体消息
 * @author xiaotianjian 2017年2月26日下午1:51:36
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class TaobaoResultBo extends AbstractSerial{
	
	private static final long serialVersionUID = 6435740945849835607L;
	
	private Date timestamp;     //查询返回结果
	private String item_id;     //商品id 
	private String item_info;  	//商品相关信息
	private String shop_name;   //商品名称
	private BigDecimal promotion_price;    //销售价格
	private boolean post_for_free;   //是否免邮 //'true' 'false'
	private String title;   //商品名称
	private BigDecimal price;   //原价
	private boolean cart_support;   //是否支持货到付款
	private String promotion_tips; //促销信息
	private Integer quantity;   //库存
	private String seller_type; //店铺类型'taobao' 'tmall'
	private boolean in_sale;   //是否还在销售
	private String[] img_urls;   //返回结果code
	
	/**
	 * @return the timestamp
	 */
	public Date getTimestamp() {
		return timestamp;
	}


	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}



	/**
	 * @return the item_info
	 */
	public String getItem_info() {
		return item_info;
	}


	/**
	 * @param item_info the item_info to set
	 */
	public void setItem_info(String item_info) {
		this.item_info = item_info;
	}


	/**
	 * @return the shop_name
	 */
	public String getShop_name() {
		return shop_name;
	}


	/**
	 * @param shop_name the shop_name to set
	 */
	public void setShop_name(String shop_name) {
		this.shop_name = shop_name;
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
	 * @return the post_for_free
	 */
	public boolean isPost_for_free() {
		return post_for_free;
	}


	/**
	 * @param post_for_free the post_for_free to set
	 */
	public void setPost_for_free(boolean post_for_free) {
		this.post_for_free = post_for_free;
	}


	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}


	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}


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
	 * @return the cart_support
	 */
	public boolean isCart_support() {
		return cart_support;
	}


	/**
	 * @param cart_support the cart_support to set
	 */
	public void setCart_support(boolean cart_support) {
		this.cart_support = cart_support;
	}


	/**
	 * @return the promotion_tips
	 */
	public String getPromotion_tips() {
		return promotion_tips;
	}


	/**
	 * @param promotion_tips the promotion_tips to set
	 */
	public void setPromotion_tips(String promotion_tips) {
		this.promotion_tips = promotion_tips;
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


	/**
	 * @return the seller_type
	 */
	public String getSeller_type() {
		return seller_type;
	}


	/**
	 * @param seller_type the seller_type to set
	 */
	public void setSeller_type(String seller_type) {
		this.seller_type = seller_type;
	}


	/**
	 * @return the in_sale
	 */
	public boolean isIn_sale() {
		return in_sale;
	}


	/**
	 * @param in_sale the in_sale to set
	 */
	public void setIn_sale(boolean in_sale) {
		this.in_sale = in_sale;
	}


	/**
	 * @return the img_urls
	 */
	public String[] getImg_urls() {
		return img_urls;
	}


	/**
	 * @param img_urls the img_urls to set
	 */
	public void setImg_urls(String[] img_urls) {
		this.img_urls = img_urls;
	}


	/**
	 * @return the item_id
	 */
	public String getItem_id() {
		return item_id;
	}


	/**
	 * @param item_id the item_id to set
	 */
	public void setItem_id(String item_id) {
		this.item_id = item_id;
	}
}
	
	


