package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.DsedUserContactsDao;
import com.ald.fanbei.api.dal.domain.DsedUserContactsDo;
import com.ald.fanbei.api.biz.service.DsedUserContactsService;

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
public class DsedUserContactsServiceImpl extends ParentServiceImpl<DsedUserContactsDo, Long> implements DsedUserContactsService {
	
    private static final Logger logger = LoggerFactory.getLogger(DsedUserContactsServiceImpl.class);
   
    @Resource
    private DsedUserContactsDao dsedUserContactsDao;

		@Override
	public BaseDao<DsedUserContactsDo, Long> getDao() {
		return dsedUserContactsDao;
	}

	@Override
	public List<DsedUserContactsDo> getUserContactsByUserId(String userId) {
		return dsedUserContactsDao.getUserContactsByUserId(userId);
	}

	@Override
	public List<DsedUserContactsDo> getUserContactsByUserIds(List<String> userIds) {
		return dsedUserContactsDao.getUserContactsByUserIds(userIds);
	}

	@Override
	public int updateByUserId(DsedUserContactsDo contactsDo) {
		return dsedUserContactsDao.updateByUserId(contactsDo);
	}
}