package com.ald.fanbei.api.biz.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.JsdUserAuthService;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.JsdUserAuthDao;
import com.ald.fanbei.api.dal.domain.JsdUserAuthDo;
import com.ald.fanbei.api.dal.domain.dto.UserAuthDto;
import com.ald.fanbei.api.dal.query.UserAuthQuery;


/**
 * ServiceImpl
 * 
 * @author CodeGenerate
 * @version 1.0.0 初始化
 * @date 2018-09-16 11:51:40
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("jsdUserAuthService")
public class JsdUserAuthServiceImpl extends ParentServiceImpl<JsdUserAuthDo, Long> implements JsdUserAuthService {
	
    @Resource
    private JsdUserAuthDao jsdUserAuthDao;

		@Override
	public BaseDao<JsdUserAuthDo, Long> getDao() {
		return jsdUserAuthDao;
	}

	@Override
	public List<UserAuthDto> getListUserAuth(UserAuthQuery query) {
		return jsdUserAuthDao.getListUserAuth(query);
	}

	@Override
	public JsdUserAuthDo getByUserId(Long userId) {
		return jsdUserAuthDao.getByUserId(userId);
	}

	@Override
	public int getSubmitPersonNum() {
		return jsdUserAuthDao.getSubmitPersonNum();
	}

	@Override
	public int getPassPersonNum() {
		return jsdUserAuthDao.getPassPersonNum();
	}
}