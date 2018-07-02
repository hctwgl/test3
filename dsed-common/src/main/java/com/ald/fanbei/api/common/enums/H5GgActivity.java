package com.ald.fanbei.api.common.enums;

public enum H5GgActivity {
    GG_ACTIVITY("GG_ACTIVITY","霸王餐活动配置信息一级类型"),
    REGISTER("REGISTER", "注册获得的优惠券"), PICK("PICK", "点击连接获取优惠券"), RECOMMEND("RECOMMEND", "推荐人获得优惠券"),
    WAIMAI("WAIMAI", "外卖"),NO_FINISH("NO_FINISH", "未完成"),ALREADY_FINISH("ALREADY_FINISH","已完成"), NO_CONSUME("NO_CONSUME","未消费"),
    INVITE_CERMONY("INVITE_CERMONY","霸王餐活动邀请有礼页二级类型"),DINE_AND_DASH("DINE_AND_DASH","霸王餐活动邀请好友页二级类型"),
    BOLUOME_COUPON("BOLUOME_COUPON","菠萝觅优惠券"),ACTIVITY_TIME("ACTIVITY_TIME","活动时间"),
    GG_SMS_INVITER("GG_SMS_INVITER","邀请者得到券时短信文案"),GG_SMS_NEW("GG_SMS_NEW","新用户注册送券短信文案"),
    TOPOPUP("Y","弹窗"),COUPON_IMAGE("COUPON_IMAGE","优惠券弹窗图"),REBATE_IMAGE("REBATE_IMAGE","返利弹窗图"),
    NOPOPUP("N","不弹窗"),
    REGISTER_GP("REGISTER_GP", "注册获得的优惠券") ;
    
    
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
