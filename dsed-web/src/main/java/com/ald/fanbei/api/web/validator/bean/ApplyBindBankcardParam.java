package com.ald.fanbei.api.web.validator.bean;

import com.ald.fanbei.api.common.enums.BankCardType;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

@Component("applyBindBankcardParam")
public class ApplyBindBankcardParam {
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

	public Integer cardType = BankCardType.DEBIT.getCode();
	public String validDate;
	public String safeCode;
}
