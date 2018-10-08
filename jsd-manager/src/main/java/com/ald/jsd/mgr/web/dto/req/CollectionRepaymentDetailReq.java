package com.ald.jsd.mgr.web.dto.req;

import javax.validation.constraints.NotNull;

public class CollectionRepaymentDetailReq extends BaseReq{
	@NotNull
	public String tradeNo;
}
