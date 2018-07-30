package com.ald.fanbei.api.common.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @类描述：
 * @author xiaotianjian 2017年3月1日上午10:15:58
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum ResourceType {
	
	HOME_BANNER("HOME_BANNER", "轮播图"),
	HOME_SECOND_BANNER("HOME_SECOND_BANNER", "第二个轮播"),
	HOME_ONE_TO_MANY("HOME_ONE_TO_MANY", "首页1+n模块"),
	HOME_ONE_TO_TWO("HOME_ONE_TO_TWO", "首页一对二模块"),
	HOME_ONE_IMAGE("HOME_ONE_IMAGE", "首页平铺模块"),
	SEARCH_BOX_BACKGROUND("SEARCH_BOX_BACKGROUND","搜索框背景图"),
	CUBE_HOMEPAGE_BACKGROUND_ASJ("CUBE_HOMEPAGE_BACKGROUND_ASJ", "爱上街背景"),
	HOME_ONE_IMAGE_BRAND("HOME_ONE_IMAGE_BRAND","逛逛楼层图"),
	HOME_ONE_IMAGE_ECOMMERCE("HOME_ONE_IMAGE_ECOMMERCE","电商楼层图"),
	HOME_FOUR_IMAGE_BRAND_POSITION("HOME_FOUR_IMAGE_BRAND_POSITION","逛逛运营位"),
	HOME_ONE_IMAGE_FINANCIAL("HOME_ONE_IMAGE_FINANCIAL","金融服务入口"),
	HOME_NAVIGATION("HOME_NAVIGATION", "导航条"),
	BRAND_SHOP("BRAND_SHOP", "品牌特卖,品牌"), 
	SEARCH_HOT("SEARCH_HOT", "热门搜索"),
	BORROW_RATE("BORROW_RATE","疯分期设置"),
	AUTH_FUND("AUTH_FUND","公积金认证"),
	
	HOME_NAVIGATION_BACKGROUND("HOME_NAVIGATION_BACKGROUND","快速导航背景"),
	BORROW_CASH_SWITCH("BORROW_CASH_SWITCH","极速贷开关"),
	BORROW_CASH_MJB_SWITCH("BORROW_CASH_MJB_SWITCH","马甲包版本控制开关"),

	orderCloseReason("ORDER_CLOSE_REASON","关闭代买订单原因"),
	ORDER_MOBILE_VERIFY_SET("ORDER_MOBILE_VERIFY_SET","自营订单电核规则配置"),
	BORROW_INFORM("BORROW_INFORM","信息通知"),
	HOME_TABBAR("HOME_TABBAR", "底部导航栏"),
	COUPON_CENTER_URL("COUPON_CENTER_URL", "领券中心"),
	NEWCOUPON_CENTER_URL("NEWCOUPON_CENTER_URL", "领券中心(新)"),
	COUPON_CENTER_BANNER("COUPON_CENTER_BANNER", "领券中心图片"),
	HOME_N_CAROUSEL_IMAGE("HOME_N_CAROUSEL_IMAGE", "首页轮播底部图片"),
	NEW_HOME_N_CAROUSEL_IMAGE("NEW_HOME_N_CAROUSEL_IMAGE", "首页轮播底部图片"),
	HOME_CAROUSEL_IMAGE("HOME_CAROUSEL_IMAGE", "轮播+N图片"),
	NEW_HOME_CAROUSEL_IMAGE("NEW_HOME_CAROUSEL_IMAGE", "轮播+N图片(新)"),
	RESERVATION_ACTIVITY("RESERVATION_ACTIVITY", "预约活动"),
	APP_UPDATE_WND("APP_UPDATE_WND", "版本升级弹窗"),
	APP_UPDATE_COUPON("APP_UPDATE_COUPON", "版本升级优惠券"),
	APP_UPGRADE_REGISTER_TIME("APP_UPGRADE_REGISTER_TIME","版本升级新用户注册时间"),
	FUND_SIDE_BORROW_CASH("FUND_SIDE_BORROW_CASH", "打款时引入资金方配置"),
	HOME_PAGE("HOMEPAGE","首页商品分类"),
	APP_UPDATE_COUPON_NEW("APP_UPDATE_COUPON_NEW","版本升级优惠券（新用户）"),
	
	ASSET_PUSH_CONF("ASSET_PUSH_CONF","债权实时推送相关配置"),
	ASSET_SIDE_CONFIG("ASSET_SIDE_CONFIG","资产方相关配置"),
	BKL_WHITE_LIST_CONF("BKL_WHITE_LIST_CONF","百可录电核白名单配置"),
	BKL_CONF_SWITCH("BKL_CONF_SWITCH","百可录电核开关"),

	HOMEPAGE_BACKGROUND("HOMEPAGE_BACKGROUND","首页背景图"),
	CUBE_HOMEPAGE_BACKGROUND("CUBE_HOMEPAGE_BACKGROUND","魔方首页背景图"),
	HOT_CARD_BANNER("HOT_CARD_BANNER","信用卡中心轮播图"),
	HOT_BANK_BANNER("HOT_BANK_BANNER","热门银行"),
	CREDIT_CARD_BANNER("CREDIT_CARD_BANNER","今日推荐"),
	BORROW_CASH_COMPANY_NAME("BORROW_CASH_COMPANY_NAME","借款公司名称"),

	RECYCLE_PROTOCOL("RECYCLE_PROTOCOL","回收协议"),

	STAGE_MONEY_LIMIT_D("STAGE_MONEY_LIMIT_D","代买（自建）"),
	STAGE_MONEY_LIMIT_Z("STAGE_MONEY_LIMIT_Z","自营"),
	STAGE_MONEY_LIMIT_B("STAGE_MONEY_LIMIT_B","菠萝觅"),
	STAGE_MONEY_LIMIT_S("STAGE_MONEY_LIMIT_S","商圈"),
	MAX_THRESHOLD_OF_DOUBLE_EXCHANGE("MAX_THRESHOLD_OF_EXCHANGE","翻倍兑换最大阈值"),
	WEAK_VERIFY_VIP_CONFIG("WEAK_VERIFY_VIP_CONFIG","弱风控权限包配置"),
	OPEN_REDPACKET("OPEN_REDPACKET", "天天拆红包"),
	OPEN_REDPACKET_MSG("OPEN_REDPACKET_MSG", "天天拆红包提示语"),
	BORROW_SUPERMAN_POPUP_SWITCH("BORROW_SUPERMAN_POPUP_SWITCH", "借款超人弹窗开关")
	;



	private String code;
	private String description;

	private static Map<String, ResourceType> codeRoleTypeMap = null;

	ResourceType(String code, String description) {
		this.code = code;
		this.description = description;
	}

	public static ResourceType findEnumByCode(String code) {
		for (ResourceType goodSource : ResourceType.values()) {
			if (goodSource.getCode().equals(code)) {
				return goodSource;
			}
		}
		return null;
	}

	public static Map<String, ResourceType> getCodeRoleTypeMap() {
		if (codeRoleTypeMap != null && codeRoleTypeMap.size() > 0) {
			return codeRoleTypeMap;
		}
		codeRoleTypeMap = new HashMap<String, ResourceType>();
		for (ResourceType item : ResourceType.values()) {
			codeRoleTypeMap.put(item.getCode(), item);
		}
		return codeRoleTypeMap;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
