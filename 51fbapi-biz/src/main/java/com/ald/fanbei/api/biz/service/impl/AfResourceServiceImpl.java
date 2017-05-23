package com.ald.fanbei.api.biz.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.CacheConstants;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.util.SerializeUtil;
import com.ald.fanbei.api.dal.dao.AfResourceDao;
import com.ald.fanbei.api.dal.domain.AfResourceDo;

/**
 * 
 * @类描述：
 * 
 * @author Xiaotianjian 2017年1月20日上午10:27:48
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afResourceService")
public class AfResourceServiceImpl implements AfResourceService {

	@Resource
	AfResourceDao afResourceDao;
	@Resource
	BizCacheUtil bizCacheUtil;

	@Override
	public List<AfResourceDo> getHomeConfigByAllTypes() {
		List<AfResourceDo> list = bizCacheUtil.getObjectList(CacheConstants.RESOURCE.RESOURCE_CONFIG_ALL_LIST.getCode());
		if (list == null) {
			list = afResourceDao.selectHomeConfigByAllTypes();
			bizCacheUtil.saveObjectList(CacheConstants.RESOURCE.RESOURCE_CONFIG_ALL_LIST.getCode(), list);
		}
		return list;
	}

	@Override
	public List<AfResourceDo> getConfigByTypes(String type) {
		List<AfResourceDo> list;
		HashMap<String, List<AfResourceDo>> data = (HashMap<String, List<AfResourceDo>>) bizCacheUtil.getObject(CacheConstants.RESOURCE.RESOURCE_CONFIG_TYPES_LIST.getCode());
		data = (data == null ? new HashMap<String, List<AfResourceDo>>() : data);
		if (data.get(type) == null) {
			list = afResourceDao.getConfigByTypes(type);
			data.put(type, list);
			bizCacheUtil.saveObjectForever(CacheConstants.RESOURCE.RESOURCE_CONFIG_TYPES_LIST.getCode(), data);
		} else {
			list = data.get(type);
		}
		return list;
	}

	@Override
	public List<AfResourceDo> getResourceListByType(String type) {
		List<AfResourceDo> list;
		HashMap<String, List<AfResourceDo>> data = (HashMap<String, List<AfResourceDo>>) bizCacheUtil.getObject(CacheConstants.RESOURCE.RESOURCE_TYPE_LIST.getCode());
		data = (data == null ? new HashMap<String, List<AfResourceDo>>() : data);
		if (data.get(type) == null) {
			list = afResourceDao.getResourceListByType(type);
			if (list != null) {
				data.put(type, list);
				bizCacheUtil.saveObjectForever(CacheConstants.RESOURCE.RESOURCE_TYPE_LIST.getCode(), data);
			}
		} else {
			list = data.get(type);
		}
		return list;
	}

	@Override
	public AfResourceDo getSingleResourceBytype(String type) {
		AfResourceDo afResourceDo;
		HashMap<String, AfResourceDo> data = (HashMap<String, AfResourceDo>) bizCacheUtil.getObject(CacheConstants.RESOURCE.RESOURCE_TYPE_DO.getCode());
		data = (data == null ? new HashMap<String, AfResourceDo>() : data);
		if (data.get(type) == null) {
			afResourceDo = afResourceDao.getSingleResourceBytype(type);
			data.put(type, afResourceDo);
			bizCacheUtil.saveObjectForever(CacheConstants.RESOURCE.RESOURCE_TYPE_DO.getCode(), data);
		} else {
			afResourceDo = data.get(type);
		}
		return afResourceDo;
	}

	@Override
	public AfResourceDo getConfigByTypesAndSecType(String type, String secType) {
		AfResourceDo afResourceDo;
		HashMap<String, AfResourceDo> data = (HashMap<String, AfResourceDo>) bizCacheUtil.getObject(CacheConstants.RESOURCE.RESOURCE_TYPE_SEC_DO.getCode());
		data = (data == null ? new HashMap<String, AfResourceDo>() : data);
		if (data.get(type + secType) == null) {
			afResourceDo = afResourceDao.getConfigByTypesAndSecType(type, secType);
			data.put(type + secType, afResourceDo);
			bizCacheUtil.saveObjectForever(CacheConstants.RESOURCE.RESOURCE_TYPE_SEC_DO.getCode(), data);
		} else {
			afResourceDo = data.get(type + secType);
		}
		return afResourceDo;
	}

	@Override
	public List<AfResourceDo> getResourceListByTypeOrderBy(String type) {
		List<AfResourceDo> list;
		HashMap<String, List<AfResourceDo>> data = (HashMap<String, List<AfResourceDo>>) bizCacheUtil.getObject(CacheConstants.RESOURCE.RESOURCE_TYPE_LIST_ORDER_BY.getCode());
		data = (data == null ? new HashMap<String, List<AfResourceDo>>() : data);
		if (data.get(type) == null) {
			list = afResourceDao.getResourceListByTypeOrderBy(type);
			data.put(type, list);
			bizCacheUtil.saveObjectForever(CacheConstants.RESOURCE.RESOURCE_TYPE_LIST_ORDER_BY.getCode(), data);
		} else {
			list = data.get(type);
		}
		return list;
	}

	@Override
	public AfResourceDo getResourceByResourceId(Long rid) {
		AfResourceDo afResourceDo;
		HashMap<String, AfResourceDo> data = (HashMap<String, AfResourceDo>) bizCacheUtil.getObject(CacheConstants.RESOURCE.RESOURCE_ID_DO.getCode());
		data = (data == null ? new HashMap<String, AfResourceDo>() : data);
		if (data.get(rid + "") == null) {
			afResourceDo = afResourceDao.getResourceByResourceId(rid);
			data.put(rid + "", afResourceDo);
			bizCacheUtil.saveObjectForever(CacheConstants.RESOURCE.RESOURCE_ID_DO.getCode(), data);
		} else {
			afResourceDo = data.get(rid + "");
		}
		return afResourceDo;
	}

	@Override
	public List<AfResourceDo> getOneToManyResourceOrderByBytype(String type) {
		List<AfResourceDo> list;
		HashMap<String, List<AfResourceDo>> data = (HashMap<String, List<AfResourceDo>>) bizCacheUtil.getObject(CacheConstants.RESOURCE.RESOURCE_ONE_TO_MANY_TYPE_LIST.getCode());
		data = (data == null ? new HashMap<String, List<AfResourceDo>>() : data);
		if (data.get(type) == null) {
			list = afResourceDao.getOneToManyResourceOrderByBytype(type);
			data.put(type, list);
			bizCacheUtil.saveObjectForever(CacheConstants.RESOURCE.RESOURCE_ONE_TO_MANY_TYPE_LIST.getCode(), data);
		} else {
			list = data.get(type);
		}
		return list;
	}

	@Override
	public List<AfResourceDo> getResourceHomeListByTypeOrderBy(String type) {
		List<AfResourceDo> list;
		HashMap<String, List<AfResourceDo>> data = (HashMap<String, List<AfResourceDo>>) bizCacheUtil.getObject(CacheConstants.RESOURCE.RESOURCE_HOME_LIST_ORDER_BY.getCode());
		data = (data == null ? new HashMap<String, List<AfResourceDo>>() : data);
		if (data.get(type) == null) {
			list = afResourceDao.getResourceHomeListByTypeOrderBy(type);
			data.put(type, list);
			bizCacheUtil.saveObjectForever(CacheConstants.RESOURCE.RESOURCE_HOME_LIST_ORDER_BY.getCode(), data);
		} else {
			list = data.get(type);
		}
		return list;
	}

	@Override
	public List<AfResourceDo> selectBorrowHomeConfigByAllTypes() {
//		List<AfResourceDo> list = bizCacheUtil.getObjectList(CacheConstants.RESOURCE.RESOURCE_BORROW_CONFIG_LIST.getCode());
//		if (list == null) {
//			list = afResourceDao.selectBorrowHomeConfigByAllTypes();
//			bizCacheUtil.saveObjectList(CacheConstants.RESOURCE.RESOURCE_BORROW_CONFIG_LIST.getCode(), list);
//		}
		return afResourceDao.selectBorrowHomeConfigByAllTypes();
	}

}
