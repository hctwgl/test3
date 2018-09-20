package com.ald.jsd.mgr.web.dto.req;

import javax.validation.constraints.NotNull;

public class CollectionBorrowReviewReq extends BaseReq{
	@NotNull
	public String tradeNoXgxy;
	@NotNull
	public String reviewStatus;
	@NotNull
	public String reviewRemark;
}
