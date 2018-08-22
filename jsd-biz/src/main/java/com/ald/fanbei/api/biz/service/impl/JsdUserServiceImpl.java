package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.JsdUserDao;
import com.ald.fanbei.api.dal.domain.JsdUserDo;
import com.ald.fanbei.api.biz.service.JsdUserService;



/**
 * 极速贷用户信息ServiceImpl
 * 
 * @author yanghailong
 * @version 1.0.0 初始化
 * @date 2018-08-22 16:43:43
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("jsdUserService")
public class JsdUserServiceImpl extends ParentServiceImpl<JsdUserDo, Long> implements JsdUserService {
	
    private static final Logger logger = LoggerFactory.getLogger(JsdUserServiceImpl.class);
   
    @Resource
    private JsdUserDao jsdUserDao;

		@Override
	public BaseDao<JsdUserDo, Long> getDao() {
		return jsdUserDao;
	}
}