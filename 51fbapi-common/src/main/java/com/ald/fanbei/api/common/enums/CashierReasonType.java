package com.ald.fanbei.api.common.enums;

import java.util.HashMap;
import java.util.Map;

public enum CashierReasonType {
    NEEDAUTH("NEEDAUTH", "需要认证"),
    USE_ABLED_LESS("USE_ABLED_LESS", "可用额度小于分期额度"),//可以使用组合
    NOT_NEED("NOT_NEED", "不需要组合支付"),
    NEEDUP("NEEDUP", "可用额度小于资源配置限制金额 比如额度只有1块钱，那么不能走分期"),
    CONSUME_MIN_AMOUNT("CONSUME_MIN_AMOUNT", "订单金额小于分期/组合支付最小额度限制"),
    VIRTUAL_GOODS_LIMIT("VIRTUAL_GOODS_LIMIT", "虚拟商品限额"),
    CASHIER("CASHIER", "收银台限制"),
    OVERDUE_BORROW("OVERDUE_BORROW", "消费分期逾期"),
    RISK_CREDIT_PAYMENT("RISK_CREDIT_PAYMENT", "风控限制使用信用支付"),
    OVERDUE_BORROW_CASH("OVERDUE_BORROW_CASH", "现金借逾期");

    private String code;
    private String name;

    private static Map<String, BorrowType> codeRoleTypeMap = null;

    CashierReasonType(String code, String name) {
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
