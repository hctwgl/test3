package com.ald.fanbei.api.web.validator.bean;

import java.math.BigDecimal;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.springframework.stereotype.Component;

@Component("dredgeWhiteCollarLoanParam")
public class DredgeWhiteCollarLoanParam {
	
	@NotNull
	private String company;  // 公司
	
	@NotNull
	private String station;  // 岗位
	
	@NotNull
	@Pattern(regexp="[0-9]{0,12}") // 联系方式
	private String phone;
	
	@NotNull
	@DecimalMin("0")
	private BigDecimal income;  // 收入
	
	@NotNull
	@Pattern(regexp="(UNMARRIED|LOVING|MARRIED)")
	private String maritalStatus; // 婚恋状态
	
	@NotNull
	private String blackBox;
	
	@NotNull
	private String bqsBlackBox;
	
	@NotNull
	@Pattern(regexp="(fund|socialsecurity|ebank)")
	private String selectedType;
	
	
	private String address;
	
	public String getSelectedType() {
		return selectedType;
	}

	public void setSelectedType(String selectedType) {
		this.selectedType = selectedType;
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

	public String getBqsBlackBox() {
		return bqsBlackBox;
	}

	public void setBqsBlackBox(String bqsBlackBox) {
		this.bqsBlackBox = bqsBlackBox;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getStation() {
		return station;
	}

	public void setStation(String station) {
		this.station = station;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public BigDecimal getIncome() {
		return income;
	}

	public void setIncome(BigDecimal income) {
		this.income = income;
	}

	public String getMaritalStatus() {
		return maritalStatus;
	}

	public void setMaritalStatus(String maritalStatus) {
		this.maritalStatus = maritalStatus;
	}
	
}
