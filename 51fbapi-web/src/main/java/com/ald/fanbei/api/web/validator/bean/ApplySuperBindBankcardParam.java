package com.ald.fanbei.api.web.validator.bean;

import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Component;

@Component("applyBindBankcardParam")
public class ApplySuperBindBankcardParam {
	@NotNull
	public String cardNumber;
	
	@NotNull
	public String mobile;
	
	@NotNull
	public String bankCode;
	
	@NotNull
	public String bankName;
	
	public String realname;
	public String idNumber;
}
