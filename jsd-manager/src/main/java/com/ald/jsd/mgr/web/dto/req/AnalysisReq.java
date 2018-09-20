package com.ald.jsd.mgr.web.dto.req;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class AnalysisReq extends BaseReq{
	
	@NotNull
	public Integer days;
}
