package com.ald.fanbei.api.common.enums;

public enum H5GgActivity {
    REGISTER("REGISTER", "注册获得的优惠券"), PICK("PICK", "点击连接获取优惠券"), RECOMMEND("RECOMMEND", "推荐人获得优惠券"),
    WAIMAI("WAIMAI", "外卖"),NOFINISH("NOFINISH", "未完成"),ALREADYCONSUME("ALREADYCONSUMED","已消费"),
    NOCONSUME("NOCONSUME","未消费");
    
    
    private String code;

    private String description;

    H5GgActivity(String code, String description) {
	this.code = code;
	this.description = description;
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
