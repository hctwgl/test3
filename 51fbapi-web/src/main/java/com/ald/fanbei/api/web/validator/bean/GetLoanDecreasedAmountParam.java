package com.ald.fanbei.api.web.validator.bean;

import org.springframework.stereotype.Component;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Component("GetLoanDecreasedAmountParam")
public class GetLoanDecreasedAmountParam {
	@Min(value=0)
	public Long userId;


	@NotNull
	public String borrowNo;


}
