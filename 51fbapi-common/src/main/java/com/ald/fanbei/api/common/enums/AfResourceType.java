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
	HomeOneToTwo("HOME_ONE_TO_TWO", "首页一对二模块"),
	HomeOneImage("HOME_ONE_IMAGE", "首页平铺模块"),
	
	
	SEARCH_HOT("SEARCH_HOT", "热门搜索"),
	INVITE("INVITE", "邀请"),
	SIGNIN("SIGNIN", "签到"),
	RebateDetailedCourse("REBATE_DETAILED_COURSE", "返利教程"),

	
	//ResourceTypeSetType不是首页配置是设置页面的配置
	ResourceTypeSet("SETTING_CONFIG_H5", "设置页面配置");

	


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
