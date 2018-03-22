package com.ald.fanbei.api.biz.bo.thirdpay;

/**
 * @author honghzengpei 2017/10/31 13:32
 * @类描述：订单支付
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum ThirdBizType {
    CASH_REPAYMENT( "现金贷还款"),
    CASH_RENEWAL( "现金贷续期"),
    LOAN_REPAYMENT ("白领贷还款"),
    REPAYMENT("分期还款");
    private String name;
    ThirdBizType(String name) {
        this.name = name;
    }
}
