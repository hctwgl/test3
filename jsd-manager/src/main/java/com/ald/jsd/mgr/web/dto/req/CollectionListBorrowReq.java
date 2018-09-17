package com.ald.jsd.mgr.web.dto.req;

import javax.validation.constraints.NotNull;

public class CollectionListBorrowReq extends BaseReq{
	@NotNull
	public String status;
}
