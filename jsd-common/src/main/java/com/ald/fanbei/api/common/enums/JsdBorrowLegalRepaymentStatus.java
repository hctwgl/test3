package com.ald.fanbei.api.common.enums;

public enum JsdBorrowLegalRepaymentStatus {

    APPLY("A", "新建状态"),
    PROCESS("P","处理中"),
    NO("N", "还款失败"),
    YES("Y", "还款成功"),
    SMS("S","快捷支付获取短信");

    private String code;
    private String name;

    JsdBorrowLegalRepaymentStatus(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
