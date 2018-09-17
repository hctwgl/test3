package com.ald.fanbei.api.biz.service.impl;

import com.ald.fanbei.api.biz.service.JsdUserContactsService;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.JsdUserContactsDao;
import com.ald.fanbei.api.dal.domain.JsdUserContactsDo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


/**
 * 都市E贷用户通讯录信息表ServiceImpl
 * 
 * @author guoshuaiqiang
 * @version 1.0.0 初始化
 * @date 2018-06-19 10:52:15
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("dsedUserContactsService")
public class JsdUserContactsServiceImpl extends ParentServiceImpl<JsdUserContactsDo, Long> implements JsdUserContactsService {
    @Resource
    private JsdUserContactsDao jsdUserContactsDao;

		@Override
	public BaseDao<JsdUserContactsDo, Long> getDao() {
		return jsdUserContactsDao;
	}

	@Override
	public List<JsdUserContactsDo> getUserContactsByUserId(String userId) {
		return jsdUserContactsDao.getUserContactsByUserId(userId);
	}

	@Override
	public List<JsdUserContactsDo> getUserContactsByUserIds(List<String> userIds) {
		return jsdUserContactsDao.getUserContactsByUserIds(userIds);
	}

	@Override
	public int updateByUserId(JsdUserContactsDo contactsDo) {
		return jsdUserContactsDao.updateByUserId(contactsDo);
	}
}