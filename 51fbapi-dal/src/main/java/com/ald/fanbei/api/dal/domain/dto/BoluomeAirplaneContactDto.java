package com.ald.fanbei.api.dal.domain.dto;


public class BoluomeAirplaneContactDto {
    private String phone;
    private String name;

    public String getPhone() {
	return phone;
    }

    public void setPhone(String phone) {
	this.phone = phone;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    @Override
    public String toString() {
	return "BoluomeWaimaiContactDto [phone=" + phone + ", name=" + name + "]";
    }

}
