/**
 * 
 */
package com.ald.fanbei.api.biz.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.dal.dao.AfResourceDao;
import com.ald.fanbei.api.dal.domain.AfResourceDo;

/**
 * @author suweili
 *
 */
@Service("AfResourceService")

public class AfResourceServiceImpl implements AfResourceService {

	@Resource
	AfResourceDao afResourceDao;
	@Override
	public List<AfResourceDo> getHomeConfigByAllTypes() {
		return afResourceDao.selectHomeConfigByAllTypes();
	}


	@Override
	public List<AfResourceDo> getConfigByTypes(String type) {
		return afResourceDao.getConfigByTypes(type);
	}

}
