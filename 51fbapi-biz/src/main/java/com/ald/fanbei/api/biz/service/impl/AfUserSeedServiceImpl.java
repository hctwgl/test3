package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfUserSeedDao;
import com.ald.fanbei.api.dal.domain.AfUserSeedDo;
import com.ald.fanbei.api.biz.service.AfUserSeedService;



/**
 * 种子用户信息ServiceImpl
 * 
 * @author hqj
 * @version 1.0.0 初始化
 * @date 2017-11-16 16:40:48
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afUserSeedService")
public class AfUserSeedServiceImpl extends ParentServiceImpl<AfUserSeedDo, Long> implements AfUserSeedService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfUserSeedServiceImpl.class);
   
    @Resource
    private AfUserSeedDao afUserSeedDao;

		@Override
	public BaseDao<AfUserSeedDo, Long> getDao() {
		return afUserSeedDao;
	}

		@Override
		public AfUserSeedDo getAfUserSeedDoByUserId(long userId) {
			return afUserSeedDao.getAfUserSeedDoByUserId(userId);
		}
}