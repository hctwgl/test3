package com.ald.fanbei.api.common.enums;

/**
 * @author honghzengpei 2017/11/21 15:32
 * @类描述：订单支付
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum AfUserAmountBizType {
	/**
	 * 分期退款
	 */
    REFUND( 0,"分期退款"),
    /**
     * 分期还款
     */
    REPAYMENT( 1,"分期还款"),
    /**
     * 分期借钱
     */
    BORROW( 2,"分期借钱"),
    /**
     * 现金贷借钱
     */
    BORROW_CASH( 3,"现金贷借钱"),
    /**
     * 现金贷续期
     */
    BORROW_CASH_RENEWAL( 4,"现金贷续期"),
    /**
     * 现金贷还款
     */
    REPAYMENT_CASH( 5,"现金贷还款");



    private int code;
    private String name;
    AfUserAmountBizType(int code, String name) {
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
