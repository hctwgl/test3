package com.ald.fanbei.api.biz.bo.thirdpay;

/**
 * @author honghzengpei 2017/11/1 16:48
 * @类描述：订单支付
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class ResulitCheck<T> {
    /**
     * 是否存在订单
     */
    private T resulit;
    /**
     * 订单是否己成功
     */
    private boolean isSuccess = false;


    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public T getResulit() {
        return resulit;
    }

    public void setResulit(T resulit) {
        this.resulit = resulit;
    }
}
