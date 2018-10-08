package com.ald.jsd.mgr.web.dto.req;

import java.math.BigDecimal;

public class UserAuthReq extends BaseReq{

    private String openId;

    private String riskNo;

    private String riskStatus;

    private BigDecimal riskAmount;

    private BigDecimal riskRate;

    private String url;

    private String riskTime;

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getRiskNo() {
        return riskNo;
    }

    public void setRiskNo(String riskNo) {
        this.riskNo = riskNo;
    }

    public String getRiskStatus() {
        return riskStatus;
    }

    public void setRiskStatus(String riskStatus) {
        this.riskStatus = riskStatus;
    }

    public BigDecimal getRiskAmount() {
        return riskAmount;
    }

    public void setRiskAmount(BigDecimal riskAmount) {
        this.riskAmount = riskAmount;
    }

    public BigDecimal getRiskRate() {
        return riskRate;
    }

    public void setRiskRate(BigDecimal riskRate) {
        this.riskRate = riskRate;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRiskTime() {
        return riskTime;
    }

    public void setRiskTime(String riskTime) {
        this.riskTime = riskTime;
    }
}
