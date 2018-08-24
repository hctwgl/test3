package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.JsdResourceDao;
import com.ald.fanbei.api.dal.domain.JsdResourceDo;
import com.ald.fanbei.api.biz.service.JsdResourceService;



/**
 * 极速贷资源配置ServiceImpl
 * 
 * @author yanghailong
 * @version 1.0.0 初始化
 * @date 2018-08-24 10:37:20
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("jsdResourceService")
public class JsdResourceServiceImpl extends ParentServiceImpl<JsdResourceDo, Long> implements JsdResourceService {
	
    private static final Logger logger = LoggerFactory.getLogger(JsdResourceServiceImpl.class);
   
    @Resource
    private JsdResourceDao jsdResourceDao;

		@Override
	public BaseDao<JsdResourceDo, Long> getDao() {
		return jsdResourceDao;
	}

	@Override
	public JsdResourceDo getByTypeAngSecType(String type, String secType) {
		return jsdResourceDao.getByTypeAngSecType(type, secType);
	}
}