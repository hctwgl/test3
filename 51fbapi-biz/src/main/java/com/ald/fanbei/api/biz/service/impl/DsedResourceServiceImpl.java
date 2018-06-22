package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import com.ald.fanbei.api.dal.domain.AfResourceDo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.DsedResourceDao;
import com.ald.fanbei.api.dal.domain.DsedResourceDo;
import com.ald.fanbei.api.biz.service.DsedResourceService;

import java.util.List;


/**
 * 资源配置表ServiceImpl
 * 
 * @author guoshuaiqiang
 * @version 1.0.0 初始化
 * @date 2018-06-22 10:49:45
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("dsedResourceService")
public class DsedResourceServiceImpl extends ParentServiceImpl<DsedResourceDo, Long> implements DsedResourceService {
	
    private static final Logger logger = LoggerFactory.getLogger(DsedResourceServiceImpl.class);
   
    @Resource
    private DsedResourceDao dsedResourceDao;

		@Override
	public BaseDao<DsedResourceDo, Long> getDao() {
		return dsedResourceDao;
	}

	@SuppressWarnings("unchecked")
	@Override
	public DsedResourceDo getConfigByTypesAndSecType(String type, String secType) {
		DsedResourceDo afResourceDo = dsedResourceDao.getConfigByTypesAndSecType(type, secType);
		return afResourceDo;
	}
}