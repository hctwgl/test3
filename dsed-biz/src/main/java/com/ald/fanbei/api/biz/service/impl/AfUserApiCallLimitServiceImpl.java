package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfUserApiCallLimitService;
import com.ald.fanbei.api.dal.dao.AfUserApiCallLimitDao;
import com.ald.fanbei.api.dal.domain.AfUserApiCallLimitDo;

@Service("afUserApiCallLimitService")
public class AfUserApiCallLimitServiceImpl implements AfUserApiCallLimitService {

	@Resource
	private AfUserApiCallLimitDao afUserApiCallLimitDao;

	@Override
	public boolean addUserApiCallLimit(AfUserApiCallLimitDo afUserApiCallLimitDo) {
		if (afUserApiCallLimitDao.addUserApiCallLimit(afUserApiCallLimitDo) > 0) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean updateUserApiCallLimit(AfUserApiCallLimitDo afUserApiCallLimitDo) {
		if (afUserApiCallLimitDao.updateUserApiCallLimit(afUserApiCallLimitDo) > 0) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public AfUserApiCallLimitDo selectByUserIdAndType(Long userId, String type) {
		return afUserApiCallLimitDao.selectByUserIdAndType(userId, type);
	}

	@Override
	public void addVisitNum(Long userId, String type) {
		afUserApiCallLimitDao.addVisitNum(userId, type);
	}

}
