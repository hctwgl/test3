package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.bo.BorrowRateBo;
import com.ald.fanbei.api.biz.bo.thirdpay.ThirdBizType;
import com.ald.fanbei.api.biz.bo.thirdpay.ThirdPayBo;
import com.ald.fanbei.api.biz.bo.thirdpay.ThirdPayStatusEnum;
import com.ald.fanbei.api.biz.bo.thirdpay.ThirdPayTypeEnum;
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
import com.ald.fanbei.api.dal.dao.AfResourceDao;
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
    public BorrowRateBo borrowRateWithResource(Integer realTotalNper, String userName) {
        BorrowRateBo borrowRate = new BorrowRateBo();
        JSONObject borrowRateJson = getBorrowRateResource(realTotalNper, userName);
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

    private JSONObject getBorrowRateResource(Integer realTotalNper, String userName) {
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
	
	public Map<String, Object> getBorrowCfgInfo() {
		List<AfResourceDo> borrowHomeConfigList = this.selectBorrowHomeConfigByAllTypes();
		Map<String, Object> data = new HashMap<String, Object>();

		for (AfResourceDo afResourceDo : borrowHomeConfigList) {
			if (StringUtils.equals(afResourceDo.getType(), AfResourceType.borrowRate.getCode())) {
				if (StringUtils.equals(afResourceDo.getSecType(), AfResourceSecType.BorrowCashRange.getCode())) {

					data.put("maxAmount", afResourceDo.getValue());
					data.put("minAmount", afResourceDo.getValue1());

				} else if (StringUtils.equals(afResourceDo.getSecType(),
						AfResourceSecType.BorrowCashBaseBankDouble.getCode())) {
					data.put("bankDouble", afResourceDo.getValue());

				} else if (StringUtils.equals(afResourceDo.getSecType(),
						AfResourceSecType.BorrowCashPoundage.getCode())) {
					data.put("poundage", afResourceDo.getValue());

				} else if (StringUtils.equals(afResourceDo.getSecType(),
						AfResourceSecType.BorrowCashOverduePoundage.getCode())) {
					data.put("overduePoundage", afResourceDo.getValue());

				} else if (StringUtils.equals(afResourceDo.getSecType(), AfResourceSecType.BaseBankRate.getCode())) {
					data.put("bankRate", afResourceDo.getValue());
				} else if (StringUtils.equals(afResourceDo.getSecType(),
						AfResourceSecType.borrowCashSupuerSwitch.getCode())) {
					data.put("supuerSwitch", afResourceDo.getValue());
				} else if (StringUtils.equals(afResourceDo.getSecType(),
						AfResourceSecType.borrowCashLenderForCash.getCode())) {
					data.put("lender", afResourceDo.getValue());

				} else if (StringUtils.equals(afResourceDo.getSecType(),
						AfResourceSecType.borrowCashTotalAmount.getCode())) {
					data.put("amountPerDay", afResourceDo.getValue());
				} else if (StringUtils.equals(afResourceDo.getSecType(),
						AfResourceSecType.borrowCashShowNum.getCode())) {
					data.put("nums", afResourceDo.getValue());
				}
			} else {
				if (StringUtils.equals(afResourceDo.getType(), AfResourceSecType.BorrowCashDay.getCode())) {
					data.put("borrowCashDay", afResourceDo.getValue());
				}
			}
		}
		return data;
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
	public List<AfResourceDo> getLoanHomeListByType(String type ,String envType){
        List<AfResourceDo> list = new ArrayList<AfResourceDo>();
        if (Constants.INVELOMENT_TYPE_ONLINE.equals(envType) || Constants.INVELOMENT_TYPE_TEST.equals(envType)) {
            list = afResourceDao.getResourceHomeListByTypeOrderBy(type);
        }else if (Constants.INVELOMENT_TYPE_PRE_ENV.equals(envType)) {
            list = afResourceDao.getResourceHomeListByTypeOrderByOnPreEnv(type);
        }
        return list;
    }

}
