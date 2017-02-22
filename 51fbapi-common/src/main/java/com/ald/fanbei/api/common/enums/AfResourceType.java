/**
 * 
 */
package com.ald.fanbei.api.common.enums;

/**
 * @author suweili
 *
 */
public enum AfResourceType {
	ResourceTypeHomeBanner("HOME_BANNER", "轮播图"),
	ResourceTypeHomeNavigation("HOME_NAVIGATION", "四个导航"),
	ResourceTypeHomeSecond("HOME_SECOND_BANNER", "第二个轮播"),
	ResourceTypeHomeOneToMany("HOME_ONE_TO_MANY", "首页1+n模块"),
	ResourceTypeHomeOneToTwo("HOME_ONE_TO_TWO", "首页一对二模块"),
	ResourceTypeHomeOneImage("HOME_ONE_IMAGE", "首页平铺模块"),
	
	
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
