package com.ald.fanbei.api.dal.domain.query;

import com.ald.fanbei.api.common.page.Page;
import com.ald.fanbei.api.dal.domain.dto.AfLimitDetailDto;

public class AfLimitDetailQuery extends Page<AfLimitDetailDto>{

	private static final long serialVersionUID = -3735304698370566135L;

	private Long userId;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
}
