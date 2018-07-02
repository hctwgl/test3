package com.ald.fanbei.api.web.validator.bean;

import java.math.BigDecimal;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Component;

@Component("applyLoanParam")
public class ApplyLoanParam {
	@NotNull
	public String prdType; 
	
	@DecimalMin("0")
	public BigDecimal amount;
	
	@Min(value=1)
	public int periods;
	
	public String remark;
	
	@NotNull
	public String loanRemark;
	
	@NotNull
	public String repayRemark;
	
	@NotNull
	public String payPwd;
	
	@NotNull
	public String latitude;
	
	@NotNull
	public String longitude;
	
	public String province;
	
	public String city;
	
	public String county;
	
	public String address;
	
	@NotNull
	public String blackBox;
	
	@NotNull
	public String bqsBlackBox;
	
	public Long couponId;
}
