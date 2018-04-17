package com.ald.fanbei.api.web.api.goods;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfAbtestDeviceNewService;
import com.ald.fanbei.api.biz.service.AfActivityGoodsService;
import com.ald.fanbei.api.biz.service.AfActivityService;
import com.ald.fanbei.api.biz.service.AfCategoryService;
import com.ald.fanbei.api.biz.service.AfGoodsService;
import com.ald.fanbei.api.biz.service.AfHomePageChannelService;
import com.ald.fanbei.api.biz.service.AfInterestFreeRulesService;
import com.ald.fanbei.api.biz.service.AfModelH5ItemService;
import com.ald.fanbei.api.biz.service.AfModelH5Service;
import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.biz.service.AfResourceH5ItemService;
import com.ald.fanbei.api.biz.service.AfResourceH5Service;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfSchemeGoodsService;
import com.ald.fanbei.api.biz.service.AfSeckillActivityService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.InterestfreeCode;
import com.ald.fanbei.api.common.enums.ResourceType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.CollectionConverterUtil;
import com.ald.fanbei.api.common.util.CollectionUtil;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.Converter;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfHomePageChannelDo;
import com.ald.fanbei.api.dal.domain.AfInterestFreeRulesDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfResourceH5ItemDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.dto.HomePageSecKillGoods;
import com.ald.fanbei.api.web.cache.Cache;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.InterestFreeUitl;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.AfHomePageChannelVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;

/**
 * @author Jiang Rongbo
 *
 */
@Component("getHomeInfoV3Api")
public class GetHomeInfoV3Api implements ApiHandle {

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
    AfOrderService afOrderService;
	@Resource
	AfModelH5Service afModelH5Service;
	@Resource
	AfHomePageChannelService afHomePageChannelService;
	@Resource
	AfResourceH5Service AfResourceH5Service;
	@Resource
	AfResourceH5ItemService afResourceH5ItemService;
	@Resource
	BizCacheUtil bizCacheUtil;
	
	@Resource
	Cache scheduledCache;
	@Resource
	AfUserService afUserService;
	AfAbtestDeviceNewService afAbtestDeviceNewService;
	@Resource
	AfSeckillActivityService afSeckillActivityService;
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Map<String, Object> data = new HashMap<String, Object>();
		String deviceType = ObjectUtils.toString(requestDataVo.getParams().get("deviceType"));
		String envType = ConfigProperties.get(Constants.CONFKEY_INVELOMENT_TYPE);
		Long userId = null;
		if (context.getUserName() != null) {
		    AfUserDo userDo = afUserService.getUserByUserName(context.getUserName());
		    if(userDo != null){
		    	userId = userDo.getRid();
		    }
		}
         AfResourceDo   searchBackground = new  AfResourceDo();
         AfResourceDo   nineBackground   =   new  AfResourceDo();
         AfResourceDo   navigationBackground = new  AfResourceDo();
		// 背景图配置
		List<AfResourceDo> backgroundList = afResourceService
				.getBackGroundByTypeOrder(ResourceType.CUBE_HOMEPAGE_BACKGROUND_ASJ.getCode());
		
		// 背景图
		if (backgroundList != null && !backgroundList.isEmpty()) {
			  for(AfResourceDo background: backgroundList ){
				  if("HOME_SEARCH".equals(background.getValue1())){
					  searchBackground = background;
				  }
				if("HOME_NINE_GRID".equals(background.getValue1())){
					  nineBackground  =    background;
				}
				if("HOME_NAVIGATION".equals(background.getValue1())){
					  navigationBackground  = background;
				 }
			  }

		} 
		if(searchBackground != null){
			Map<String, Object> searchBoxBgImage = new HashMap<String, Object>();
			searchBoxBgImage.put("backgroundImage", searchBackground.getValue());
			searchBoxBgImage.put("color", searchBackground.getValue3());
			searchBoxBgImage.put("showType", searchBackground.getSecType());
			data.put("searchBoxBgImage", searchBoxBgImage);
		}
		
		// tabList[]
		List<AfHomePageChannelDo> channelList =  afHomePageChannelService.getListOrderBySortDesc();
		List<AfHomePageChannelVo> tabList = new ArrayList<AfHomePageChannelVo>();
		if (CollectionUtil.isNotEmpty(channelList)) {
			tabList = CollectionConverterUtil.convertToListFromList(channelList, new Converter<AfHomePageChannelDo, AfHomePageChannelVo>() {
				@Override
				public AfHomePageChannelVo convert(AfHomePageChannelDo source) {
					return parseDoToVo(source);
				}
			});
		}
		data.put("tabList", tabList);
		String topTabBar = AfResourceType.TABBAR_HOME_TOP.getCode();	
		List<Object> topTabBarList = new ArrayList<Object>();
		if (Constants.INVELOMENT_TYPE_ONLINE.equals(envType) || Constants.INVELOMENT_TYPE_TEST.equals(envType)) {
			topTabBarList = getBannerInfoWithResourceDolist(
					afResourceService.getResourceHomeListByTypeOrderBy(topTabBar));
		} else if (Constants.INVELOMENT_TYPE_PRE_ENV.equals(envType)) {
			topTabBarList = getBannerInfoWithResourceDolist(
					afResourceService.getResourceHomeListByTypeOrderByOnPreEnv(topTabBar));
		}
		Object topTab = new Object();
		if(topTabBarList != null && topTabBarList.size()>0){
			topTab =  topTabBarList.get(0);
			data.put("topTab", topTab);
		}

		
//		
//		String bottomTabBar = AfResourceType.TABBAR_HOME_BUTTOM.getCode();	
//		List<Object> bottomTabBarList = new ArrayList<Object>();
//		if (Constants.INVELOMENT_TYPE_ONLINE.equals(envType) || Constants.INVELOMENT_TYPE_TEST.equals(envType)) {
//			bottomTabBarList = getBannerInfoWithResourceDolist(
//					afResourceService.getResourceHomeListByTypeOrderBy(bottomTabBar));
//		} else if (Constants.INVELOMENT_TYPE_PRE_ENV.equals(envType)) {
//			bottomTabBarList = getBannerInfoWithResourceDolist(
//					afResourceService.getResourceHomeListByTypeOrderByOnPreEnv(bottomTabBar));
//		}
//		Object bottomTab = new Object();
//		if(bottomTabBarList != null && bottomTabBarList.size()>0){
//			bottomTab =  bottomTabBarList.get(0);
//			data.put("bottomTab", bottomTab);
//		}
		

		
		
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

		String sloganImage = "";
		String homeImages = AfResourceType.HOME_IAMGES.getCode();
		String slogan = AfResourceType.SLOGAN_IMAGE.getCode();

		 AfResourceDo homeImage = afResourceService.getConfigByTypesAndSecType(homeImages, slogan);
		if(homeImage !=null ){
			sloganImage = homeImage.getValue();
		}
		// 快速导航信息
		Map<String, Object> navigationInfo = getNavigationInfoWithResourceDolist(
				afResourceService.getHomeIndexListByOrderby(AfResourceType.HomeNavigation.getCode()),navigationBackground);

		// 新增运营位1,快捷导航上方活动专场
		List<Object> navigationUpOne = getNavigationUpOneResourceDoList(
				afResourceService.getNavigationUpOneResourceDoList(AfResourceType.HomeNavigationUpOneV401.getCode()));

		// 新增运营位2,快捷导航下方活动专场
		List<Object> navigationDownOne = getNavigationDownTwoResourceDoList(afResourceService
				.getNavigationDownTwoResourceDoList(AfResourceType.HomeNavigationDownTwoV401.getCode()));
/////////////
		// 获取金融服务入口
		Map<String, Object> financialEntranceInfo = getFinancialEntranceInfo();
		//九宫，1,3,6,9
		Map<String, Object> gridViewInfo = getGridViewInfoList();
		// 九宫板块信息
				if (!gridViewInfo.isEmpty()) {
					if(nineBackground != null){
						//Map<String, Object> nineBackgroundmage = new HashMap<String, Object>();
						gridViewInfo.put("backgroundImage", nineBackground.getValue());
						gridViewInfo.put("color", nineBackground.getValue3());
						gridViewInfo.put("showType", nineBackground.getSecType());
					}
					data.put("gridViewInfo", gridViewInfo);
				}



				//未登录显示，新用户（商城没有一笔支付成功的订单） 显示，  其余均不显示
				
				boolean newExclusive = false; //s是否符合新人专享
				if(userId != null ){
				int  selfsupportPaySuccessOrder = 	afOrderService.getSelfsupportPaySuccessOrderByUserId(userId);
					if(selfsupportPaySuccessOrder < 1 ){
						newExclusive = true;
					}
				}
				
				if(userId == null  || newExclusive){
				
				// 新人专享位（是否加入缓存？）
				List<Object> newExclusiveList = new ArrayList<Object>();
				Object newExclusiveInfo = new Object();
				String operateBanner = AfResourceType.OPERATION_POSITION_BANNER.getCode();
				
				// 正式环境和预发布环境区分
				if (Constants.INVELOMENT_TYPE_ONLINE.equals(envType) || Constants.INVELOMENT_TYPE_TEST.equals(envType)) {
					newExclusiveList = getBannerInfoWithResourceDolist(
							afResourceService.getResourceHomeListByTypeOrderBy(operateBanner));
				} else if (Constants.INVELOMENT_TYPE_PRE_ENV.equals(envType)) {
					newExclusiveList = getBannerInfoWithResourceDolist(
							afResourceService.getResourceHomeListByTypeOrderByOnPreEnv(operateBanner));
				}
				// 顶部轮播
				if (!newExclusiveList.isEmpty()) {
					newExclusiveInfo = newExclusiveList.get(0);
							data.put("newExclusiveInfo", newExclusiveInfo);
				}
			}
		
		// 获取常驻运营位信息
		List<Object> homeNomalPositionList = getHomeNomalPositonInfoResourceDoList(
				afResourceService.getHomeNomalPositionList());
//++++++电商运营位
		Map<String, Object> ecommerceAreaInfo = getEcommerceAreaInfo();
		// 九宫板块信息
				if (!ecommerceAreaInfo.isEmpty()) {
					data.put("ecommerceAreaInfo", ecommerceAreaInfo);
				}
				
				//限时抢购
				Map<String, Object> flashSaleInfo = new HashMap<String, Object>();
				 
		         //注意实时数据缓存时效
				// 商品展示
//				List<Long> goodsIdList = new ArrayList<Long>(); 
//				List<HomePageSecKillGoods> list = afSeckillActivityService.getHomePageSecKillGoodsByConfigureResourceH5(userId,goodsIdList);
//				List<Map<String, Object>> flashSaleGoodsList = getGoodsInfoList(list);
//				flashSaleInfo.put("goodsInfoList", flashSaleGoodsList);
//				flashSaleInfo.put("", value);
//				flashSaleInfo.put("", value);
//				flashSaleInfo.put("", value);
			//取当天一页的数据
				 
				 String topImage = "TOP_IMAGE"; 
				 String goodsResourceList = "GOODS";
				 String flashSaleTag = "FLASH_SALE";       //限时抢购
				 String newProductTag = "NEW_GOODS";       //品质新品
				 String activityGoodsTag = "HOME_SEL";     // 精选活动
				 String brandTag = "MAJOR_SUIT";           //大牌汇聚
				 //限时抢购
			     AfResourceH5ItemDo  afResourceH5ItemDo =  afResourceH5ItemService.getByTagAndType(flashSaleTag,topImage);
				AfResourceDo afResourceHomeSecKillDo = afResourceService.getSingleResourceBytype("HOME_SECKILL_CONFIG");
				List<HomePageSecKillGoods> flashSaleGoodsList = afSeckillActivityService.getHomePageSecKillGoods(userId, afResourceHomeSecKillDo.getValue(),0, 1);
				List<Map<String, Object>> flashSaleGoods = getGoodsInfoList(flashSaleGoodsList,flashSaleTag,afResourceH5ItemDo);
				String flashSaleContent = "";
				String flashSaleImageUrl = "";
				if(afResourceH5ItemDo !=null){
					flashSaleContent = afResourceH5ItemDo.getValue1();
					flashSaleImageUrl = afResourceH5ItemDo.getValue3();
				}
				flashSaleInfo.put("content",flashSaleContent);
				flashSaleInfo.put("imageUrl",flashSaleImageUrl);
				flashSaleInfo.put("startTime", DateUtil.getStartOfDate(new Date()).getTime()+ Long.parseLong(afResourceHomeSecKillDo.getValue1()));
				flashSaleInfo.put("currentTime", new Date().getTime());
				flashSaleInfo.put("endTime", DateUtil.getStartOfDate(new Date()).getTime()+ Long.parseLong(afResourceHomeSecKillDo.getValue2()));
				flashSaleInfo.put("goodsList", flashSaleGoods);
				//品质新品
				Map<String, Object> newProduct = new HashMap<String, Object>();
			   //  List<Long> newProductGoodsIdList = new ArrayList<Long>(); 
				 List<Object> newProductGoodsIdList = new ArrayList<Object>();
				  List<AfResourceH5ItemDo>  newProductList =  afResourceH5ItemService.getByTag(newProductTag);
				 if(newProductList != null && newProductList.size() >0 ){
					 for(AfResourceH5ItemDo newProductDo:newProductList ){
						 if("TOP_IMAGE".equals(newProductDo.getValue2())){
							 newProduct.put("imageUrl", newProductDo.getValue3());
							 newProduct.put("content", newProductDo.getValue1());
						 }else  if("GOODS".equals(newProductDo.getValue2())){
							if(newProductDo.getValue1() != null){
								Map<String, Object> newProductInfo = new HashMap<String, Object>();
								newProductInfo.put("content", newProductDo.getValue1());
								newProductInfo.put("sort", newProductDo.getSort());
								newProductInfo.put("imageUrl", newProductDo.getValue3());
								newProductInfo.put("type", "GOODS_ID");
							    newProductGoodsIdList.add(newProductInfo);
							}
					     }
						 
						 List<Object> newProductGoodsIdListConvert  = getNewProductGoodsIdList(newProductGoodsIdList);
						 newProduct.put("newProductList", newProductGoodsIdListConvert);
				     }
				 }

				 Map<String, Object> activityGoodsInfo = new HashMap<String, Object>();
				 // 精选活动
				 try{
					
						  List<AfResourceH5ItemDo>  activityList =  afResourceH5ItemService.getByTag(activityGoodsTag);
						 if(activityList != null && activityList.size() >0 ){
							 List<Object> activityGoodsInfoList1 = new ArrayList<Object>(); 
							 for(AfResourceH5ItemDo activityDo:activityList ){
								  if("GOODS".equals(activityDo.getValue2())){
								           List<Long> goodsIdList = new ArrayList<Long>(); 
										   if(activityDo.getValue1() != null){
											 String goodsIds = activityDo.getValue1();
											 String[] goodsId = goodsIds.split(",");  
											 Long[] gids = (Long[]) ConvertUtils.convert(goodsId,Long.class);
												 for(Long gid: gids){
													 goodsIdList.add(gid);
												 }
										    }
										    List<HomePageSecKillGoods> goodsList = afSeckillActivityService.getHomePageSecKillGoodsByConfigureResourceH5(userId,goodsIdList);
											List<Map<String, Object>> activityGoodsInfoList = getGoodsInfoList(goodsList,activityGoodsTag,null);
											//没有商品整块不显示
											
											if(activityGoodsInfoList != null && activityGoodsInfoList.size()  >0){
												Map<String, Object> goodsInfo = new HashMap<String, Object>();
												goodsInfo.put("goodsList", activityGoodsInfoList);
												goodsInfo.put("imageUrl", activityDo.getValue3());
												goodsInfo.put("type", "H5_URL");
												goodsInfo.put("content",activityDo.getValue4() );
												activityGoodsInfoList1.add(goodsInfo);
											}
							      }else  if("TOP_IMAGE".equals(activityDo.getValue2())){
										 activityGoodsInfo.put("imageUrl", activityDo.getValue3());
										 activityGoodsInfo.put("content", activityDo.getValue1());
								  } 
						    }
							 activityGoodsInfo.put("activityGoodsList", activityGoodsInfoList1);
							 
					    }
				 }catch(Exception e){
					 
				 }
				 Map<String, Object> brandInfo = new HashMap<String, Object>();
				 // 大牌汇聚
				 try{
					
			        List<Object> brandList1 = new ArrayList<Object>(); 

					List<AfResourceH5ItemDo>  brandGoodsList =  afResourceH5ItemService.getByTag(brandTag);
					if(brandGoodsList != null && brandGoodsList.size() >0 ){
						//循环查，数据量不多（查一次会重新把数据排序，对每个商品加入对应数据复杂， FIELD()列表中进行查找效率慢。）
							 for(AfResourceH5ItemDo activityDo:brandGoodsList ){
								  if("GOODS".equals(activityDo.getValue2())){
										  List<Long> goodsIdList = new ArrayList<Long>();    
										  if(activityDo.getValue1() != null){
													 String goodsIdsAndContents = activityDo.getValue1();
													 String[] goodsIdAndContents = goodsIdsAndContents.split(","); 
													 Long[] gids = new Long[goodsIdAndContents.length];
													 if(goodsIdAndContents.length >0){
														    int i = -1;
														 	for(String goodsId :goodsIdAndContents){
														 		 ++i;
														 		 String[] goodsIdAndContent = goodsId.split(":"); 
														 		 Long gdsId  = NumberUtil.objToLongDefault(goodsIdAndContent[0], 0); 
														 		 gids[i] = gdsId;
														 	}
													 }
													 
												      if(gids != null){
														 for(Long gid: gids){
															 goodsIdList.add(gid);
														 }
												      }
										  }
										    
										    List<HomePageSecKillGoods> goodsList = afSeckillActivityService.getHomePageSecKillGoodsByConfigureResourceH5(userId,goodsIdList);
											List<Map<String, Object>> brandGoodsInfoList = getGoodsInfoList(goodsList,brandTag,activityDo);
											if(brandGoodsInfoList != null && brandGoodsInfoList.size()>0){
												Map<String, Object> goodsInfo = new HashMap<String, Object>();
												goodsInfo.put("brandGoodsList", brandGoodsInfoList);
												goodsInfo.put("imageUrl", activityDo.getValue3());
												brandList1.add(goodsInfo);
											}
							      	}else if("TOP_IMAGE".equals(activityDo.getValue2())){
									 brandInfo.put("imageUrl", activityDo.getValue3());
									 brandInfo.put("content", activityDo.getValue1());
									 brandInfo.put("type", activityDo.getValue4());
							      	}
								 
						    }
							 brandInfo.put("brandList", brandList1);
							 
				 }
				 }catch(Exception e){
					logger.error("home brandList error = "+e); 
				 }
				 
	

		// 顶部轮播
		if (!topBannerList.isEmpty()) {
			data.put("topBannerList", topBannerList);
		}
		
		// 快速导航
		if (!navigationInfo.isEmpty()) {
				if(navigationBackground != null){
					navigationInfo.put("backgroundImage", navigationBackground.getValue());
					navigationInfo.put("color", navigationBackground.getValue3());
					navigationInfo.put("showType", navigationBackground.getSecType());
				}
			
			data.put("navigationInfo", navigationInfo);
		}
		
		if (!sloganImage.isEmpty()) {
			data.put("sloganImage", sloganImage);
		}
		// 新增运营位1,快捷导航上方活动专场
		if (!navigationUpOne.isEmpty()) {
			data.put("navigationUpOneList", navigationUpOne);
		}

		// 新增运营位2,快捷导航下方活动专场
		if (!navigationDownOne.isEmpty()) {
			data.put("navigationDownOneList", navigationDownOne);
		}

		// 常驻运营位
		if (!homeNomalPositionList.isEmpty()) {
			data.put("normalPositionList", homeNomalPositionList);
		}
	
		// 电商板块信息
		if (!ecommerceAreaInfo.isEmpty()) {
			data.put("ecommerceAreaInfo", ecommerceAreaInfo);
		}

		
	
		// 首页背景图
		if (!backgroundList.isEmpty()) {
			data.put("backgroundList", backgroundList);
		}

		// 金融服务入口
		if (!financialEntranceInfo.isEmpty()) {
			data.put("financialEntranceInfo", financialEntranceInfo);
		}

		// 	限时抢购
		if (!flashSaleInfo.isEmpty()) {
			data.put("flashSaleInfo", flashSaleInfo);
		}
		// 品质新品
		if (!newProduct.isEmpty()) {
			data.put("newProduct", newProduct);
		}
		// 活动运营商品
		if (!activityGoodsInfo.isEmpty()) {
			data.put("activityGoodsInfo", activityGoodsInfo);
		}
		// 大牌汇聚
		if (!brandInfo.isEmpty()) {
			data.put("brandInfo", brandInfo);
		}
		
		logger.info("getHomeInfoV2 cfp data = " + data);
		resp.setResponseData(data);
		return resp;
	}

	private List<Object> getNewProductGoodsIdList(
			List<Object> newProductGoodsIdList) {
		// TODO Auto-generated method stub
  		List<Object>  newProductGoodsIdLists = new ArrayList<Object>();
		if(newProductGoodsIdList != null && newProductGoodsIdList.size() > 0 ){
				int size = newProductGoodsIdList.size();
				if(size < 4 && size > 1 ){
					newProductGoodsIdLists.addAll(newProductGoodsIdList.subList(0, 2));
				}if(size > 4 ){
					newProductGoodsIdLists.addAll(newProductGoodsIdList.subList(0, 4));
				}
			
		}
		return newProductGoodsIdLists;
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

	

	public Map<String, Object> getEcommerceAreaInfo() {
		Map<String, Object> ecommerceAreaInfoMap = Maps.newHashMap();
		// 获取电商楼层图信息
//		AfResourceDo ecommerceFloorImgRes = afResourceService.getEcommerceFloor();
//
//		if (ecommerceFloorImgRes != null) {
//			Map<String, Object> ecommerceFloorInfo = Maps.newHashMap();
//			ecommerceFloorInfo.put("imageUrl", ecommerceFloorImgRes.getValue());
//			ecommerceFloorInfo.put("type", ecommerceFloorImgRes.getValue1());
//			ecommerceFloorInfo.put("content", ecommerceFloorImgRes.getValue2());
//			ecommerceAreaInfoMap.put("ecommerceAreaInfo", ecommerceFloorInfo);
//		}
		// 获取上方4个电商运营位,如果配置不全，则不显示
		List<AfResourceDo> ecommercePosUpRescList = afResourceService.getEcommercePositionUp();
		if (ecommercePosUpRescList != null && ecommercePosUpRescList.size() == 3) {
			List<Object> ecommercePositionUpInfoList = getHomeObjectInfoWithResourceDolist(ecommercePosUpRescList);
			if (!ecommercePositionUpInfoList.isEmpty()) {
				ecommerceAreaInfoMap.put("ecommercePosUpInfoList", ecommercePositionUpInfoList);
			}
		}
		// 获取下方3个电商运营位置，如果不全，则不显示
		List<AfResourceDo> ecommercePosDownRescList = afResourceService.getEcommercePositionDown();
		if (ecommercePosDownRescList != null && ecommercePosDownRescList.size() == 3) {
			List<Object> ecommercePositionDownInfoList = getHomeObjectInfoWithResourceDolist(ecommercePosDownRescList);
			if (!ecommercePositionDownInfoList.isEmpty()) {
				ecommerceAreaInfoMap.put("ecommercePosDownInfoList", ecommercePositionDownInfoList);
			}
		}
		// 获取电商轮播图片
//		List<Object> ecommerceBannerList = getBannerInfoWithResourceDolist(
//				afResourceService.getResourceHomeListByTypeOrderBy(AfResourceType.HomeBannerEcommerce.getCode()));
//		if (!ecommerceBannerList.isEmpty()) {
//			ecommerceAreaInfoMap.put("ecommerceBannerList", ecommerceBannerList);
//		}
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
			//data.put("titleName", afResourceDo.getName());
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

	private Map<String, Object> getNavigationInfoWithResourceDolist(List<AfResourceDo> navResclist,AfResourceDo backgroupd) {
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
			//data.put("titleName", afResourceDo.getName());
			if (afResourceDo.getType().equals(AfResourceType.HomeNavigation.getCode())) {
				data.put("type", afResourceDo.getSecType());
			} else {
				data.put("type", afResourceDo.getValue1());
			}
			
//			if (afResourceDo.getType().equals(AfResourceType.MANEY_PICUTRES_ASJ.getCode())  || afResourceDo.getType().equals(AfResourceType.HOME_IMAGE_ECOMMERCE_POSITION_ASJ.getCode())) {
//				data.put("position", afResourceDo.getValue3());
//			
//			
//			
//			}
			
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
	private AfHomePageChannelVo parseDoToVo(AfHomePageChannelDo afHomePageChannelDo) {
		AfHomePageChannelVo vo = new AfHomePageChannelVo();
		vo.setTabId(afHomePageChannelDo.getRid());
		vo.setTabName(afHomePageChannelDo.getName());
		return vo;
	}
	public Map<String, Object> getGridViewInfoList() {
		Map<String, Object> gridViewInfoMap = Maps.newHashMap();
		// 获取九宫图上方
		List<AfResourceDo> gridViewInfoUpList = afResourceService.getGridViewInfoUpList();
		if (gridViewInfoUpList != null && gridViewInfoUpList.size() == 3) {
			List<Object> gridViewPositionInfoUpList = getHomeObjectInfoWithResourceDolist(gridViewInfoUpList);
			if (!gridViewPositionInfoUpList.isEmpty()) {
				gridViewInfoMap.put("gridViewUpInfoList", gridViewPositionInfoUpList);
			}
		}
		
		
		// 获取中方,如果配置不全，则不显示
		List<AfResourceDo> gridViewInfoCenterList = afResourceService.getGridViewInfoCenterList();
		if (gridViewInfoCenterList != null && gridViewInfoCenterList.size() == 3) {
			List<Object> gridViewPositionInfoCenterList = getHomeObjectInfoWithResourceDolist(gridViewInfoCenterList);
			if (!gridViewPositionInfoCenterList.isEmpty()) {
				gridViewInfoMap.put("gridViewCenterInfoList", gridViewPositionInfoCenterList);
			}
		}
		// 获取下方3个电商运营位置，如果不全，则不显示
		List<AfResourceDo> gridViewInfoDownList = afResourceService.getGridViewInfoDownList();
		if (gridViewInfoDownList != null && gridViewInfoDownList.size() == 3) {
			List<Object> gridViewPositionInfoDownList = getHomeObjectInfoWithResourceDolist(gridViewInfoDownList);
			if (!gridViewPositionInfoDownList.isEmpty()) {
				gridViewInfoMap.put("gridViewDownInfoList", gridViewPositionInfoDownList);
			}
		}
		
		return gridViewInfoMap;
	}
	private List<Map<String, Object>> getGoodsInfoList(List<HomePageSecKillGoods> list,String tag,AfResourceH5ItemDo afResourceH5ItemDo){
		List<Map<String, Object>> goodsList = new ArrayList<Map<String, Object>>();
		// 获取借款分期配置信息
		AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE, Constants.RES_BORROW_CONSUME);
		JSONArray array = JSON.parseArray(resource.getValue());
		if (array == null) {
		    throw new FanbeiException(FanbeiExceptionCode.BORROW_CONSUME_NOT_EXIST_ERROR);
		}

		for (HomePageSecKillGoods homePageSecKillGoods : list) {
		    Map<String, Object> goodsInfo = new HashMap<String, Object>();
		    goodsInfo.put("goodsName", homePageSecKillGoods.getGoodName());
		    goodsInfo.put("rebateAmount", homePageSecKillGoods.getRebateAmount());
		    goodsInfo.put("saleAmount", homePageSecKillGoods.getSaleAmount());
		    goodsInfo.put("priceAmount", homePageSecKillGoods.getPriceAmount());
		    goodsInfo.put("activityAmount", homePageSecKillGoods.getActivityAmount());
		    goodsInfo.put("goodsIcon", homePageSecKillGoods.getGoodsIcon());
		    goodsInfo.put("goodsId", homePageSecKillGoods.getGoodsId());
		    goodsInfo.put("goodsUrl", homePageSecKillGoods.getGoodsUrl());
		    goodsInfo.put("goodsType", "0");
		    goodsInfo.put("subscribe", homePageSecKillGoods.getSubscribe());
		    goodsInfo.put("volume", homePageSecKillGoods.getVolume());
		    goodsInfo.put("total", homePageSecKillGoods.getTotal());	    
		    
		    // 如果是分期免息商品，则计算分期
		    Long goodsId = homePageSecKillGoods.getGoodsId();
		    JSONArray interestFreeArray = null;
		    if (homePageSecKillGoods.getInterestFreeId() != null) {
			AfInterestFreeRulesDo interestFreeRulesDo = afInterestFreeRulesService.getById(homePageSecKillGoods.getInterestFreeId().longValue());
			String interestFreeJson = interestFreeRulesDo.getRuleJson();
			if (StringUtils.isNotBlank(interestFreeJson) && !"0".equals(interestFreeJson)) {
			    interestFreeArray = JSON.parseArray(interestFreeJson);
			}
		    }
		    
		    List<Map<String, Object>> nperList = InterestFreeUitl.getConsumeList(array, interestFreeArray, BigDecimal.ONE.intValue(), 
			    homePageSecKillGoods.getSaleAmount(), resource.getValue1(), resource.getValue2(), goodsId, "0");
		    if (nperList != null) {
			goodsInfo.put("goodsType", "1");
			Map<String, Object> nperMap = nperList.get(nperList.size() - 1);
			String isFree = (String) nperMap.get("isFree");
			if (InterestfreeCode.NO_FREE.getCode().equals(isFree)) {
			    nperMap.put("freeAmount", nperMap.get("amount"));
			}
			goodsInfo.put("nperMap", nperMap);
		     //更换content和type可跳转商品详情
				if("FLASH_SALE".equals(tag)){
					  String content = null;
					 if(afResourceH5ItemDo != null){
						 content = afResourceH5ItemDo.getValue1();
					 }
			    	  goodsInfo.put("type", "H5_URL");
			    	  goodsInfo.put("content", content);
			     }
				if("MAJOR_SUIT".equals(tag)){
					 try{
							List<Object> contentList = new ArrayList<Object>();
							 if(afResourceH5ItemDo != null){
								 	 String goodsIdsAndContents =  afResourceH5ItemDo.getValue1();
									 String[] goodsIdAndContents = goodsIdsAndContents.split(","); 
									 if(goodsIdAndContents.length >0){
										 	for(String goodsIdContent :goodsIdAndContents){
										 		 String[] goodsIdAndContent = goodsIdContent.split(":"); 
										 		 Long gdsId  = NumberUtil.objToLongDefault(goodsIdAndContent[0], 0); 
										 		 //暂时放入某数组
										 		 
										 		// String[] goodsIdAndContent = goodsIdContent.split(":"); 
										 		 //相同的时候，赋值
										 		 if(gdsId.longValue()== homePageSecKillGoods.getGoodsId()){
										 			int i= -1;
										 			 for(String  content: goodsIdAndContent){
										 				 i++;
										 				Map<String, Object> contenInfo = new HashMap<String, Object>();
														if(i > 0){
											 				contenInfo.put("content",  content);
															contentList.add(contenInfo);
														}
										 			}
										 			 break;
										 		}
										 	}
										 }
									 
								 goodsInfo.put("contentList", contentList);
							 }
					 }catch(Exception e){
						 
					 }
			     }
				
		   }
		    goodsList.add(goodsInfo);
		}
		return goodsList;
	}

}
