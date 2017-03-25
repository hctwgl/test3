package com.ald.fanbei.api.biz.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.dal.dao.AfResourceDao;
import com.ald.fanbei.api.dal.domain.AfResourceDo;

/**

 *@类描述：
 *@author Xiaotianjian 2017年1月20日上午10:27:48
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afResourceService")
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
	@Override
	public List<AfResourceDo> getResourceListByType(String type) {
		return afResourceDao.getResourceListByType(type);
	}

	@Override
	public AfResourceDo getSingleResourceBytype(String type) {
		return afResourceDao.getSingleResourceBytype(type);
	}



	@Override
	public AfResourceDo getConfigByTypesAndSecType(String type, String secType) {
		return afResourceDao.getConfigByTypesAndSecType(type, secType);
	}


	
	@Override
	public List<AfResourceDo> getResourceListByTypeOrderBy(String type) {
		return afResourceDao.getResourceListByTypeOrderBy(type);
	}


	@Override
	public AfResourceDo getResourceByResourceId(Long rid) {
		return afResourceDao.getResourceByResourceId(rid);
	}

	@Override
	public List<AfResourceDo> getOneToManyResourceOrderByBytype(String type) {
		return afResourceDao.getOneToManyResourceOrderByBytype(type);
	}


	@Override
	public List<AfResourceDo> getResourceHomeListByTypeOrderBy(String type) {
		return afResourceDao.getResourceHomeListByTypeOrderBy(type);
	}


	
	@Override
	public List<AfResourceDo> selectBorrowHomeConfigByAllTypes() {
		return afResourceDao.selectBorrowHomeConfigByAllTypes();
	}

}
