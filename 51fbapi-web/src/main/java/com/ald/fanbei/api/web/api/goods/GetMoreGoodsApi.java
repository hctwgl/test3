package com.ald.fanbei.api.web.api.goods;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
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
import com.ald.fanbei.api.common.enums.HomePageType;
import com.ald.fanbei.api.common.enums.InterestfreeCode;
import com.ald.fanbei.api.common.enums.ResourceType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.CollectionConverterUtil;
import com.ald.fanbei.api.common.util.CollectionUtil;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.Converter;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
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
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Map<String, Object> data = new HashMap<String, Object>();
		String deviceType = ObjectUtils.toString(requestDataVo.getParams().get("deviceType"));
		Integer pageNo = NumberUtil.objToIntDefault(requestDataVo.getParams().get("pageNo"), null);
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
		 String ASJ_IMAGES = 		   HomePageType.ASJ_IMAGES.getCode();//爱上街顶部图组
		 String GUESS_YOU_LIKE_TOP_IMAGE = 		   HomePageType.GUESS_YOU_LIKE_TOP_IMAGE.getCode();//猜你喜欢顶部图
		 Map<String, Object> goodsInfo = new HashMap<String, Object>();
		 //更换查询表
		 //List<HomePageSecKillGoods> goodsList = afSeckillActivityService.getHomePageSecKillGoodsByActivityModel(userId,activityTag,activityType,tabId,pageNo);
		 List<HomePageSecKillGoods> goodsList = afSeckillActivityService.getMoreGoodsByBottomGoodsTable(userId,pageNo,pageFlag);
		 List<Map<String, Object>> moreGoodsInfoList = getGoodsInfoList(goodsList,null,null);
//		     goodsInfo.put("moreGoodsList", moreGoodsInfoList);
		     String imageUrl = "";
		     List<AfResourceH5ItemDo>  recommendList =  afResourceH5ItemService.getByTagAndValue2(ASJ_IMAGES,GUESS_YOU_LIKE_TOP_IMAGE);
		     if(recommendList != null && recommendList.size() >0){
		    	 AfResourceH5ItemDo recommend = recommendList.get(0);
		    	 imageUrl = recommend.getValue3();
		     }
						 if(StringUtil.isNotEmpty(imageUrl) && moreGoodsInfoList != null && moreGoodsInfoList.size()>0){
							 goodsInfo.put("imageUrl",imageUrl); 
						     goodsInfo.put("moreGoodsList", moreGoodsInfoList);
		         }
		     
			 if (!goodsInfo.isEmpty()) {
					data.put("moreGoodsInfo", goodsInfo);
				}
		 }catch(Exception e){
			 logger.error("home page get moreGoodsList error = "+e);
		 }
		
		resp.setResponseData(data);
		return resp;
		
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
			    nperMap.put("freeAmount", nperMap.get("amount"));
			}
			goodsInfo.put("nperMap", nperMap);
		     //更换content和type可跳转商品详情
			
		   }
		    goodsList.add(goodsInfo);
		}
		return goodsList;
	}



}
