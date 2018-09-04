package com.ald.fanbei.api.common.enums;

public enum JsedLoanRepaymentStatus {
    APPLY("新建状态"),
    SUCC("还款成功"),
    FAIL("还款失败"),
    PROCESSING("处理中"),
    CLOSED("关闭"),
    SMS("获取短信");

    public String desz;

    JsedLoanRepaymentStatus(String desz) {
        this.desz = desz;
    }
}
