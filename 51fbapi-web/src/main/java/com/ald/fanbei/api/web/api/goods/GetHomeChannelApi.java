package com.ald.fanbei.api.web.api.goods;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import jodd.util.StringUtil;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfAbtestDeviceNewService;
import com.ald.fanbei.api.biz.service.AfActivityGoodsService;
import com.ald.fanbei.api.biz.service.AfActivityService;
import com.ald.fanbei.api.biz.service.AfCategoryService;
import com.ald.fanbei.api.biz.service.AfGoodsService;
import com.ald.fanbei.api.biz.service.AfHomePageChannelConfigureService;
import com.ald.fanbei.api.biz.service.AfHomePageChannelService;
import com.ald.fanbei.api.biz.service.AfInterestFreeRulesService;
import com.ald.fanbei.api.biz.service.AfModelH5ItemService;
import com.ald.fanbei.api.biz.service.AfModelH5Service;
import com.ald.fanbei.api.biz.service.AfResourceH5ItemService;
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
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfHomePageChannelConfigureDo;
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

/**
 * @author Jiang Rongbo
 *
 */
@Component("getHomeChannelApi")
public class GetHomeChannelApi implements ApiHandle {

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
	AfHomePageChannelService afHomePageChannelService;

	@Resource
	BizCacheUtil bizCacheUtil;
	@Resource
	AfHomePageChannelConfigureService afHomePageChannelConfigureService;
	@Resource
	AfUserService afUserService;
	@Resource
	Cache scheduledCache;
	@Resource
	AfAbtestDeviceNewService afAbtestDeviceNewService;
	@Resource
	AfSeckillActivityService afSeckillActivityService;
	@Resource
	AfResourceH5ItemService afResourceH5ItemService;
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Map<String, Object> data = new HashMap<String, Object>();
		String deviceType = ObjectUtils.toString(requestDataVo.getParams().get("deviceType"));
		Long tabId = NumberUtil.objToLongDefault(requestDataVo.getParams().get("tabId"), null);
		if(tabId == null){
			logger.error("tabId is null");
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PARAM_ERROR);
	      }
		//
		Long userId = null;
		if (context.getUserName() != null) {
		    AfUserDo userDo = afUserService.getUserByUserName(context.getUserName());
		    if(userDo != null){
		    	userId = userDo.getRid();
		    }
		}
		
		String envType = ConfigProperties.get(Constants.CONFKEY_INVELOMENT_TYPE);
		// 搜索框背景图
//		List<AfResourceDo> serchBoxRescList = afResourceService
//				.getConfigByTypes(ResourceType.SEARCH_BOX_BACKGROUND.getCode());
//		if (serchBoxRescList != null && !serchBoxRescList.isEmpty()) {
//			AfResourceDo serchBoxInfo = serchBoxRescList.get(0);
//			String searchBoxBgImage = serchBoxInfo.getValue();
//			data.put("searchBoxBgImage", searchBoxBgImage);
//		} 
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
		}
		data.put("topTab", topTab);
//		List<AfResourceDo> backgroundList = afResourceService
//				.getBackGroundByType(ResourceType.CUBE_HOMEPAGE_BACKGROUND.getCode());
//		// 首页背景图  ?确认是否要首页的
//		if (!backgroundList.isEmpty()) {
//					data.put("backgroundList", backgroundList);
//		}
		
	    List<Object> topBannerList = new ArrayList<Object>();
	    List<Object> navigationList = new ArrayList<Object>();
	    List<Object> onePlusThreeArea = new ArrayList<Object>();
		Map<String, Object> navigationInfo = new HashMap<String, Object>();
		Map<String, Object> onePlusThreeInfo = new HashMap<String, Object>();
		Map<String, Object> onePlusThreeBanner = new HashMap<String, Object>();
	   List<AfHomePageChannelConfigureDo> channelConfigureList =  afHomePageChannelConfigureService.getByChannelId(tabId);
		if(channelConfigureList != null && channelConfigureList.size() >0){
			for(AfHomePageChannelConfigureDo homePageChannelConfigure:channelConfigureList){
				//0轮播
				if(0 == homePageChannelConfigure.getConfigureType() ){
					Map<String, Object> bannerInfo = new HashMap<String, Object>();
					bannerInfo.put("imageUrl", homePageChannelConfigure.getImageUrl());
					bannerInfo.put("type", "H5_URL");
					bannerInfo.put("content", homePageChannelConfigure.getJumpUrl());
					bannerInfo.put("sort", homePageChannelConfigure.getSort());
					if (Constants.INVELOMENT_TYPE_ONLINE.equals(envType) || Constants.INVELOMENT_TYPE_TEST.equals(envType)) {
						if(1 == homePageChannelConfigure.getStatus()){
							topBannerList.add(bannerInfo);
						}
					} else if (Constants.INVELOMENT_TYPE_PRE_ENV.equals(envType)) {
						topBannerList.add(bannerInfo);
					}
				}
				//1导航
				if(1 == homePageChannelConfigure.getConfigureType() ){
					Map<String, Object> navigation = new HashMap<String, Object>();
					navigation.put("imageUrl", homePageChannelConfigure.getImageUrl());
					navigation.put("type", homePageChannelConfigure.getJumpType());
					navigation.put("titleName", homePageChannelConfigure.getName());
					navigation.put("content", homePageChannelConfigure.getJumpUrl());
					navigation.put("sort", homePageChannelConfigure.getSort());
					if (Constants.INVELOMENT_TYPE_ONLINE.equals(envType) || Constants.INVELOMENT_TYPE_TEST.equals(envType)) {
						if(1 == homePageChannelConfigure.getStatus()){
							navigationList.add(navigation);
						}
					} else if (Constants.INVELOMENT_TYPE_PRE_ENV.equals(envType)) {
						navigationList.add(navigation);
					}
					
				}
				//2运营
				if(2 == homePageChannelConfigure.getConfigureType() ){
					if(0 ==  homePageChannelConfigure.getPosition()&& 1 == homePageChannelConfigure.getStatus()){
						onePlusThreeBanner.put("imageUrl", homePageChannelConfigure.getImageUrl());
						onePlusThreeBanner.put("type", homePageChannelConfigure.getJumpType());
						onePlusThreeBanner.put("content", homePageChannelConfigure.getJumpUrl());
					}else if (0 <  homePageChannelConfigure.getPosition() && 1 == homePageChannelConfigure.getStatus()){
						Map<String, Object> onePlusThree = new HashMap<String, Object>(); 
						if(homePageChannelConfigure.getSort() == 1){
							
						}
						onePlusThree.put("imageUrl", homePageChannelConfigure.getImageUrl());
						onePlusThree.put("type", homePageChannelConfigure.getJumpType());
						onePlusThree.put("content", homePageChannelConfigure.getJumpUrl());
						onePlusThree.put("sort", homePageChannelConfigure.getSort());
						onePlusThreeArea.add(onePlusThree);
					}
				}
			}
			navigationInfo = getNavigationInfolist(navigationList);
			onePlusThreeArea = getOnePlusThreeArea(onePlusThreeArea);
			//data.put("topBannerList", topBannerList);
			//data.put("navigationInfo", navigationInfo);
			//onePlusThreeInfo.put("onePlusThreeBanner", onePlusThreeBanner);
			//onePlusThreeInfo.put("onePlusThreeArea", onePlusThreeArea);
			//data.put("onePlusThreeInfo", onePlusThreeInfo);
			if (!topBannerList.isEmpty()) {
				data.put("topBannerList", topBannerList);
			}
			if (!navigationInfo.isEmpty()) {
				data.put("navigationInfo", navigationInfo);
			}
			if (!onePlusThreeBanner.isEmpty()) {
				onePlusThreeInfo.put("onePlusThreeBanner", onePlusThreeBanner);
			}
			if (!onePlusThreeArea.isEmpty()) {
				onePlusThreeInfo.put("onePlusThreeArea", onePlusThreeArea);
			}
			if (!onePlusThreeInfo.isEmpty()) {
				data.put("onePlusThreeInfo", onePlusThreeInfo);
			}
			
		}
	   //推荐商品
		 Map<String, Object> recommendGoodsInfo = new HashMap<String, Object>();
		 try{
			
				Map<String, Object> goodsInfo = new HashMap<String, Object>();
				String type = "HOME_PAGE_CHANNEL_RECOMMEND_GOODS";
				String recommendTag = "HC_IMAGE";
				AfResourceDo recommendGoods =  afResourceService.getConfigByTypesAndValue(type, tabId.toString());
		        if(recommendGoods != null){
		        	 String goodsIds = recommendGoods.getValue3();
		        	  if(goodsIds != null){
						     String[] goodsId = goodsIds.split(",");  
						     Long[] gids = (Long[]) ConvertUtils.convert(goodsId,Long.class);
						     List<Long> goodsIdList = new ArrayList<Long>();
							 for(Long gid: gids){
								 goodsIdList.add(gid);
							 }
							  List<HomePageSecKillGoods> goodsList = afSeckillActivityService.getHomePageSecKillGoodsByConfigureResourceH5(userId,goodsIdList);
							  List<Map<String, Object>> recommendGoodsInfoList = getGoodsInfoList(goodsList,null,null);
							  recommendGoodsInfo.put("goodsList", recommendGoodsInfoList);
								 String imageUrl = "";
								 String content = "";
							     List<AfResourceH5ItemDo>  recommendList =  afResourceH5ItemService.getByTag(recommendTag);
							     if(recommendList != null && recommendList.size() >0){
							    	 for(AfResourceH5ItemDo recommend:recommendList ){
										  if("RECOMMEND_TOP_IMAGE".equals(recommend.getValue2())){
											  content =  recommend.getValue1();
											  imageUrl= recommend.getValue3();
											  break;
										  }
							    }
							    	 if(StringUtil.isNotEmpty(imageUrl)){
								    	 recommendGoodsInfo.put("imageUrl", imageUrl);
								    	 recommendGoodsInfo.put("content", content);
							    	 }
		        	  }
		         }
		 }
		 }catch(Exception e){
			 
		 }
		//更多商品
//		 Map<String, Object> goodsInfo = new HashMap<String, Object>();
//		 Map<String, Object> moreGoodsInfo = new HashMap<String, Object>();
//		 try{
//		 String moreGoodsTag = "MORE_IMAGE";
//		 String activityTag = "HOME_CHANNEL_MORE_GOODS";
//		 Integer activityType = 5;
		
//		
//		 List<HomePageSecKillGoods> goodsList = afSeckillActivityService.getHomePageSecKillGoodsByActivityModel(userId,activityTag,activityType,tabId,1);
//		  List<Map<String, Object>> moreGoodsInfoList = getGoodsInfoList(goodsList,null,null);
//		    moreGoodsInfo.put("moreGoodsList", moreGoodsInfoList);
//		     String imageUrl = "";
//			 String content = "";
//		     List<AfResourceH5ItemDo>  recommendList =  afResourceH5ItemService.getByTag(moreGoodsTag);
//		     if(recommendList != null && recommendList.size() >0){
//		    	 for(AfResourceH5ItemDo recommend:recommendList ){
//						  if("MORE_GOODS_TOP_IMAGE".equals(recommend.getValue2())){
//							  content =  recommend.getValue1();
//							  imageUrl= recommend.getValue3();
//							  break;
//						  }
//		         }
//		     }
//		     if(StringUtil.isNotEmpty(imageUrl)){
//		    	   moreGoodsInfo.put("imageUrl",imageUrl);
//		    	   moreGoodsInfo.put("content", content);
//	    	 }
//		  
//			
//		 }catch(Exception e){
//			 
//		 }

		
		 if (!recommendGoodsInfo.isEmpty()) {
				data.put("recommendGoodsInfo", recommendGoodsInfo);
			}
//			if (!moreGoodsInfo.isEmpty()) {
//				data.put("moreGoodsInfo", moreGoodsInfo);
//			}
		resp.setResponseData(data);
		return resp;

		
	}
	
	
	
	private List<Object> getOnePlusThreeArea(List<Object> onePlusThreeArea) {
		// TODO Auto-generated method stub
//		int optCount = onePlusThreeArea.size();
//		for (int i = 0; i < optCount; i++) {
//			
//		Object onePlusThreeAreaVo =  onePlusThreeArea.get(0);
//		Class<?> clazz = onePlusThreeAreaVo.getClass();
//		}
//			
		
		
		List<Object>  onePlusThreeAreaList = new ArrayList<Object>();
		if(onePlusThreeArea != null && onePlusThreeArea.size() > 0 ){
				int size = onePlusThreeArea.size();
				if(size > 3 ){
					onePlusThreeAreaList.addAll(onePlusThreeArea.subList(0, 3));
				}if(size <= 3 ){
					onePlusThreeAreaList.addAll(onePlusThreeArea);
				}
			
		}
		return onePlusThreeAreaList;
	}



	private AfHomePageChannelVo parseDoToVo(AfHomePageChannelDo afHomePageChannelDo) {
		AfHomePageChannelVo vo = new AfHomePageChannelVo();
		vo.setTabId(afHomePageChannelDo.getRid());
		vo.setTabName(afHomePageChannelDo.getName());
		return vo;
	}

	private Map<String, Object> getNavigationInfolist(List<Object> navigationList2) {
		Map<String, Object> navigationInfo = new HashMap<String, Object>();
		List<Object> navigationList = new ArrayList<Object>();
		if(navigationList2 != null &&navigationList2.size()>0){
		int navCount = navigationList2.size();
		if (navCount >= 5 && navCount < 10) {
				navigationList.addAll(navigationList2.subList(0, 5));
		} else if (navCount >= 10) {
				navigationList.addAll(navigationList2.subList(5, 10));
		}
		navigationInfo.put("navigationList", navigationList);
		}
		return navigationInfo;
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
		   }
		    goodsList.add(goodsInfo);
		}
		return goodsList;
	}


}
