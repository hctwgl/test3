package com.ald.fanbei.api.biz.bo.thirdpay;

/**
 * @author honghzengpei 2017/10/30 13:59
 * @类描述：订单支付
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum ThirdPayTypeEnum {
    WXPAY( "微信","WX"),
    XXHK("线下还款","XXHK"),
    ZFBPAY( "支付宝","ZFB");

    private String name;
    private String code;
    ThirdPayTypeEnum(String name,String code) {
        this.name = name;
        this.code = code;
    }

    public static ThirdPayTypeEnum findPayTypeByCode(String  name) {
        for (ThirdPayTypeEnum payType : ThirdPayTypeEnum.values()) {
            if (payType.getName().equals(name)) {
                return payType;
            }
        }
        return null;
    }
    public String getName() {
        return name;
    }

    public String getCode(){
        return code;
    }
}
