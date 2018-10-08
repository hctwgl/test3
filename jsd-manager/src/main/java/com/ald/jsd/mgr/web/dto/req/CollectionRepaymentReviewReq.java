package com.ald.jsd.mgr.web.dto.req;

import javax.validation.constraints.NotNull;

public class CollectionRepaymentReviewReq extends BaseReq{
	@NotNull
	public String tradeNo;
	
	@NotNull
	public String reviewStatus;
}
