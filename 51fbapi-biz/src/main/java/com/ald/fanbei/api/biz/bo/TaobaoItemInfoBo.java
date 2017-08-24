package com.ald.fanbei.api.biz.bo;

import com.ald.fanbei.api.common.AbstractSerial;
import com.alibaba.fastjson.JSONArray;


/**
 * 
 * @类描述：
 * @author xiaotianjian 2017年5月18日上午10:59:56
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class TaobaoItemInfoBo extends AbstractSerial{
	
	private static final long serialVersionUID = -1109911397641746700L;
	
	private String location;     //地址
	private JSONArray sku_infos;   //
	private String promotion_price;    //销售价格
	private boolean post_for_free;   //是否免邮 //'true' 'false'
	private String title;   //商品名称
	private String price;   //原价
	private boolean cart_support;   //是否支持货到付款
	private String promotion_tips; //促销信息
	private Integer quantity;   //库存
	private String seller_type; //店铺类型'taobao' 'tmall'
	private boolean in_sale;   //是否还在销售
	private JSONArray img_urls;   //返回结果图片
	/**
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}
	/**
	 * @param location the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}
	/**
	 * @return the promotion_price
	 */
	public String getPromotion_price() {
		return promotion_price;
	}
	/**
	 * @param promotion_price the promotion_price to set
	 */
	public void setPromotion_price(String promotion_price) {
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
	public String getPrice() {
		return price;
	}
	/**
	 * @param price the price to set
	 */
	public void setPrice(String price) {
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
	public JSONArray getImg_urls() {
		return img_urls;
	}
	/**
	 * @param img_urls the img_urls to set
	 */
	public void setImg_urls(JSONArray img_urls) {
		this.img_urls = img_urls;
	}
	/**
	 * @return the sku_infos
	 */
	public JSONArray getSku_infos() {
		return sku_infos;
	}
	/**
	 * @param sku_infos the sku_infos to set
	 */
	public void setSku_infos(JSONArray sku_infos) {
		this.sku_infos = sku_infos;
	}
	
}
	
	


