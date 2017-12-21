package com.ald.fanbei.api.web.api.goods;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfActivityGoodsService;
import com.ald.fanbei.api.biz.service.AfActivityService;
import com.ald.fanbei.api.biz.service.AfCategoryService;
import com.ald.fanbei.api.biz.service.AfGoodsService;
import com.ald.fanbei.api.biz.service.AfInterestFreeRulesService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfSchemeGoodsService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfResourceSecType;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.ImageType;
import com.ald.fanbei.api.common.enums.InterestfreeCode;
import com.ald.fanbei.api.common.enums.ResourceType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfCategoryDo;
import com.ald.fanbei.api.dal.domain.AfGoodsDo;
import com.ald.fanbei.api.dal.domain.AfInterestFreeRulesDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfSchemeGoodsDo;
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

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("homePageType", "NEW");
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
		// 正式环境和预发布环境区分
		if (Constants.INVELOMENT_TYPE_ONLINE.equals(envType) || Constants.INVELOMENT_TYPE_TEST.equals(envType)) {
			topBannerList = getBannerInfoWithResourceDolist(
					afResourceService.getResourceHomeListByTypeOrderBy(topBanner));
		} else if (Constants.INVELOMENT_TYPE_PRE_ENV.equals(envType)) {
			topBannerList = getBannerInfoWithResourceDolist(
					afResourceService.getResourceHomeListByTypeOrderByOnPreEnv(topBanner));
		}
		// logger.info("home page top banner info => {}",
		// JSONObject.toJSONString(topBannerList));

		// 快速导航信息
		Map<String, Object> navigationInfo = getNavigationInfoWithResourceDolist(
				afResourceService.getHomeIndexListByOrderby(AfResourceType.HomeNavigation.getCode()));
		// logger.info("home page fast nav info => {}",
		// JSONObject.toJSONString(topBannerList));

		// 新增运营位1,快捷导航上方活动专场
		List<Object> navigationUpOne = getNavigationUpOneResourceDoList(
				afResourceService.getNavigationUpOneResourceDoList(AfResourceType.HomeNavigationUpOne.getCode()));

		// logger.info("home page nav up ad position info => {}" +
		// JSONObject.toJSONString(navigationUpOne));

		// 新增运营位2,快捷导航下方活动专场
		List<Object> navigationDownOne = getNavigationDownTwoResourceDoList(
				afResourceService.getNavigationDownTwoResourceDoList(AfResourceType.HomeNavigationDownTwo.getCode()));
		// logger.info("home page nav down ad position info => {}" +
		// JSONObject.toJSONString(navigationUpOne));

		// 获取常驻运营位信息
		List<Object> homeNomalPositionList = getHomeNomalPositonInfoResourceDoList(
				afResourceService.getResourceHomeListByTypeOrderBy(AfResourceType.HomeFourImageNomalPositon.getCode()));
		// logger.info("home page nomal ad position info => {}" +
		// JSONObject.toJSONString(homeNomalPositionList));

		// 获取逛逛信息
		Map<String, Object> brandAreaInfo = getBrandAreaInfo();
		// logger.info("home page brand area info => {}" +
		// JSONObject.toJSONString(brandAreaInfo));

		// 获取电商板块信息
		Map<String, Object> ecommerceAreaInfo = getEcommerceAreaInfo();
		// logger.info("home page ecommerce area info => {}" +
		// JSONObject.toJSONString(ecommerceAreaInfo));

		// 获取首页商品信息
		List<Map<String, Object>> categoryGoodsInfo = getHomePageGoodsCategoryInfo();
		// logger.info("home page category goods info => {}" +
		// JSONObject.toJSONString(categoryGoodsInfo));

		// 背景图配置
		List<AfResourceDo> backgroundList = afResourceService
				.getBackGroundByType(ResourceType.HOMEPAGE_BACKGROUND.getCode());

		// 获取金融服务入口
		Map<String, Object> financialEntranceInfo = getFinancialEntranceInfo();

		// 顶部轮播
		data.put("topBannerList", topBannerList);
		// 快速导航
		data.put("navigationInfo", navigationInfo);
		// 新增运营位1,快捷导航上方活动专场
		data.put("navigationUpOne", navigationUpOne);
		// 新增运营位2,快捷导航下方活动专场
		data.put("navigationDownOne", navigationDownOne);
		// 常驻运营位
		data.put("nomalPositionList", homeNomalPositionList);
		// 逛逛板块信息
		data.put("brandAreaInfo", brandAreaInfo);
		// 电商板块信息
		data.put("ecommerceAreaInfo", ecommerceAreaInfo);
		// 首页分类商品信息
		data.put("categoryGoodsInfo", categoryGoodsInfo);
		// 首页背景图
		data.put("backgroundList", backgroundList);
		// 金融服务入口
		data.put("financialEntranceInfo", financialEntranceInfo);
		resp.setResponseData(data);
		return resp;
	}

	private Map<String, Object> getFinancialEntranceInfo() {
		Map<String, Object> financialEntranceInfo = Maps.newHashMap();
		AfResourceDo rescDo = afResourceService.getConfigByTypesAndSecType(
				ResourceType.HOME_ONE_IMAGE_FINANCIAL.getCode(), ImageType.MAIN_IMAGE.getCode());
		if (rescDo != null) {
			financialEntranceInfo.put("imageUrl", rescDo.getValue());
			financialEntranceInfo.put("type", rescDo.getValue1());
			financialEntranceInfo.put("content", rescDo.getValue2());
		}
		return financialEntranceInfo;
	}

	private List<Map<String, Object>> getHomePageGoodsCategoryInfo() {
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
		removeSecondNper(array);

		if (categoryInfoList != null && !categoryInfoList.isEmpty()) {

			Map<String, Object> infoMap = categoryInfoList.get(0);
			Long categoryId = (Long) infoMap.get("categoryId");
			// 第一个类目下查询商品
			List<AfGoodsDo> goodsDoList = afGoodsService.getGoodsByCategoryId(categoryId);
			List<Map<String, Object>> goodsInfoList = Lists.newArrayList();
			for (AfGoodsDo goodsDo : goodsDoList) {
				Map<String, Object> goodsInfo = new HashMap<String, Object>();
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
						BigDecimal.ONE.intValue(), goodsDo.getSaleAmount(), resource.getValue1(), resource.getValue2());

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

	private Map<String, Object> getEcommerceAreaInfo() {
		Map<String, Object> ecommerceAreaInfoMap = Maps.newHashMap();
		// 获取电商楼层图信息
		AfResourceDo ecommerceFloorImgRes = afResourceService.getConfigByTypesAndSecType(
				ResourceType.HOME_ONE_IMAGE_BRAND.getCode(), ImageType.MAIN_IMAGE.getCode());
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
			ecommerceAreaInfoMap.put("ecommercePosUpInfoList", ecommercePositionUpInfoList);
		}
		// 获取下方3个电商运营位置，如果不全，则不显示
		List<AfResourceDo> ecommercePosDownRescList = afResourceService.getEcommercePositionDownRescoure();
		if (ecommercePosDownRescList != null && ecommercePosDownRescList.size() == 3) {
			List<Object> ecommercePositionDownInfoList = getHomeObjectInfoWithResourceDolist(ecommercePosDownRescList);
			ecommerceAreaInfoMap.put("ecommercePosDownInfoList", ecommercePositionDownInfoList);
		}
		// 获取电商轮播图片
		List<Object> ecommerceBannerList = getBannerInfoWithResourceDolist(
				afResourceService.getResourceHomeListByTypeOrderBy(AfResourceType.HomeBannerEcommerce.getCode()));
		ecommerceAreaInfoMap.put("ecommerceBannerList", ecommerceBannerList);
		return ecommerceAreaInfoMap;
	}

	private Map<String, Object> getBrandAreaInfo() {
		Map<String, Object> brandAreaInfoMap = Maps.newHashMap();
		// 获取逛逛楼层图信息
		AfResourceDo brandFloorImgRes = afResourceService.getConfigByTypesAndSecType(
				ResourceType.HOME_ONE_IMAGE_BRAND.getCode(), ImageType.MAIN_IMAGE.getCode());
		if (brandFloorImgRes != null) {
			Map<String, Object> brandFloorInfo = Maps.newHashMap();
			brandFloorInfo.put("imageUrl", brandFloorImgRes.getValue());
			brandFloorInfo.put("type", brandFloorImgRes.getValue1());
			brandFloorInfo.put("content", brandFloorImgRes.getValue2());
			brandAreaInfoMap.put("brandFloorInfo", brandFloorInfo);
		}
		// 逛逛运营位置
		List<Object> brandPositionInfoList = getHomeBrandPositonInfoResourceDoList(afResourceService
				.getResourceHomeListByTypeOrderBy(ResourceType.HOME_FOUR_IMAGE_BRAND_POSITION.getCode()));
		brandAreaInfoMap.put("brandPositionInfoList", brandPositionInfoList);
		// 逛逛轮播图
		List<Object> brandBannerList = getBannerInfoWithResourceDolist(
				afResourceService.getResourceHomeListByTypeOrderBy(AfResourceType.HomeBannerBrand.getCode()));
		brandAreaInfoMap.put("brandBannerList", brandBannerList);
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

		Map<String, Object> backgroundInfo = new HashMap<String, Object>();
		// 查询快速导航背景图信息
		List<AfResourceDo> bgRescList = afResourceService
				.getConfigByTypes(ResourceType.HOME_NAVIGATION_BACKGROUND.getCode());
		if (bgRescList != null && !bgRescList.isEmpty()) {
			AfResourceDo afResourceDo = bgRescList.get(0);
			backgroundInfo.put("imageUrl", afResourceDo.getValue());
			backgroundInfo.put("type", AfResourceSecType.NAVIGATION_BACKGROUND.getCode());
		}
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
		navigationInfo.put("backgroundInfo", backgroundInfo);
		return navigationInfo;
	}

	private void removeSecondNper(JSONArray array) {
		if (array == null) {
			return;
		}
		Iterator<Object> it = array.iterator();
		while (it.hasNext()) {
			JSONObject json = (JSONObject) it.next();
			if (json.getString(Constants.DEFAULT_NPER).equals("2")) {
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