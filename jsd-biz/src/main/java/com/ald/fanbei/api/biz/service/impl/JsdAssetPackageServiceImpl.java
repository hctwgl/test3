package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.JsdAssetPackageDao;
import com.ald.fanbei.api.dal.domain.JsdAssetPackageDo;
import com.ald.fanbei.api.biz.service.JsdAssetPackageService;



/**
 * ServiceImpl
 * 
 * @author CodeGenerate
 * @version 1.0.0 初始化
 * @date 2018-11-15 11:22:07
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("jsdAssetPackageService")
public class JsdAssetPackageServiceImpl extends ParentServiceImpl<JsdAssetPackageDo, Long> implements JsdAssetPackageService {
	
    private static final Logger logger = LoggerFactory.getLogger(JsdAssetPackageServiceImpl.class);
   
    @Resource
    private JsdAssetPackageDao jsdAssetPackageDao;

		@Override
	public BaseDao<JsdAssetPackageDo, Long> getDao() {
		return jsdAssetPackageDao;
	}
}