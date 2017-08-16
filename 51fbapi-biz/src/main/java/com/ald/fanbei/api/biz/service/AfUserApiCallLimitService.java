package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.AfUserApiCallLimitDo;

public interface AfUserApiCallLimitService {

	boolean addUserApiCallLimit(AfUserApiCallLimitDo afUserApiCallLimitDo);

	boolean updateUserApiCallLimit(AfUserApiCallLimitDo afUserApiCallLimitDo);

	AfUserApiCallLimitDo selectByUserIdAndType(Long userId, String type);

	void addVisitNum(Long userId, String type);
}
