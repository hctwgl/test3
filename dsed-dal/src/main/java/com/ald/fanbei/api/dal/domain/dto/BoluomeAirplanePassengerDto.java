package com.ald.fanbei.api.dal.domain.dto;

import java.math.BigDecimal;

public class BoluomeAirplanePassengerDto {
    private BigDecimal airportFee;
    // "1957-02-10"
    private String birthday;
    // "370323195702101621",
    private String credentialCode;
    // 身份证
    private String credentialType;

    private BigDecimal facePrice;
    private String name;

    public BigDecimal getAirportFee() {
	return airportFee;
    }

    public void setAirportFee(BigDecimal airportFee) {
	this.airportFee = airportFee;
    }

    public String getBirthday() {
	return birthday;
    }

    public void setBirthday(String birthday) {
	this.birthday = birthday;
    }

    public String getCredentialCode() {
	return credentialCode;
    }

    public void setCredentialCode(String credentialCode) {
	this.credentialCode = credentialCode;
    }

    public String getCredentialType() {
	return credentialType;
    }

    public void setCredentialType(String credentialType) {
	this.credentialType = credentialType;
    }

    public BigDecimal getFacePrice() {
	return facePrice;
    }

    public void setFacePrice(BigDecimal facePrice) {
	this.facePrice = facePrice;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    @Override
    public String toString() {
	return "BoluomeAirplanePassengerDto [airportFee=" + airportFee + ", birthday=" + birthday + ", credentialCode=" + credentialCode + ", credentialType=" + credentialType + ", facePrice=" + facePrice + ", name=" + name + "]";
    }

}
