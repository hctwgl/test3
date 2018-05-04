package com.ald.fanbei.api.web.validator.bean;

import org.springframework.stereotype.Component;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;

@Component("applyRecycleBorrowCashParam")
public class ApplyRecycleBorrowCashParam {

	@NotNull
	@DecimalMin("0")
	private BigDecimal amount;
	@NotNull
	private String pwd;
	@NotNull
	@Pattern(regexp = "^[0-9]*[1-9][0-9]*$")
	private String type;
	@NotNull
	private BigDecimal latitude;
	@NotNull
	private BigDecimal longitude;
	
	private String province;
	
	private String city;
	
	private String county;
	@NotNull
	private String address;
	@NotNull
	private String blackBox;
	@NotNull
	private String bqsBlackBox;
	private String borrowRemark;
	private String refundRemark;
	@NotNull
	private String goodsName;
	@NotNull
	private String propertyValue;
	private String goodsImg;

	public String getPropertyValue() {
		return propertyValue;
	}

	public void setPropertyValue(String propertyValue) {
		this.propertyValue = propertyValue;
	}

	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	public String getGoodsImg() {
		return goodsImg;
	}

	public void setGoodsImg(String goodsImg) {
		this.goodsImg = goodsImg;
	}

	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public BigDecimal getLatitude() {
		return latitude;
	}
	public void setLatitude(BigDecimal latitude) {
		this.latitude = latitude;
	}
	public BigDecimal getLongitude() {
		return longitude;
	}
	public void setLongitude(BigDecimal longitude) {
		this.longitude = longitude;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getCounty() {
		return county;
	}
	public void setCounty(String county) {
		this.county = county;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getBlackBox() {
		return blackBox;
	}
	public void setBlackBox(String blackBox) {
		this.blackBox = blackBox;
	}
	public String getBorrowRemark() {
		return borrowRemark;
	}
	public void setBorrowRemark(String borrowRemark) {
		this.borrowRemark = borrowRemark;
	}
	public String getRefundRemark() {
		return refundRemark;
	}
	public void setRefundRemark(String refundRemark) {
		this.refundRemark = refundRemark;
	}

	public String getBqsBlackBox() {
		return bqsBlackBox;
	}

	public void setBqsBlackBox(String bqsBlackBox) {
		this.bqsBlackBox = bqsBlackBox;
	}
}
