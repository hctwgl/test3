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
	
	@Null
	String remark;
	
	@NotNull
	String loanRemark;
	
	@NotNull
	String repayRemark;
	
	@Min(value=1)
	Long cardId;
	
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
