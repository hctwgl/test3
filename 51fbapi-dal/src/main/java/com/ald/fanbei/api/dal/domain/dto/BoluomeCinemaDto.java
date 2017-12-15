package com.ald.fanbei.api.dal.domain.dto;

public class BoluomeCinemaDto {
    private String addr;
    private String cityId;
    private String id;
    private String lat;
    private String lng;
    private String name;

    public String getAddr() {
	return addr;
    }

    public void setAddr(String addr) {
	this.addr = addr;
    }

    public String getCityId() {
	return cityId;
    }

    public void setCityId(String cityId) {
	this.cityId = cityId;
    }

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getLat() {
	return lat;
    }

    public void setLat(String lat) {
	this.lat = lat;
    }

    public String getLng() {
	return lng;
    }

    public void setLng(String lng) {
	this.lng = lng;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    @Override
    public String toString() {
	return "BoluomeCinema [addr=" + addr + ", cityId=" + cityId + ", id=" + id + ", lat=" + lat + ", lng=" + lng + ", name=" + name + "]";
    }

}
