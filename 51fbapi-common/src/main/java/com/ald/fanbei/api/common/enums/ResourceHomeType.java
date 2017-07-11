/**
 * 
 */
package com.ald.fanbei.api.common.enums;

/**
 * @author suweili
 *
 */
public enum ResourceHomeType {
	ResourceHomeTypeBanner("HOME_BANNER", "首页导航条"),
	ResourceHomeTypeTools("HOME_TOOLS", "首页功能模块"),
	ResourceHomeTypeCoupon("HOME_COUPON", "首页优惠券模块"),
	ResourceHomeTypeOneToMany("HOME_ONE_TO_MANY", "首页一对多模块"),
	ResourceHomeTypeOneToTwo("HOME_ONE_TO_TWO", "首页一对二模块"),
	ResourceHomeTypeOneToOne("HOME_ONE_TO_ONE", "首页一对一模块"),
	
	
	//ResourceTypeSetType不是首页配置是设置页面的配置
	ResourceTypeSet("SETTING_CONFIG_H5", "设置页面配置");

	


	 private String    code;

    private String name;
    ResourceHomeType(String code, String name) {
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
