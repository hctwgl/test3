package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.CacheConstants;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.AfResourceDao;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 
 * @类描述：
 * 
 * @author Xiaotianjian 2017年1月20日上午10:27:48
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@SuppressWarnings("unchecked")
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
			bizCacheUtil.saveObject(CacheConstants.RESOURCE.RESOURCE_CONFIG_TYPES_LIST.getCode(), data);
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
				bizCacheUtil.saveObject(CacheConstants.RESOURCE.RESOURCE_TYPE_LIST.getCode(), data);
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
			bizCacheUtil.saveObject(CacheConstants.RESOURCE.RESOURCE_TYPE_DO.getCode(), data);
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
			bizCacheUtil.saveObject(CacheConstants.RESOURCE.RESOURCE_TYPE_SEC_DO.getCode(), data);
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
			bizCacheUtil.saveObject(CacheConstants.RESOURCE.RESOURCE_TYPE_LIST_ORDER_BY.getCode(), data);
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
			bizCacheUtil.saveObject(CacheConstants.RESOURCE.RESOURCE_ID_DO.getCode(), data);
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
			bizCacheUtil.saveObject(CacheConstants.RESOURCE.RESOURCE_ONE_TO_MANY_TYPE_LIST.getCode(), data);
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
			bizCacheUtil.saveObject(CacheConstants.RESOURCE.RESOURCE_HOME_LIST_ORDER_BY.getCode(), data);
		} else {
			list = data.get(type);
		}
		return list;
	}

	@Override
	public List<AfResourceDo> selectBorrowHomeConfigByAllTypes() {
		List<AfResourceDo> list = bizCacheUtil.getObjectList(CacheConstants.RESOURCE.RESOURCE_BORROW_CONFIG_LIST.getCode());
		if (list == null) {
			list = afResourceDao.selectBorrowHomeConfigByAllTypes();
			bizCacheUtil.saveObjectList(CacheConstants.RESOURCE.RESOURCE_BORROW_CONFIG_LIST.getCode(), list);
		}
		return list;
	}
	@Override
	public JSONObject borrowRateWithResource(Integer realTotalNper){
		//获取借款分期配置信息
		AfResourceDo resource = (AfResourceDo) bizCacheUtil.getObject(Constants.CACHEKEY_BORROW_CONSUME);
		if(null == resource){
			resource = afResourceDao.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE,Constants.RES_BORROW_CONSUME);
			bizCacheUtil.saveObject(Constants.CACHEKEY_BORROW_CONSUME, resource, Constants.SECOND_OF_HALF_HOUR);
		}
		BigDecimal rangeBegin = NumberUtil.objToBigDecimalDefault(Constants.DEFAULT_CHARGE_MIN, BigDecimal.ZERO);
		BigDecimal rangeEnd = NumberUtil.objToBigDecimalDefault(Constants.DEFAULT_CHARGE_MAX, BigDecimal.ZERO);
		String[] range = StringUtil.split(resource.getValue2(), ",");
		if(null != range && range.length==2){
			rangeBegin = NumberUtil.objToBigDecimalDefault(range[0], BigDecimal.ZERO);
			rangeEnd = NumberUtil.objToBigDecimalDefault(range[1], BigDecimal.ZERO);
		}
		JSONArray array = JSON.parseArray(resource.getValue());
		//如果是重新生成的账单，需要原来账单的总期数
		JSONObject borrowRate =null;
		for (int i = 0; i < array.size(); i++) {
			JSONObject obj = array.getJSONObject(i);
			if(obj.getInteger(Constants.DEFAULT_NPER)==realTotalNper){
				obj.put("rangeBegin", rangeBegin);
				obj.put("rangeEnd", rangeEnd);
				obj.put("poundageRate", new BigDecimal(resource.getValue1()));

				borrowRate = obj;
			}
		}
		
		return borrowRate;
	}

	@Override
	public List<AfResourceDo> getHomeIndexListByOrderby(String type) {
		// TODO Auto-generated method stub
		return afResourceDao.getHomeIndexListByOrderby(type);
	}
}
