package com.ald.fanbei.api.dal.domain.dto;

public class DsedLenderInfoDto {
    private String userName;
    private String edspayUserCardId;
    private String investorAmount;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEdspayUserCardId() {
        return edspayUserCardId;
    }

    public void setEdspayUserCardId(String edspayUserCardId) {
        this.edspayUserCardId = edspayUserCardId;
    }

    public String getInvestorAmount() {
        return investorAmount;
    }

    public void setInvestorAmount(String investorAmount) {
        this.investorAmount = investorAmount;
    }
}
