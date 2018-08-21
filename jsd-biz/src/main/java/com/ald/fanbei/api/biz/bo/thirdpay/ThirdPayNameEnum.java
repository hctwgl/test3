package com.ald.fanbei.api.biz.bo.thirdpay;

/**
 * @author honghzengpei 2017/10/30 13:59
 * @类描述：订单支付
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum ThirdPayNameEnum {
    HUICAOPAY( "汇潮","huichao"),
    YIBAOPAY( "易宝","yibao");

    private String name;
    private String code;
    ThirdPayNameEnum(String name,String code) {
        this.name = name;
        this.code = code;
    }

    public static ThirdPayNameEnum findTypeNameByCode(String  name) {
        for (ThirdPayNameEnum typeName : ThirdPayNameEnum.values()) {
            if (typeName.getName().equals(name)) {
                return typeName;
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
