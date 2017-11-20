package com.ald.fanbei.api.biz.third.util.huichaopay;

import com.ald.fanbei.api.biz.bo.thirdpay.ThirdPayNameEnum;

/**
 * @author honghzengpei 2017/11/2 17:27
 * @类描述：订单支付
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum HuiCaoOrderStatus {
    SUCCESS( "成功","1"),
    FAIL( "失败","2"),
    PROCESSING( "处理中","3"),
    TIMEOUT( "超时","3");

    private String name;
    private String code;
    HuiCaoOrderStatus(String name, String code) {
        this.name = name;
        this.code = code;
    }


    public String getName() {
        return name;
    }

    public String getCode(){
        return code;
    }
}
