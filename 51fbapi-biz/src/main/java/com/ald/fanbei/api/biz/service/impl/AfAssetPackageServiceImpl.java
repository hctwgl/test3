package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfAssetPackageDao;
import com.ald.fanbei.api.dal.domain.AfAssetPackageDo;
import com.ald.fanbei.api.biz.service.AfAssetPackageService;



/**
 * 资产包信息ServiceImpl
 * 
 * @author chengkang
 * @version 1.0.0 初始化
 * @date 2017-11-27 15:47:30
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afAssetPackageService")
public class AfAssetPackageServiceImpl extends ParentServiceImpl<AfAssetPackageDo, Long> implements AfAssetPackageService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfAssetPackageServiceImpl.class);
   
    @Resource
    private AfAssetPackageDao afAssetPackageDao;

		@Override
	public BaseDao<AfAssetPackageDo, Long> getDao() {
		return afAssetPackageDao;
	}
}