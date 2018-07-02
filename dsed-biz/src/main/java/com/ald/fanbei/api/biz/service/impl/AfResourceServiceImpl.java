package com.ald.fanbei.api.biz.service.impl;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import com.ald.fanbei.api.common.enums.ResourceType;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.ald.fanbei.api.biz.bo.BorrowRateBo;
import com.ald.fanbei.api.biz.bo.thirdpay.ThirdBizType;
import com.ald.fanbei.api.biz.bo.thirdpay.ThirdPayBo;
import com.ald.fanbei.api.biz.bo.thirdpay.ThirdPayStatusEnum;
import com.ald.fanbei.api.biz.bo.thirdpay.ThirdPayTypeEnum;
import com.ald.fanbei.api.biz.service.AfGoodsService;
import com.ald.fanbei.api.biz.service.AfInterestFreeRulesService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.AfResourceSecType;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.util.CollectionConverterUtil;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.Converter;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.AfGoodsDao;
import com.ald.fanbei.api.dal.dao.AfResourceDao;
import com.ald.fanbei.api.dal.domain.AfGoodsDo;
import com.ald.fanbei.api.dal.domain.AfInterestReduceRulesDo;
import com.ald.fanbei.api.dal.domain.AfInterestReduceSchemeDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * @author Xiaotianjian 2017年1月20日上午10:27:48
 * @类描述：
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@SuppressWarnings("unchecked")
@Service("afResourceService")
public class AfResourceServiceImpl implements AfResourceService {

    protected static Logger logger = LoggerFactory.getLogger(AfResourceServiceImpl.class);
    String types = Constants.RES_GAME_AWARD_OF_CATCH_DOLL + "," + Constants.RES_GAME_CATCH_DOLL_CLIENT_RATE + ","
            + Constants.RES_GAME_AWARD_COUNT_LIMIT;
    @Resource
    BizCacheUtil bizCacheUtil;
    @Resource
    AfResourceDao afResourceDao;
    @Resource
    private AfGoodsService afGoodsService;
    @Resource
    private AfInterestFreeRulesService afInterestFreeRulesService;
    @Resource
    AfGoodsDao afGoodsDao;
    //	@Resource
//	BizCacheUtil bizCacheUtil;
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
//		List<AfResourceDo> list = bizCacheUtil
//				.getObjectList(CacheConstants.RESOURCE.RESOURCE_CONFIG_ALL_LIST.getCode());
//		if (list == null) {
//			list = afResourceDao.selectHomeConfigByAllTypes();
//			bizCacheUtil.saveObjectList(CacheConstants.RESOURCE.RESOURCE_CONFIG_ALL_LIST.getCode(), list);
//		}
        List<AfResourceDo> list = afResourceDao.selectHomeConfigByAllTypes();
        return list;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<AfResourceDo> getConfigByTypes(String type) {
        List<AfResourceDo> list;
//		HashMap<String, List<AfResourceDo>> data = (HashMap<String, List<AfResourceDo>>) bizCacheUtil
//				.getObject(CacheConstants.RESOURCE.RESOURCE_CONFIG_TYPES_LIST.getCode());
//		data = (data == null ? new HashMap<String, List<AfResourceDo>>() : data);
//		if (data.get(type) == null) {
        list = afResourceDao.getConfigByTypes(type);
//			data.put(type, list);
//			bizCacheUtil.saveObject(CacheConstants.RESOURCE.RESOURCE_CONFIG_TYPES_LIST.getCode(), data);
//		} else {
//			list = data.get(type);
//		}
        return list;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<AfResourceDo> getResourceListByType(String type) {
        List<AfResourceDo> list;
//		HashMap<String, List<AfResourceDo>> data = (HashMap<String, List<AfResourceDo>>) bizCacheUtil
//				.getObject(CacheConstants.RESOURCE.RESOURCE_TYPE_LIST.getCode());
//		data = (data == null ? new HashMap<String, List<AfResourceDo>>() : data);
//		if (data.get(type) == null) {
        list = afResourceDao.getResourceListByType(type);
//			if (list != null) {
//				data.put(type, list);
//				bizCacheUtil.saveObject(CacheConstants.RESOURCE.RESOURCE_TYPE_LIST.getCode(), data);
//			}
//		} else {
//			list = data.get(type);
//		}
        return list;
    }

    @SuppressWarnings("unchecked")
    @Override
    public AfResourceDo getSingleResourceBytype(String type) {
        AfResourceDo afResourceDo;
//		HashMap<String, AfResourceDo> data = (HashMap<String, AfResourceDo>) bizCacheUtil
//				.getObject(CacheConstants.RESOURCE.RESOURCE_TYPE_DO.getCode());
//		data = (data == null ? new HashMap<String, AfResourceDo>() : data);
//		if (data.get(type) == null) {
        afResourceDo = afResourceDao.getSingleResourceBytype(type);
//			data.put(type, afResourceDo);
//			bizCacheUtil.saveObject(CacheConstants.RESOURCE.RESOURCE_TYPE_DO.getCode(), data);
//		} else {
//			afResourceDo = data.get(type);
//		}
        return afResourceDo;
    }

    @SuppressWarnings("unchecked")
    @Override
    public AfResourceDo getConfigByTypesAndSecType(String type, String secType) {
        AfResourceDo afResourceDo;
//		HashMap<String, AfResourceDo> data = (HashMap<String, AfResourceDo>) bizCacheUtil
//				.getObject(CacheConstants.RESOURCE.RESOURCE_TYPE_SEC_DO.getCode());
//		data = (data == null ? new HashMap<String, AfResourceDo>() : data);
//		if (data.get(type + secType) == null) {
        afResourceDo = afResourceDao.getConfigByTypesAndSecType(type, secType);
//			data.put(type + secType, afResourceDo);
//			bizCacheUtil.saveObject(CacheConstants.RESOURCE.RESOURCE_TYPE_SEC_DO.getCode(), data);
//		} else {
//			afResourceDo = data.get(type + secType);
//		}
        return afResourceDo;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<AfResourceDo> getResourceListByTypeOrderBy(String type) {
        List<AfResourceDo> list;
//		HashMap<String, List<AfResourceDo>> data = (HashMap<String, List<AfResourceDo>>) bizCacheUtil
//				.getObject(CacheConstants.RESOURCE.RESOURCE_TYPE_LIST_ORDER_BY.getCode());
//		data = (data == null ? new HashMap<String, List<AfResourceDo>>() : data);
//		if (data.get(type) == null) {
        list = afResourceDao.getResourceListByTypeOrderBy(type);
//			data.put(type, list);
//			bizCacheUtil.saveObject(CacheConstants.RESOURCE.RESOURCE_TYPE_LIST_ORDER_BY.getCode(), data);
//		} else {
//			list = data.get(type);
//		}
        return list;
    }

    @SuppressWarnings("unchecked")
    @Override
    public AfResourceDo getResourceByResourceId(Long rid) {
        AfResourceDo afResourceDo;
//		HashMap<String, AfResourceDo> data = (HashMap<String, AfResourceDo>) bizCacheUtil
//				.getObject(CacheConstants.RESOURCE.RESOURCE_ID_DO.getCode());
//		data = (data == null ? new HashMap<String, AfResourceDo>() : data);
//		if (data.get(rid + "") == null) {
        afResourceDo = afResourceDao.getResourceByResourceId(rid);
//			data.put(rid + "", afResourceDo);
//			bizCacheUtil.saveObject(CacheConstants.RESOURCE.RESOURCE_ID_DO.getCode(), data);
//		} else {
//			afResourceDo = data.get(rid + "");
//		}
        return afResourceDo;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<AfResourceDo> getOneToManyResourceOrderByBytype(String type) {
        List<AfResourceDo> list;
//		HashMap<String, List<AfResourceDo>> data = (HashMap<String, List<AfResourceDo>>) bizCacheUtil
//				.getObject(CacheConstants.RESOURCE.RESOURCE_ONE_TO_MANY_TYPE_LIST.getCode());
//		data = (data == null ? new HashMap<String, List<AfResourceDo>>() : data);
//		if (data.get(type) == null) {
        list = afResourceDao.getOneToManyResourceOrderByBytype(type);
//			data.put(type, list);
//			bizCacheUtil.saveObject(CacheConstants.RESOURCE.RESOURCE_ONE_TO_MANY_TYPE_LIST.getCode(), data);
//		} else {
//			list = data.get(type);
//		}
        return list;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<AfResourceDo> getResourceHomeListByTypeOrderBy(String type) {
        List<AfResourceDo> list;
//		HashMap<String, List<AfResourceDo>> data = (HashMap<String, List<AfResourceDo>>) bizCacheUtil
//				.getObject(CacheConstants.RESOURCE.RESOURCE_HOME_LIST_ORDER_BY.getCode());
//		data = (data == null ? new HashMap<String, List<AfResourceDo>>() : data);
//		if (data.get(type) == null) {
        list = afResourceDao.getResourceHomeListByTypeOrderBy(type);
//			data.put(type, list);
//			bizCacheUtil.saveObject(CacheConstants.RESOURCE.RESOURCE_HOME_LIST_ORDER_BY.getCode(), data);
//		} else {
//			list = data.get(type);
//		}
        return list;
    }

    @Override
    public List<AfResourceDo> selectBorrowHomeConfigByAllTypes() {
//		List<AfResourceDo> list = bizCacheUtil
//				.getObjectList(CacheConstants.RESOURCE.RESOURCE_BORROW_CONFIG_LIST.getCode());
//		if (list == null) {
        List<AfResourceDo> list = afResourceDao.selectBorrowHomeConfigByAllTypes();
//			bizCacheUtil.saveObjectList(CacheConstants.RESOURCE.RESOURCE_BORROW_CONFIG_LIST.getCode(), list);
//		}
        return list;
    }

    @Override
    public List<AfResourceDo> newSelectBorrowHomeConfigByAllTypes() {
        List<AfResourceDo> list = afResourceDao.newSelectBorrowHomeConfigByAllTypes();
        return list;
    }

    @Override
    public BorrowRateBo borrowRateWithResource(Integer realTotalNper, String userName,Long goodId) {
        BorrowRateBo borrowRate = new BorrowRateBo();
        JSONObject borrowRateJson = getBorrowRateResource(realTotalNper, userName, goodId);
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
    public BorrowRateBo borrowRateWithResourceCredit(Integer realTotalNper) {
        BorrowRateBo borrowRate = new BorrowRateBo();
        JSONObject borrowRateOverdueJson = getBorrowOverdueRateResource(realTotalNper);
        borrowRate.setNper(1);
        borrowRate.setRate(BigDecimal.ZERO);
        borrowRate.setPoundageRate(BigDecimal.ZERO);
        borrowRate.setRangeBegin(BigDecimal.ZERO);
        borrowRate.setRangeEnd(BigDecimal.ZERO);

        borrowRate.setOverdueRate(borrowRateOverdueJson.getBigDecimal("overdueRate"));
        borrowRate.setOverduePoundageRate(borrowRateOverdueJson.getBigDecimal("overduePoundageRate"));
        borrowRate.setOverdueRangeBegin(borrowRateOverdueJson.getBigDecimal("overdueRangeBegin"));
        borrowRate.setOverdueRangeEnd(borrowRateOverdueJson.getBigDecimal("overdueRangeEnd"));
        return borrowRate;
    }


    @Override
    public JSONObject borrowRateWithResourceOld(Integer realTotalNper) {
        // 获取借款分期配置信息
//		AfResourceDo resource = (AfResourceDo) bizCacheUtil.getObject(Constants.CACHEKEY_BORROW_CONSUME);
//		if (null == resource) {
        AfResourceDo resource = afResourceDao.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE,
                Constants.RES_BORROW_CONSUME);
//			bizCacheUtil.saveObject(Constants.CACHEKEY_BORROW_CONSUME, resource, Constants.SECOND_OF_HALF_HOUR);
//		}
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

    private JSONObject getBorrowRateResource(Integer realTotalNper, String userName,Long goodId) {
        // 获取借款分期配置信息
        AfResourceDo resource = getVipUserRate(userName);
        if (null == resource) {
            resource = afResourceDao.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE,
                    Constants.RES_BORROW_CONSUME);
        }
        BigDecimal rangeBegin = NumberUtil.objToBigDecimalDefault(Constants.DEFAULT_CHARGE_MIN, BigDecimal.ZERO);
        BigDecimal rangeEnd = NumberUtil.objToBigDecimalDefault(Constants.DEFAULT_CHARGE_MAX, BigDecimal.ZERO);
        String[] range = StringUtil.split(resource.getValue2(), ",");
        if (null != range && range.length == 2) {
            rangeBegin = NumberUtil.objToBigDecimalDefault(range[0], BigDecimal.ZERO);
            rangeEnd = NumberUtil.objToBigDecimalDefault(range[1], BigDecimal.ZERO);
        }
        JSONArray array = JSON.parseArray(resource.getValue());
        array = checkNper(goodId,"1",array);
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
//		AfResourceDo resourceOverdue = (AfResourceDo) bizCacheUtil.getObject(Constants.CACHEKEY_BORROW_CONSUME_OVERDUE);
//		if (null == resourceOverdue) {
        AfResourceDo resourceOverdue = afResourceDao.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE,
                Constants.RES_BORROW_CONSUME_OVERDUE);
//			bizCacheUtil.saveObject(Constants.CACHEKEY_BORROW_CONSUME_OVERDUE, resourceOverdue,
//					Constants.SECOND_OF_HALF_HOUR);
//		}
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
    public  JSONArray  checkNper(Long goodsid,String method,JSONArray array){
        if (goodsid != null && goodsid >0l){
            AfGoodsDo goods = afGoodsDao.getGoodsById(goodsid);
            AfInterestReduceSchemeDo afInterestReduceSchemeDo = afInterestFreeRulesService.getReduceSchemeByGoodId(goods.getRid(),goods.getBrandId(),goods.getCategoryId());
            JSONArray temparray = new JSONArray();
            boolean flag = false;
            if (afInterestReduceSchemeDo != null){
                AfInterestReduceRulesDo afInterestReduceRulesDo =  afInterestFreeRulesService.getReduceRuleById(afInterestReduceSchemeDo.getInterestReduceId());
                if (afInterestReduceRulesDo != null){

                    temparray= getreducenpers(afInterestReduceRulesDo);
                    flag = true;
                }
            }
            /*AfResourceDo resource1 = afResourceService.getBrandRate(goodsid);//资源配置中的品牌利率*/
            //if(resource1!=null){

            if ("1".equals(method)){
                Set<String> set = new HashSet<>();

                for (Object temp1:array){
                    JSONObject tempobj1 = (JSONObject)temp1;
                    set.add(tempobj1.getString("nper"));
                }
                if (flag){
                    JSONArray arr = new JSONArray();
                    for (Object temp:temparray){
                        JSONObject tempobj = (JSONObject)temp;
                        String nper = tempobj.getString("nper");
                        if (set.contains(nper)){
                            arr.add(tempobj);
                        }
                    }
                    array = arr;
                }

            }else{
                if (flag)
                    array = temparray;
            }

            //  }
        /*    afInterestReduceGoodsService = (AfInterestReduceGoodsService)SpringBeanContextUtil.getBean("afInterestReduceGoodsService");

            	JSONArray newArray = afInterestReduceGoodsService.checkIfReduce(goodsid);
                if (newArray != null) {
                	array = newArray;

			}*/


        }
        return array;
    }
    private  JSONArray getreducenpers(AfInterestReduceRulesDo afInterestReduceRulesDo){
        JSONArray array = new JSONArray();
        BigDecimal nper1 = afInterestReduceRulesDo.getNper1();
        BigDecimal nper2 = afInterestReduceRulesDo.getNper2();
        BigDecimal nper3 = afInterestReduceRulesDo.getNper3();
        BigDecimal nper6 = afInterestReduceRulesDo.getNper6();
        BigDecimal nper9 = afInterestReduceRulesDo.getNper9();
        BigDecimal nper12 = afInterestReduceRulesDo.getNper12();
        JSONObject temp1 = new JSONObject();
        temp1.put("rate",nper1);
        temp1.put(Constants.DEFAULT_NPER,1);
        JSONObject temp2 = new JSONObject();
        temp2.put("rate",nper2);
        temp2.put(Constants.DEFAULT_NPER,2);
        JSONObject temp3 = new JSONObject();
        temp3.put("rate",nper3);
        temp3.put(Constants.DEFAULT_NPER,3);
        JSONObject temp6 = new JSONObject();
        temp6.put("rate",nper6);
        temp6.put(Constants.DEFAULT_NPER,6);
        JSONObject temp9 = new JSONObject();
        temp9.put("rate",nper9);
        temp9.put(Constants.DEFAULT_NPER,9);
        JSONObject temp12 = new JSONObject();
        temp12.put("rate",nper12);
        temp12.put(Constants.DEFAULT_NPER,12);
        array.add(temp1);
        array.add(temp2);
        array.add(temp3);
        array.add(temp6);
        array.add(temp9);
        array.add(temp12);

        return array;
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

    @Override
    public AfResourceDo getScrollbarByType() {
        AfResourceDo resourceDo = new AfResourceDo();
        List<AfResourceDo> list = afResourceDao.getScrollbarByType();
        if (list != null && list.size() > 0) {
            resourceDo = list.get(0);
        }
        return resourceDo;

    }

    @Override
    public List<AfResourceDo> getOneToManyResourceOrderByBytypeOnPreEnv(String code) {
        // TODO Auto-generated method stub
        return afResourceDao.getOneToManyResourceOrderByBytypeOnPreEnv(code);
    }

    //	public List<AfResourceDo> getHomeIndexListByOrderbyOnPreEnv(String code) {
//	    // TODO Auto-generated method stub
//	    return afResourceDao.getHomeIndexListByOrderbyOnPreEnv(code);
//	}
    public AfResourceDo getFakePersonByActivityId(String string) {
        AfResourceDo resourceDo = new AfResourceDo();
        List<AfResourceDo> list = afResourceDao.getFakePersonByActivityId(string);
        if (list != null && list.size() > 0) {
            resourceDo = list.get(0);
        }
        return resourceDo;

    }

    @Override
    public AfResourceDo getGGSpecificBanner(String value2) {
        return afResourceDao.getGGSpecificBanner(value2);
    }

    public List<AfResourceDo> getCarouselToManyResourceOrderByType(String type) {
        List<AfResourceDo> list;
        /*HashMap<String, List<AfResourceDo>> data = (HashMap<String, List<AfResourceDo>>) bizCacheUtil
				.getObject(CacheConstants.RESOURCE.HOME_CAROUSEL_TO_MANY_TYPE_LIST.getCode());
		data = (data == null ? new HashMap<String, List<AfResourceDo>>() : data);
		if (data.get(type) == null) {*/
        list = afResourceDao.getCarouselToManyResourceOrderByType(type);
			/*data.put(type, list);
			bizCacheUtil.saveObject(CacheConstants.RESOURCE.HOME_CAROUSEL_TO_MANY_TYPE_LIST.getCode(), data);
		} else {
			list = data.get(type);
		}*/
        return list;
    }

    public List<AfResourceDo> getManyPricutresResourceDoList(String type) {
        List<AfResourceDo> list;

        list = afResourceDao.getManyPricutresResourceDoList(type);

        return list;
    }

    @Override
    public List<AfResourceDo> getConfigsByTypesAndSecType(String type, String secType) {
        // TODO Auto-generated method stub
        return afResourceDao.getConfigsByTypesAndSecType(type, secType);
    }

    public AfResourceDo getAfResourceAppVesion() {
        AfResourceDo afResourceDo = (AfResourceDo) bizCacheUtil.getObject("check_app_version");
        if (afResourceDo != null) return afResourceDo;
        List<AfResourceDo> list = afResourceDao.getResourceListByType("check_app_version");
        if (list != null && list.size() > 0) {
            afResourceDo = list.get(0);
            bizCacheUtil.saveObject("check_app_version", afResourceDo);
        }
        return afResourceDo;
    }

    public String getAfResourceAppVesionV1() {
        String afResource = (String) bizCacheUtil.getObject("check_app_versionV1");
        if(afResource == null){
            List<AfResourceDo> list = afResourceDao.getResourceListByType("check_app_versionV1");
            if (list != null && list.size() > 0) {
                AfResourceDo afResourceDo = list.get(0);
                afResource = afResourceDo.getValue();
                bizCacheUtil.saveObjectForever("check_app_versionV1", afResource);
            }
        }
        return afResource;
    }

	/**
	 * 获取第三方支付通道
	 * @param thirdPayTypeEnum
	 * @return
	 */
	public ThirdPayBo getThirdPayBo(ThirdPayTypeEnum thirdPayTypeEnum) {
		List<AfResourceDo> list = afResourceDao.getResourceListByType("THIRD_PAY_SELECTED");
		if(list == null || list.size() == 0){
			return null;
		}
		List<ThirdPayBo> thirdPayList =JSON.parseArray(list.get(0).getValue(),ThirdPayBo.class);
		ThirdPayBo _thirdPayBo = null;
		for (ThirdPayBo thirdPayBo :thirdPayList){
			if(thirdPayBo.getStatus() == ThirdPayStatusEnum.OPEN.getStatus() && thirdPayBo.getPayType().equals(thirdPayTypeEnum.getName())){
				_thirdPayBo = thirdPayBo;
				break;
			}
		}
		return _thirdPayBo;
	}

	/**
	 * 判断是否能支付
	 * @param thirdPayTypeEnum
	 * @return
	 */
	public boolean checkThirdPayByType(ThirdBizType thirdBizType,ThirdPayTypeEnum thirdPayTypeEnum) {
		boolean ret = false;
		List<AfResourceDo> list = afResourceDao.getResourceListByType("THIRD_PAY_CONTROL");
		if(list == null || list.size() == 0){
			return ret;
		}

		for (AfResourceDo bizControl :list){
			if(bizControl.getSecType().equals(thirdBizType.name())){
				if(thirdPayTypeEnum.getName().equals(ThirdPayTypeEnum.WXPAY.getName()) ){
					if(bizControl.getValue().equals("1")){
						ret =true;
						break;
					}
				}
				else{
					if(bizControl.getValue1().equals("1")){
						ret =true;
						break;
					}
				}
				break;
			}
		}
		return ret;
	}
    @Override
    public List<AfResourceDo> getBackGroundByType(String code) {
        return afResourceDao.getBackGroundByType(code);
    }

    @Override
    public List<AfResourceDo> getNavigationUpOneResourceDoList(String code) {
        return afResourceDao.getNavigationUpOneResourceDoList(code);
    }

    @Override
    public List<AfResourceDo> getNavigationDownTwoResourceDoList(String code) {
        return afResourceDao.getNavigationUpOneResourceDoList(code);
    }

    @Override
    public AfResourceDo getOpenBoluomeCouponById(long rid) {
        // TODO Auto-generated method stub
        AfResourceDo afResourceDo;
        afResourceDo = afResourceDao.getOpenBoluomeCouponById(rid);

        return afResourceDo;
    }

    @Override
    public AfResourceDo getLaunchImageInfoByTypeAndVersion(String resourceType, String appVersion) {
        return afResourceDao.getLaunchImageInfoByTypeAndVersion(resourceType, appVersion);
    }

    @Override
    public AfResourceDo getLaunchImageInfoByType(String resourceType) {
        // TODO Auto-generated method stub
        return afResourceDao.getLaunchImageInfoByType(resourceType);
    }

    @Override
    public List<AfResourceDo> getScrollbarListByType(String type) {
        return afResourceDao.getScrollbarListByType(type);
    }


   @Override
	public List<AfResourceDo> getEcommercePositionUpRescoure() {
		return afResourceDao.getEcommercePositionUpRescoure();
	}

	@Override
	public List<AfResourceDo> getEcommercePositionDownRescoure() {
		return afResourceDao.getEcommercePositionDownRescoure();
	}
	/**
     * 获取vip用户专有利率
     *
     * @param userName 用户名
     * @return 利率相关详情
     */
    @Override
    public AfResourceDo getVipUserRate(String userName) {
        if(StringUtil.isEmpty(userName)){
            return null;
        }
        AfResourceDo resource = afResourceDao.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE, Constants.RES_BORROW_CONSUME_VIP);
        if (resource == null) return null;
        String userStr = resource.getValue3();
        if (userStr.indexOf(userName) != -1) {
            return resource;
        }
        return null;
    }
    /**
     * 获取品牌专有利率
     *
     * @param goodsId 用户名
     * @return 利率相关详情
     */
    @Override
    public AfResourceDo getBrandRate(long goodsId) {
        if(goodsId<=0l){
            return null;
        }
        AfGoodsDo goods = afGoodsService.getGoodsById(goodsId);
        if (goods == null){
            return null;
        }
        AfResourceDo resource = afResourceDao.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE, Constants.RES_BORROW_CONSUME_VIP+goods.getBrandId());

        return resource;
    }
    @Override
    public boolean getBorrowCashCLosed() {

        AfResourceDo resource = afResourceDao.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE, AfResourceSecType.borrowCashSupuerSwitch.getCode());
        if (resource != null && "N".equals(resource.getValue1())){
            return true;
        }
        return false;
    }
    /**
     * 获取黑名单
     *
     * @return 风控黑名单相关详情
     */
    @Override
    public boolean getBlackList() {

        try{
            AfResourceDo resource = afResourceDao.getSingleResourceBytype(Constants.DEVICE_UUID_BLACK);
            if (resource !=null && resource.getValue3() != null){
                ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder
                        .getRequestAttributes();
                String uuid = "";
                if (requestAttributes != null) {
                    String id = requestAttributes.getRequest().getHeader(Constants.REQ_SYS_NODE_ID);
                    String array[] = id == null ? null : id.split("_");
                    uuid = array == null || array.length < 2 ? "" : array[1];
                }
                if (uuid != null && !"".equals(uuid)){
                    if (resource.getValue3().contains(uuid)){
                        return true;
                    }
                }
            }

        }catch (Exception e){
            logger.info("获取黑名单 失败{}",e);
        }

        return false;
    }
	@Override
	public AfResourceDo getEcommerceFloorImgRes() {
		return afResourceDao.getEcommerceFloorImgRes();
	}

	@Override
	public AfResourceDo getBrandFloorImgRes() {
		return afResourceDao.getBrandFloorImgRes();
	}

	@Override
	public List<AfResourceDo> getHomeNomalPositionList() {
		return afResourceDao.getHomeNomalPositionList();
	}

	@Override
	public List<AfResourceDo> getHomeBrandPositonInfoList() {
		return afResourceDao.getHomeBrandPositonInfoList();
	}

	@Override
	public AfResourceDo getFinancialEntranceInfo() {
		return afResourceDao.getFinancialEntranceInfo();
	}

    public AfResourceDo getWechatConfig() {
	// 获取配置信息
	String resourceType = "ACCESSTOKEN";
	String resourceSecType = "WX";
	String type = ConfigProperties.get(Constants.CONFKEY_INVELOMENT_TYPE);
	if (Constants.INVELOMENT_TYPE_PRE_ENV.equals(type)) {
	    resourceType = "ACCESSTOKEN_PRE";
	    resourceSecType = "WX_PRE";
	}
	return afResourceDao.getConfigByTypesAndSecType(resourceType, resourceSecType);
    }

	@Override
	public AfResourceDo getConfigByTypesAndValue(String type, String value) {
		return afResourceDao.getConfigByTypesAndValue(type,value);
	}

	@Override
	public int editResource(AfResourceDo assetPushResource) {
		return afResourceDao.editResource(assetPushResource);
	}
	public BorrowLegalCfgBean getBorrowLegalCfgInfo() {
		List<AfResourceDo> borrowHomeConfigList = this.newSelectBorrowHomeConfigByAllTypes();
		BorrowLegalCfgBean cfgBean = new BorrowLegalCfgBean();

		for (AfResourceDo afResourceDo : borrowHomeConfigList) {
			String secType = afResourceDo.getSecType();

			String v = afResourceDo.getValue();
			if (StringUtils.equals(afResourceDo.getType(), AfResourceType.borrowRate.getCode())) {
				if (StringUtils.equals(secType, AfResourceSecType.BorrowCashBaseBankDouble.getCode())) {
					cfgBean.bankDouble = new BigDecimal(v);
				} else if (StringUtils.equals(secType, AfResourceSecType.BorrowCashPoundage.getCode())) {
					cfgBean.poundage = new BigDecimal(v);
				} else if (StringUtils.equals(secType, AfResourceSecType.BorrowCashOverduePoundage.getCode())) {
					cfgBean.overduePoundage = new BigDecimal(v);
				} else if (StringUtils.equals(secType, AfResourceSecType.BaseBankRate.getCode())) {
					cfgBean.bankRate = new BigDecimal(v);
				} else if (StringUtils.equals(secType, AfResourceSecType.borrowCashSupuerSwitch.getCode())) {
					cfgBean.supuerSwitch = v;
				} else if (StringUtils.equals(secType, AfResourceSecType.borrowCashLenderForCash.getCode())) {
					cfgBean.lender = v;
				} else if (StringUtils.equals(secType, AfResourceSecType.borrowCashTotalAmount.getCode())) {
					cfgBean.amountPerDay = new BigDecimal(v);
				} else if (StringUtils.equals(secType, AfResourceSecType.borrowCashShowNum.getCode())) {
					cfgBean.showNums = Integer.valueOf(v);
				}else if (StringUtils.equals(secType, AfResourceSecType.BORROW_RECYCLE_INFO_LEGAL_NEW.getCode())) {
					cfgBean.borrowCashDay = afResourceDo.getTypeDesc();
					cfgBean.maxAmount = new BigDecimal(afResourceDo.getValue1());
					cfgBean.minAmount = new BigDecimal(afResourceDo.getValue4());
				}
			}
		}

		return cfgBean;
	}
	public static class BorrowLegalCfgBean {
		public BigDecimal maxAmount;
		public BigDecimal minAmount;
		public BigDecimal bankDouble;
		public BigDecimal poundage;
		public BigDecimal overduePoundage;
		public BigDecimal bankRate;
		public String   supuerSwitch;
		public String lender;
		public BigDecimal amountPerDay;
		public Integer showNums;
		public String borrowCashDay;
	}

	public List<Object> extractBannerCfgInfo(List<AfResourceDo> bannerResclist) {
		List<Object> bannerList = new ArrayList<Object>();
		for (AfResourceDo afResourceDo : bannerResclist) {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("imageUrl", afResourceDo.getValue());
			data.put("titleName", afResourceDo.getName());
			data.put("type", afResourceDo.getValue1());
			data.put("content", afResourceDo.getValue2());
			data.put("sort", afResourceDo.getSort());

			bannerList.add(data);
		}
		return bannerList;
	}

	@Override
	public List<Object> getLoanHomeListByType(){
        String type = "BORROW_CASH_BANNER_DOWN";
        String envType = ConfigProperties.get(Constants.CONFKEY_INVELOMENT_TYPE);
        List<Object> list = new ArrayList<Object>();
        if (Constants.INVELOMENT_TYPE_ONLINE.equals(envType) || Constants.INVELOMENT_TYPE_TEST.equals(envType)) {
            list = extractBannerCfgInfo(afResourceDao.getResourceHomeListByTypeOrderBy(type));
        }else if (Constants.INVELOMENT_TYPE_PRE_ENV.equals(envType)) {
            list = extractBannerCfgInfo(afResourceDao.getResourceHomeListByTypeOrderByOnPreEnv(type));
        }
        return list;
    }

    @Override
    public List<Object> getBorrowRecycleHomeListByType() {
        String type = "BORROW_RECYCLE_HOME";
        String envType = ConfigProperties.get(Constants.CONFKEY_INVELOMENT_TYPE);
        List<Object> list = new ArrayList<Object>();
        if (Constants.INVELOMENT_TYPE_ONLINE.equals(envType) || Constants.INVELOMENT_TYPE_TEST.equals(envType)) {
            list = extractBannerCfgInfo(afResourceDao.getResourceHomeListByTypeOrderBy(type));
        }else if (Constants.INVELOMENT_TYPE_PRE_ENV.equals(envType)) {
            list = extractBannerCfgInfo(afResourceDao.getResourceHomeListByTypeOrderByOnPreEnv(type));
        }
        return list;
    }

    @Override
    public List<Object> getBorrowFinanceHomeListByType() {
        String type = "BORROW_FINANCE_HOME";
        String envType = ConfigProperties.get(Constants.CONFKEY_INVELOMENT_TYPE);
        List<Object> list = new ArrayList<Object>();
        if (Constants.INVELOMENT_TYPE_ONLINE.equals(envType) || Constants.INVELOMENT_TYPE_TEST.equals(envType)) {
            list = extractBannerCfgInfo(afResourceDao.getResourceHomeListByTypeOrderBy(type));
        }else if (Constants.INVELOMENT_TYPE_PRE_ENV.equals(envType)) {
            list = extractBannerCfgInfo(afResourceDao.getResourceHomeListByTypeOrderByOnPreEnv(type));
        }
        return list;
    }

    @Override
	public List<AfResourceDo> getFlowFlayerResourceConfig(String resourceType, String secType) {

		return afResourceDao.getFlowFlayerResourceConfig(resourceType,secType);
	}
	
	public List<AfResourceDo> getConfigsListByTypesAndSecType(String type, String secType) {
	    // TODO Auto-generated method stub
	    return afResourceDao.getConfigsListByTypesAndSecType(type,secType);
	}	


    /**
     * 获取新的专场信息(未出账单列表页|已出账单列表页)
     * @param type
     * @return
     */
	@Override
   public List<AfResourceDo> getNewSpecialResource(String type){
        return afResourceDao.getNewSpecialResource(type);
    }

    @Override
    public String getCashProductName() {
	AfResourceDo afResourceDo = afResourceDao.getSingleResourceBytype("CASH_PRODUCT_NAME");
	if(afResourceDo!=null)
	{
	    return afResourceDo.getValue();
	}
	else {
	    return "网上购物商品";
	}
    }

	@Override
	public int getIsShow(Long goodsId) {
		int result = 0 ;
		AfResourceDo resourceDo = afResourceDao.getConfigByTypesAndSecType("GOODS_IS_SHOW", "SHOW_BRAND_LIST");
		AfGoodsDo goodsDo = afGoodsService.getGoodsById(goodsId);
		
		if (resourceDo != null && goodsDo != null && goodsDo.getCategoryId() != null) {
			String brandListStr = resourceDo.getValue1();
			if (StringUtil.isNotBlank(brandListStr)) {
				String[] brandArray = brandListStr.split(";");
				for(String string : brandArray){
					if (string.equals(goodsDo.getCategoryId().toString())) {
						return 1;
					}
				}
			}
		}
		return result;
	}

	@Override
	public List<AfResourceDo> getGridViewInfoUpList() {
		// TODO Auto-generated method stub
		return afResourceDao.getGridViewInfoUpList();
	}

	@Override
	public List<AfResourceDo> getGridViewInfoCenterList() {
		// TODO Auto-generated method stub
		return afResourceDao.getGridViewInfoCenterList();
	}

	@Override
	public List<AfResourceDo> getGridViewInfoDownList() {
		// TODO Auto-generated method stub
		return afResourceDao.getGridViewInfoDownList();
	}

//	@Override
//	public AfResourceDo getEcommerceFloor() {
//		// TODO Auto-generated method stub
//		return afResourceDao.getEcommerceFloor();
//	}

	@Override
	public List<AfResourceDo> getEcommercePositionUp() {
		// TODO Auto-generated method stub
		return afResourceDao.getEcommercePositionUp();
	}

	@Override
	public List<AfResourceDo> getEcommercePositionDown() {
		// TODO Auto-generated method stub
		return afResourceDao.getEcommercePositionDown();
	}

	@Override
	public List<AfResourceDo> getBackGroundByTypeOrder(String code) {
		// TODO Auto-generated method stub
		return afResourceDao.getBackGroundByTypeOrder(code);
	}

	@Override
	public List<AfResourceDo> getBackGroundByTypeAndStatusOrder(String code) {
		// TODO Auto-generated method stub
		return afResourceDao.getBackGroundByTypeAndStatusOrder(code);
	}

	@Override
	public List<String> getBorrowCashWhiteList() {
		List<String> whiteIdsList = new ArrayList<String>();
		AfResourceDo whiteListInfo = afResourceDao.getSingleResourceBytype(Constants.APPLY_BRROW_CASH_WHITE_LIST);
		if (whiteListInfo != null) {
			whiteIdsList = CollectionConverterUtil.convertToListFromArray(whiteListInfo.getValue3().split(","),
				new Converter<String, String>() {
					@Override
					public String convert(String source) {
						return source.trim();
					}
				});
		}
		return whiteIdsList;
	}

    @Override
    public Map<String, Object> getRateInfo(String borrowRate, String borrowType, String tag,String secType) {
        AfResourceDo afResourceDo = afResourceDao.getConfigByTypesAndSecType(ResourceType.BORROW_RATE.getCode(), secType);
        String oneDay = "";
        String twoDay = "";
        if(null != afResourceDo){
            oneDay = afResourceDo.getTypeDesc().split(",")[0];
            twoDay = afResourceDo.getTypeDesc().split(",")[1];
        }
        Map<String, Object> rateInfo = Maps.newHashMap();
        double serviceRate = 0;
        double interestRate = 0;
        double overdueRate = 0;
        JSONArray array = JSONObject.parseArray(borrowRate);
        double totalRate = 0;
        for (int i = 0; i < array.size(); i++) {
            JSONObject info = array.getJSONObject(i);
            String borrowTag = info.getString(tag + "Tag");
            if (StringUtils.equals("INTEREST_RATE", borrowTag)) {
                if (StringUtils.equals(oneDay, borrowType)) {
                    interestRate = info.getDouble(tag + "FirstType");
                    totalRate += interestRate;
                } else if(StringUtils.equals(twoDay, borrowType)) {
                    interestRate = info.getDouble(tag + "SecondType");
                    totalRate += interestRate;
                }
            }
            if (StringUtils.equals("SERVICE_RATE", borrowTag)) {
                if (StringUtils.equals(oneDay, borrowType)) {
                    serviceRate = info.getDouble(tag + "FirstType");
                    totalRate += serviceRate;
                } else if(StringUtils.equals(twoDay, borrowType)){
                    serviceRate = info.getDouble(tag + "SecondType");
                    totalRate += serviceRate;
                }
            }
            if (StringUtils.equals("CONTRACT_RATE", borrowTag)) {
                if (StringUtils.equals(oneDay, borrowType)) {
                    overdueRate = info.getDouble(tag + "FirstType");
                    totalRate += serviceRate;
                } else if(StringUtils.equals(twoDay, borrowType)){
                    overdueRate = info.getDouble(tag + "SecondType");
                    totalRate += serviceRate;
                }
            }

        }
        rateInfo.put("serviceRate", serviceRate);
        rateInfo.put("interestRate", interestRate);
        rateInfo.put("overdueRate", overdueRate);
        rateInfo.put("totalRate", totalRate);
        return rateInfo;
    }


    public List<Object> rewardBannerList(String type,String homeBanner){
        List<Object> rewardBannerList = new ArrayList<Object>();
        if (Constants.INVELOMENT_TYPE_ONLINE.equals(type) || Constants.INVELOMENT_TYPE_TEST.equals(type)) {
            rewardBannerList = getObjectWithResourceDolist(getResourceHomeListByTypeOrderBy(homeBanner));
        } else if (Constants.INVELOMENT_TYPE_PRE_ENV.equals(type)) {
            rewardBannerList = getObjectWithResourceDolist(getResourceHomeListByTypeOrderByOnPreEnv(homeBanner));
        }
        return rewardBannerList;
    }

    public List<Object> getObjectWithResourceDolist(List<AfResourceDo> bannerResclist) {
        List<Object> bannerList = new ArrayList<Object>();
        for (AfResourceDo afResourceDo : bannerResclist) {
            Map<String, Object> data = new HashMap<String, Object>();
            data.put("imageUrl", afResourceDo.getValue());
            data.put("titleName", afResourceDo.getName());
            data.put("type", afResourceDo.getSecType());
            data.put("content", afResourceDo.getValue2());
            data.put("sort", afResourceDo.getSort());
            bannerList.add(data);
        }
        return bannerList;
    }
}
