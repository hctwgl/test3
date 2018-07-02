
/**
 * 
 */
package com.ald.fanbei.api.common.enums;

/**
 * @author chenqiwei
 *
 */
public enum HomePageType {
	
	HOME_IAMGE_SLOGAN("HOME_IAMGE_SLOGAN", "首页SLOGAN"),
	 HOME_PAGE_CHANNEL_RECOMMEND_GOODS("HOME_PAGE_CHANNEL_RECOMMEND_GOODS", "频道页推荐商品id组"),
	 HOME_CHANNEL_MORE_GOODS("HOME_CHANNEL_MORE_GOODS", "渠道页更多"),
	 ASJ_IMAGES("ASJ_IMAGES", "爱上街顶部及楼层图"),
	 OPERATE("OPERATE", "OPERATE"),
	 TABBAR("TABBAR", "TABBAR"),
	 TABBAR_HOME_TOP("TABBAR_HOME_TOP", "首页顶部tabbar"),
	 NEW_EXCLUSIVE("NEW_EXCLUSIVE", "新人专享运营位"),
	 GUESS_YOU_LIKE_TOP_IMAGE("GUESS_YOU_LIKE_TOP_IMAGE", "猜你喜欢顶部图"),
	 CHANNEL_MORE_GOODS_TOP_IMAGE("CHANNEL_MORE_GOODS_TOP_IMAGE", "频道页更多商品顶部图"),
	 CHANNEL_RECOMMEND_GOODS_TOP_IMAGE("CHANNEL_RECOMMEND_GOODS_TOP_IMAGE", "频道页推荐商品顶部图"),
	 HOME_FLASH_SALE_FLOOR_IMAGE("HOME_FLASH_SALE_FLOOR_IMAGE", "限时抢购顶部楼层图顶部图"),
	 GOODS("GOODS", "商品"),
	 GOODS_ID("GOODS_ID", "商品id"),
	 H5_URL("H5_URL", "商品id"),
	 CATEGORY_ID("CATEGORY_ID", "分类id"),
	 TOP_IMAGE("TOP_IMAGE", "楼层图"),
	 FLASH_SALE("FLASH_SALE", "限时抢购"),
	 NEW_GOODS("NEW_GOODS", "品质新品"),
	 MAJOR_SUIT("MAJOR_SUIT", "大牌汇聚"),
	 HOME_SEL("HOME_SEL", "精选活动");
	private String code;
    private String name;
    HomePageType(String code, String name) {
        this.code = code;
        this.name = name;
    }
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
