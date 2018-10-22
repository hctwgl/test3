package com.ald.fanbei.api.dal.domain.dto;

import com.ald.fanbei.api.dal.domain.JsdBorrowCashOverdueLogDo;

public class JsdBorrowCashOverdueLogDto extends JsdBorrowCashOverdueLogDo {
    private String payTime;//线下还款支付时间

    public String getPayTime() {
        return payTime;
    }

    public void setPayTime(String payTime) {
        this.payTime = payTime;
    }
}
