package com.ald.fanbei.api.biz.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfSignInActivityService;
import com.ald.fanbei.api.dal.dao.AfSignInActivityDao;
import com.ald.fanbei.api.dal.domain.AfSignInActivityDo;

@Service("afSignInActivityService")
public class AfSignInActivityServiceImpl implements AfSignInActivityService{

	@Resource
	AfSignInActivityDao afSignInActivityDao;
	@Override
	public List<String> initActivitySign(Long userId, Long activityId) {
		if (!userId.equals(null) && !activityId.equals(null)) {
			return afSignInActivityDao.initActivitySign(userId, activityId);
		}
		return null;
	}

	@Override
	public Integer signIn(AfSignInActivityDo afSignInActivityDo) {
		if (!afSignInActivityDo.equals(null)) {
			return afSignInActivityDao.singIn(afSignInActivityDo);
		}
		return null;
	}

}
