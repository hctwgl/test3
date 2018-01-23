package com.ald.fanbei.api.dal.domain.dto;

public class UserDrawInfo {

    private String phone;
    private String name;
    private String nickName;
    private String headerImg;
    private Long rid;

    public Long getRid() {
	return rid;
    }

    public void setRid(Long rid) {
	this.rid = rid;
    }

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

    public String getNickName() {
	return nickName;
    }

    public void setNickName(String nickName) {
	this.nickName = nickName;
    }

    public String getHeaderImg() {
	return headerImg;
    }

    public void setHeaderImg(String headerImg) {
	this.headerImg = headerImg;
    }

    @Override
    public String toString() {
	return "UserDrawInfo [phone=" + phone + ", name=" + name + ", nickName=" + nickName + ", headerImg=" + headerImg + ", rid=" + rid + "]";
    }

}
