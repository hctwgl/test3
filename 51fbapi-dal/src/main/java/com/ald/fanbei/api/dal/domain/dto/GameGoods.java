package com.ald.fanbei.api.dal.domain.dto;

public class GameGoods {
    private Integer id;
    private String name;
    private String image;
    private Double discout;
    private Double rebate;

    public Integer getId() {
	return id;
    }

    public void setId(Integer id) {
	this.id = id;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getImage() {
	return image;
    }

    public void setImage(String image) {
	this.image = image;
    }

    public Double getDiscout() {
	return discout;
    }

    public void setDiscout(Double discout) {
	this.discout = discout;
    }

    public Double getRebate() {
	return rebate;
    }

    public void setRebate(Double rebate) {
	this.rebate = rebate;
    }

    @Override
    public String toString() {
	return "GameGoods [id=" + id + ", name=" + name + ", image=" + image + ", discout=" + discout + ", rebate=" + rebate + "]";
    }

}
