package com.ald.fanbei.api.biz.bo.thirdpay;

/**
 * @author honghzengpei 2017/10/30 14:00
 * @类描述：订单支付
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum ThirdPayStatusEnum {
    OPEN( 1),
    CLOSE(0);

    private int status;
    ThirdPayStatusEnum(int status) {
        this.status = status;
    }

    public static ThirdPayStatusEnum findPayTypeByCode(int  stats) {
        for (ThirdPayStatusEnum typeName : ThirdPayStatusEnum.values()) {
            if (typeName.getStatus() == stats) {
                return typeName;
            }
        }
        return null;
    }
    public int getStatus() {
        return status;
    }
}
