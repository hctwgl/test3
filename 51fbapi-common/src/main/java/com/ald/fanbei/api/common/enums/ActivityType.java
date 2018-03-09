package com.ald.fanbei.api.common.enums;

/**
 * 
 * @类描述：
 * @author xiaotianjian 2017年2月7日下午2:34:01
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum ActivityType {

	H5_TEMPLATE("H5_TEMPLATE", "H5页面模版"),
	ACTIVITY_TEMPLATE("ACTIVITY_TEMPLATE", "618会场模版"),
	ENCORE_TEMPLATE("ENCORE_TEMPLATE", "返场活动模版"),
	EXCLUSIVE_CREDIT("EXCLUSIVE_CREDIT","新人专享--信用专享"),
	FIRST_SINGLE("FIRST_SINGLE","新人专享--首单爆品");
	
    private String code;
    private String name;
    
    ActivityType(String code, String name) {
        this.code = code;
        this.name = name;
    }

	public String getName() {
		return name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setName(String name) {
		this.name = name;
	}

}
