package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.JsdUpsLogDao;
import com.ald.fanbei.api.dal.domain.JsdUpsLogDo;
import com.ald.fanbei.api.biz.service.JsdUpsLogService;



/**
 * ServiceImpl
 * 
 * @author yanghailong
 * @version 1.0.0 初始化
 * @date 2018-08-30 10:47:20
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("jsdUpsLogService")
public class JsdUpsLogServiceImpl extends ParentServiceImpl<JsdUpsLogDo, Long> implements JsdUpsLogService {
	
    private static final Logger logger = LoggerFactory.getLogger(JsdUpsLogServiceImpl.class);
   
    @Resource
    private JsdUpsLogDao jsdUpsLogDao;

		@Override
	public BaseDao<JsdUpsLogDo, Long> getDao() {
		return jsdUpsLogDao;
	}
}