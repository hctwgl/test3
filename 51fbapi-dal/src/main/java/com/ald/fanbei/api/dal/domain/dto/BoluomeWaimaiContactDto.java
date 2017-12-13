package com.ald.fanbei.api.dal.domain.dto;

public class BoluomeWaimaiContactDto {
    private String mobile;
    private String name;
    private String address;

    public String getMobile() {
	return mobile;
    }

    public void setMobile(String mobile) {
	this.mobile = mobile;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getAddress() {
	return address;
    }

    public void setAddress(String address) {
	this.address = address;
    }

    @Override
    public String toString() {
	return "BoluomeWaimaiContactDto [mobile=" + mobile + ", name=" + name + ", address=" + address + "]";
    }

}
