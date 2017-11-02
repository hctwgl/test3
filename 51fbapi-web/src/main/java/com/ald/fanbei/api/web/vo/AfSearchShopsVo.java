package com.ald.fanbei.api.web.vo;

import com.ald.fanbei.api.common.AbstractSerial;
/**
 * @date 2017-9-7 17:59:11
 * @author qiaopan
 * @description 商品店铺
 *
 */
public class AfSearchShopsVo extends AbstractSerial{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6170185078584057681L;
	
	private Number userId;//卖家id 
	private String shopTitle;//店铺名称
	private String shopType;//店铺类型，B：天猫，C：淘宝
	private String sellerNick;//卖家昵称
	private String pictUrl;//店标图片
	private String shopUrl;//店铺地址
	public Number getUserId() {
		return userId;
	}
	public void setUserId(Number userId) {
		this.userId = userId;
	}
	public String getShopTitle() {
		return shopTitle;
	}
	public void setShopTitle(String shopTitle) {
		this.shopTitle = shopTitle;
	}
	public String getShopType() {
		return shopType;
	}
	public void setShopType(String shopType) {
		this.shopType = shopType;
	}
	public String getSellerNick() {
		return sellerNick;
	}
	public void setSellerNick(String sellerNick) {
		this.sellerNick = sellerNick;
	}
	public String getPictUrl() {
		return pictUrl;
	}
	public void setPictUrl(String pictUrl) {
		this.pictUrl = pictUrl;
	}
	public String getShopUrl() {
		return shopUrl;
	}
	public void setShopUrl(String shopUrl) {
		this.shopUrl = shopUrl;
	}
	
	

}
