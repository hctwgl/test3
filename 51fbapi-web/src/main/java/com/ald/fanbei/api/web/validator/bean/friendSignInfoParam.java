package com.ald.fanbei.api.web.validator.bean;

import org.springframework.stereotype.Component;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Component("friendSignInfoParam")
public class friendSignInfoParam {
	@NotNull
	public String userName;
	
	@NotNull
	public BigDecimal userId;
	

}
