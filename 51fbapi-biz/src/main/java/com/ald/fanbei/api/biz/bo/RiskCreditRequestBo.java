package com.ald.fanbei.api.biz.bo;

import java.math.BigDecimal;
import java.util.HashMap;

public class RiskCreditRequestBo extends HashMap<String, String> {
    private String orderNo;
    private String consumerNo;
    private String signInfo;


    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
        this.put("orderNo", orderNo);
    }

    public String getConsumerNo() {
        return consumerNo;
    }

    public void setConsumerNo(String consumerNo) {
        this.consumerNo = consumerNo;
        this.put("consumerNo", consumerNo);
    }


    public String getSignInfo() {
        return signInfo;
    }

    public void setSignInfo(String signInfo) {
        this.signInfo = signInfo;
        this.put("signInfo", signInfo);
    }
}
