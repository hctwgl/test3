package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfUserProfileDao;
import com.ald.fanbei.api.dal.domain.AfUserProfileDo;
import com.ald.fanbei.api.biz.service.AfUserProfileService;

import java.util.List;


/**
 * 用户关联账号ServiceImpl
 * 
 * @author xieqiang
 * @version 1.0.0 初始化
 * @date 2018-01-24 16:04:53
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afUserProfileService")
public class AfUserProfileServiceImpl extends ParentServiceImpl<AfUserProfileDo, Long> implements AfUserProfileService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfUserProfileServiceImpl.class);
   
    @Resource
    private AfUserProfileDao afUserProfileDao;

	@Override
	public BaseDao<AfUserProfileDo, Long> getDao() {
		return afUserProfileDao;
	}

	@Override
	public void saveUserProfile(AfUserProfileDo userProfileDo) {
		afUserProfileDao.saveUserProfile(userProfileDo);
	}

	@Override
	public void updateUserProfileById(AfUserProfileDo userProfileDo) {
		afUserProfileDao.updateUserProfileById(userProfileDo);
	}

	@Override
	public AfUserProfileDo getUserProfileById(AfUserProfileDo userProfileDo) {
		return afUserProfileDao.getUserProfileById(userProfileDo);
	}

	@Override
	public AfUserProfileDo getUserProfileByCommonCondition(AfUserProfileDo userProfileDo) {
		return afUserProfileDao.getUserProfileByCommonCondition(userProfileDo);
	}

	@Override
	public List<AfUserProfileDo> getUserProfileListByCommonCondition(AfUserProfileDo userProfileDo) {
		return afUserProfileDao.getUserProfileListByCommonCondition(userProfileDo);
	}

	@Override
	public void updateDeleteUserProfileById(long id) {
		afUserProfileDao.updateDeleteUserProfileById(id);
	}
}