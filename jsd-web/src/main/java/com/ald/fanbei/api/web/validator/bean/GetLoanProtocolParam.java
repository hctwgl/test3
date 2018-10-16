package com.ald.fanbei.api.web.validator.bean;

import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Component;

@Component("getLoanProtocolParam")
public class GetLoanProtocolParam {
	@NotNull
	public String openId;
	
	public String bizNo;

	@NotNull
	public String type;

	public String previewParam;

	public String tyingType;

	public String isTying;
}
