package com.ald.fanbei.api.web.api.goods;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.ald.fanbei.api.biz.util.JobThreadPoolUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.PickBrandCouponRequestBo;
import com.ald.fanbei.api.biz.service.AfAbtestDeviceNewService;
import com.ald.fanbei.api.biz.service.AfActivityGoodsService;
import com.ald.fanbei.api.biz.service.AfActivityService;
import com.ald.fanbei.api.biz.service.AfAdvertiseService;
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
import com.ald.fanbei.api.biz.service.AfUserCouponService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.service.JpushService;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.CacheConstants;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfAdvertisePositionCode;
import com.ald.fanbei.api.common.enums.AfResourceSecType;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.HomePageType;
import com.ald.fanbei.api.common.enums.InterestfreeCode;
import com.ald.fanbei.api.common.enums.ResourceType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.CollectionConverterUtil;
import com.ald.fanbei.api.common.util.CollectionUtil;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.Converter;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfHomePageChannelDo;
import com.ald.fanbei.api.dal.domain.AfInterestFreeRulesDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfResourceH5ItemDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.dto.AfAdvertiseDto;
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
 * @author chenqiwei
 * 爱上街首页
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
	@Resource
	JpushService jpushService;
	@Resource
	AfUserCouponService afUserCouponService;
	@Resource
	AfAdvertiseService afAdvertiseService;
	@Resource
	JobThreadPoolUtils jobThreadPoolUtils;
	

	private static final String TABBAR =		              HomePageType.TABBAR.getCode(); 
	private static final String TABBAR_HOME_TOP =		          HomePageType.TABBAR_HOME_TOP.getCode(); 
	private static final String OPERATE =		              HomePageType.OPERATE.getCode(); 
	private static final String NEW_EXCLUSIVE =		          HomePageType.NEW_EXCLUSIVE.getCode(); 
	private static final String HOME_IAMGE_SLOGAN =		      HomePageType.HOME_IAMGE_SLOGAN.getCode(); 
	private static final String ASJ_IMAGES =		   			  HomePageType.ASJ_IMAGES.getCode();    //爱上街图片组
	private static final String TOP_IMAGE = 		  		      HomePageType.TOP_IMAGE.getCode(); 
	private static final String GOODS = 		  			      HomePageType.GOODS.getCode(); 
	private static final String H5_URL =          		      HomePageType.H5_URL.getCode(); 
	private static final String NEW_GOODS =   			 	  HomePageType.NEW_GOODS.getCode();    //品质新品
	private static final String HOME_SEL = 				 	  HomePageType.HOME_SEL.getCode();     // 精选活动
	private static final String MAJOR_SUIT =		  		 	  HomePageType.MAJOR_SUIT.getCode();    //大牌汇聚
	private static final String HOME_FLASH_SALE_FLOOR_IMAGE =  HomePageType.HOME_FLASH_SALE_FLOOR_IMAGE.getCode();   //限时抢购楼层图
	
	
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Map<String, Object> data = new HashMap<String, Object>();
		String deviceType = ObjectUtils.toString(requestDataVo.getParams().get("deviceType"));
		String envType = ConfigProperties.get(Constants.CONFKEY_INVELOMENT_TYPE);
		Long userId = null;
		Integer appVersion = context.getAppVersion();
		String userName = context.getUserName();
		if (context.getUserName() != null) {
		    AfUserDo userDo = afUserService.getUserByUserName(context.getUserName());
		    if(userDo != null){
		    	userId = userDo.getRid();
		    }
		}
		  doStrongRiseAndCoupon(userId,userName,appVersion);

		
		 String cacheKey = CacheConstants.ASJ_HOME_PAGE.ASJ_HOME_PAGE_INFO_FIRST.getCode()+"_"+envType;
		String cacheKey2 = CacheConstants.ASJ_HOME_PAGE.ASJ_HOME_PAGE_INFO_SECOND.getCode()+"_"+envType;
		String processKey = CacheConstants.ASJ_HOME_PAGE.ASJ_HOME_PAGE_INFO_PROCESS_KEY.getCode()+"_"+envType;

		 Object cacheResult =(Map<String, Object>) bizCacheUtil.getMap(cacheKey);
		if (cacheResult != null) {
			data =  (Map<String, Object>) cacheResult;
		}else
          {

			  boolean isGetLock = bizCacheUtil.getLock30Second(processKey, "1");
			  data = (Map<String, Object>) bizCacheUtil.getMap(cacheKey2);
			  logger.info("getHomeInfov3 cache2" + Thread.currentThread().getName() + "isGetLock:" + isGetLock + "data= " + JSONArray.toJSONString(data) + "cacheKey2 = " + cacheKey2);
			  //调用异步请求加入缓存
			  if (isGetLock) {
				  logger.info("getHomeInfov3" + Thread.currentThread().getName() + "getHomeInfov3 is null" + "cacheKey2 = " + cacheKey2);
				  Runnable process = new GetHomeCache(cacheKey,cacheKey2,userId,deviceType);
				  jobThreadPoolUtils.asynProcessBusiness(process);
			  }

			  if(data== null || data.isEmpty()) {
				  data = toAddHomeCacheInfo(userId, deviceType);
				  if (data != null && !data.isEmpty()) {
					  bizCacheUtil.saveMap(cacheKey, data, Constants.MINITS_OF_TWO);
					  bizCacheUtil.saveMapForever(cacheKey2, data);
				  }
			  }


/*			  AfResourceDo   searchBackground = new  AfResourceDo();
			  AfResourceDo   nineBackground   =   new  AfResourceDo();
			  AfResourceDo   navigationBackground = new  AfResourceDo();
			  // 背景图配置
			  List<AfResourceDo> backgroundList  = new ArrayList<AfResourceDo>();
			  backgroundList = afResourceService
						.getBackGroundByTypeAndStatusOrder(ResourceType.CUBE_HOMEPAGE_BACKGROUND_ASJ.getCode());

				// 背景图
				if (backgroundList != null && !backgroundList.isEmpty()) {
					  for(AfResourceDo background: backgroundList ){
						 
						  if(AfResourceType.HOME_SEARCH.getCode().equals(background.getValue1())){
							  if (!StringUtils.equals(deviceType, "IPHONEX")) {
								  searchBackground = background;
								}
						  }
						  if(AfResourceType.HOME_SEARCH_IPHONEX.getCode().equals(background.getValue1())){
							  if (StringUtils.equals(deviceType, "IPHONEX")) {
								  searchBackground = background;
								}
						  }
						 
						if(AfResourceType.HOME_NINE_GRID.getCode().equals(background.getValue1())){
							  nineBackground  =    background;
						}
						if(AfResourceType.HOME_NAVIGATION.getCode().equals(background.getValue1())){
							  navigationBackground  = background;
						 }
						 if(searchBackground.getValue() != null && nineBackground.getValue()!= null & navigationBackground.getValue() != null){
							  break;
						  }
					  }
				} 
				
				
				// tabList[]
		    	List<AfHomePageChannelDo> channelList =   new ArrayList<AfHomePageChannelDo>();
		    		  channelList =  afHomePageChannelService.getListOrderBySortDesc();
				List<AfHomePageChannelVo> tabList = new ArrayList<AfHomePageChannelVo>();
				try{
					if (CollectionUtil.isNotEmpty(channelList)) {
						tabList = CollectionConverterUtil.convertToListFromList(channelList, new Converter<AfHomePageChannelDo, AfHomePageChannelVo>() {
							@Override
							public AfHomePageChannelVo convert(AfHomePageChannelDo source) {
								return parseDoToVo(source);
							}
						});
					}
				}catch(Exception e){
					logger.error("channelList convertToListFromList error"+e);
				}
				 List<AfResourceH5ItemDo> tabbarList  = new ArrayList<AfResourceH5ItemDo>();
				  tabbarList = afResourceH5ItemService.getByTagAndValue2(TABBAR,TABBAR_HOME_TOP);
				 
				// 顶部导航信息


//				String topBanner = AfResourceType.HomeBannerV401.getCode();
//				// 正式环境和预发布环境区分
//				if (Constants.INVELOMENT_TYPE_ONLINE.equals(envType) || Constants.INVELOMENT_TYPE_TEST.equals(envType)) {
//						  topBannerList = getBannerInfoWithResourceDolist(afResourceService.getResourceHomeListByTypeOrderBy(topBanner));
//
//				} else if (Constants.INVELOMENT_TYPE_PRE_ENV.equals(envType)) {
//						  topBannerList = getBannerInfoWithResourceDolist(afResourceService.getResourceHomeListByTypeOrderByOnPreEnv(topBanner));
//
//				}
//				if(userId != null){
//				 toAddImage(topBannerList,AfAdvertisePositionCode.HOME_TOP_BANNER.getCode(),userId);
//				}

				   String sloganImage = "";
				   List<AfResourceH5ItemDo> sloganList = new ArrayList<AfResourceH5ItemDo>();
						  sloganList =    afResourceH5ItemService.getByTagAndValue2(ASJ_IMAGES,HOME_IAMGE_SLOGAN);
				     if(sloganList != null && sloganList.size() >0){
				    	 sloganImage = sloganList.get(0).getValue3();
				     }
				// 快速导航信息
				Map<String, Object> navigationInfo =  new  HashMap<String, Object>();
				
			    navigationInfo = getNavigationInfoWithResourceDolist(
								afResourceService.getHomeIndexListByOrderby(AfResourceType.HomeNavigation.getCode()),navigationBackground);
//				
				// 新增运营位1,快捷导航上方活动专场

//				  if(navigationUpOne == null || navigationUpOne.size()<1){
//					  navigationUpOne = 	getNavigationUpOneResourceDoList(
//								afResourceService.getNavigationUpOneResourceDoList(AfResourceType.HomeNavigationUpOneV401.getCode()));
//				if(userId != null){
//				  toAddImage(navigationUpOne,AfAdvertisePositionCode.HOME_NAVIGATION_UP_ONE.getCode(),userId);
//			    }

				// 新增运营位2,快捷导航下方活动专场
//						  navigationDownOne = getNavigationDownTwoResourceDoList(afResourceService
//									.getNavigationDownTwoResourceDoList(AfResourceType.HomeNavigationDownTwoV401.getCode()));
//				if(userId != null){
//				   toAddImage(navigationDownOne,AfAdvertisePositionCode.HOME_NAVIGATION_DOWN_ONE.getCode(),userId);
//				}

				
				// 获取金融服务入口
				
				 Map<String, Object> financialEntranceInfo =  new  HashMap<String, Object>();
						financialEntranceInfo = getFinancialEntranceInfo();
				//九宫3,6,9
				 Map<String, Object> gridViewInfo =  new  HashMap<String, Object>();
					    gridViewInfo = getGridViewInfoList();
				   //电商运营位
				 Map<String, Object> ecommerceAreaInfo =  new  HashMap<String, Object>();
					  ecommerceAreaInfo = getEcommerceAreaInfo();
				
				// 获取常驻运营位信息
		     
				List<Object> homeNomalPositionList = new  ArrayList<Object>();
					  homeNomalPositionList = getHomeNomalPositonInfoResourceDoList(afResourceService.getHomeNomalPositionList());
						
				  	Map<String, Object> flashSaleInfo = new HashMap<String, Object>();
					   //限时抢购。有活动时间，整体不加入缓存。可部分加入缓存
						AfResourceH5ItemDo  afResourceH5ItemDo = new AfResourceH5ItemDo();
						   List<AfResourceH5ItemDo>  flashSaleList =  afResourceH5ItemService.getByTagAndValue2(ASJ_IMAGES,HOME_FLASH_SALE_FLOOR_IMAGE);
						     if(flashSaleList != null && flashSaleList.size() >0){
						    	  afResourceH5ItemDo = flashSaleList.get(0);
						     }
						
						//活动信息
						AfResourceDo afResourceHomeSecKillDo = afResourceService.getSingleResourceBytype("HOME_SECKILL_CONFIG");
						
						List<HomePageSecKillGoods> flashSaleGoodsList = afSeckillActivityService.getHomePageSecKillGoods(userId, afResourceHomeSecKillDo.getValue(),0, 1);
						List<Map<String, Object>> flashSaleGoods = getGoodsInfoList(flashSaleGoodsList,HOME_FLASH_SALE_FLOOR_IMAGE,afResourceH5ItemDo);
						String flashSaleContent = "";
						String flashSaleImageUrl = "";
						String flashSaleType = "";
						if(afResourceH5ItemDo !=null){
							flashSaleContent = afResourceH5ItemDo.getValue1();
							flashSaleImageUrl = afResourceH5ItemDo.getValue3();
							flashSaleType   = afResourceH5ItemDo.getValue4();
						}
		
						//大于等于10个显示
						if(flashSaleGoods.size()>=10 && StringUtil.isNotEmpty(flashSaleImageUrl)){
							flashSaleInfo.put("content",flashSaleContent);
							flashSaleInfo.put("imageUrl",flashSaleImageUrl);
							flashSaleInfo.put("type",flashSaleType);
							flashSaleInfo.put("currentTime", new Date().getTime());
							if(flashSaleGoodsList != null && flashSaleGoodsList.size() >0){
								flashSaleInfo.put("startTime", flashSaleGoodsList.get(0).getActivityStart().getTime());
								flashSaleInfo.put("endTime", flashSaleGoodsList.get(0).getActivityEnd().getTime());
							}else{
						
								flashSaleInfo.put("startTime", DateUtil.getToday().getTime());
								flashSaleInfo.put("endTime", DateUtil.getTodayLast().getTime());
							}
							flashSaleInfo.put("goodsList", flashSaleGoods);
						}
						
						
						
						//品质新品
					    Map<String, Object> newProduct = new HashMap<String, Object>();
					    //整体缓存取
					try{
					      //数据库查
						  List<Object> newProductGoodsIdList = new ArrayList<Object>();
						  List<AfResourceH5ItemDo>  newProductList =  afResourceH5ItemService.getByTag(NEW_GOODS);
						  if(newProductList != null && newProductList.size() >0 ){
							 boolean newProductTopImage = false;
							 boolean newProductGoodsList = false;
							 for(AfResourceH5ItemDo newProductDo:newProductList ){
								 if(TOP_IMAGE.equals(newProductDo.getValue2())){
									 String imageUrl = newProductDo.getValue3();
									 if(StringUtil.isNotEmpty(imageUrl)){
										 newProduct.put("imageUrl", newProductDo.getValue3());
										 newProduct.put("content", newProductDo.getValue1());
										 newProduct.put("type", newProductDo.getValue4());
										 newProductTopImage = true;
									 }
									 
								 }else  if(GOODS.equals(newProductDo.getValue2())){
									if(newProductDo.getValue1() != null){
										 String imageUrl = newProductDo.getValue3();
										 if(StringUtil.isNotEmpty(imageUrl)){
											Map<String, Object> newProductInfo = new HashMap<String, Object>();
											newProductInfo.put("content", newProductDo.getValue1());
											newProductInfo.put("sort", newProductDo.getSort());
											newProductInfo.put("imageUrl", newProductDo.getValue3());
											newProductInfo.put("type", newProductDo.getValue4());
										    newProductGoodsIdList.add(newProductInfo);
										    newProductGoodsList = true;
										 }
									}
							     }
								 
						     }
							 if(newProductGoodsList &&newProductTopImage){
								 List<Object> newProductGoodsIdListConvert  = getNewProductGoodsIdList(newProductGoodsIdList);
								 newProduct.put("newProductList", newProductGoodsIdListConvert);
								// 品质新品
									if (!newProduct.isEmpty()) {
										data.put("newProduct", newProduct);
									}
							 }
						 }
						  
					}catch(Exception e){
							logger.error("get newProduct error"+e);
					}
							     Map<String, Object> activityGoodsInfo = new HashMap<String, Object>();
						    // 精选活动
						 try{
									 List<AfResourceH5ItemDo>  activityList =  afResourceH5ItemService.getByTag(HOME_SEL);
									 if(activityList != null && activityList.size() >0 ){
										 List<Object> activityGoodsInfoList1 = new ArrayList<Object>(); 
										 boolean activityTopImage = false;
										 boolean activityGoodsList = false;
										 for(AfResourceH5ItemDo activityDo:activityList ){
											  if(GOODS.equals(activityDo.getValue2())){
											           List<Long> goodsIdList = new ArrayList<Long>(); 
													   if(activityDo.getValue1() != null){
														 String goodsIds = activityDo.getValue1();
														 String[] goodsId = goodsIds.split(",");  
														 Long[] gids = (Long[]) ConvertUtils.convert(goodsId,Long.class);
															 for(Long gid: gids){
																 goodsIdList.add(gid);
															 }
													    }
													    List<HomePageSecKillGoods> goodsLists = afSeckillActivityService.getHomePageSecKillGoodsByConfigureResourceH5(userId,goodsIdList);
													  //重新排序，in 会重排，sql里保持排序，性能差
														  List<HomePageSecKillGoods> goodsList = new  ArrayList<HomePageSecKillGoods>();
														 // List<Long> goodsIdList = new ArrayList<Long>();    
														  if(goodsLists != null && goodsLists.size()>0){
															  for(Long goodsid:goodsIdList){
																   for(HomePageSecKillGoods goods:goodsLists ){
																	   if(goodsid.longValue() == goods.getGoodsId().longValue()){
																		   goodsList.add(goods);
																	   }
																   }
															  }
														  }
													    
													    List<Map<String, Object>> activityGoodsInfoList = getGoodsInfoList(goodsList,HOME_SEL,null);
														//没有商品整块不显示
														String imageUrl = activityDo.getValue3();
														String type = activityDo.getValue4();
														if(activityGoodsInfoList != null && activityGoodsInfoList.size()  >0 && StringUtil.isNotEmpty(imageUrl)){
															Map<String, Object> goodsInfo = new HashMap<String, Object>();
															goodsInfo.put("goodsList", activityGoodsInfoList);
															goodsInfo.put("imageUrl", imageUrl);
															//1+n上图类型
															goodsInfo.put("type", H5_URL);
															//1+多
															goodsInfo.put("content",activityDo.getValue4() );
															activityGoodsInfoList1.add(goodsInfo);
															activityGoodsList = true;
														}
										      }else  if(TOP_IMAGE.equals(activityDo.getValue2())){
										    	  if( activityDo.getValue3() != null && !"".equals(activityDo.getValue3())){
													 activityGoodsInfo.put("imageUrl", activityDo.getValue3());
													 activityGoodsInfo.put("content", activityDo.getValue1());
													 activityGoodsInfo.put("type", activityDo.getValue4());	
													 activityTopImage = true;
										    	  }
											  } 
									    }
										 if(activityGoodsList && activityTopImage){
											 activityGoodsInfo.put("activityGoodsList", activityGoodsInfoList1);
										 }
								    }
						 }catch(Exception e){
							 logger.error("activityGoodsList goodsInfo error "+ e);
						 }
						 Map<String, Object> brandInfo = new HashMap<String, Object>();
						 // 大牌汇聚
						 try{
							 
						        List<Object> brandList1 = new ArrayList<Object>(); 
								List<AfResourceH5ItemDo>  brandGoodsList =  afResourceH5ItemService.getByTag(MAJOR_SUIT);
								if(brandGoodsList != null && brandGoodsList.size() >0 ){
									 boolean brandTopImage = false;
									 boolean brandGoods = false;
									//循环查，数据量不多（查一次会重新把数据排序，对每个商品加入对应数据复杂， FIELD()列表中进行查找效率慢。）
										 for(AfResourceH5ItemDo activityDo:brandGoodsList ){
											  if(GOODS.equals(activityDo.getValue2())){
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
														List<Map<String, Object>> brandGoodsInfoList = getGoodsInfoList(goodsList,MAJOR_SUIT,activityDo);
														String imageUrl =  activityDo.getValue3() ;
														if(brandGoodsInfoList != null && brandGoodsInfoList.size()>0 && StringUtil.isNotEmpty(imageUrl)){
															Map<String, Object> goodsInfo = new HashMap<String, Object>();
															goodsInfo.put("brandGoodsList", brandGoodsInfoList);
															goodsInfo.put("imageUrl",imageUrl );
															brandList1.add(goodsInfo);
															brandGoods = true;
														}
										      	}else if(TOP_IMAGE.equals(activityDo.getValue2())){
										      		String imageUrl =  activityDo.getValue3() ;
										      		if( StringUtil.isNotEmpty(imageUrl)){
														 brandInfo.put("imageUrl", activityDo.getValue3());
														 brandInfo.put("content", activityDo.getValue1());
														 brandInfo.put("type", activityDo.getValue4());
														 brandTopImage = true;
										      		}
										      	}
									    }
										 if(brandGoods && brandTopImage){
											 brandInfo.put("brandList", brandList1);
										 }
								}
//						 }
						 }catch(Exception e){
							logger.error("home brandList error = "+e); 
				 }
						 
						 
						 
				if(tabbarList != null && tabbarList.size() >0){
					    	 AfResourceH5ItemDo recommend = tabbarList.get(0);
					    	 Map<String, Object> topTab = new HashMap<String, Object>();
					    		//Object topTab = new Object();
					    	 if(StringUtil.isNotEmpty(recommend.getValue3())&& StringUtil.isNotEmpty(recommend.getValue1())
					    			 && StringUtil.isNotEmpty(recommend.getValue4())
					    			 ){
					    		 topTab.put("imageUrl", recommend.getValue3());
						    	 topTab.put("type", recommend.getValue4());
						    	 topTab.put("content", recommend.getValue1());
						    	 data.put("topTab", topTab);
					    	 }
			     }		 
						 
				if(searchBackground != null && searchBackground.getValue() != null){
								Map<String, Object> searchBoxBgImage = new HashMap<String, Object>();
								searchBoxBgImage.put("backgroundImage", searchBackground.getValue());
								searchBoxBgImage.put("color", searchBackground.getValue3());
								searchBoxBgImage.put("showType", searchBackground.getSecType());
								data.put("searchBoxBgImage", searchBoxBgImage);
				}		 
						 
				if(tabList != null && tabList.size()>0){
					data.put("tabList", tabList);
				}
		
				// 九宫板块信息
				if (!gridViewInfo.isEmpty()) {
					if(nineBackground != null){
						gridViewInfo.put("backgroundImage", nineBackground.getValue());
						gridViewInfo.put("color", nineBackground.getValue3());
						gridViewInfo.put("showType", nineBackground.getSecType());
					}
					data.put("gridViewInfo", gridViewInfo);
				}
				if (!ecommerceAreaInfo.isEmpty()) {
					data.put("ecommerceAreaInfo", ecommerceAreaInfo);
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
				if (!backgroundList.isEmpty()) {
					data.put("backgroundList", backgroundList);
				}
				// 常驻运营位
				if (!homeNomalPositionList.isEmpty()) {
					data.put("normalPositionList", homeNomalPositionList);
				}
				// 电商板块信息
				if (!ecommerceAreaInfo.isEmpty()) {
					data.put("ecommerceAreaInfo", ecommerceAreaInfo);
				}

				// 金融服务入口
				if (!financialEntranceInfo.isEmpty()) {
					data.put("financialEntranceInfo", financialEntranceInfo);
				}
				// 	限时抢购
				if (!flashSaleInfo.isEmpty()) {
					data.put("flashSaleInfo", flashSaleInfo);
				}
				
				// 活动运营商品
				if (!activityGoodsInfo.isEmpty()) {
					data.put("activityGoodsInfo", activityGoodsInfo);
				}
				// 大牌汇聚
				if (!brandInfo.isEmpty()) {
					data.put("brandInfo", brandInfo);
				}
				
				 bizCacheUtil.saveMap(cacheKey, data, Constants.MINITS_OF_TWO);	 */
         }
           try{

			//未登录显示，新用户（商城没有一笔支付成功（包括退款）的订单） 显示，  其余均不显示
			 boolean newExclusive = false; //s是否符合新人专享
			 if(userId != null ){
				int  selfsupportPaySuccessOrder = 	afOrderService.getSelfsupportPaySuccessOrderByUserId(userId);
				    if(selfsupportPaySuccessOrder < 1 ){
					 newExclusive = true;
				}
			 }
             if(userId == null  || newExclusive){
						// 新人专享位（加入缓存）
					  	List<AfResourceH5ItemDo>  newExclusiveList = new ArrayList<AfResourceH5ItemDo>();
						newExclusiveList =  bizCacheUtil.getObjectList(CacheConstants.ASJ_HOME_PAGE.ASJ_HOME_NEW_EXCLUSIVE.getCode());
					    if(newExclusiveList == null || newExclusiveList.size()<1){
						  newExclusiveList =  afResourceH5ItemService.getByTagAndValue2(OPERATE,NEW_EXCLUSIVE);
					      bizCacheUtil.saveObjectListExpire(CacheConstants.ASJ_HOME_PAGE.ASJ_HOME_NEW_EXCLUSIVE.getCode(), newExclusiveList, Constants.MINITS_OF_TWO);
					    }
				
					
					       if(newExclusiveList != null && newExclusiveList.size() >0){
						    AfResourceH5ItemDo newExclusiveDo = newExclusiveList.get(0);
						    Map<String, Object> newExclusiveInfo = new HashMap<String, Object>();
						    		//Object topTab = new Object();
						     if(StringUtil.isNotEmpty(newExclusiveDo.getValue3()) && StringUtil.isNotEmpty(newExclusiveDo.getValue1())
						    			 && StringUtil.isNotEmpty(newExclusiveDo.getValue4())
						    			 ){
						    		 newExclusiveInfo.put("imageUrl", newExclusiveDo.getValue3());
						    		 newExclusiveInfo.put("type", newExclusiveDo.getValue4());
						    		 newExclusiveInfo.put("content", newExclusiveDo.getValue1());
							    	 data.put("newExclusiveInfo", newExclusiveInfo);
					     	}
					 }
	      }
           //
           }catch(Exception  e){
          	 logger.error("getHomeInfoV3 newExclusive error = " + e);
           }
		        List<Object> navigationUpOne = new  ArrayList<Object>();
		 		List<Object> navigationDownOne = new  ArrayList<Object>();
		 		List<Object> topBannerList = new ArrayList<Object>();

//               // 去掉缓存
//		   	    String navigationUpOneCacheKey = CacheConstants.ASJ_HOME_PAGE.ADVERTISE_HOME_NAVIGATION_UP_ONE +":"+userId;
//		   	    String navigationDownOneCacheKey = CacheConstants.ASJ_HOME_PAGE.ADVERTISE_HOME_NAVIGATION_DOWN_ONE +":"+userId;
//		   	    String topBannerCacheKey = CacheConstants.ASJ_HOME_PAGE.ADVERTISE_HOME_TOP_BANNER +":"+userId;
//		   	    navigationUpOne = null;
//		   	    navigationDownOne = null;
//		   	    topBannerList = null;
//		   	    navigationUpOne = bizCacheUtil.getObjectList(navigationUpOneCacheKey);
//		   	    navigationDownOne = bizCacheUtil.getObjectList(navigationDownOneCacheKey);
//		   	    topBannerList = bizCacheUtil.getObjectList(topBannerCacheKey);
		   	    if(navigationUpOne == null || navigationUpOne.size()<1){
			   	    navigationUpOne = 	getNavigationUpOneResourceDoList(
				            afResourceService.getNavigationUpOneResourceDoList(AfResourceType.HomeNavigationUpOneV401.getCode()));
			   	   if(userId != null){
			   	      toAddImage(navigationUpOne,AfAdvertisePositionCode.HOME_NAVIGATION_UP_ONE.getCode(),userId);
			   	   }
			        if(navigationUpOne!= null && navigationUpOne.size()>0){
			        	navigationUpOne =  navigationUpOne.subList(0, 1);
			        	// bizCacheUtil.saveObjectListExpire(navigationUpOneCacheKey, navigationUpOne, Constants.MINITS_OF_TWO);
			        }

		   	    }
		   	   if(navigationDownOne == null || navigationDownOne.size()<1){
				   navigationDownOne = getNavigationDownTwoResourceDoList(afResourceService
							.getNavigationDownTwoResourceDoList(AfResourceType.HomeNavigationDownTwoV401.getCode()));
				   if(userId != null){
					   toAddImage(navigationDownOne,AfAdvertisePositionCode.HOME_NAVIGATION_DOWN_ONE.getCode(),userId);
				   }
				    if(navigationDownOne!= null && navigationDownOne.size()>0){
				    	navigationDownOne = navigationDownOne.subList(0, 1);
				    	//bizCacheUtil.saveObjectListExpire(navigationDownOneCacheKey, navigationDownOne, Constants.MINITS_OF_TWO);
				    }


		   	   }
			   	if(topBannerList == null || topBannerList.size()<1){
					String topBanner = AfResourceType.HomeBannerV401.getCode();
					// 正式环境和预发布环境区分
					if (Constants.INVELOMENT_TYPE_ONLINE.equals(envType) || Constants.INVELOMENT_TYPE_TEST.equals(envType)) {
							  topBannerList = getBannerInfoWithResourceDolist(afResourceService.getResourceHomeListByTypeOrderBy(topBanner));

					} else if (Constants.INVELOMENT_TYPE_PRE_ENV.equals(envType)) {
							  topBannerList = getBannerInfoWithResourceDolist(afResourceService.getResourceHomeListByTypeOrderByOnPreEnv(topBanner));

					}
					 if(userId != null){
						 toAddImage(topBannerList,AfAdvertisePositionCode.HOME_TOP_BANNER.getCode(),userId);
					 }
			   	  if(topBannerList!= null && topBannerList.size()>0){
					//bizCacheUtil.saveObjectListExpire(topBannerCacheKey, topBannerList, Constants.MINITS_OF_TWO);
			   	  }
			   	}


         	// 首页背景图
         // 顶部轮播
				if (!topBannerList.isEmpty()) {
					data.put("topBannerList", topBannerList);
				}
				// 新增运营位1,快捷导航上方活动专场
				if (!navigationUpOne.isEmpty()) {
					data.put("navigationUpOneList", navigationUpOne);
				}

				// 新增运营位2,快捷导航下方活动专场
				if (!navigationDownOne.isEmpty()) {
					data.put("navigationDownOneList", navigationDownOne);
				}

		//首页搜索栏位等优化为可配置 cxk 2018年5月23日13:49:18
//		try{
//			HashMap<String, Object> newConfigInfo =  (HashMap<String, Object>)bizCacheUtil.getMap(CacheConstants.ASJ_HOME_PAGE.NEW_CONFIG_INFO.getCode());
//			if (newConfigInfo == null || newConfigInfo.size()<1) {
//				List<AfResourceDo> backgroundList  = new ArrayList<AfResourceDo>();
//				backgroundList = afResourceService.getBackGroundByTypeAndStatusOrder(ResourceType.CUBE_HOMEPAGE_BACKGROUND_ASJ.getCode());
//				newConfigInfo = new HashMap<String, Object>();
//				newConfigInfo.put("searchBackColor","");
//				newConfigInfo.put("searchFontColor","");
//				newConfigInfo.put("searchGlass","");
//				newConfigInfo.put("messageIcon","");
//				newConfigInfo.put("fontColor","");
//				newConfigInfo.put("remainFontColor","");
//				newConfigInfo.put("navigationOpen","");
//				newConfigInfo.put("navigationClose","");
//				newConfigInfo.put("bannerBackImg","");
//				newConfigInfo.put("statusBarColor","");
//				for (AfResourceDo afResourceDo :	backgroundList) {
//					if (StringUtil.equals(afResourceDo.getValue1(),AfResourceType.APP_SEARCH_BACK_COLOR.getCode())) {
//						//app搜索栏背景色
//						newConfigInfo.put("searchBackColor",afResourceDo.getValue3());
//					}else if (StringUtil.equals(afResourceDo.getValue1(),AfResourceType.APP_SEARCH_FONT_COLOR.getCode())) {
//						//app搜索栏文字颜色
//						newConfigInfo.put("searchFontColor",afResourceDo.getValue3());
//					}else if(StringUtil.equals(afResourceDo.getValue1(),AfResourceType.APP_SEARCH_GLASS.getCode())) {
//						//app搜索栏放大镜
//						newConfigInfo.put("searchGlass",afResourceDo.getValue());
//					}else if (StringUtil.equals(afResourceDo.getValue1(),AfResourceType.APP_MESSAGE_ICON.getCode())) {
//						//app消息图标
//						newConfigInfo.put("messageIcon",afResourceDo.getValue());
//					}else if (StringUtil.equals(afResourceDo.getValue1(),AfResourceType.APP_NAVIGATION_BAR.getCode())) {
//						//app导航栏文字及下划线选中颜色
//						newConfigInfo.put("fontColor",afResourceDo.getValue3());
//					}else if (StringUtil.equals(afResourceDo.getValue1(),AfResourceType.APP_NAVIGATION_BAR_REMAIN.getCode())) {
//						//app导航栏文字未选中颜色
//						newConfigInfo.put("remainFontColor",afResourceDo.getValue3());
//					}else if (StringUtil.equals(afResourceDo.getValue1(),AfResourceType.APP_NAVIGATION_OPEN.getCode())) {
//						//app导航栏展开控件
//						newConfigInfo.put("navigationOpen",afResourceDo.getValue());
//					}else if (StringUtil.equals(afResourceDo.getValue1(),AfResourceType.APP_NAVIGATION_CLOSE.getCode())) {
//						//app导航栏关闭控件
//						newConfigInfo.put("navigationClose",afResourceDo.getValue());
//					}else if (StringUtil.equals(afResourceDo.getValue1(),AfResourceType.BANNER_BACKIMG.getCode())) {
//						//Banner背景图片
//						newConfigInfo.put("bannerBackImg",afResourceDo.getValue());
//					}else if (StringUtil.equals(afResourceDo.getValue1(),AfResourceType.APP_STATUS_BAR_COLOR.getCode())) {
//                        //状态栏颜色0黑色，1白色
//                        newConfigInfo.put("statusBarColor",afResourceDo.getValue3());
//					}
//				}
//				//添加缓存
//				bizCacheUtil.saveMap(CacheConstants.ASJ_HOME_PAGE.NEW_CONFIG_INFO.getCode(), newConfigInfo, Constants.MINITS_OF_TWO);
//			}
//			data.put("newConfigInfo",newConfigInfo);
//		}catch(Exception  e){
//			logger.error("getHomeInfoV3 newConfigInfo error = " + e);
//		}


		//新人运营位查库
		logger.info("getHomeInfoV3data = " + data);
		//浏览任务多少时间算完成任务
		AfResourceDo afResourceDo = afResourceService.getSingleResourceBytype("TASK_BROWSE_TIME");
		if(afResourceDo == null){
			data.put("taskBrowseTime",10);
		}else{
			if(afResourceDo.getValue() == null){
				data.put("taskBrowseTime",10);
			}else {
				data.put("taskBrowseTime",afResourceDo.getValue());
			}
		}
		resp.setResponseData(data);
		return resp;
	}

	class GetHomeCache implements Runnable {

		protected  final Logger logger = LoggerFactory.getLogger(GetHomeChannelApi.GetHomeChannelCache.class);

		private String  deviceType;
		private Long userId;
		private String  firstKey;
		private String  secondKey;
		@Resource
		BizCacheUtil bizCacheUtil;
		GetHomeCache(String firstKey,String secondKey,Long userId,String deviceType) {

			this.deviceType = deviceType;
			this.userId = userId;
			this.firstKey = firstKey;
			this.secondKey = secondKey;
		}
		@Override
		public void run() {
			logger.info("pool:getHomeCacheInfo"+Thread.currentThread().getName() + "getHomeCacheInfo");
			try{
				getHomeCacheInfo( firstKey,secondKey , userId,deviceType);

			}catch (Exception e){
				logger.error("pool:getHomeCacheInfo error for" + e);
			}
		}
	}
	private Map<String, Object> getHomeCacheInfo(String firstKey , String secondKey ,Long userId,String deviceType) {
		//获取所有活动
		Map<String, Object> data =  toAddHomeCacheInfo(userId, deviceType);
		if(data != null) {
			bizCacheUtil.saveMap(firstKey, data, Constants.MINITS_OF_TWO);
			bizCacheUtil.saveMapForever(secondKey, data);
		}
		return null;
	}


	Map<String, Object> toAddHomeCacheInfo(Long userId,String deviceType){
		Map<String, Object> data =  new  HashMap<String, Object>();
		AfResourceDo   searchBackground = new  AfResourceDo();
		AfResourceDo   nineBackground   =   new  AfResourceDo();
		AfResourceDo   navigationBackground = new  AfResourceDo();
		// 背景图配置
		List<AfResourceDo> backgroundList  = new ArrayList<AfResourceDo>();
		backgroundList = afResourceService
				.getBackGroundByTypeAndStatusOrder(ResourceType.CUBE_HOMEPAGE_BACKGROUND_ASJ.getCode());

		// 背景图
		if (backgroundList != null && !backgroundList.isEmpty()) {
			for(AfResourceDo background: backgroundList ){

				if(AfResourceType.HOME_SEARCH.getCode().equals(background.getValue1())){
					if (!StringUtils.equals(deviceType, "IPHONEX")) {
						searchBackground = background;
					}
				}
				if(AfResourceType.HOME_SEARCH_IPHONEX.getCode().equals(background.getValue1())){
					if (StringUtils.equals(deviceType, "IPHONEX")) {
						searchBackground = background;
					}
				}

				if(AfResourceType.HOME_NINE_GRID.getCode().equals(background.getValue1())){
					nineBackground  =    background;
				}
				if(AfResourceType.HOME_NAVIGATION.getCode().equals(background.getValue1())){
					navigationBackground  = background;
				}
				if(searchBackground.getValue() != null && nineBackground.getValue()!= null & navigationBackground.getValue() != null){
					break;
				}
			}
		}


		// tabList[]
		List<AfHomePageChannelDo> channelList =   new ArrayList<AfHomePageChannelDo>();
		channelList =  afHomePageChannelService.getListOrderBySortDesc();
		List<AfHomePageChannelVo> tabList = new ArrayList<AfHomePageChannelVo>();
		try{
			if (CollectionUtil.isNotEmpty(channelList)) {
				tabList = CollectionConverterUtil.convertToListFromList(channelList, new Converter<AfHomePageChannelDo, AfHomePageChannelVo>() {
					@Override
					public AfHomePageChannelVo convert(AfHomePageChannelDo source) {
						return parseDoToVo(source);
					}
				});
			}
		}catch(Exception e){
			logger.error("channelList convertToListFromList error"+e);
		}
		List<AfResourceH5ItemDo> tabbarList  = new ArrayList<AfResourceH5ItemDo>();
		tabbarList = afResourceH5ItemService.getByTagAndValue2(TABBAR,TABBAR_HOME_TOP);

		// 顶部导航信息


	//				String topBanner = AfResourceType.HomeBannerV401.getCode();
	//				// 正式环境和预发布环境区分
	//				if (Constants.INVELOMENT_TYPE_ONLINE.equals(envType) || Constants.INVELOMENT_TYPE_TEST.equals(envType)) {
	//						  topBannerList = getBannerInfoWithResourceDolist(afResourceService.getResourceHomeListByTypeOrderBy(topBanner));
	//
	//				} else if (Constants.INVELOMENT_TYPE_PRE_ENV.equals(envType)) {
	//						  topBannerList = getBannerInfoWithResourceDolist(afResourceService.getResourceHomeListByTypeOrderByOnPreEnv(topBanner));
	//
	//				}
	//				if(userId != null){
	//				 toAddImage(topBannerList,AfAdvertisePositionCode.HOME_TOP_BANNER.getCode(),userId);
	//				}

		String sloganImage = "";
		List<AfResourceH5ItemDo> sloganList = new ArrayList<AfResourceH5ItemDo>();
		sloganList =    afResourceH5ItemService.getByTagAndValue2(ASJ_IMAGES,HOME_IAMGE_SLOGAN);
		if(sloganList != null && sloganList.size() >0){
			sloganImage = sloganList.get(0).getValue3();
		}
		// 快速导航信息
		Map<String, Object> navigationInfo =  new  HashMap<String, Object>();

		navigationInfo = getNavigationInfoWithResourceDolist(
				afResourceService.getHomeIndexListByOrderby(AfResourceType.HomeNavigation.getCode()),navigationBackground);
	//
		// 新增运营位1,快捷导航上方活动专场

	//				  if(navigationUpOne == null || navigationUpOne.size()<1){
	//					  navigationUpOne = 	getNavigationUpOneResourceDoList(
	//								afResourceService.getNavigationUpOneResourceDoList(AfResourceType.HomeNavigationUpOneV401.getCode()));
	//				if(userId != null){
	//				  toAddImage(navigationUpOne,AfAdvertisePositionCode.HOME_NAVIGATION_UP_ONE.getCode(),userId);
	//			    }

		// 新增运营位2,快捷导航下方活动专场
	//						  navigationDownOne = getNavigationDownTwoResourceDoList(afResourceService
	//									.getNavigationDownTwoResourceDoList(AfResourceType.HomeNavigationDownTwoV401.getCode()));
	//				if(userId != null){
	//				   toAddImage(navigationDownOne,AfAdvertisePositionCode.HOME_NAVIGATION_DOWN_ONE.getCode(),userId);
	//				}


		// 获取金融服务入口

		Map<String, Object> financialEntranceInfo =  new  HashMap<String, Object>();
		financialEntranceInfo = getFinancialEntranceInfo();
		//九宫3,6,9
		Map<String, Object> gridViewInfo =  new  HashMap<String, Object>();
		gridViewInfo = getGridViewInfoList();
		//电商运营位
		Map<String, Object> ecommerceAreaInfo =  new  HashMap<String, Object>();
		ecommerceAreaInfo = getEcommerceAreaInfo();

		// 获取常驻运营位信息

		List<Object> homeNomalPositionList = new  ArrayList<Object>();
		homeNomalPositionList = getHomeNomalPositonInfoResourceDoList(afResourceService.getHomeNomalPositionList());

		Map<String, Object> flashSaleInfo = new HashMap<String, Object>();
		//限时抢购。有活动时间，整体不加入缓存。可部分加入缓存
		AfResourceH5ItemDo  afResourceH5ItemDo = new AfResourceH5ItemDo();
		List<AfResourceH5ItemDo>  flashSaleList =  afResourceH5ItemService.getByTagAndValue2(ASJ_IMAGES,HOME_FLASH_SALE_FLOOR_IMAGE);
		if(flashSaleList != null && flashSaleList.size() >0){
			afResourceH5ItemDo = flashSaleList.get(0);
		}

		//活动信息
		AfResourceDo afResourceHomeSecKillDo = afResourceService.getSingleResourceBytype("HOME_SECKILL_CONFIG");

		List<HomePageSecKillGoods> flashSaleGoodsList = afSeckillActivityService.getHomePageSecKillGoods(userId, afResourceHomeSecKillDo.getValue(),0, 1);
		List<Map<String, Object>> flashSaleGoods = getGoodsInfoList(flashSaleGoodsList,HOME_FLASH_SALE_FLOOR_IMAGE,afResourceH5ItemDo);
		String flashSaleContent = "";
		String flashSaleImageUrl = "";
		String flashSaleType = "";
		if(afResourceH5ItemDo !=null){
			flashSaleContent = afResourceH5ItemDo.getValue1();
			flashSaleImageUrl = afResourceH5ItemDo.getValue3();
			flashSaleType   = afResourceH5ItemDo.getValue4();
		}

		//大于等于10个显示
		if(flashSaleGoods.size()>=10 && StringUtil.isNotEmpty(flashSaleImageUrl)){
			flashSaleInfo.put("content",flashSaleContent);
			flashSaleInfo.put("imageUrl",flashSaleImageUrl);
			flashSaleInfo.put("type",flashSaleType);
			flashSaleInfo.put("currentTime", new Date().getTime());
			if(flashSaleGoodsList != null && flashSaleGoodsList.size() >0){
				flashSaleInfo.put("startTime", flashSaleGoodsList.get(0).getActivityStart().getTime());
				flashSaleInfo.put("endTime", flashSaleGoodsList.get(0).getActivityEnd().getTime());
			}else{

				flashSaleInfo.put("startTime", DateUtil.getToday().getTime());
				flashSaleInfo.put("endTime", DateUtil.getTodayLast().getTime());
			}
			flashSaleInfo.put("goodsList", flashSaleGoods);
		}



		//品质新品
		Map<String, Object> newProduct = new HashMap<String, Object>();
		//整体缓存取
		try{
			//数据库查
			List<Object> newProductGoodsIdList = new ArrayList<Object>();
			List<AfResourceH5ItemDo>  newProductList =  afResourceH5ItemService.getByTag(NEW_GOODS);
			if(newProductList != null && newProductList.size() >0 ){
				boolean newProductTopImage = false;
				boolean newProductGoodsList = false;
				for(AfResourceH5ItemDo newProductDo:newProductList ){
					if(TOP_IMAGE.equals(newProductDo.getValue2())){
						String imageUrl = newProductDo.getValue3();
						if(StringUtil.isNotEmpty(imageUrl)){
							newProduct.put("imageUrl", newProductDo.getValue3());
							newProduct.put("content", newProductDo.getValue1());
							newProduct.put("type", newProductDo.getValue4());
							newProductTopImage = true;
						}

					}else  if(GOODS.equals(newProductDo.getValue2())){
						if(newProductDo.getValue1() != null){
							String imageUrl = newProductDo.getValue3();
							if(StringUtil.isNotEmpty(imageUrl)){
								Map<String, Object> newProductInfo = new HashMap<String, Object>();
								newProductInfo.put("content", newProductDo.getValue1());
								newProductInfo.put("sort", newProductDo.getSort());
								newProductInfo.put("imageUrl", newProductDo.getValue3());
								newProductInfo.put("type", newProductDo.getValue4());
								newProductGoodsIdList.add(newProductInfo);
								newProductGoodsList = true;
							}
						}
					}

				}
				if(newProductGoodsList &&newProductTopImage){
					List<Object> newProductGoodsIdListConvert  = getNewProductGoodsIdList(newProductGoodsIdList);
					newProduct.put("newProductList", newProductGoodsIdListConvert);
					// 品质新品
					if (!newProduct.isEmpty()) {
						data.put("newProduct", newProduct);
					}
				}
			}

		}catch(Exception e){
			logger.error("get newProduct error"+e);
		}
		Map<String, Object> activityGoodsInfo = new HashMap<String, Object>();
		// 精选活动
		try{
			List<AfResourceH5ItemDo>  activityList =  afResourceH5ItemService.getByTag(HOME_SEL);
			if(activityList != null && activityList.size() >0 ){
				List<Object> activityGoodsInfoList1 = new ArrayList<Object>();
				boolean activityTopImage = false;
				boolean activityGoodsList = false;
				for(AfResourceH5ItemDo activityDo:activityList ){
					if(GOODS.equals(activityDo.getValue2())){
						List<Long> goodsIdList = new ArrayList<Long>();
						if(activityDo.getValue1() != null){
							String goodsIds = activityDo.getValue1();
							String[] goodsId = goodsIds.split(",");
							Long[] gids = (Long[]) ConvertUtils.convert(goodsId,Long.class);
							for(Long gid: gids){
								goodsIdList.add(gid);
							}
						}
						List<HomePageSecKillGoods> goodsLists = afSeckillActivityService.getHomePageSecKillGoodsByConfigureResourceH5(userId,goodsIdList);
						//重新排序，in 会重排，sql里保持排序，性能差
						List<HomePageSecKillGoods> goodsList = new  ArrayList<HomePageSecKillGoods>();
						// List<Long> goodsIdList = new ArrayList<Long>();
						if(goodsLists != null && goodsLists.size()>0){
							for(Long goodsid:goodsIdList){
								for(HomePageSecKillGoods goods:goodsLists ){
									if(goodsid.longValue() == goods.getGoodsId().longValue()){
										goodsList.add(goods);
									}
								}
							}
						}

						List<Map<String, Object>> activityGoodsInfoList = getGoodsInfoList(goodsList,HOME_SEL,null);
						//没有商品整块不显示
						String imageUrl = activityDo.getValue3();
						String type = activityDo.getValue4();
						if(activityGoodsInfoList != null && activityGoodsInfoList.size()  >0 && StringUtil.isNotEmpty(imageUrl)){
							Map<String, Object> goodsInfo = new HashMap<String, Object>();
							goodsInfo.put("goodsList", activityGoodsInfoList);
							goodsInfo.put("imageUrl", imageUrl);
							//1+n上图类型
							goodsInfo.put("type", H5_URL);
							//1+多
							goodsInfo.put("content",activityDo.getValue4() );
							activityGoodsInfoList1.add(goodsInfo);
							activityGoodsList = true;
						}
					}else  if(TOP_IMAGE.equals(activityDo.getValue2())){
						if( activityDo.getValue3() != null && !"".equals(activityDo.getValue3())){
							activityGoodsInfo.put("imageUrl", activityDo.getValue3());
							activityGoodsInfo.put("content", activityDo.getValue1());
							activityGoodsInfo.put("type", activityDo.getValue4());
							activityTopImage = true;
						}
					}
				}
				if(activityGoodsList && activityTopImage){
					activityGoodsInfo.put("activityGoodsList", activityGoodsInfoList1);
				}
			}
		}catch(Exception e){
			logger.error("activityGoodsList goodsInfo error "+ e);
		}
		Map<String, Object> brandInfo = new HashMap<String, Object>();
		// 大牌汇聚
		try{

			List<Object> brandList1 = new ArrayList<Object>();
			List<AfResourceH5ItemDo>  brandGoodsList =  afResourceH5ItemService.getByTag(MAJOR_SUIT);
			if(brandGoodsList != null && brandGoodsList.size() >0 ){
				boolean brandTopImage = false;
				boolean brandGoods = false;
				//循环查，数据量不多（查一次会重新把数据排序，对每个商品加入对应数据复杂， FIELD()列表中进行查找效率慢。）
				for(AfResourceH5ItemDo activityDo:brandGoodsList ){
					if(GOODS.equals(activityDo.getValue2())){
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
						List<Map<String, Object>> brandGoodsInfoList = getGoodsInfoList(goodsList,MAJOR_SUIT,activityDo);
						String imageUrl =  activityDo.getValue3() ;
						if(brandGoodsInfoList != null && brandGoodsInfoList.size()>0 && StringUtil.isNotEmpty(imageUrl)){
							Map<String, Object> goodsInfo = new HashMap<String, Object>();
							goodsInfo.put("brandGoodsList", brandGoodsInfoList);
							goodsInfo.put("imageUrl",imageUrl );
							brandList1.add(goodsInfo);
							brandGoods = true;
						}
					}else if(TOP_IMAGE.equals(activityDo.getValue2())){
						String imageUrl =  activityDo.getValue3() ;
						if( StringUtil.isNotEmpty(imageUrl)){
							brandInfo.put("imageUrl", activityDo.getValue3());
							brandInfo.put("content", activityDo.getValue1());
							brandInfo.put("type", activityDo.getValue4());
							brandTopImage = true;
						}
					}
				}
				if(brandGoods && brandTopImage){
					brandInfo.put("brandList", brandList1);
				}
			}
	//						 }
		}catch(Exception e){
			logger.error("home brandList error = "+e);
		}



		if(tabbarList != null && tabbarList.size() >0){
			AfResourceH5ItemDo recommend = tabbarList.get(0);
			Map<String, Object> topTab = new HashMap<String, Object>();
			//Object topTab = new Object();
			if(StringUtil.isNotEmpty(recommend.getValue3())&& StringUtil.isNotEmpty(recommend.getValue1())
					&& StringUtil.isNotEmpty(recommend.getValue4())
					){
				topTab.put("imageUrl", recommend.getValue3());
				topTab.put("type", recommend.getValue4());
				topTab.put("content", recommend.getValue1());
				data.put("topTab", topTab);
			}
		}

		if(searchBackground != null && searchBackground.getValue() != null){
			Map<String, Object> searchBoxBgImage = new HashMap<String, Object>();
			searchBoxBgImage.put("backgroundImage", searchBackground.getValue());
			searchBoxBgImage.put("color", searchBackground.getValue3());
			searchBoxBgImage.put("showType", searchBackground.getSecType());
			data.put("searchBoxBgImage", searchBoxBgImage);
		}

		if(tabList != null && tabList.size()>0){
			data.put("tabList", tabList);
		}

		// 九宫板块信息
		if (!gridViewInfo.isEmpty()) {
			if(nineBackground != null){
				gridViewInfo.put("backgroundImage", nineBackground.getValue());
				gridViewInfo.put("color", nineBackground.getValue3());
				gridViewInfo.put("showType", nineBackground.getSecType());
			}
			data.put("gridViewInfo", gridViewInfo);
		}
		if (!ecommerceAreaInfo.isEmpty()) {
			data.put("ecommerceAreaInfo", ecommerceAreaInfo);
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
		if (!backgroundList.isEmpty()) {
			data.put("backgroundList", backgroundList);
		}
		// 常驻运营位
		if (!homeNomalPositionList.isEmpty()) {
			data.put("normalPositionList", homeNomalPositionList);
		}
		// 电商板块信息
		if (!ecommerceAreaInfo.isEmpty()) {
			data.put("ecommerceAreaInfo", ecommerceAreaInfo);
		}

		// 金融服务入口
		if (!financialEntranceInfo.isEmpty()) {
			data.put("financialEntranceInfo", financialEntranceInfo);
		}
		// 	限时抢购
		if (!flashSaleInfo.isEmpty()) {
			data.put("flashSaleInfo", flashSaleInfo);
		}

		// 活动运营商品
		if (!activityGoodsInfo.isEmpty()) {
			data.put("activityGoodsInfo", activityGoodsInfo);
		}
		// 大牌汇聚
		if (!brandInfo.isEmpty()) {
			data.put("brandInfo", brandInfo);
		}
		try{
//			HashMap<String, Object> newConfigInfo =  (HashMap<String, Object>)bizCacheUtil.getMap(CacheConstants.ASJ_HOME_PAGE.NEW_CONFIG_INFO.getCode());
//			if (newConfigInfo == null || newConfigInfo.size()<1) {
			     HashMap<String, Object> newConfigInfo = new HashMap<String, Object>();
				List<AfResourceDo> backgroundLists  = new ArrayList<AfResourceDo>();
				backgroundLists = afResourceService.getBackGroundByTypeAndStatusOrder(ResourceType.CUBE_HOMEPAGE_BACKGROUND_ASJ.getCode());
				newConfigInfo = new HashMap<String, Object>();
				newConfigInfo.put("searchBackColor","");
				newConfigInfo.put("searchFontColor","");
				newConfigInfo.put("searchGlass","");
				newConfigInfo.put("messageIcon","");
				newConfigInfo.put("fontColor","");
				newConfigInfo.put("remainFontColor","");
				newConfigInfo.put("navigationOpen","");
				newConfigInfo.put("navigationClose","");
				newConfigInfo.put("bannerBackImg","");
				newConfigInfo.put("statusBarColor","");
				for (AfResourceDo afResourceDo :	backgroundLists) {
					if (StringUtil.equals(afResourceDo.getValue1(),AfResourceType.APP_SEARCH_BACK_COLOR.getCode())) {
						//app搜索栏背景色
						newConfigInfo.put("searchBackColor",afResourceDo.getValue3());
					}else if (StringUtil.equals(afResourceDo.getValue1(),AfResourceType.APP_SEARCH_FONT_COLOR.getCode())) {
						//app搜索栏文字颜色
						newConfigInfo.put("searchFontColor",afResourceDo.getValue3());
					}else if(StringUtil.equals(afResourceDo.getValue1(),AfResourceType.APP_SEARCH_GLASS.getCode())) {
						//app搜索栏放大镜
						newConfigInfo.put("searchGlass",afResourceDo.getValue());
					}else if (StringUtil.equals(afResourceDo.getValue1(),AfResourceType.APP_MESSAGE_ICON.getCode())) {
						//app消息图标
						newConfigInfo.put("messageIcon",afResourceDo.getValue());
					}else if (StringUtil.equals(afResourceDo.getValue1(),AfResourceType.APP_NAVIGATION_BAR.getCode())) {
						//app导航栏文字及下划线选中颜色
						newConfigInfo.put("fontColor",afResourceDo.getValue3());
					}else if (StringUtil.equals(afResourceDo.getValue1(),AfResourceType.APP_NAVIGATION_BAR_REMAIN.getCode())) {
						//app导航栏文字未选中颜色
						newConfigInfo.put("remainFontColor",afResourceDo.getValue3());
					}else if (StringUtil.equals(afResourceDo.getValue1(),AfResourceType.APP_NAVIGATION_OPEN.getCode())) {
						//app导航栏展开控件
						newConfigInfo.put("navigationOpen",afResourceDo.getValue());
					}else if (StringUtil.equals(afResourceDo.getValue1(),AfResourceType.APP_NAVIGATION_CLOSE.getCode())) {
						//app导航栏关闭控件
						newConfigInfo.put("navigationClose",afResourceDo.getValue());
					}else if (StringUtil.equals(afResourceDo.getValue1(),AfResourceType.BANNER_BACKIMG.getCode())) {
						//Banner背景图片
						newConfigInfo.put("bannerBackImg",afResourceDo.getValue());
					}else if (StringUtil.equals(afResourceDo.getValue1(),AfResourceType.APP_STATUS_BAR_COLOR.getCode())) {
						//状态栏颜色0黑色，1白色
						newConfigInfo.put("statusBarColor",afResourceDo.getValue3());
					}
				}
				//添加缓存
			//	bizCacheUtil.saveMap(CacheConstants.ASJ_HOME_PAGE.NEW_CONFIG_INFO.getCode(), newConfigInfo, Constants.MINITS_OF_TWO);
			//}
			data.put("newConfigInfo",newConfigInfo);
		}catch(Exception  e){
			logger.error("getHomeInfoV3 newConfigInfo error = " + e);
		}


		return data;

	}



		private void toAddImage(List<Object> topBannerList, String code, Long userId) {
			// TODO Auto-generated method stub
			try {

				AfAdvertiseDto  afAdvertiseDto  = afAdvertiseService.getDirectionalRecommendInfo(code,userId);
				logger.info("getDirectionalRecommendInfo = "+JSONObject.toJSONString(afAdvertiseDto)+"userId = "+userId);
				 if(afAdvertiseDto != null){
					 Integer appendMode = afAdvertiseDto.getAppendMode();
					 String imageUrl = afAdvertiseDto.getImage();
					 String type = afAdvertiseDto.getUrlType();
					 String content = afAdvertiseDto.getUrl();
					 if(appendMode != null && imageUrl != null  && type!= null ){
						 if(appendMode.intValue() == 1 || appendMode.intValue() == 2){

							Map<String, Object> dataMap = new HashMap<String, Object>();
							dataMap.put("imageUrl", imageUrl);
							dataMap.put("type",type);
							dataMap.put("content", content);
							Integer sort = 0;
							if(appendMode.intValue() == 1 ){
								sort = 9999;
								dataMap.put("sort", sort);
								topBannerList.add(0, dataMap);
							}
							if(appendMode.intValue() == 2){
								sort = -1;
								dataMap.put("sort", sort);
								topBannerList.add(dataMap);
							}

						 }
					 }
				 }

				} catch (Exception e) {
					logger.error("toAddImage  error =>" + e.getMessage());
				}
	}

	private void doStrongRiseAndCoupon(Long userId, String userName,
			Integer appVersion) {
		// TODO Auto-generated method stub
		
		try {
			if (userName != null && userId != null) {
				// 获取后台配置的注册时间
				String regTime = "";
				List<AfResourceDo> regTimeList = afResourceService
						.getConfigByTypes(ResourceType.APP_UPGRADE_REGISTER_TIME.getCode());
				if (regTimeList != null && !regTimeList.isEmpty()) {
					AfResourceDo regTimeInfo = regTimeList.get(0);
					regTime = regTimeInfo.getValue();
				}
				Date regDate = DateUtil.stringToDate(regTime);
				AfUserDo userDo = afUserService.getUserById(userId);
				Date gmtCreate = userDo.getGmtCreate();
				// 用户已登录,将登录信息存放到缓存中
				String ltStoreKey = "GET_HOME_INFO_LT" + userName;
				Object ltSaveObj = bizCacheUtil.getObject(ltStoreKey);
				List<AfResourceDo> resList = afResourceService.getConfigByTypes(ResourceType.APP_UPDATE_WND.getCode());
				Integer givenVersion = 0;
				String onOff = "";
				if (resList != null && !resList.isEmpty()) {
					AfResourceDo versionInfoRes = resList.get(0);
					onOff = versionInfoRes.getValue1();
					String version = versionInfoRes.getValue();
					givenVersion = Integer.valueOf(version);
				}
				logger.info("GetHomeInfoApi3 userName=>" + userName);
				if (ltSaveObj == null) {
					long secs = DateUtil.getSecsEndOfDay();
					if (appVersion.compareTo(givenVersion) < 0 && "Y".equals(onOff)) {
						jpushService.jPushPopupWnd("LT_GIVEN_VERSION_WND", userName);
						logger.error("LT_GIVEN_VERSION_WND send success");
						bizCacheUtil.saveObject(ltStoreKey, "Y", secs); // 单位:秒
					}
				}
				String gtStoreKey = "GET_HOME_INFO_GT" + userName;
				Object gtSaveObj = bizCacheUtil.getObject(gtStoreKey);
				// 获取后台配置的优惠券信息
				List<AfResourceDo> couponsList = null;

				if (gmtCreate.after(regDate)) {
					// 新用户
					couponsList = afResourceService.getConfigByTypes(ResourceType.APP_UPDATE_COUPON_NEW.getCode());
				} else {
					// 老用户
					couponsList = afResourceService.getConfigByTypes(ResourceType.APP_UPDATE_COUPON.getCode());
				}
				if (couponsList != null && !couponsList.isEmpty()) {
					AfResourceDo couponInfoRes = couponsList.get(0);
					String couponIdStr = couponInfoRes.getValue();
					String[] couponIds = couponIdStr.split(",");
					boolean sended = false;
					for (String couponId : couponIds) {
						String[] tmp = couponId.split(":");
						if (tmp.length > 1)
							continue;
						int count = afUserCouponService.getUserCouponByUserIdAndCouponId(userId,
								Long.parseLong(couponId));
						if (count > 0)
							sended = true;
						break;
					}
					if (!sended && gtSaveObj == null) {
						if (appVersion.compareTo(givenVersion) >= 0 && "Y".equals(onOff)) {
							jpushService.jPushPopupWnd("GT_GIVEN_VERSION_WND", userName);
							logger.error("GT_GIVEN_VERSION_WND send success");
							bizCacheUtil.saveObject(gtStoreKey, "Y");
							for (String couponId : couponIds) {
								String[] tmp = couponId.split(":");
								// 用户发券
								try {
									if (tmp.length > 1) {
										String sceneId = tmp[0];
										grantBoluomiCoupon(Long.parseLong(sceneId), userId);
									} else {
										afUserCouponService.grantCoupon(userId, Long.parseLong(couponId), "updatePrize",
												"home");
									}
								} catch (Exception e) {
									logger.error("grant coupon error=>" + e.getMessage());
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("push wnd error=>" + e.getMessage());
		}
		
	}


	private List<Object> getNewProductGoodsIdList(
			List<Object> newProductGoodsIdList) {
		// TODO Auto-generated method stub
  		List<Object>  newProductGoodsIdLists = new ArrayList<Object>();
		if(newProductGoodsIdList != null && newProductGoodsIdList.size() > 0 ){
				int size = newProductGoodsIdList.size();
				if(size < 4 && size > 1 ){
					newProductGoodsIdLists.addAll(newProductGoodsIdList.subList(0, 2));
				}if(size >= 4 ){
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
		// 获取上方3个电商运营位,如果配置不全，则不显示
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
		int count = 0;
		if (navCount >= 5 && navCount < 10) {
			count = 5;
		} else if (navCount >= 10) {
			count = 10;
		}
		if(count > 0){
			for (int i = 0; i < count; i++) {
				// 如果配置大于5个，小于10个，则只显示5个
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
		}
		
		return navigationInfo;
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
		    String  rebateAmount =   homePageSecKillGoods.getRebateAmount().toString();
		    String priceAmount =  homePageSecKillGoods.getPriceAmount().toString();
		    String saleAmount  = homePageSecKillGoods.getSaleAmount().toString();
		    //如果大于等于8位，直接截取前8位。否则判断小数点后面的是否是00,10.分别去掉两位，一位
		    if(null == homePageSecKillGoods.getActivityAmount()){
		    	 goodsInfo.put("activityAmount", homePageSecKillGoods.getActivityAmount());
		    }else{
		    	 goodsInfo.put("activityAmount", substringAmount(homePageSecKillGoods.getActivityAmount().toString()));
		    }
		    
		    goodsInfo.put("goodsName", homePageSecKillGoods.getGoodName());
		    goodsInfo.put("rebateAmount",  substringAmount(rebateAmount));
		    goodsInfo.put("saleAmount",    substringAmount(saleAmount));
		    goodsInfo.put("priceAmount",   substringAmount(priceAmount));
		   
		    goodsInfo.put("goodsIcon", homePageSecKillGoods.getGoodsIcon());
		    goodsInfo.put("goodsId", homePageSecKillGoods.getGoodsId());
		    goodsInfo.put("goodsUrl", homePageSecKillGoods.getGoodsUrl());
		    goodsInfo.put("goodsType", "0");
		    goodsInfo.put("subscribe", homePageSecKillGoods.getSubscribe());
		    goodsInfo.put("volume", homePageSecKillGoods.getVolume());
		    goodsInfo.put("total", homePageSecKillGoods.getTotal());	
		    goodsInfo.put("source", homePageSecKillGoods.getSource()); 
		    
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
		    BigDecimal  showAmount =  homePageSecKillGoods.getSaleAmount();
		   if(null != homePageSecKillGoods.getActivityAmount()){
			   showAmount = homePageSecKillGoods.getActivityAmount();
		   }
		    List<Map<String, Object>> nperList = InterestFreeUitl.getConsumeList(array, interestFreeArray, BigDecimal.ONE.intValue(), 
		    		showAmount, resource.getValue1(), resource.getValue2(), goodsId, "0");
		    if (nperList != null) {
			goodsInfo.put("goodsType", "1");
			Map<String, Object> nperMap = nperList.get(nperList.size() - 1);
			String isFree = (String) nperMap.get("isFree");
			if (InterestfreeCode.NO_FREE.getCode().equals(isFree)) {
				//不影响其他业务，此处加
				Object oAmount =  nperMap.get("amount");
				String amount = "";
				if(oAmount != null){
					amount = oAmount.toString();
				}
				nperMap.put("amount",substringAmount(amount));
			    nperMap.put("freeAmount",substringAmount(amount));
			}
			goodsInfo.put("nperMap", nperMap);
		     //更换content和type可跳转商品详情
				if(HOME_FLASH_SALE_FLOOR_IMAGE.equals(tag)){
					  String content = "";
					  String type   = "";
					 if(afResourceH5ItemDo != null){
						 content = afResourceH5ItemDo.getValue1();
						 type = afResourceH5ItemDo.getValue4();
					 }
			    	  goodsInfo.put("type",type);
			    	  goodsInfo.put("content", content);
			     }
				//不影响其他业务，此处加
				if(MAJOR_SUIT.equals(tag)){
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
						 logger.error("big deal get goodsInfo error "+ e);
					 }
			     }
		   }
		    goodsList.add(goodsInfo);
		}
		return goodsList;
	}

	private  BigDecimal substringAmount(String amount) {
		BigDecimal substringAmount = new BigDecimal(0);
		try{
		//判断小数点后面的是否是00,10.分别去掉两位，一位。去掉之后大于等于8位，则截取前8位。
		 String tempNumber = "0";
		 String afterNumber =  amount.substring(amount.indexOf(".")+1,amount.length());
		 if("00".equals(afterNumber)){
			 tempNumber = amount.substring(0,amount.length()-3);
		 } else if("10".equals(afterNumber)){
			 tempNumber = amount.substring(0,amount.length()-1);
		 }else{
			 tempNumber = amount;
		 }
		 if(tempNumber.length() >8 ){
			 tempNumber =  tempNumber.substring(0,8);
			 String t = tempNumber.substring(tempNumber.length()-1, tempNumber.length());
			 if(".".equals(t)){
				 tempNumber =  tempNumber.substring(0,tempNumber.length()-1);
			 }
		 }
		 substringAmount = new BigDecimal(tempNumber);
	 
		}catch(Exception e){
			logger.error("substringAmount error"+e);
		}
		return substringAmount;
	}
	private void grantBoluomiCoupon(Long sceneId, Long userId) {
		logger.info(" pickBoluomeCoupon begin , sceneId = {}, userId = {}", sceneId, userId);
		if (sceneId == null) {
			throw new FanbeiException(FanbeiExceptionCode.REQUEST_PARAM_NOT_EXIST);
		}
		AfResourceDo resourceInfo = afResourceService.getResourceByResourceId(sceneId);
		if (resourceInfo == null) {
			logger.error("couponSceneId is invalid");
			throw new FanbeiException(FanbeiExceptionCode.PARAM_ERROR);
		}

		PickBrandCouponRequestBo bo = new PickBrandCouponRequestBo();
		bo.setUser_id(userId + StringUtil.EMPTY);

		Date gmtStart = DateUtil.parseDate(resourceInfo.getValue1(), DateUtil.DATE_TIME_SHORT);
		Date gmtEnd = DateUtil.parseDate(resourceInfo.getValue2(), DateUtil.DATE_TIME_SHORT);

		if (DateUtil.beforeDay(new Date(), gmtStart)) {
			throw new FanbeiException(FanbeiExceptionCode.PICK_BRAND_COUPON_NOT_START);
		}
		if (DateUtil.afterDay(new Date(), gmtEnd)) {
			throw new FanbeiException(FanbeiExceptionCode.PICK_BRAND_COUPON_DATE_END);
		}
		String url = resourceInfo.getValue();
		if (url != null) {
			url = url.replace(" ", "");
		}
		String resultString = HttpUtil.doHttpPostJsonParam(url, JSONObject.toJSONString(bo));
		logger.info("pickBoluomeCoupon boluome bo = {}, resultString = {}", JSONObject.toJSONString(bo), resultString);
		JSONObject resultJson = JSONObject.parseObject(resultString);
		if (!"0".equals(resultJson.getString("code"))) {
			throw new FanbeiException(resultJson.getString("msg"));
		} else if (JSONArray.parseArray(resultJson.getString("data")).size() == 0) {
			throw new FanbeiException("仅限领取一次，请勿重复领取！");
		}
	}
}
