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
import com.ald.fanbei.api.common.enums.HomePageType;
import com.ald.fanbei.api.common.enums.InterestfreeCode;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfHomePageChannelConfigureDo;
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
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

/**
 * 频道页
 * @author chenqiwei
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
	
	private String HOME_PAGE_CHANNEL_RECOMMEND_GOODS = 	HomePageType.HOME_PAGE_CHANNEL_RECOMMEND_GOODS.getCode(); //频道页推荐商品id组
	private String ASJ_IMAGES = 		   HomePageType.ASJ_IMAGES.getCode();//爱上街顶部图组
	private String CHANNEL_RECOMMEND_GOODS_TOP_IMAGE = 		   HomePageType.CHANNEL_RECOMMEND_GOODS_TOP_IMAGE.getCode();//爱上街顶部图组
	private String H5_URL = 		   HomePageType.H5_URL.getCode();//h5
	private String CATEGORY_ID = 		   HomePageType.CATEGORY_ID.getCode();//h5
	
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
					bannerInfo.put("type", H5_URL);
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
					//跳转类型。1:品牌；2:类目；3:H5
					String type = "";
					if(homePageChannelConfigure.getJumpType() == 2){
						type = CATEGORY_ID;
					}else{
						type = H5_URL;
					}
					Map<String, Object> navigation = new HashMap<String, Object>();
					navigation.put("imageUrl", homePageChannelConfigure.getImageUrl());
					navigation.put("type",type );
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
						onePlusThreeBanner.put("type", H5_URL);
						onePlusThreeBanner.put("content", homePageChannelConfigure.getJumpUrl());
					}else if (0 <  homePageChannelConfigure.getPosition() && 1 == homePageChannelConfigure.getStatus()){
						Map<String, Object> onePlusThree = new HashMap<String, Object>(); 
						if(homePageChannelConfigure.getSort() == 1){
							
						}
						onePlusThree.put("imageUrl", homePageChannelConfigure.getImageUrl());
						onePlusThree.put("type", H5_URL);
						onePlusThree.put("content", homePageChannelConfigure.getJumpUrl());
						onePlusThree.put("sort", homePageChannelConfigure.getSort());
						onePlusThreeArea.add(onePlusThree);
					}
				}
			}
			navigationInfo = getNavigationInfolist(navigationList);
			onePlusThreeArea = getOnePlusThreeArea(onePlusThreeArea);
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
			
				//String recommendTag = "HC_IMAGE";
				AfResourceDo recommendGoods =  afResourceService.getConfigByTypesAndValue(HOME_PAGE_CHANNEL_RECOMMEND_GOODS, tabId.toString());
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
							
								 String imageUrl = "";
								 String content = "";
								 String type = "";
								   List<AfResourceH5ItemDo>  recommendList =  afResourceH5ItemService.getByTagAndValue2(ASJ_IMAGES,CHANNEL_RECOMMEND_GOODS_TOP_IMAGE);
								     if(recommendList != null && recommendList.size() >0){
								    	 AfResourceH5ItemDo recommend = recommendList.get(0);
								    	  content =  recommend.getValue1();
								    	  imageUrl = recommend.getValue3();
								    	  type = recommend.getValue4();
								     }
								 
							    	 if(StringUtil.isNotEmpty(imageUrl)){
							    		 if(recommendGoodsInfoList.size()>=3){
								    	 recommendGoodsInfo.put("imageUrl", imageUrl);
								    	 recommendGoodsInfo.put("content", content);
								    	 recommendGoodsInfo.put("type", type);
								    	 recommendGoodsInfo.put("goodsList", recommendGoodsInfoList);
							    	 }
							    }
		        	  }
		         }
		        
		        
		 }catch(Exception e){
			 logger.error("recommendGoodsInfo goodsInfo error "+ e);
		 }
		 if (!recommendGoodsInfo.isEmpty()) {
				data.put("recommendGoodsInfo", recommendGoodsInfo);
			}
		resp.setResponseData(data);
		return resp;
	}
	
	
	private List<Object> getOnePlusThreeArea(List<Object> onePlusThreeArea) {
		List<Object>  onePlusThreeAreaList = new ArrayList<Object>();
		if(onePlusThreeArea != null && onePlusThreeArea.size() > 0 ){
				int size = onePlusThreeArea.size();
				if(size >= 3 ){
					onePlusThreeAreaList.addAll(onePlusThreeArea.subList(0, 3));
				}
			
		}
		return onePlusThreeAreaList;
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
		    
		    List<Map<String, Object>> nperList = InterestFreeUitl.getConsumeList(array, interestFreeArray, BigDecimal.ONE.intValue(), 
			    homePageSecKillGoods.getSaleAmount(), resource.getValue1(), resource.getValue2(), goodsId, "0");
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

}
