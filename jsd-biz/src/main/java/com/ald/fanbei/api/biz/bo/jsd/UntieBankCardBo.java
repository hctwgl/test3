package com.ald.fanbei.api.biz.bo.jsd;

import org.springframework.stereotype.Component;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class UntieBankCardBo {

	public Long userId;
    public UntieBankCardReq req;
	
	@Component("untieBorrowCashReq")
	public static class UntieBankCardReq{
		@NotNull
		public String openId;
		@NotNull
		public String bankNo;
		

	}
    

}
