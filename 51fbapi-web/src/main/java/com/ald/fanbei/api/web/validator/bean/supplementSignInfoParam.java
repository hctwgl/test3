package com.ald.fanbei.api.web.validator.bean;

import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Component("friendSignInfoParam")
public class supplementSignInfoParam {
	@NotNull
	public String userName;
	
	@NotNull
	public BigDecimal userId;
	

}
