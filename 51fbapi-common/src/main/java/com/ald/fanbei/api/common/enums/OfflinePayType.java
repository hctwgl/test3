package com.ald.fanbei.api.common.enums;

import org.apache.commons.lang.StringUtils;

/**
 * @类描述：
 * @author chengkang 2017年8月2日下午5:00:34
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum OfflinePayType {
	
    ALIPAY("ALIPAY", "支付宝"), 
    WECHAT("WECHAT", "微信"), 
    APP("APP","APP支付"), 
    BANK("BANK","银行卡"), 
    OTHER("OTHER","其他支付");

    private String code;
    private String name;

    OfflinePayType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static OfflinePayType findPayTypeByCode(String code) {
        for (OfflinePayType roleType : OfflinePayType.values()) {
            if (StringUtils.equals(code, roleType.getCode())) {
                return roleType;
            }
        }
        return null;
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
