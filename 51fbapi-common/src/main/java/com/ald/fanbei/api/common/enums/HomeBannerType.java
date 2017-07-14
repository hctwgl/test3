package com.ald.fanbei.api.common.enums;

public enum HomeBannerType {
	HomeBannerTypeH5("BANNER_H5", "h5"),
	HomeBannerTypeCategory("BANNER_TYPE_CATEGORY", "类别");

    private String    code;

    private String name;
    HomeBannerType(String code, String name) {
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
