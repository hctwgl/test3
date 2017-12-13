package com.ald.fanbei.api.dal.domain.dto;

import com.alibaba.fastjson.annotation.JSONField;

public class BoluomeJiudianContactDto {
    private String mobile;
    private String name;

    @JSONField(name = "Mobile")
    public String getMobile() {
	return mobile;
    }

    @JSONField(name = "Mobile")
    public void setMobile(String mobile) {
	this.mobile = mobile;
    }

    @JSONField(name = "Name")
    public String getName() {
	return name;
    }

    @JSONField(name = "Name")
    public void setName(String name) {
	this.name = name;
    }

    @Override
    public String toString() {
	return "BoluomeContactDto [mobile=" + mobile + ", name=" + name + "]";
    }

}
