package com.ald.fanbei.api.biz.service;

import java.util.Map;

import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;

public interface AfBorrowLegalService extends ParentService<AfBorrowCashDo, Long>{
	
	Map<String, Object> getHomeInfo(Long userId);
	
}
