package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfAssetSideInfoDao;
import com.ald.fanbei.api.dal.domain.AfAssetSideInfoDo;
import com.ald.fanbei.api.biz.service.AfAssetSideInfoService;



/**
 * 资产方信息ServiceImpl
 * 
 * @author chengkang
 * @version 1.0.0 初始化
 * @date 2017-11-27 15:47:07
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afAssetSideInfoService")
public class AfAssetSideInfoServiceImpl extends ParentServiceImpl<AfAssetSideInfoDo, Long> implements AfAssetSideInfoService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfAssetSideInfoServiceImpl.class);
   
    @Resource
    private AfAssetSideInfoDao afAssetSideInfoDao;

		@Override
	public BaseDao<AfAssetSideInfoDo, Long> getDao() {
		return afAssetSideInfoDao;
	}

	@Override
	public AfAssetSideInfoDo getByFlag(String assetSideFlag) {
		return afAssetSideInfoDao.getByAssetSideFlag(assetSideFlag);
	}
}		