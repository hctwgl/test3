package com.ald.fanbei.api.web.api.goods;

import com.ald.fanbei.api.biz.service.*;

import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.biz.util.JobThreadPoolUtils;
import com.ald.fanbei.api.common.CacheConstants;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.HomePageType;
import com.ald.fanbei.api.common.enums.InterestfreeCode;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.dal.domain.dto.HomePageSecKillGoods;
import com.ald.fanbei.api.dal.domain.query.AfSeckillActivityQuery;
import com.ald.fanbei.api.dal.domain.query.HomePageSecKillByBottomGoodsQuery;

import com.ald.fanbei.api.web.cache.Cache;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.InterestFreeUitl;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jiang Rongbo
 *
 */
@Component("getMoreGoodsApi")
public class GetMoreGoodsApi implements ApiHandle {

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
	@Resource
	JobThreadPoolUtils jobThreadPoolUtils;

	private static final	 String ASJ_IMAGES = 		   HomePageType.ASJ_IMAGES.getCode();//爱上街顶部图组
	private static final    String GUESS_YOU_LIKE_TOP_IMAGE =   HomePageType.GUESS_YOU_LIKE_TOP_IMAGE.getCode();//猜你喜欢顶部图
	
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Map<String, Object> data = new HashMap<String, Object>();
		String deviceType = ObjectUtils.toString(requestDataVo.getParams().get("deviceType"));
		Integer pageNo = NumberUtil.objToIntDefault(requestDataVo.getParams().get("pageNo"), 1);
		String pageFlag = ObjectUtils.toString(requestDataVo.getParams().get("pageFlag"), null);
		
		if(pageFlag == null || pageNo == null){
			logger.error("pageFlag or pageNo is null");
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
		
		//更多商品
	 try{
		
		 Map<String, Object> goodsInfo = new HashMap<String, Object>();
		// Map<String, Object> goodsInfoTemp = new HashMap<String, Object>();
		 String cacheKey = CacheConstants.ASJ_HOME_PAGE.ASJ_HOME_MORE_GOODS_PAGENO_FIRST.getCode()+ ":"+pageFlag+":"+pageNo;
		 String cacheKey2 = CacheConstants.ASJ_HOME_PAGE.ASJ_HOME_MORE_GOODS_PAGENO_SECOND.getCode()+ ":"+pageFlag+":"+pageNo;
		 String processKey = CacheConstants.ASJ_HOME_PAGE.ASJ_HOME_MORE_GOODS_PAGENO_SECOND_PROCESS_KEY.getCode()+ ":"+pageFlag+":"+pageNo;
		 String source = "app";

		// goodsInfoTemp =  (Map<String, Object>) bizCacheUtil.getMap(cacheKey);
		 goodsInfo =  (Map<String, Object>) bizCacheUtil.getMap(cacheKey);
		  logger.info("getMoreGoodsApi"+Thread.currentThread().getName() + "getMoreGoodsApi = "+JSONArray.toJSONString(goodsInfo)+"cacheKey = "+ cacheKey);
		   /*if(goodsInfoTemp != null){
			   goodsInfo = goodsInfoTemp;
		   }*/
		   if(goodsInfo == null || goodsInfo.isEmpty()){
			   boolean isGetLock = bizCacheUtil.getLock30Second(processKey, "1");
			   goodsInfo = (Map<String, Object>) bizCacheUtil.getMap(cacheKey2);
			   logger.info("getMoreGoodsApi"+Thread.currentThread().getName() + "getMoreGoodsApi isGetLock:"+isGetLock+"goodsInfo= "+JSONArray.toJSONString(goodsInfo)+"cacheKey2 = "+ cacheKey2);

			   //调用异步请求加入缓存
			   if(isGetLock){
				   logger.info("getMoreGoodsApi"+Thread.currentThread().getName() + "getMoreGoodsApi is null"+"cacheKey2 = "+ cacheKey2);
				   Runnable process = new GetMoreGoodsInfo(cacheKey,cacheKey2,null,pageNo,pageFlag,source);
				   jobThreadPoolUtils.asynProcessBusiness(process);
			   }
		   }
		 //调用异步请求加入缓存
		if(goodsInfo==null){
			    goodsInfo = toGetMoreGoodsInfoMap(null,pageNo,pageFlag,source);
			if(goodsInfo != null) {
				bizCacheUtil.saveMap(cacheKey, goodsInfo, Constants.MINITS_OF_TWO);
				bizCacheUtil.saveMapForever(cacheKey2, goodsInfo);
			}
		}

	/*		   //更换查询表
		 //List<HomePageSecKillGoods> goodsList = afSeckillActivityService.getHomePageSecKillGoodsByActivityModel(userId,activityTag,activityType,tabId,pageNo);
		 Map<String, Object> goodsListMap = afSeckillActivityService.getMoreGoodsByBottomGoodsTable(userId,pageNo,pageFlag,"app");
		 List<HomePageSecKillGoods> goodsList = (List<HomePageSecKillGoods>) goodsListMap.get("goodsList");
		 List<Map<String, Object>> moreGoodsInfoList = getGoodsInfoList(goodsList,null,null);
		     String imageUrl = "";
		     String content = "";
		     String type = "";
		     List<AfResourceH5ItemDo>  recommendList =  afResourceH5ItemService.getByTagAndValue2(ASJ_IMAGES,GUESS_YOU_LIKE_TOP_IMAGE);
		     if(recommendList != null && recommendList.size() >0){
		    	 AfResourceH5ItemDo recommend = recommendList.get(0);
		    	 imageUrl = recommend.getValue3();
		    	 content = recommend.getValue1();
		    	 type = recommend.getValue4();
		     }
//						 if(StringUtil.isNotEmpty(imageUrl) && moreGoodsInfoList != null && moreGoodsInfoList.size()>0){
			   			 // update by wangli 2018/5/17 楼层图没有配置时，也返回配置的商品
			             if(moreGoodsInfoList != null && moreGoodsInfoList.size()>0){
							 HomePageSecKillByBottomGoodsQuery homePageSecKillGoods = (HomePageSecKillByBottomGoodsQuery)goodsListMap.get("query");
							 if(homePageSecKillGoods != null){
								 int pageSize = homePageSecKillGoods.getPageSize();
								 int size = goodsList.size();
								 if(pageSize > size){
									 goodsInfo.put("nextPageNo",-1); 
								 }else{
									 goodsInfo.put("nextPageNo",pageNo+1); 
								 }
								 goodsInfo.put("imageUrl",imageUrl); 
								 goodsInfo.put("content",content); 
								 goodsInfo.put("type",type); 
								 goodsInfo.put("moreGoodsList", moreGoodsInfoList);
							 }
		         }
						 
						 bizCacheUtil.saveMap(cacheKey, goodsInfo, Constants.MINITS_OF_TWO);	 		 
						 
		   }
		     */
			 if (!goodsInfo.isEmpty()) {
					data.put("moreGoodsInfo", goodsInfo);
				}
		 }catch(Exception e){
			 logger.error("home page get moreGoodsList error = "+e);
		 }
		
		resp.setResponseData(data);
		return resp;
		
	}
class GetMoreGoodsInfo implements Runnable {

	protected  final Logger logger = LoggerFactory.getLogger(GetMoreGoodsInfo.class);

	private String source;
	private String  pageFlag;
	private Integer pageNo;
	private Long userId;
	private String  firstKey;
	private String  secondKey;
	@Resource
	BizCacheUtil bizCacheUtil;
	GetMoreGoodsInfo(String firstKey,String secondKey,Long userId,Integer pageNo,String pageFlag,String source) {

		this.source = source;
		this.pageFlag = pageFlag;
		this.pageNo = pageNo;
		this.userId = userId;
		this.firstKey = firstKey;
		this.secondKey = secondKey;
	}
	@Override
	public void run() {
		logger.info("pool:GetMoreGoodsInfo"+Thread.currentThread().getName() + "GetMoreGoodsInfo");
		try{
			GetMoreGoodsInfoMap( firstKey,secondKey , userId, pageNo, pageFlag,source);

		}catch (Exception e){
			logger.error("pool:GetMoreGoodsInfo error for" + e);
		}
	}
	}
	private Map<String, Object> GetMoreGoodsInfoMap(String firstKey , String secondKey ,Long userId,Integer pageNo,String pageFlag,String source) {
		//获取所有活动
		Map<String, Object> goodsInfo =  toGetMoreGoodsInfoMap(userId, pageNo, pageFlag, source);
		if(goodsInfo != null) {
			bizCacheUtil.saveMap(firstKey, goodsInfo, Constants.MINITS_OF_TWO);
			bizCacheUtil.saveMapForever(secondKey, goodsInfo);
		}
		return null;
	}

	Map<String, Object> toGetMoreGoodsInfoMap(Long userId,Integer pageNo,String pageFlag,String source ){

		Map<String, Object> goodsInfo = new HashMap<String, Object>();
		userId = null; // 不查，且放入缓存会有问题。
	//更换查询表
	//List<HomePageSecKillGoods> goodsList = afSeckillActivityService.getHomePageSecKillGoodsByActivityModel(userId,activityTag,activityType,tabId,pageNo);
		Map<String, Object> goodsListMap = afSeckillActivityService.getMoreGoodsByBottomGoodsTable(userId,pageNo,pageFlag,source);
		List<HomePageSecKillGoods> goodsList = (List<HomePageSecKillGoods>) goodsListMap.get("goodsList");
		List<Map<String, Object>> moreGoodsInfoList = getGoodsInfoList(goodsList,null,null);
		String imageUrl = "";
		String content = "";
		String type = "";
		List<AfResourceH5ItemDo>  recommendList =  afResourceH5ItemService.getByTagAndValue2(ASJ_IMAGES,GUESS_YOU_LIKE_TOP_IMAGE);
				 if(recommendList != null && recommendList.size() >0){
			AfResourceH5ItemDo recommend = recommendList.get(0);
			imageUrl = recommend.getValue3();
			content = recommend.getValue1();
			type = recommend.getValue4();
		}
     //						 if(StringUtil.isNotEmpty(imageUrl) && moreGoodsInfoList != null && moreGoodsInfoList.size()>0){
	// update by wangli 2018/5/17 楼层图没有配置时，也返回配置的商品
			             if(moreGoodsInfoList != null && moreGoodsInfoList.size()>0){
							 HomePageSecKillByBottomGoodsQuery homePageSecKillGoods = (HomePageSecKillByBottomGoodsQuery)goodsListMap.get("query");
							if(homePageSecKillGoods != null){
								int pageSize = homePageSecKillGoods.getPageSize();
								int size = goodsList.size();
								if(pageSize > size){
									goodsInfo.put("nextPageNo",-1);
								}else{
									goodsInfo.put("nextPageNo",pageNo+1);
								}
								goodsInfo.put("imageUrl",imageUrl);
								goodsInfo.put("content",content);
								goodsInfo.put("type",type);
								goodsInfo.put("moreGoodsList", moreGoodsInfoList);
							}
						}
						return goodsInfo;
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
