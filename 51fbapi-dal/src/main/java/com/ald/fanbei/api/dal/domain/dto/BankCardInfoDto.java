package com.ald.fanbei.api.dal.domain.dto;

public class BankCardInfoDto {
    private String bankName;
    private String cardName;
    private Integer cardType;
    private String bankCode;

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public Integer getCardType() {
        return cardType;
    }

    public void setCardType(Integer cardType) {
        this.cardType = cardType;
    }

    @Override
    public String toString() {
        return "BankCardInfoDto{" +
                "bankName='" + bankName + '\'' +
                ", cardName='" + cardName + '\'' +
                ", cardType=" + cardType +
                ", bankCode='" + bankCode + '\'' +
                '}';
    }
}
