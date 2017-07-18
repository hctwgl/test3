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
	
	
	HOME_NAVIGATION("HOME_NAVIGATION", "导航条"),
	BRAND_SHOP("BRAND_SHOP", "品牌特卖,品牌"), 
	SEARCH_HOT("SEARCH_HOT", "热门搜索"),
	BORROW_RATE("BORROW_RATE","疯分期设置"),

	orderCloseReason("ORDER_CLOSE_REASON","关闭代买订单原因"),
	
	BORROW_INFORM("BORROW_INFORM","信息通知"),
	HOME_TABBAR("HOME_TABBAR", "底部导航栏"),
	COUPON_CENTER_URL("COUPON_CENTER_URL", "底部导航栏"),
	RESERVATION_ACTIVITY("RESERVATION_ACTIVITY", "预约活动");
	
	
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
