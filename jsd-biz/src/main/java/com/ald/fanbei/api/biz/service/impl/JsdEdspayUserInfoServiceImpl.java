package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.JsdEdspayUserInfoDao;
import com.ald.fanbei.api.dal.domain.JsdEdspayUserInfoDo;
import com.ald.fanbei.api.biz.service.JsdEdspayUserInfoService;



/**
 * ServiceImpl
 * 
 * @author CodeGenerate
 * @version 1.0.0 初始化
 * @date 2018-11-15 18:37:18
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("jsdEdspayUserInfoService")
public class JsdEdspayUserInfoServiceImpl extends ParentServiceImpl<JsdEdspayUserInfoDo, Long> implements JsdEdspayUserInfoService {
	
    private static final Logger logger = LoggerFactory.getLogger(JsdEdspayUserInfoServiceImpl.class);
   
    @Resource
    private JsdEdspayUserInfoDao jsdEdspayUserInfoDao;

		@Override
	public BaseDao<JsdEdspayUserInfoDo, Long> getDao() {
		return jsdEdspayUserInfoDao;
	}
}