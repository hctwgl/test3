package com.ald.fanbei.api.web.validator.bean;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Component;

@Component("submitBindBankcardParam")
public class SubmitBindBankcardParam {
	
	public String orderId;
	public BigDecimal amount;
	public String pauPwd;
	
	@NotNull
	public String realName;
	
	@NotNull
	public String idNumber;
	
	@NotNull
	public String cardNumber;
	
	@NotNull
	public String mobile;
	
	@NotNull
	public String bankCode;
	
	@NotNull
	public String bankName;
	
	@NotNull
	public String smsCode;
	
}
