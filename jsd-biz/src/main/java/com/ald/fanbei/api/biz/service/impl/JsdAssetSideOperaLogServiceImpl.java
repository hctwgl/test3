package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.JsdAssetSideOperaLogDao;
import com.ald.fanbei.api.dal.domain.JsdAssetSideOperaLogDo;
import com.ald.fanbei.api.biz.service.JsdAssetSideOperaLogService;



/**
 * ServiceImpl
 * 
 * @author CodeGenerate
 * @version 1.0.0 初始化
 * @date 2018-11-15 15:12:35
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("jsdAssetSideOperaLogService")
public class JsdAssetSideOperaLogServiceImpl extends ParentServiceImpl<JsdAssetSideOperaLogDo, Long> implements JsdAssetSideOperaLogService {
	
    private static final Logger logger = LoggerFactory.getLogger(JsdAssetSideOperaLogServiceImpl.class);
   
    @Resource
    private JsdAssetSideOperaLogDao jsdAssetSideOperaLogDao;

		@Override
	public BaseDao<JsdAssetSideOperaLogDo, Long> getDao() {
		return jsdAssetSideOperaLogDao;
	}
}