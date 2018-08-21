package com.ald.fanbei.api.biz.bo.thirdpay;

/**
 * @author honghzengpei 2017/10/30 13:47
 * @类描述：订单支付
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class ThirdPayBo {
    /**
     * 状态
     */
    private int status;
    /**
     * 名字
     */
    private String name;
    /**
     * 支付类型
     */
    private String payType;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }
}
