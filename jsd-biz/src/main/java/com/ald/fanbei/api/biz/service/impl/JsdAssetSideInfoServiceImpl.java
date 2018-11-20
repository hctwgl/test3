package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.JsdAssetSideInfoDao;
import com.ald.fanbei.api.dal.domain.JsdAssetSideInfoDo;
import com.ald.fanbei.api.biz.service.JsdAssetSideInfoService;



/**
 * ServiceImpl
 * 
 * @author CodeGenerate
 * @version 1.0.0 初始化
 * @date 2018-11-15 11:12:15
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("jsdAssetSideInfoService")
public class JsdAssetSideInfoServiceImpl extends ParentServiceImpl<JsdAssetSideInfoDo, Long> implements JsdAssetSideInfoService {
	
    private static final Logger logger = LoggerFactory.getLogger(JsdAssetSideInfoServiceImpl.class);
   
    @Resource
    private JsdAssetSideInfoDao jsdAssetSideInfoDao;

		@Override
	public BaseDao<JsdAssetSideInfoDo, Long> getDao() {
		return jsdAssetSideInfoDao;
	}
}