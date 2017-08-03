
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
	HomeNavigation("HOME_NAVIGATION", "四个导航"),
	HomeSecondBanner("HOME_SECOND_BANNER", "第二个轮播"),
	HomeOneToMany("HOME_ONE_TO_MANY", "首页1+n模块"),
	HomeOneToTwo("HOME_ONE_TO_TWO", "首页一对二(1大左2小右)模块"),
	HomeOneToTwo2("HOME_ONE_TO_TWO2", "首页一对二(1大上2小下)模块"),
	HomeOneImage("HOME_ONE_IMAGE", "首页平铺模块"),
	HomeTabbar("HOME_TABBAR", "首页tabbar"),
	
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

	
	ResourceTypeSet("SETTING_CONFIG_H5", "设置页面配置"),

	SelfSupportGoods("SELFSUPPORT_GOODS", "自营商品相关配置"),
	
	NewUserCouponGift("NEW_USER_COUPON_GIFT", "新手礼包优惠券"),
	
	SuperCouponGift("SUPER_COUPON_GIFT", "神券礼包"),
	
	ActivityCouponGift("ACTIVITY_COUPON_GIFT", "活动优惠券"),
	
	CANCEL_ORDER_REASON("CANCEL_ORDER_REASON","用户取消订单原因"),
	ORDER_SEARCH_CONDITION("ORDER_SEARCH_CONDITION","订单筛选条件"),
	
	VIRTUAL_GOODS_SERVICE_PROVIDER("VIRTUAL_GOODS_SERVICE_PROVIDER", "虚拟商品供应商");

	


	 private String    code;

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
