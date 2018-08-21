package com.ald.fanbei.api.web.validator.bean;

import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

@Component("smsCodeSubmitParam")
public class SmsCodeSubmitParam {

	
	@NotNull
	public Long busiFlag;
	@NotNull
	public String code;
	@NotNull
	public String userId;

}
