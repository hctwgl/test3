package com.ald.jsd.mgr.web.dto.req;

import javax.validation.constraints.NotNull;

public class AnalysisReq extends BaseReq{
	
	@NotNull
	public Integer days;
}
