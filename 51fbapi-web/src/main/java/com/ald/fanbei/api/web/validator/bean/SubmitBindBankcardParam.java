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

	//支付密码
	public String newPassword;
	public String realname;
	public String idNumber;
	
	@NotNull
	public Long bankCardId;
	@NotNull
	public String smsCode;
	
}
