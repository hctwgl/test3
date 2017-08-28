package com.ald.fanbei.api.common.enums;

/**
 * 第三方跳转渠道
 * @类描述:
 *
 * @auther caihuan 2017年8月21日
 * @注意:本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum ThirdPartyLinkChannel {

	DEFAULT("h5"), //默认
    WX("wechat"),
    SMS("sms");

    private String code;
    ThirdPartyLinkChannel(String code){
        this.code = code;
    }
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	public static String getChannel(String code) {
		ThirdPartyLinkChannel [] channels = ThirdPartyLinkChannel.values();
        for(ThirdPartyLinkChannel channel:channels){
            if(channel.getCode().equals(code))
                return code;
        }
        return DEFAULT.getCode();
    }
}
