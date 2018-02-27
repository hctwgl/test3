package com.ald.fanbei.api.web.validator.bean;

import org.springframework.stereotype.Component;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;

@Component("replaceMainCardParam")
public class ReplaceMainCardParam {


	@NotNull
	@DecimalMin("0")
	private Long userId;

	@NotNull
	private String backcard;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getBackcard() {
		return backcard;
	}

	public void setBackcard(String backcard) {
		this.backcard = backcard;
	}
}
