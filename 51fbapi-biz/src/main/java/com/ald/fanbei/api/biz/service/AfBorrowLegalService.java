package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.biz.service.impl.AfBorrowLegalServiceImpl.BorrowLegalHomeInfoBo;
import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;

import java.util.List;

public interface AfBorrowLegalService extends ParentService<AfBorrowCashDo, Long>{
	
	BorrowLegalHomeInfoBo getHomeInfo(Long userId);

	BorrowLegalHomeInfoBo getRecycleInfo(Long userId);

	List<BorrowLegalHomeInfoBo> getRecycleRecord(Long userId, Integer start);


}
