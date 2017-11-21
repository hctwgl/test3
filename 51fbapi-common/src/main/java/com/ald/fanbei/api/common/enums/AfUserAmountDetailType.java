package com.ald.fanbei.api.common.enums;

/**
 * @author honghzengpei 2017/11/21 15:53
 * @类描述：订单支付
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum AfUserAmountDetailType {
    //分期
    BENJIN( 0,"本金"),
    FENQISHOUXUFEI( 1,"分期手续费"),
    YUQILIXI( 2,"逾期利息"),
    ZHANGHUYUERDIKOU( 3,"账户余额抵扣"),
    YOUHUIJUANGDIKOU( 4,"优惠券抵扣");



    private int code;
    private String name;
    AfUserAmountDetailType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }
    public void setCode(int code) {
        this.code = code;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
