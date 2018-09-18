package com.ald.jsd.mgr.web.dto.req;

public class UserAuthReq extends BaseReq{

    private String openId;

    private String riskNo;

    private String riskStatus;

    private String riskAmount;

    private String riskRate;

    private String url;

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

    public String getRiskAmount() {
        return riskAmount;
    }

    public void setRiskAmount(String riskAmount) {
        this.riskAmount = riskAmount;
    }

    public String getRiskRate() {
        return riskRate;
    }

    public void setRiskRate(String riskRate) {
        this.riskRate = riskRate;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
