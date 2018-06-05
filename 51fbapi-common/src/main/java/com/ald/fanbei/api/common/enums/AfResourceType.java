
/**
 * 
 */
package com.ald.fanbei.api.common.enums;

/**
 * @author suweili
 *
 */
public enum AfResourceType {
	HomeBanner("HOME_BANNER", "轮播图"),
	HomeBannerNew("HOME_BANNER_NEW", "轮播图（新版）"),
	HomeBannerBrand("HOME_BANNER_BRAND","逛逛轮播图"),
	HomeBannerEcommerce("HOME_BANNER_ECOMMERCE","首页电商轮播图"),
	HomeBannerNewMost("HOME_BANNER_NEW_MOST", "轮播图（V396）"),
	HomeBannerNewMostiPhoneX("HOME_BANNER_NEW_MOST_IPHONEX", "轮播图（V396-IPHONEX）"),
	HomeBannerV401("HOME_BANNER_V401", "轮播图（V401版本）"),
	TABBAR_HOME_TOP("TABBAR_HOME_TOP", "顶部tabBar"),
	TABBAR_HOME_BUTTOM("TABBAR_HOME_BUTTOM", "底部tabBar"),
	SLOGAN_IMAGE("SLOGAN_IMAGE", "SLOGAN_IMAGE）"),
	HOME_IAMGES("HOME_IAMGES", "HOME_IAMGES）"),
	HOME_SEARCH("HOME_SEARCH", "首页搜索背景）"),
	HOME_SEARCH_IPHONEX("HOME_SEARCH_IPHONEX", "首页搜索背景 IPHONEX）"),
	HOME_NINE_GRID("HOME_NINE_GRID", "首页九宫格背景）"),
	HOME_NAVIGATION("HOME_NAVIGATION", "首页快速导航）"),
	OPERATION_POSITION_BANNER("OPERATION_POSITION_BANNER", "运营位轮播图"),
	HomeBannerV401iPhoneX("HOME_BANNER_V401_IPHONEX", "轮播图（V401版本-IPHONEX）"),
	HomeFourImageNomalPositon("HOME_FOUR_IMAGE_NOMAL_POSITION","常驻运营位置"),
	HomeNavigation("HOME_NAVIGATION", "四个导航"),
	PERSONAL_CENTER_NAVIGATION("PERSONAL_CENTER_NAVIGATION", "个人中心四个导航"),
	HOME_IMAGE_ECOMMERCE_POSITION_ASJ("HOME_IMAGE_ECOMMERCE_POSITION_ASJ", "爱上街电商运营位"),
	MANEY_PICUTRES_ASJ("MANEY_PICUTRES_ASJ", "爱上街九宫"),
	
	HomeSecondBanner("HOME_SECOND_BANNER", "第二个轮播"),
	HomeOneToMany("HOME_ONE_TO_MANY", "首页1+n模块"),
	HomeOneToTwo("HOME_ONE_TO_TWO", "首页一对二(1大左2小右)模块"),
	NewHomeOneToTwo("NEW_HOME_ONE_TO_TWO","1大左2小右(新版)"),
	HomeOneToTwo2("HOME_ONE_TO_TWO2", "首页一对二(1大上2小下)模块"),
	HomeOneImage("HOME_ONE_IMAGE", "首页平铺模块"),
	HomeTabbar("HOME_TABBAR", "首页tabbar"),
	ASJHomeTabbar("ASJ_HOME_TABBAR", "首页tabbar"),
	HomeNavigationUpOne("HOME_NAVIGATION_UP","快捷导航上方活动专场"),
	HomeNavigationDownTwo("HOME_NAVIGATION_DOWN","快捷导航下方活动专场"),
	HomeNavigationUpOneV401("HOME_NAVIGATION_UP_V401","快捷导航上方活动专场"),
	HomeNavigationDownTwoV401("HOME_NAVIGATION_DOWN_V401","快捷导航下方活动专场"),
	NewHomeCarouseToMany("NEW_HOME_CAROUSEL_TO_MANY","显示双十一东西"),
	LimitedPurchaseBanner("LIMITED_PURCHASE_BANNER","限时抢购顶部轮播"),
	HomeActivity("HOME_ACTIVITY", "活动模块"),
	
	CodeMaxFail("CODE_MAX_FAIL", "验证码失败次数"),
	APPCONSUMERPHONE("APP_CONSUMER_PHONE", "客服电话"),

	SEARCH_HOT("SEARCH_HOT", "热门搜索"),
	INVITE("INVITE", "邀请"),
	SIGNIN("SIGNIN", "签到"),
	
	PickedCoupon("PICKED_COUPON", "领券专场"),
	RegisterProtocol("REGISTER_PROTOCOL", "注册协议"),
	ShareInviteCode("SHARE_INVITECODE", "分享邀请码"),

	AppDownloadUrl("APP_DOWNLOAD_URL", "app下载地址"),
	RebateDetailedCourse("REBATE_DETAILED_COURSE", "返利教程"),
	AgencyRebateInfo("AGENCY_REBATE_INFO", "返利教程"),

	//活动相关
    ReservationActivity("RESERVATION_ACTIVITY", "预约活动"),
    Iphone8ReservationActivity("IPHONE8_RESERVATION_ACTIVITY","iPhone8预约功能"),
    
	//借钱模块
	BaseBankRate("BASE_BANK_RATE", "央行基准利率"),
	BorrowCashRange("BORROW_CASH_RANGE", "借款金额"),
	BorrowCashBaseBankDouble("BORROW_CASH_BASE_BANK_DOUBLE", "借钱最高倍数"),
	BorrowCashPoundage("BORROW_CASH_POUNDAGE", "借钱手续费率（日）"),
	BorrowCashOverduePoundage("BORROW_CASH_OVERDUE_POUNDAGE", "借钱逾期手续费率（日）"),
	BorrowCashDay("BORROW_CASH_DAY", "借钱时间"),
	borrowRate("BORROW_RATE", "疯分期设置"),
	RiskManagementBorrowcashLimit("RISK_MANAGEMENT_BORROWCASH_LIMIT", "风控对用户现金借款限制相关配置"),
	BorrowTopScrollbar("BORROW_TOP_SCROLLBAR","借贷超市顶部滚动条"),
	
	
	registTongdunSwitch("REGIST_TONGDUN_SWITCH", "注册同盾风控"),
	loginTongdunSwitch("LOGIN_TONGDUN_SWITCH", "登录同盾风控"),
	promotionTongdunSwitch("PROMOTION_TONGDUN_SWITCH", "渠道推广同盾风控"),
	TRADE_TONGDUN_SWITCH("TRADE_TONGDUN_SWITCH", "借钱申请同盾风控"),
	tradeTongdunSwitch("TRADE_TONGDUN_SWITCH", "交易申请同盾风控"),

	tongdunAccecptLevel("TONGDUN_ACCECPT_LEVEL", "同盾风控不通过等级"),


	BorrowTopBanner("BORROW_TOP_BANNER", "借钱页面顶部轮播"),
	BorrowShopBanner("BORROW_SHOP_BANNER", "借贷超市轮播"),
	agencyRecommendGoods("AGENCY_RECOMMEND_GOODS", "代买推荐商品"),
	GGTopBanner("GG_TOP_BANNER","逛逛活动点亮顶部轮播"),
	GGHomeTopBanner("GG_HOME_TOP_BANNER","逛逛活动顶部轮播"),
	GGPOPUP("GG_POP_UP","逛逛点亮活动弹窗"),
	HomeCarouseToMany("HOME_CAROUSEL_TO_MANY","首页轮播+N"),
	ResourceTypeSet("SETTING_CONFIG_H5", "设置页面配置"),
	ManeyPictrues("MANEY_PICUTRES","3/6/9运营位"),


	SelfSupportGoods("SELFSUPPORT_GOODS", "自营商品相关配置"),
	
	NewUserCouponGift("NEW_USER_COUPON_GIFT", "新手礼包优惠券"),
	
	SuperCouponGift("SUPER_COUPON_GIFT", "神券礼包"),
	
	ActivityCouponGift("ACTIVITY_COUPON_GIFT", "活动优惠券"),
	
	CANCEL_ORDER_REASON("CANCEL_ORDER_REASON","用户取消订单原因"),
	ORDER_SEARCH_CONDITION("ORDER_SEARCH_CONDITION","订单筛选条件"),
	
	VIRTUAL_GOODS_SERVICE_PROVIDER("VIRTUAL_GOODS_SERVICE_PROVIDER", "虚拟商品供应商"),
	VirtualGoodsKeywords("VIRTUAL_GOODS_KEYWORDS", "虚拟商品关键字"),
	VirtualShopsKeywords("VIRTUAL_SHOPS_KEYWORDS", "虚拟店铺关键字"),

	SMS_TEMPLATE("SMS_TEMPLATE","短信模板"),
	SMS_LIMIT("SMS_LIMIT","短信限制"),
	
	//app端 借钱按钮高亮显示时长配置
	HIGH_LIGHT_TIME("HIGH_LIGHT_TIME","高亮显示时间"),
	IS_USE_IMG("IS_USE_IMG","底部菜单栏是否使用图片"),
	ASJ_IS_USE_IMG("ASJ_IS_USE_IMG","底部菜单栏是否使用图片"),
	YIXIN_AFU_SEARCH("YIXIN_AFU_SEARCH","宜信阿福查询配置"),
	RISK_POUNDAGE_USERNAME_LIST("RISK_POUNDAGE_USERNAME_LIST","用户分层利率从风控直接取的手机号配置"),

	LEASE_BANNER("LEASE_BANNER", "租赁首页轮播"),

	//资产方相关配置
	ASSET_SIDE_CONFIG("ASSET_SIDE_CONFIG","资产方相关配置"),
	//登录白名单
	LOGIN_WHITE_LIST("LOGIN_WHITE_LIST","登录白名单"),


	ARBITRATION_TYPE("ARBITRATION_TYPE","在线仲裁系统相关配置"),
	ARBITRATION_SEC_TYPE("ARBITRATION_SEC_TYPE","在线仲裁系统相关配置"),

	//芝麻信用认证相关配置

	ZHIMA_VERIFY_CONFIG("ZHIMA_VERIFY_CONFIG","芝麻信用认证相关配置"),

	BUBBLE_AUTH_CONFIG("BUBBLE_AUTH_CONFIG","芝麻信用认证相关配置"),

	APP_SEARCH_BACK_COLOR("APP_SEARCH_BACK_COLOR","app搜索栏背景色"),
	APP_SEARCH_FONT_COLOR("APP_SEARCH_FONT_COLOR","app搜索栏文字颜色"),
	APP_SEARCH_GLASS("APP_SEARCH_GLASS","app搜索栏放大镜"),
	APP_MESSAGE_ICON("APP_MESSAGE_ICON","app消息图标"),
	APP_NAVIGATION_BAR("APP_NAVIGATION_BAR","app导航栏文字及下划线选中颜色"),
	APP_NAVIGATION_BAR_REMAIN("APP_NAVIGATION_BAR_REMAIN","app导航栏文字未选中颜色"),
	APP_NAVIGATION_OPEN("APP_NAVIGATION_OPEN","app导航栏展开控件"),
	APP_NAVIGATION_CLOSE("APP_NAVIGATION_CLOSE","app导航栏关闭控件"),
	BANNER_BACKIMG("BANNER_BACKIMG","Banner背景图片"),
	APP_STATUS_BAR_COLOR("APP_STATUS_BAR_COLOR","状态栏颜色");


	private String code;
    private String name;
    AfResourceType(String code, String name) {
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
