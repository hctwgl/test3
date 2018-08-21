package com.ald.fanbei.api.biz.bo.thirdpay;

/**
 * @类描述：有得卖 回收业务 系统系数类型枚举
 * @author weiqingeng 2018/2/27 14:59
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum ThirdRecycleEnum {
    RETURN_MONEY( "返现",1),
    EXCHANGE( "兑换",2);

    private String des;
    private Integer type;
    ThirdRecycleEnum(String des, Integer type) {
        this.des = des;
        this.type = type;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
