package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import com.ald.fanbei.api.dal.query.JsdUserAuthQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.JsdUserAuthDao;
import com.ald.fanbei.api.dal.domain.JsdUserAuthDo;
import com.ald.fanbei.api.biz.service.JsdUserAuthService;

import java.util.List;


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
	
    private static final Logger logger = LoggerFactory.getLogger(JsdUserAuthServiceImpl.class);
   
    @Resource
    private JsdUserAuthDao jsdUserAuthDao;

		@Override
	public BaseDao<JsdUserAuthDo, Long> getDao() {
		return jsdUserAuthDao;
	}

	@Override
	public List<JsdUserAuthDo> getListJsdUserAuth(JsdUserAuthQuery query) {
		return jsdUserAuthDao.getListJsdUserAuth(query);
	}
}