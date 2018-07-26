package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.DsedUserService;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.DsedUserDao;
import com.ald.fanbei.api.dal.domain.DsedUserDo;



/**
 * 都市易贷用户信息ServiceImpl
 * 
 * @author guoshuaiqiang
 * @version 1.0.0 初始化
 * @date 2018-06-19 10:51:34
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("dsedUserService")
public class DsedUserServiceImpl extends ParentServiceImpl<DsedUserDo, Long> implements DsedUserService {
    @Resource
    private DsedUserDao dsedUserDao;

		@Override
	public BaseDao<DsedUserDo, Long> getDao() {
		return dsedUserDao;
	}

	@Override
	public int updateUser(DsedUserDo userDo) {
		return dsedUserDao.updateUser(userDo);
	}

	@Override
	public DsedUserDo getByOpenId(String openId) {
		return dsedUserDao.getByOpenId(openId);
	}

	@Override
	public DsedUserDo getUserById(Long userId) {
		return dsedUserDao.getUserById(userId) ;
	}

	@Override
	public DsedUserDo getUserByMobile(String mobile) {
		return dsedUserDao.getUserByMobile(mobile);
	}
}