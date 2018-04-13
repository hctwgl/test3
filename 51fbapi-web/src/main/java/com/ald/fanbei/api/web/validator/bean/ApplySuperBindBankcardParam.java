package com.ald.fanbei.api.web.validator.bean;

import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Component;

@Component("applySuperBindBankcardParam")
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
	public String payPwd;
	
	public Long orderId;
	public String payId;
	public Integer nper;
	public String type;
	public String isCombinationPay;
	public String lat;
	public String lng;
	public String bankPayType;
	
}
