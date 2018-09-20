package com.ald.jsd.mgr.web.dto.req;

import javax.validation.constraints.NotNull;

public class CollectionBorrowManualReq extends BaseReq{
	@NotNull
	public String tradeNoXgxy;
	
	@NotNull
	public String reviewRemark;
}
