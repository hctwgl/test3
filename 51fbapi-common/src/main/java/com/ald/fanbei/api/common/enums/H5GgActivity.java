package com.ald.fanbei.api.common.enums;

public enum H5GgActivity {
    GGACTIVITY("GGACTIVITY","霸王餐活动配置信息一级类型"),
    REGISTER("REGISTER", "注册获得的优惠券"), PICK("PICK", "点击连接获取优惠券"), RECOMMEND("RECOMMEND", "推荐人获得优惠券"),
    WAIMAI("WAIMAI", "外卖"),NOFINISH("NOFINISH", "未完成"),ALREADYFINISH("ALREADYFINISH","已完成"), NOCONSUME("NOCONSUME","未消费"),
    INVITECERMONY("INVITECERMONY","霸王餐活动邀请有礼页二级类型"),DINEANDDASH("DINEANDDASH","霸王餐活动邀请好友页二级类型"),
    BOLUOMECOUPON("BOLUOMECOUPON","菠萝觅优惠券"),ACTIVITYTIME("ACTIVITYTIME","活动时间"),
    GGSMSINVITER("GGSMSINVITER","邀请者得到券时短信文案"),GGSMSNEW("GGSMSNEW","新用户注册送券短信文案"),
    TOPOPUP("Y","弹窗"),COUPONIMAGE("COUPONIMAGE","优惠券弹窗图"),REBATEIMAGE("REBATEIMAGE","返利弹窗图"),
    NOPOPUP("N","不弹窗");
    
    
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
