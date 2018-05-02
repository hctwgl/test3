package com.ald.fanbei.api.web.api.goods;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.dal.domain.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.CacheConstants;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.InterestfreeCode;
import com.ald.fanbei.api.common.enums.ResourceType;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.web.cache.Cache;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.InterestFreeUitl;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @author Jiang Rongbo
 *
 */
@Component("getHomeInfoV2Api")
public class GetHomeInfoV2Api implements ApiHandle {

	@Resource
	AfResourceService afResourceService;

	@Resource
	AfActivityGoodsService afActivityGoodsService;

	@Resource
	AfActivityService afActivityService;

	@Resource
	AfSchemeGoodsService afSchemeGoodsService;

	@Resource
	AfInterestFreeRulesService afInterestFreeRulesService;

	@Resource
	AfCategoryService afCategoryService;

	@Resource
	AfGoodsService afGoodsService;

	@Resource
	AfModelH5ItemService afModelH5ItemService;

	@Resource
	AfModelH5Service afModelH5Service;

	@Resource
	BizCacheUtil bizCacheUtil;
	
	@Resource
	Cache scheduledCache;
	@Resource
	AfAbtestDeviceNewService afAbtestDeviceNewService;
	@Resource
	AfSeckillActivityService afSeckillActivityService;
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Map<String, Object> data = new HashMap<String, Object>();
		String deviceType = ObjectUtils.toString(requestDataVo.getParams().get("deviceType"));
		data.put("homePageType", "NEW");
		
//		String userName = context.getUserName();
//		Long userId = context.getUserId();
//		if (userName != null && userId != null) {
//		    try {
//			String deviceId = ObjectUtils.toString(requestDataVo.getParams().get("deviceId"));
//			if (StringUtils.isNotEmpty(deviceId)) {
//			  //String deviceIdTail = StringUtil.getDeviceTailNum(deviceId);
//				AfAbtestDeviceNewDo abTestDeviceDo = new AfAbtestDeviceNewDo();
//				abTestDeviceDo.setUserId(userId);
//				abTestDeviceDo.setDeviceNum(deviceId);
//				// 通过唯一组合索引控制数据不重复
//				afAbtestDeviceNewService.addUserDeviceInfo(abTestDeviceDo);
//			}
//		}  catch (Exception e) {
//			// ignore error.
//		}
//		}
		
		
		String envType = ConfigProperties.get(Constants.CONFKEY_INVELOMENT_TYPE);
		// 搜索框背景图
		List<AfResourceDo> serchBoxRescList = afResourceService
				.getConfigByTypes(ResourceType.SEARCH_BOX_BACKGROUND.getCode());
		if (serchBoxRescList != null && !serchBoxRescList.isEmpty()) {
			AfResourceDo serchBoxInfo = serchBoxRescList.get(0);
			String searchBoxBgImage = serchBoxInfo.getValue();
			data.put("searchBoxBgImage", searchBoxBgImage);
		} 

		// 顶部导航信息
		List<Object> topBannerList = new ArrayList<Object>();

		String topBanner = AfResourceType.HomeBannerV401.getCode();
		if (StringUtils.equals(deviceType, "IPHONEX")) {
			topBanner = AfResourceType.HomeBannerV401iPhoneX.getCode();
		}
		// 正式环境和预发布环境区分
		if (Constants.INVELOMENT_TYPE_ONLINE.equals(envType) || Constants.INVELOMENT_TYPE_TEST.equals(envType)) {
			topBannerList = getBannerInfoWithResourceDolist(
					afResourceService.getResourceHomeListByTypeOrderBy(topBanner));
		} else if (Constants.INVELOMENT_TYPE_PRE_ENV.equals(envType)) {
			topBannerList = getBannerInfoWithResourceDolist(
					afResourceService.getResourceHomeListByTypeOrderByOnPreEnv(topBanner));
		}

		// 快速导航信息
		Map<String, Object> navigationInfo = getNavigationInfoWithResourceDolist(
				afResourceService.getHomeIndexListByOrderby(AfResourceType.HomeNavigation.getCode()));

		// 新增运营位1,快捷导航上方活动专场
		List<Object> navigationUpOne = getNavigationUpOneResourceDoList(
				afResourceService.getNavigationUpOneResourceDoList(AfResourceType.HomeNavigationUpOneV401.getCode()));

		// 新增运营位2,快捷导航下方活动专场
		List<Object> navigationDownOne = getNavigationDownTwoResourceDoList(afResourceService
				.getNavigationDownTwoResourceDoList(AfResourceType.HomeNavigationDownTwoV401.getCode()));

		// 获取常驻运营位信息
		List<Object> homeNomalPositionList = getHomeNomalPositonInfoResourceDoList(
				afResourceService.getHomeNomalPositionList());

		// 获取逛逛信息
		Map<String, Object> brandAreaInfo = getBrandAreaInfo();

		// 获取电商板块信息
		Map<String, Object> ecommerceAreaInfo = getEcommerceAreaInfo();

		// 获取首页商品信息
		List<Map<String, Object>> categoryGoodsInfo = null;
		// 做线上和预发开关
		AfResourceDo afResourceDo = afResourceService.getSingleResourceBytype(ResourceType.HOME_PAGE.getCode());
		if (StringUtils.equals(afResourceDo.getValue(), YesNoStatus.YES.getCode())
				&& request.getRequestURL().indexOf("//app") != -1) {
			if (StringUtils.equals(afResourceDo.getValue1(), "N")) {
				// 缓存首页商品，10分钟刷新一次
				String cacheKey = CacheConstants.HOME_PAGE.GET_HOME_INFO_V2_GOODS_INFO_FOR_NEW.getCode();

				categoryGoodsInfo = bizCacheUtil.getObjectList(cacheKey);
				
				if(categoryGoodsInfo == null) {
					categoryGoodsInfo = (List<Map<String, Object>>) scheduledCache.getObject(cacheKey);
				}
				
				if (categoryGoodsInfo == null) {
					categoryGoodsInfo = getHomePageGoodsCategoryInfoV1();
					bizCacheUtil.saveListForever(cacheKey, categoryGoodsInfo);

				}
			} else if (StringUtils.equals(afResourceDo.getValue1(), "Y")) {
				String cacheKey = CacheConstants.HOME_PAGE.GET_HOME_INFO_V2_GOODS_INFO_FOR_OLD.getCode();
				categoryGoodsInfo = bizCacheUtil.getObjectList(cacheKey);
				if(categoryGoodsInfo == null) {
					categoryGoodsInfo = (List<Map<String, Object>>) scheduledCache.getObject(cacheKey);
				}
				if (categoryGoodsInfo == null) {
					categoryGoodsInfo = getHomePageGoodsCategoryInfo();
					bizCacheUtil.saveListForever(cacheKey, categoryGoodsInfo);
				}
			}
		} else {
			if (StringUtils.equals(afResourceDo.getValue2(), "N")) {
				// 缓存首页商品，10分钟刷新一次
				String cacheKey = CacheConstants.HOME_PAGE.GET_HOME_INFO_V2_GOODS_INFO_FOR_NEW.getCode();
				categoryGoodsInfo = bizCacheUtil.getObjectList(cacheKey);
				if(categoryGoodsInfo == null) {
					categoryGoodsInfo = (List<Map<String, Object>>) scheduledCache.getObject(cacheKey);
				}
				
				if (categoryGoodsInfo == null) {
					categoryGoodsInfo = getHomePageGoodsCategoryInfoV1();
					logger.info("getHomeInfoV2 cfp categoryGoodsInfo = " + categoryGoodsInfo);
					bizCacheUtil.saveListForever(cacheKey, categoryGoodsInfo);
				}
			} else if (StringUtils.equals(afResourceDo.getValue2(), "Y")) {
				String cacheKey = CacheConstants.HOME_PAGE.GET_HOME_INFO_V2_GOODS_INFO_FOR_OLD.getCode();
				categoryGoodsInfo = bizCacheUtil.getObjectList(cacheKey);
				if(categoryGoodsInfo == null) {
					categoryGoodsInfo = (List<Map<String, Object>>) scheduledCache.getObject(cacheKey);
				}
				if (categoryGoodsInfo == null) {
					categoryGoodsInfo = getHomePageGoodsCategoryInfo();
					bizCacheUtil.saveListForever(cacheKey, categoryGoodsInfo);
				}
			}
		}

		// 背景图配置
		List<AfResourceDo> backgroundList = afResourceService
				.getBackGroundByType(ResourceType.CUBE_HOMEPAGE_BACKGROUND.getCode());

		// 获取金融服务入口
		Map<String, Object> financialEntranceInfo = getFinancialEntranceInfo();

		// 顶部轮播
		if (!topBannerList.isEmpty()) {
			data.put("topBannerList", topBannerList);
		}
		// 快速导航
		if (!navigationInfo.isEmpty()) {
			data.put("navigationInfo", navigationInfo);
		}
		// 新增运营位1,快捷导航上方活动专场
		if (!navigationUpOne.isEmpty()) {
			data.put("navigationUpOne", navigationUpOne);
		}

		// 新增运营位2,快捷导航下方活动专场
		if (!navigationDownOne.isEmpty()) {
			data.put("navigationDownOne", navigationDownOne);
		}

		// 常驻运营位
		if (!homeNomalPositionList.isEmpty()) {
			data.put("nomalPositionList", homeNomalPositionList);
		}
		// 逛逛板块信息
		if (!brandAreaInfo.isEmpty()) {
			data.put("brandAreaInfo", brandAreaInfo);
		}
		// 电商板块信息
		if (!ecommerceAreaInfo.isEmpty()) {
			data.put("ecommerceAreaInfo", ecommerceAreaInfo);
		}

		// 首页分类商品信息
		if (!categoryGoodsInfo.isEmpty()) {
			data.put("categoryGoodsInfo", categoryGoodsInfo);
		}
		// 首页背景图
		if (!backgroundList.isEmpty()) {
			data.put("backgroundList", backgroundList);
		}

		// 金融服务入口
		if (!financialEntranceInfo.isEmpty()) {
			data.put("financialEntranceInfo", financialEntranceInfo);
		}
		logger.info("getHomeInfoV2 cfp data = " + data);
		resp.setResponseData(data);
		return resp;
	}

	private Map<String, Object> getFinancialEntranceInfo() {
		Map<String, Object> financialEntranceInfo = Maps.newHashMap();
		AfResourceDo rescDo = afResourceService.getFinancialEntranceInfo();
		if (rescDo != null) {
			financialEntranceInfo.put("imageUrl", rescDo.getValue());
			financialEntranceInfo.put("type", rescDo.getValue1());
			financialEntranceInfo.put("content", rescDo.getValue2());
		}
		return financialEntranceInfo;
	}

	public List<Map<String, Object>> getHomePageGoodsCategoryInfo() {
		List<AfCategoryDo> categoryList = afCategoryService.getHomePageCategoryInfo();
		List<Map<String, Object>> categoryInfoList = Lists.newArrayList();
		for (AfCategoryDo categoryDo : categoryList) {
			Map<String, Object> categoryInfoMap = Maps.newHashMap();
			categoryInfoMap.put("categoryId", categoryDo.getRid());
			categoryInfoMap.put("categoryName", categoryDo.getName());
			categoryInfoList.add(categoryInfoMap);
		}
		// 爬取商品开关
		AfResourceDo isWormResc = afResourceService.getConfigByTypesAndSecType(Constants.THIRD_GOODS_TYPE,
				Constants.THIRD_GOODS_IS_WORM_SECTYPE);
		String isWorm = "0";
		if (null != isWormResc) {
			isWorm = isWormResc.getValue();
		}
		// 获取借款分期配置信息
		AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE,
				Constants.RES_BORROW_CONSUME);
		JSONArray array = JSON.parseArray(resource.getValue());
		// 删除2分期
		if (array == null) {
			throw new FanbeiException(FanbeiExceptionCode.BORROW_CONSUME_NOT_EXIST_ERROR);
		}
		// removeSecondNper(array);

		if (categoryInfoList != null && !categoryInfoList.isEmpty()) {

			Map<String, Object> infoMap = categoryInfoList.get(0);
			Long categoryId = (Long) infoMap.get("categoryId");
			// 第一个类目下查询商品
			List<AfGoodsDo> goodsDoList = afGoodsService.getGoodsByCategoryId(categoryId);
			List<Map<String, Object>> goodsInfoList = Lists.newArrayList();
			//判断商品是否处于活动中
			List<AfSeckillActivityGoodsDo> activityGoodsDos = new ArrayList<>();
			List<Long> goodsIdList = new ArrayList<>();
			for (AfGoodsDo goodsDo : goodsDoList) {
				goodsIdList.add(goodsDo.getRid());
			}
			if(goodsIdList!=null&&goodsIdList.size()>0){
				activityGoodsDos =afSeckillActivityService.getActivityGoodsByGoodsIds(goodsIdList);
			}
			for (AfGoodsDo goodsDo : goodsDoList) {
				Map<String, Object> goodsInfo = new HashMap<String, Object>();
				for (AfSeckillActivityGoodsDo activityGoodsDo : activityGoodsDos) {
					if(activityGoodsDo.getGoodsId().equals(goodsDo.getRid())){
						goodsDo.setSaleAmount(activityGoodsDo.getSpecialPrice());
						BigDecimal secKillRebAmount = goodsDo.getSaleAmount().multiply(goodsDo.getRebateRate()).setScale(2,BigDecimal.ROUND_HALF_UP);
						if(goodsDo.getRebateAmount().compareTo(secKillRebAmount)>0){
							goodsDo.setRebateAmount(secKillRebAmount);
						}
						break;
					}
				}
				goodsInfo.put("goodName", goodsDo.getName());
				goodsInfo.put("rebateAmount", goodsDo.getRebateAmount());
				goodsInfo.put("saleAmount", goodsDo.getSaleAmount());
				goodsInfo.put("priceAmount", goodsDo.getPriceAmount());
				goodsInfo.put("goodsIcon", goodsDo.getGoodsIcon());
				goodsInfo.put("goodsId", goodsDo.getRid());
				goodsInfo.put("goodsUrl", goodsDo.getGoodsUrl());
				goodsInfo.put("thumbnailIcon", goodsDo.getThumbnailIcon());
				goodsInfo.put("source", goodsDo.getSource());
				goodsInfo.put("goodsType", "0");
				goodsInfo.put("remark", StringUtil.null2Str(goodsDo.getRemark()));
				goodsInfo.put("isWorm", isWorm);
				// 如果是分期免息商品，则计算分期
				Long goodsId = goodsDo.getRid();
				AfSchemeGoodsDo schemeGoodsDo = null;
				try {
					schemeGoodsDo = afSchemeGoodsService.getSchemeGoodsByGoodsId(goodsId);
				} catch (Exception e) {
					logger.error(e.toString());
				}
				JSONArray interestFreeArray = null;
				if (schemeGoodsDo != null) {
					AfInterestFreeRulesDo interestFreeRulesDo = afInterestFreeRulesService
							.getById(schemeGoodsDo.getInterestFreeId());
					String interestFreeJson = interestFreeRulesDo.getRuleJson();
					if (StringUtils.isNotBlank(interestFreeJson) && !"0".equals(interestFreeJson)) {
						interestFreeArray = JSON.parseArray(interestFreeJson);
					}
				}
				List<Map<String, Object>> nperList = InterestFreeUitl.getConsumeList(array, interestFreeArray,
						BigDecimal.ONE.intValue(), goodsDo.getSaleAmount(), resource.getValue1(), resource.getValue2(),goodsId,"0");

				if (nperList != null) {
					goodsInfo.put("goodsType", "1");
					Map<String, Object> nperMap = nperList.get(nperList.size() - 1);
					String isFree = (String) nperMap.get("isFree");
					if (InterestfreeCode.NO_FREE.getCode().equals(isFree)) {
						nperMap.put("freeAmount", nperMap.get("amount"));
					}
					goodsInfo.put("nperMap", nperMap);
				}
				goodsInfoList.add(goodsInfo);
				infoMap.put("goodsInfoList", goodsInfoList);
			}
		}
		return categoryInfoList;
	}

	public List<Map<String, Object>> getHomePageGoodsCategoryInfoV1() {
		List<AfModelH5ItemDo> categoryList = afModelH5ItemService.selectModelByTag();
		List<Map<String, Object>> categoryInfoList = Lists.newArrayList();
		for (AfModelH5ItemDo modelH5ItemDo : categoryList) {
			Map<String, Object> categoryInfoMap = Maps.newHashMap();
			categoryInfoMap.put("categoryId",modelH5ItemDo.getRid());
			categoryInfoMap.put("categoryName", modelH5ItemDo.getItemValue());
			categoryInfoList.add(categoryInfoMap);
		}
		// 爬取商品开关
		AfResourceDo isWormResc = afResourceService.getConfigByTypesAndSecType(Constants.THIRD_GOODS_TYPE,
				Constants.THIRD_GOODS_IS_WORM_SECTYPE);
		String isWorm = "0";
		if (null != isWormResc) {
			isWorm = isWormResc.getValue();
		}
		// 获取借款分期配置信息
		AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE,
				Constants.RES_BORROW_CONSUME);
		JSONArray array = JSON.parseArray(resource.getValue());
		// 删除2分期
		if (array == null) {
			throw new FanbeiException(FanbeiExceptionCode.BORROW_CONSUME_NOT_EXIST_ERROR);
		}
		removeSecondNper(array);
		if (null != categoryList && !categoryList.isEmpty()) {
			Map<String, Object> infoMap = categoryInfoList.get(0);
			List<AfGoodsDo> goodsDoList = null;
			if (null != categoryInfoList.get(0)) {
				Long categoryId = Long.valueOf(String.valueOf(infoMap.get("categoryId")));
				goodsDoList = afGoodsService.getGoodsByItem(categoryId);
			}
			List<Map<String, Object>> goodsInfoList = new ArrayList<Map<String, Object>>();
			//判断商品是否处于活动中
			List<AfSeckillActivityGoodsDo> activityGoodsDos = new ArrayList<>();
			List<Long> goodsIdList = new ArrayList<>();
			for (AfGoodsDo goodsDo : goodsDoList) {
				goodsIdList.add(goodsDo.getRid());
			}
			if(goodsIdList!=null&&goodsIdList.size()>0){
				activityGoodsDos =afSeckillActivityService.getActivityGoodsByGoodsIds(goodsIdList);
			}
			for (AfGoodsDo goodsDo : goodsDoList) {
				Map<String, Object> goodsInfo = new HashMap<String, Object>();
				for (AfSeckillActivityGoodsDo activityGoodsDo : activityGoodsDos) {
					if(activityGoodsDo.getGoodsId().equals(goodsDo.getRid())){
						goodsDo.setSaleAmount(activityGoodsDo.getSpecialPrice());
						BigDecimal secKillRebAmount = goodsDo.getSaleAmount().multiply(goodsDo.getRebateRate()).setScale(2,BigDecimal.ROUND_HALF_UP);
						if(goodsDo.getRebateAmount().compareTo(secKillRebAmount)>0){
							goodsDo.setRebateAmount(secKillRebAmount);
						}
						break;
					}
				}
				goodsInfo.put("goodName", goodsDo.getName());
				goodsInfo.put("rebateAmount", goodsDo.getRebateAmount());
				goodsInfo.put("saleAmount", goodsDo.getSaleAmount());
				goodsInfo.put("priceAmount", goodsDo.getPriceAmount());
				goodsInfo.put("goodsIcon", goodsDo.getGoodsIcon());
				goodsInfo.put("goodsId", goodsDo.getRid());
				goodsInfo.put("goodsUrl", goodsDo.getGoodsUrl());
				goodsInfo.put("thumbnailIcon", goodsDo.getThumbnailIcon());
				goodsInfo.put("source", goodsDo.getSource());
				goodsInfo.put("goodsType", "0");
				goodsInfo.put("remark", StringUtil.null2Str(goodsDo.getRemark()));
				goodsInfo.put("isWorm", isWorm);
				// 如果是分期免息商品，则计算分期
				Long goodsId = goodsDo.getRid();
				AfSchemeGoodsDo schemeGoodsDo = null;
				try {
					schemeGoodsDo = afSchemeGoodsService.getSchemeGoodsByGoodsId(goodsId);
				} catch (Exception e) {
					logger.error(e.toString());
				}
				JSONArray interestFreeArray = null;
				if (schemeGoodsDo != null) {
					AfInterestFreeRulesDo interestFreeRulesDo = afInterestFreeRulesService
							.getById(schemeGoodsDo.getInterestFreeId());
					String interestFreeJson = interestFreeRulesDo.getRuleJson();
					if (StringUtils.isNotBlank(interestFreeJson) && !"0".equals(interestFreeJson)) {
						interestFreeArray = JSON.parseArray(interestFreeJson);
					}
				}
				List<Map<String, Object>> nperList = InterestFreeUitl.getConsumeList(array, interestFreeArray,
						BigDecimal.ONE.intValue(), goodsDo.getSaleAmount(), resource.getValue1(), resource.getValue2(),goodsId,"0");

				if (nperList != null) {
					goodsInfo.put("goodsType", "1");
					Map<String, Object> nperMap = nperList.get(nperList.size() - 1);
					String isFree = (String) nperMap.get("isFree");
					if (InterestfreeCode.NO_FREE.getCode().equals(isFree)) {
						nperMap.put("freeAmount", nperMap.get("amount"));
					}
					goodsInfo.put("nperMap", nperMap);
				}
				goodsInfoList.add(goodsInfo);
				infoMap.put("goodsInfoList", goodsInfoList);
			}
		}
		return categoryInfoList;
	}

	public Map<String, Object> getEcommerceAreaInfo() {
		Map<String, Object> ecommerceAreaInfoMap = Maps.newHashMap();
		// 获取电商楼层图信息
		AfResourceDo ecommerceFloorImgRes = afResourceService.getEcommerceFloorImgRes();

		if (ecommerceFloorImgRes != null) {
			Map<String, Object> ecommerceFloorInfo = Maps.newHashMap();
			ecommerceFloorInfo.put("imageUrl", ecommerceFloorImgRes.getValue());
			ecommerceFloorInfo.put("type", ecommerceFloorImgRes.getValue1());
			ecommerceFloorInfo.put("content", ecommerceFloorImgRes.getValue2());
			ecommerceAreaInfoMap.put("ecommerceFloorInfo", ecommerceFloorInfo);
		}
		// 获取上方4个电商运营位,如果配置不全，则不显示
		List<AfResourceDo> ecommercePosUpRescList = afResourceService.getEcommercePositionUpRescoure();
		if (ecommercePosUpRescList != null && ecommercePosUpRescList.size() == 4) {
			List<Object> ecommercePositionUpInfoList = getHomeObjectInfoWithResourceDolist(ecommercePosUpRescList);
			if (!ecommercePositionUpInfoList.isEmpty()) {
				ecommerceAreaInfoMap.put("ecommercePosUpInfoList", ecommercePositionUpInfoList);
			}
		}
		// 获取下方3个电商运营位置，如果不全，则不显示
		List<AfResourceDo> ecommercePosDownRescList = afResourceService.getEcommercePositionDownRescoure();
		if (ecommercePosDownRescList != null && ecommercePosDownRescList.size() == 3) {
			List<Object> ecommercePositionDownInfoList = getHomeObjectInfoWithResourceDolist(ecommercePosDownRescList);
			if (!ecommercePositionDownInfoList.isEmpty()) {
				ecommerceAreaInfoMap.put("ecommercePosDownInfoList", ecommercePositionDownInfoList);
			}
		}
		// 获取电商轮播图片
		List<Object> ecommerceBannerList = getBannerInfoWithResourceDolist(
				afResourceService.getResourceHomeListByTypeOrderBy(AfResourceType.HomeBannerEcommerce.getCode()));
		if (!ecommerceBannerList.isEmpty()) {
			ecommerceAreaInfoMap.put("ecommerceBannerList", ecommerceBannerList);
		}
		return ecommerceAreaInfoMap;
	}

	public Map<String, Object> getBrandAreaInfo() {
		Map<String, Object> brandAreaInfoMap = Maps.newHashMap();
		// 获取逛逛楼层图信息
		AfResourceDo brandFloorImgRes = afResourceService.getBrandFloorImgRes();
		if (brandFloorImgRes != null) {
			Map<String, Object> brandFloorInfo = Maps.newHashMap();
			brandFloorInfo.put("imageUrl", brandFloorImgRes.getValue());
			brandFloorInfo.put("type", brandFloorImgRes.getValue1());
			brandFloorInfo.put("content", brandFloorImgRes.getValue2());
			brandAreaInfoMap.put("brandFloorInfo", brandFloorInfo);
		}
		// 逛逛运营位置
		List<Object> brandPositionInfoList = getHomeBrandPositonInfoResourceDoList(
				afResourceService.getHomeBrandPositonInfoList());
		if (!brandPositionInfoList.isEmpty()) {
			brandAreaInfoMap.put("brandPositionInfoList", brandPositionInfoList);
		}
		// 逛逛轮播图
		List<Object> brandBannerList = getBannerInfoWithResourceDolist(
				afResourceService.getResourceHomeListByTypeOrderBy(AfResourceType.HomeBannerBrand.getCode()));
		if (!brandBannerList.isEmpty()) {
			brandAreaInfoMap.put("brandBannerList", brandBannerList);
		}
		return brandAreaInfoMap;
	}

	private List<Object> getHomeBrandPositonInfoResourceDoList(List<AfResourceDo> brandPositionRescList) {
		// 如果少于4个，则不展示
		if (brandPositionRescList != null && brandPositionRescList.size() < 4) {
			return new ArrayList<Object>();
		}
		return getHomeObjectInfoWithResourceDolist(brandPositionRescList);
	}

	private List<Object> getHomeNomalPositonInfoResourceDoList(List<AfResourceDo> homeNomalPositonRescList) {
		List<Object> homeNomalPositionList = new ArrayList<Object>();
		int nomalPosCount = 0;
		if (homeNomalPositonRescList != null) {
			nomalPosCount = homeNomalPositonRescList.size();
		}
		// 如果配置小于两个，则不显示
		if (nomalPosCount < 2) {
			return homeNomalPositionList;
		}
		// 如果配置少于4个，则只显示两个
		if (2 <= nomalPosCount && nomalPosCount < 4) {
			nomalPosCount = 2;
		}
		// 如果配置多余4个，则只显示4个
		if (nomalPosCount >= 4) {
			nomalPosCount = 4;
		}

		for (int i = 0; i < nomalPosCount; i++) {
			AfResourceDo afResourceDo = homeNomalPositonRescList.get(i);
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("imageUrl", afResourceDo.getValue());
			data.put("titleName", afResourceDo.getName());
			data.put("type", afResourceDo.getValue1());
			data.put("content", afResourceDo.getValue2());
			data.put("sort", afResourceDo.getSort());
			homeNomalPositionList.add(data);
		}
		return homeNomalPositionList;
	}

	/**
	 * 根据rescList获取顶部导航信息
	 * 
	 * @param rescList
	 *            ,资源列表
	 * @return 顶部导航信息列表
	 */
	private List<Object> getBannerInfoWithResourceDolist(List<AfResourceDo> rescList) {
		List<Object> bannerList = new ArrayList<Object>();
		for (AfResourceDo rescDo : rescList) {
			Map<String, Object> dataMap = new HashMap<String, Object>();
			dataMap.put("imageUrl", rescDo.getValue());
			dataMap.put("type", rescDo.getValue1());
			dataMap.put("content", rescDo.getValue2());
			dataMap.put("sort", rescDo.getSort());
			bannerList.add(dataMap);
		}
		return bannerList;
	}

	private Map<String, Object> getNavigationInfoWithResourceDolist(List<AfResourceDo> navResclist) {
		Map<String, Object> navigationInfo = new HashMap<String, Object>();
		List<Object> navigationList = new ArrayList<Object>();
		int navCount = navResclist.size();
		for (int i = 0; i < navCount; i++) {
			// 如果配置大于5个，小于10个，则只显示5个
			if (navCount > 5 && navCount < 10) {
				if (i >= 5) {
					break;
				}
			} else if (navCount > 10) {
				// 如果配置大于10个，则只显示10个
				if (i >= 10) {
					break;
				}
			}
			AfResourceDo afResourceDo = navResclist.get(i);
			String secType = afResourceDo.getSecType();
			Map<String, Object> dataMap = new HashMap<String, Object>();
			dataMap.put("imageUrl", afResourceDo.getValue());
			dataMap.put("titleName", afResourceDo.getName());
			dataMap.put("type", secType);
			dataMap.put("content", afResourceDo.getValue2());
			dataMap.put("sort", afResourceDo.getSort());
			dataMap.put("color", afResourceDo.getValue3());
			navigationList.add(dataMap);
		}
		navigationInfo.put("navigationList", navigationList);
		return navigationInfo;
	}

	private void removeSecondNper(JSONArray array) {
		if (array == null) {
			return;
		}
		Iterator<Object> it = array.iterator();
		while (it.hasNext()) {
			JSONObject json = (JSONObject) it.next();
			if (json.getString(Constants.DEFAULT_NPER).equals("2")) {// mark
				it.remove();
				break;
			}
		}
	}

	private List<Object> getHomeObjectInfoWithResourceDolist(List<AfResourceDo> resclist) {
		List<Object> homeObjList = new ArrayList<Object>();
		for (AfResourceDo afResourceDo : resclist) {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("imageUrl", afResourceDo.getValue());
			data.put("titleName", afResourceDo.getName());
			if (afResourceDo.getType().equals(AfResourceType.HomeNavigation.getCode())) {
				data.put("type", afResourceDo.getSecType());
			} else {
				data.put("type", afResourceDo.getValue1());
			}
			data.put("content", afResourceDo.getValue2());
			data.put("sort", afResourceDo.getSort());
			homeObjList.add(data);
		}
		return homeObjList;
	}

	private List<Object> getNavigationUpOneResourceDoList(List<AfResourceDo> navigationUplist) {
		return getHomeObjectInfoWithResourceDolist(navigationUplist);
	}

	private List<Object> getNavigationDownTwoResourceDoList(List<AfResourceDo> navigationDownlist) {
		return getHomeObjectInfoWithResourceDolist(navigationDownlist);
	}

}
