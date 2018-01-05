package com.ald.fanbei.api.common.enums;

/**
 * @author honghzengpei 2017/11/22 13:30
 * @类描述：订单支付
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum AfUserAmountProcessStatus {
    NEW( 0,"新建"),
    PROCESS( 1,"处理中"),
    SUCCESS( 2,"成功"),
    FAIL(3,"失败");

    private Integer code;
    private String name;
    AfUserAmountProcessStatus(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }
    public void setCode(Integer code) {
        this.code = code;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
