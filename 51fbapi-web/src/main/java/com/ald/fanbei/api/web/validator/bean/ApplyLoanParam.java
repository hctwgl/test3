package com.ald.fanbei.api.web.validator.bean;

import java.math.BigDecimal;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

import org.springframework.stereotype.Component;

@Component("applyLoanParam")
public class ApplyLoanParam {
	@NotNull
	String prdType; 
	
	@DecimalMin("0")
	BigDecimal amount;
	
	@Min(value=1)
	int periods;
	
	@NotNull
	String repayType;
	
	@NotNull
	String loan_remark;
	
	@NotNull
	String repay_remark;
	
	@NotNull
	String cardId;
	
	@NotNull
	String pwd;
	
	@NotNull
	String latitude;
	
	@NotNull
	String longitude;
	
	@NotNull
	String province;
	
	@NotNull
	String city;
	
	@NotNull
	String county;
	
	@Null
	String address;
	
	@NotNull
	String blackBox;
	
	@Null
	Long couponId;
}
