package com.ald.fanbei.api.biz.service.impl;

import java.util.ArrayList;
import java.util.Date;
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
	public List<Date> initActivitySign(Long userId, Long activityId) {
		if (userId!=null && activityId!=null) {
			return afSignInActivityDao.initActivitySign(userId, activityId);
		}
		return new ArrayList<>();
	}

	@Override
	public Integer signIn(AfSignInActivityDo afSignInActivityDo) {
		if (afSignInActivityDo!=null) {
			return afSignInActivityDao.singIn(afSignInActivityDo);
		}
		return 0;
	}

}
