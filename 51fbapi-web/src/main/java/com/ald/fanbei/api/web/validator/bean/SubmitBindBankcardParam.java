package com.ald.fanbei.api.web.validator.bean;

import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Component;

@Component("submitBindBankcardParam")
public class SubmitBindBankcardParam {
	public Long orderId;
	
	public String isCombinationPay;
	public String orderType;
	public Integer orderNper;
	public String lat;
	public String lng;
	
	public String payPwd;
	public String realname;
	public String idNumber;
	
	@NotNull
	public Long bankCardId;
	@NotNull
	public String smsCode;
	
}
