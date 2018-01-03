package com.ald.fanbei.api.web.validator.bean;

import javax.validation.constraints.Pattern;

import org.springframework.stereotype.Component;

@Component("getHomeInfoV2Param")
public class GetHomeInfoV2Param {
	
	@Pattern(regexp="IPHONEX")
	private String deviceType;

	
}
