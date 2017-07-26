package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.bo.BorrowRateBo;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.CacheConstants;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.util.CollectionConverterUtil;
import com.ald.fanbei.api.common.util.Converter;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.AfResourceDao;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sun.org.apache.bcel.internal.generic.RETURN;

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

	protected static Logger logger = LoggerFactory.getLogger(AfResourceServiceImpl.class);
	String types = Constants.RES_GAME_AWARD_OF_CATCH_DOLL + "," + Constants.RES_GAME_CATCH_DOLL_CLIENT_RATE + ","
			+ Constants.RES_GAME_AWARD_COUNT_LIMIT;

	@Resource
	AfResourceDao afResourceDao;
	@Resource
	BizCacheUtil bizCacheUtil;
	private static Map<String, List<AfResourceDo>> localResource = null;

	public List<AfResourceDo> getLocalByType(String type) {
		if (localResource == null) {
			logger.info("local conf reload again:types=" + types);
			List<AfResourceDo> list = afResourceDao.getConfigByTypeList(StringUtil.splitToList(types, ","));
			localResource = CollectionConverterUtil.convertToMapListFromList(list,
					new Converter<AfResourceDo, String>() {
						@Override
						public String convert(AfResourceDo source) {
							return source.getType();
						}
					});
		}
		List<AfResourceDo> result = localResource.get(type);
		if (result == null || result.size() == 0) {
			result = this.getConfigByTypes(type);
		}
		return result;
	}

	@Override
	public void cleanLocalCache() {
		localResource = null;
	}

	@Override
	public List<AfResourceDo> getHomeConfigByAllTypes() {
		List<AfResourceDo> list = bizCacheUtil
				.getObjectList(CacheConstants.RESOURCE.RESOURCE_CONFIG_ALL_LIST.getCode());
		if (list == null) {
			list = afResourceDao.selectHomeConfigByAllTypes();
			bizCacheUtil.saveObjectList(CacheConstants.RESOURCE.RESOURCE_CONFIG_ALL_LIST.getCode(), list);
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AfResourceDo> getConfigByTypes(String type) {
		List<AfResourceDo> list;
		HashMap<String, List<AfResourceDo>> data = (HashMap<String, List<AfResourceDo>>) bizCacheUtil
				.getObject(CacheConstants.RESOURCE.RESOURCE_CONFIG_TYPES_LIST.getCode());
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

	@SuppressWarnings("unchecked")
	@Override
	public List<AfResourceDo> getResourceListByType(String type) {
		List<AfResourceDo> list;
		HashMap<String, List<AfResourceDo>> data = (HashMap<String, List<AfResourceDo>>) bizCacheUtil
				.getObject(CacheConstants.RESOURCE.RESOURCE_TYPE_LIST.getCode());
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

	@SuppressWarnings("unchecked")
	@Override
	public AfResourceDo getSingleResourceBytype(String type) {
		AfResourceDo afResourceDo;
		HashMap<String, AfResourceDo> data = (HashMap<String, AfResourceDo>) bizCacheUtil
				.getObject(CacheConstants.RESOURCE.RESOURCE_TYPE_DO.getCode());
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

	@SuppressWarnings("unchecked")
	@Override
	public AfResourceDo getConfigByTypesAndSecType(String type, String secType) {
		AfResourceDo afResourceDo;
		HashMap<String, AfResourceDo> data = (HashMap<String, AfResourceDo>) bizCacheUtil
				.getObject(CacheConstants.RESOURCE.RESOURCE_TYPE_SEC_DO.getCode());
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

	@SuppressWarnings("unchecked")
	@Override
	public List<AfResourceDo> getResourceListByTypeOrderBy(String type) {
		List<AfResourceDo> list;
		HashMap<String, List<AfResourceDo>> data = (HashMap<String, List<AfResourceDo>>) bizCacheUtil
				.getObject(CacheConstants.RESOURCE.RESOURCE_TYPE_LIST_ORDER_BY.getCode());
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

	@SuppressWarnings("unchecked")
	@Override
	public AfResourceDo getResourceByResourceId(Long rid) {
		AfResourceDo afResourceDo;
		HashMap<String, AfResourceDo> data = (HashMap<String, AfResourceDo>) bizCacheUtil
				.getObject(CacheConstants.RESOURCE.RESOURCE_ID_DO.getCode());
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

	@SuppressWarnings("unchecked")
	@Override
	public List<AfResourceDo> getOneToManyResourceOrderByBytype(String type) {
		List<AfResourceDo> list;
		HashMap<String, List<AfResourceDo>> data = (HashMap<String, List<AfResourceDo>>) bizCacheUtil
				.getObject(CacheConstants.RESOURCE.RESOURCE_ONE_TO_MANY_TYPE_LIST.getCode());
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

	@SuppressWarnings("unchecked")
	@Override
	public List<AfResourceDo> getResourceHomeListByTypeOrderBy(String type) {
		List<AfResourceDo> list;
		HashMap<String, List<AfResourceDo>> data = (HashMap<String, List<AfResourceDo>>) bizCacheUtil
				.getObject(CacheConstants.RESOURCE.RESOURCE_HOME_LIST_ORDER_BY.getCode());
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
		List<AfResourceDo> list = bizCacheUtil
				.getObjectList(CacheConstants.RESOURCE.RESOURCE_BORROW_CONFIG_LIST.getCode());
		if (list == null) {
			list = afResourceDao.selectBorrowHomeConfigByAllTypes();
			bizCacheUtil.saveObjectList(CacheConstants.RESOURCE.RESOURCE_BORROW_CONFIG_LIST.getCode(), list);
		}
		return list;
	}

	@Override
	public BorrowRateBo borrowRateWithResource(Integer realTotalNper) {
		BorrowRateBo borrowRate = new BorrowRateBo();
		JSONObject borrowRateJson = getBorrowRateResource(realTotalNper);
		JSONObject borrowRateOverdueJson = getBorrowOverdueRateResource(realTotalNper);
		borrowRate.setNper(borrowRateJson.getInteger("nper"));
		borrowRate.setRate(borrowRateJson.getBigDecimal("rate"));
		borrowRate.setPoundageRate(borrowRateJson.getBigDecimal("poundageRate"));
		borrowRate.setRangeBegin(borrowRateJson.getBigDecimal("rangeBegin"));
		borrowRate.setRangeEnd(borrowRateJson.getBigDecimal("rangeEnd"));

		borrowRate.setOverdueRate(borrowRateOverdueJson.getBigDecimal("overdueRate"));
		borrowRate.setOverduePoundageRate(borrowRateOverdueJson.getBigDecimal("overduePoundageRate"));
		borrowRate.setOverdueRangeBegin(borrowRateOverdueJson.getBigDecimal("overdueRangeBegin"));
		borrowRate.setOverdueRangeEnd(borrowRateOverdueJson.getBigDecimal("overdueRangeEnd"));
		return borrowRate;
	}

	@Override
	public JSONObject borrowRateWithResourceOld(Integer realTotalNper) {
		// 获取借款分期配置信息
		AfResourceDo resource = (AfResourceDo) bizCacheUtil.getObject(Constants.CACHEKEY_BORROW_CONSUME);
		if (null == resource) {
			resource = afResourceDao.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE,
					Constants.RES_BORROW_CONSUME);
			bizCacheUtil.saveObject(Constants.CACHEKEY_BORROW_CONSUME, resource, Constants.SECOND_OF_HALF_HOUR);
		}
		BigDecimal rangeBegin = NumberUtil.objToBigDecimalDefault(Constants.DEFAULT_CHARGE_MIN, BigDecimal.ZERO);
		BigDecimal rangeEnd = NumberUtil.objToBigDecimalDefault(Constants.DEFAULT_CHARGE_MAX, BigDecimal.ZERO);
		String[] range = StringUtil.split(resource.getValue2(), ",");
		if (null != range && range.length == 2) {
			rangeBegin = NumberUtil.objToBigDecimalDefault(range[0], BigDecimal.ZERO);
			rangeEnd = NumberUtil.objToBigDecimalDefault(range[1], BigDecimal.ZERO);
		}
		JSONArray array = JSON.parseArray(resource.getValue());
		// 如果是重新生成的账单，需要原来账单的总期数
		JSONObject borrowRate = null;
		for (int i = 0; i < array.size(); i++) {
			JSONObject obj = array.getJSONObject(i);
			if (obj.getInteger(Constants.DEFAULT_NPER) == realTotalNper) {
				obj.put("rangeBegin", rangeBegin);
				obj.put("rangeEnd", rangeEnd);
				obj.put("poundageRate", new BigDecimal(resource.getValue1()));

				borrowRate = obj;
			}
		}

		return borrowRate;
	}

	private JSONObject getBorrowRateResource(Integer realTotalNper) {
		// 获取借款分期配置信息
		AfResourceDo resource = (AfResourceDo) bizCacheUtil.getObject(Constants.CACHEKEY_BORROW_CONSUME);
		if (null == resource) {
			resource = afResourceDao.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE,
					Constants.RES_BORROW_CONSUME);
			bizCacheUtil.saveObject(Constants.CACHEKEY_BORROW_CONSUME, resource, Constants.SECOND_OF_HALF_HOUR);
		}
		BigDecimal rangeBegin = NumberUtil.objToBigDecimalDefault(Constants.DEFAULT_CHARGE_MIN, BigDecimal.ZERO);
		BigDecimal rangeEnd = NumberUtil.objToBigDecimalDefault(Constants.DEFAULT_CHARGE_MAX, BigDecimal.ZERO);
		String[] range = StringUtil.split(resource.getValue2(), ",");
		if (null != range && range.length == 2) {
			rangeBegin = NumberUtil.objToBigDecimalDefault(range[0], BigDecimal.ZERO);
			rangeEnd = NumberUtil.objToBigDecimalDefault(range[1], BigDecimal.ZERO);
		}
		JSONArray array = JSON.parseArray(resource.getValue());
		// 如果是重新生成的账单，需要原来账单的总期数
		JSONObject borrowRate = new JSONObject();
		for (int i = 0; i < array.size(); i++) {
			JSONObject obj = array.getJSONObject(i);
			if (obj.getInteger(Constants.DEFAULT_NPER) == realTotalNper) {
				borrowRate.put("nper", realTotalNper);
				borrowRate.put("rate", obj.get(Constants.DEFAULT_RATE));
				borrowRate.put("rangeBegin", rangeBegin);
				borrowRate.put("rangeEnd", rangeEnd);
				borrowRate.put("poundageRate", new BigDecimal(resource.getValue1()));
				break;
			}
		}
		return borrowRate;
	}

	private JSONObject getBorrowOverdueRateResource(Integer realTotalNper) {
		// 获取借款分期逾期配置信息
		AfResourceDo resourceOverdue = (AfResourceDo) bizCacheUtil.getObject(Constants.CACHEKEY_BORROW_CONSUME_OVERDUE);
		if (null == resourceOverdue) {
			resourceOverdue = afResourceDao.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE,
					Constants.RES_BORROW_CONSUME_OVERDUE);
			bizCacheUtil.saveObject(Constants.CACHEKEY_BORROW_CONSUME_OVERDUE, resourceOverdue,
					Constants.SECOND_OF_HALF_HOUR);
		}
		BigDecimal rangeBegin = NumberUtil.objToBigDecimalDefault(Constants.DEFAULT_CHARGE_MIN, BigDecimal.ZERO);
		BigDecimal rangeEnd = NumberUtil.objToBigDecimalDefault(Constants.DEFAULT_CHARGE_MAX, BigDecimal.ZERO);
		String[] range = StringUtil.split(resourceOverdue.getValue2(), ",");
		if (null != range && range.length == 2) {
			rangeBegin = NumberUtil.objToBigDecimalDefault(range[0], BigDecimal.ZERO);
			rangeEnd = NumberUtil.objToBigDecimalDefault(range[1], BigDecimal.ZERO);
		}
		JSONArray array = JSON.parseArray(resourceOverdue.getValue());
		// 如果是重新生成的账单，需要原来账单的总期数
		JSONObject borrowRate = new JSONObject();
		for (int i = 0; i < array.size(); i++) {
			JSONObject obj = array.getJSONObject(i);
			if (obj.getInteger(Constants.DEFAULT_NPER) == realTotalNper) {
				borrowRate.put("overdueRate", obj.get(Constants.DEFAULT_RATE));
				borrowRate.put("overdueRangeBegin", rangeBegin);
				borrowRate.put("overdueRangeEnd", rangeEnd);
				borrowRate.put("overduePoundageRate", new BigDecimal(resourceOverdue.getValue1()));
				break;
			}
		}
		return borrowRate;
	}

	@Override
	public List<AfResourceDo> getHomeIndexListByOrderby(String type) {
		// TODO Auto-generated method stub
		return afResourceDao.getHomeIndexListByOrderby(type);
	}

	@Override
	public List<AfResourceDo> getResourceHomeListByTypeOrderByOnPreEnv(String type) {
		return afResourceDao.getResourceHomeListByTypeOrderByOnPreEnv(type);
	}

	@Override
	public List<AfResourceDo> selectActivityConfig() {
		return afResourceDao.selectActivityConfig();
	}

	@Override
	public BorrowRateBo borrowRateWithResourceForTrade(Integer realTotalNper) {
		BorrowRateBo borrowRate = new BorrowRateBo();
		JSONObject borrowRateJson = getBorrowRateResourceForTrade(realTotalNper);
		JSONObject borrowRateOverdueJson = getBorrowOverdueRateResource(realTotalNper);
		borrowRate.setNper(borrowRateJson.getInteger("nper"));
		borrowRate.setRate(borrowRateJson.getBigDecimal("rate"));
		borrowRate.setPoundageRate(borrowRateJson.getBigDecimal("poundageRate"));
		borrowRate.setRangeBegin(borrowRateJson.getBigDecimal("rangeBegin"));
		borrowRate.setRangeEnd(borrowRateJson.getBigDecimal("rangeEnd"));

		borrowRate.setOverdueRate(borrowRateOverdueJson.getBigDecimal("overdueRate"));
		borrowRate.setOverduePoundageRate(borrowRateOverdueJson.getBigDecimal("overduePoundageRate"));
		borrowRate.setOverdueRangeBegin(borrowRateOverdueJson.getBigDecimal("overdueRangeBegin"));
		borrowRate.setOverdueRangeEnd(borrowRateOverdueJson.getBigDecimal("overdueRangeEnd"));
		return borrowRate;
	}

	private JSONObject getBorrowRateResourceForTrade(Integer realTotalNper) {
		// 获取商圈借款分期配置信息
		AfResourceDo resource = afResourceDao.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE, Constants.RES_BORROW_TRADE);
		BigDecimal rangeBegin = NumberUtil.objToBigDecimalDefault(Constants.DEFAULT_CHARGE_MIN, BigDecimal.ZERO);
		BigDecimal rangeEnd = NumberUtil.objToBigDecimalDefault(Constants.DEFAULT_CHARGE_MAX, BigDecimal.ZERO);
		String[] range = StringUtil.split(resource.getValue2(), ",");
		if (null != range && range.length == 2) {
			rangeBegin = NumberUtil.objToBigDecimalDefault(range[0], BigDecimal.ZERO);
			rangeEnd = NumberUtil.objToBigDecimalDefault(range[1], BigDecimal.ZERO);
		}
		JSONArray array = JSON.parseArray(resource.getValue());
		JSONObject borrowRate = new JSONObject();
		for (int i = 0; i < array.size(); i++) {
			JSONObject obj = array.getJSONObject(i);
			if (obj.getInteger(Constants.DEFAULT_NPER).equals(realTotalNper)) {
				borrowRate.put("nper", realTotalNper);
				borrowRate.put("rate", obj.get(Constants.DEFAULT_RATE));
				borrowRate.put("rangeBegin", rangeBegin);
				borrowRate.put("rangeEnd", rangeEnd);
				borrowRate.put("poundageRate", new BigDecimal(resource.getValue1()));
				break;
			}
		}
		return borrowRate;
	}

}
