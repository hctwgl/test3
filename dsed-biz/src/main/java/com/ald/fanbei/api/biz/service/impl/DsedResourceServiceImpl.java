package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.DsedResourceService;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.DsedResourceDao;
import com.ald.fanbei.api.dal.domain.DsedResourceDo;


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
	
    @Resource
    private DsedResourceDao dsedResourceDao;

	@Override
	public BaseDao<DsedResourceDo, Long> getDao() {
		return dsedResourceDao;
	}
		
	@Override
	public DsedResourceDo getConfigByTypesAndSecType(String type, String secType) {
		DsedResourceDo afResourceDo = dsedResourceDao.getConfigByTypesAndSecType(type, secType);
		return afResourceDo;
	}
}