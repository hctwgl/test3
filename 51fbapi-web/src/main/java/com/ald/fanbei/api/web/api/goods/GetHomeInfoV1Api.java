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
import com.ald.fanbei.api.biz.service.AfInterestFreeRulesService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfSchemeGoodsService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfResourceSecType;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.InterestfreeCode;
import com.ald.fanbei.api.common.enums.ResourceType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfActivityDo;
import com.ald.fanbei.api.dal.domain.AfInterestFreeRulesDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfSchemeGoodsDo;
import com.ald.fanbei.api.dal.domain.dto.AfEncoreGoodsDto;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.InterestFreeUitl;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;


/**
 * @author Jiang Rongbo
 *
 */
@Component("getHomeInfoV1Api")
public class GetHomeInfoV1Api implements ApiHandle {

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
	
	private FanbeiContext contextApp;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		contextApp = context;
		Map<String, Object> data = new HashMap<String, Object>();
		String type = ConfigProperties.get(Constants.CONFKEY_INVELOMENT_TYPE);
		// 搜索框背景图
		List<AfResourceDo> serchBoxRescList = afResourceService.getConfigByTypes(ResourceType.SEARCH_BOX_BACKGROUND.getCode());
		if(serchBoxRescList != null && !serchBoxRescList.isEmpty()) {
			AfResourceDo serchBoxInfo = serchBoxRescList.get(0);
			String searchBoxBgImage = serchBoxInfo.getValue();
			data.put("searchBoxBgImage", searchBoxBgImage);
		}
		//爬取商品开关
		AfResourceDo isWorm = afResourceService.getConfigByTypesAndSecType(Constants.THIRD_GOODS_TYPE,Constants.THIRD_GOODS_IS_WORM_SECTYPE);
		String value = "";
		if(null == isWorm){
			value = "0" ;
		}else{
			value = isWorm.getValue();
		}


		// 顶部导航信息
		List<Object> topBannerList = new ArrayList<Object>();
		//正式环境和预发布环境区分
		if (Constants.INVELOMENT_TYPE_ONLINE.equals(type) || Constants.INVELOMENT_TYPE_TEST.equals(type)) {
			topBannerList = getObjectWithResourceDolist(
					afResourceService.getResourceHomeListByTypeOrderBy(AfResourceType.HomeBannerNew.getCode()));
		} else if (Constants.INVELOMENT_TYPE_PRE_ENV.equals(type) ){
			topBannerList = getObjectWithResourceDolist(
					afResourceService.getResourceHomeListByTypeOrderByOnPreEnv(AfResourceType.HomeBannerNew.getCode()));
		}
		// 快速导航信息
		Map<String,Object> navigationInfo = getNavigationInfoWithResourceDolist(
				afResourceService.getHomeIndexListByOrderby(AfResourceType.HomeNavigation.getCode()));
		
		// 1大左2小右
		List<Object> one2TwoInfoList = getOne2TwoObjectWithResourceDolist(
				afResourceService.getOneToManyResourceOrderByBytype(AfResourceType.HomeOneToTwo.getCode()));
		
		// 获取轮播 +N信息
		Map<String,Object> midBanner2Many = getCarouselToManyWithResourceDoList(
				afResourceService.getCarouselToManyResourceOrderByType(AfResourceType.HomeCarouseToMany.getCode()));
		// 获取首页活动信息
		//获取借款分期配置信息
        AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE, Constants.RES_BORROW_CONSUME);
        JSONArray array = JSON.parseArray(resource.getValue());
        //删除2分期
        if (array == null) {
            throw new FanbeiException(FanbeiExceptionCode.BORROW_CONSUME_NOT_EXIST_ERROR);
        }
        removeSecondNper(array);
		List<AfActivityDo> activityList = afActivityService.listAllHomeActivity();
		List<Map<String,Object>> activityInfoList = new ArrayList<Map<String,Object>>();
		for(AfActivityDo afActivityDo : activityList) {
			Map<String,Object> activityData = new HashMap<String,Object> ();
			activityData.put("titleName", afActivityDo.getName());
			activityData.put("imageUrl", afActivityDo.getIconUrl());
			activityData.put("type", afActivityDo.getLinkType());
			activityData.put("content", afActivityDo.getLinkContent());
			// 活动商品
			List<Map<String,Object>> goodsList = new ArrayList<Map<String,Object>>();
			// 获取活动商品
    		List<AfEncoreGoodsDto> activityGoodsDoList = afActivityGoodsService.listHomeActivityGoodsByActivityId(afActivityDo.getId());
    		for(AfEncoreGoodsDto goodsDo : activityGoodsDoList) {
    			Map<String, Object> goodsInfo = new HashMap<String, Object>();
    			goodsInfo.put("goodName",goodsDo.getName());
    			goodsInfo.put("rebateAmount", goodsDo.getRebateAmount());
    			goodsInfo.put("saleAmount", goodsDo.getSaleAmount());
    			goodsInfo.put("priceAmount", goodsDo.getPriceAmount());
    			goodsInfo.put("goodsIcon", goodsDo.getGoodsIcon());
    			goodsInfo.put("goodsId", goodsDo.getRid());
    			goodsInfo.put("goodsUrl", goodsDo.getGoodsUrl());
    			goodsInfo.put("thumbnailIcon", goodsDo.getThumbnailIcon());
    			goodsInfo.put("source", goodsDo.getSource());
    			String doubleRebate = goodsDo.getDoubleRebate();
    			goodsInfo.put("doubleRebate","0".equals(doubleRebate)?"N":"Y" );
    			goodsInfo.put("goodsType", "0");
    			goodsInfo.put("remark", StringUtil.null2Str(goodsDo.getRemark()));
				goodsInfo.put("isWorm", value);
    			// 如果是分期免息商品，则计算分期
    			Long goodsId = goodsDo.getRid();
				AfSchemeGoodsDo  schemeGoodsDo = null;
				try {
					schemeGoodsDo = afSchemeGoodsService.getSchemeGoodsByGoodsId(goodsId);
				} catch(Exception e){
					logger.error(e.toString());
				}
				JSONArray interestFreeArray = null;
				if(schemeGoodsDo != null){
					AfInterestFreeRulesDo  interestFreeRulesDo = afInterestFreeRulesService.getById(schemeGoodsDo.getInterestFreeId());
					String interestFreeJson = interestFreeRulesDo.getRuleJson();
					if (StringUtils.isNotBlank(interestFreeJson) && !"0".equals(interestFreeJson)) {
						interestFreeArray = JSON.parseArray(interestFreeJson);
					}
				}
				List<Map<String, Object>> nperList = InterestFreeUitl.getConsumeList(array, interestFreeArray, BigDecimal.ONE.intValue(),
						goodsDo.getSaleAmount(), resource.getValue1(), resource.getValue2());
				if(nperList!= null){
					goodsInfo.put("goodsType", "1");
					Map<String, Object> nperMap = nperList.get(nperList.size() - 1);
					String isFree = (String)nperMap.get("isFree");
					if(InterestfreeCode.NO_FREE.getCode().equals(isFree)) {
						nperMap.put("freeAmount", nperMap.get("amount"));
					}
					goodsInfo.put("nperMap", nperMap);
				}
				
				goodsList.add(goodsInfo);
    		}
    		activityData.put("goodsList", goodsList);
    		activityInfoList.add(activityData);
		}
		// 更多商品
		Map<String ,Object> moreGoodsInfo = new HashMap<String,Object>();
		AfActivityDo moreActivity = afActivityService.getHomeMoreActivity();
		List<Map<String,Object>> moreGoodsList = new ArrayList<Map<String,Object>>();
		List<AfEncoreGoodsDto> moreGoodsDoList = afActivityGoodsService.listHomeActivityGoodsByActivityId(moreActivity.getId());
		for(AfEncoreGoodsDto goodsDo : moreGoodsDoList) {
			Map<String, Object> goodsInfo = new HashMap<String, Object>();
			goodsInfo.put("goodName",goodsDo.getName());
			goodsInfo.put("rebateAmount", goodsDo.getRebateAmount());
			goodsInfo.put("saleAmount", goodsDo.getSaleAmount());
			goodsInfo.put("priceAmount", goodsDo.getPriceAmount());
			goodsInfo.put("goodsIcon", goodsDo.getGoodsIcon());
			goodsInfo.put("goodsId", goodsDo.getRid());
			goodsInfo.put("goodsUrl", goodsDo.getGoodsUrl());
			goodsInfo.put("thumbnailIcon", goodsDo.getThumbnailIcon());
			goodsInfo.put("source", goodsDo.getSource());
			String doubleRebate = goodsDo.getDoubleRebate();
			goodsInfo.put("doubleRebate","0".equals(doubleRebate)?"N":"Y" );
			goodsInfo.put("goodsType", "0");
			goodsInfo.put("remark", StringUtil.null2Str(goodsDo.getRemark()));
			goodsInfo.put("isWorm", value);
			// 如果是分期免息商品，则计算分期
			Long goodsId = goodsDo.getRid();
			AfSchemeGoodsDo  schemeGoodsDo = null;
			try {
				schemeGoodsDo = afSchemeGoodsService.getSchemeGoodsByGoodsId(goodsId);
			} catch(Exception e){
				logger.error(e.toString());
			}
			JSONArray interestFreeArray = null;
			if(schemeGoodsDo != null){
				AfInterestFreeRulesDo  interestFreeRulesDo = afInterestFreeRulesService.getById(schemeGoodsDo.getInterestFreeId());
				String interestFreeJson = interestFreeRulesDo.getRuleJson();
				if (StringUtils.isNotBlank(interestFreeJson) && !"0".equals(interestFreeJson)) {
					interestFreeArray = JSON.parseArray(interestFreeJson);
				}
			}
			List<Map<String, Object>> nperList = InterestFreeUitl.getConsumeList(array, interestFreeArray, BigDecimal.ONE.intValue(),
					goodsDo.getSaleAmount(), resource.getValue1(), resource.getValue2());
			
			if(nperList!= null){
				goodsInfo.put("goodsType", "1");
				Map<String, Object> nperMap = nperList.get(nperList.size() - 1);
				String isFree = (String)nperMap.get("isFree");
				if(InterestfreeCode.NO_FREE.getCode().equals(isFree)) {
					nperMap.put("freeAmount", nperMap.get("amount"));
				}
				goodsInfo.put("nperMap", nperMap);
			}
			
			moreGoodsList.add(goodsInfo);
		}
		moreGoodsInfo.put("moreGoodsList", moreGoodsList);
		moreGoodsInfo.put("titleName", "更多商品");
		// 顶部轮播
		data.put("topBannerList", topBannerList);
		// 快速导航
		data.put("navigationInfo", navigationInfo);
		// 1大左2小右
		data.put("one2TwoInfoList", one2TwoInfoList);
		// 轮播+N
		data.put("midBanner2Many", midBanner2Many);
		// 首页活动商品
		data.put("activityInfoList", activityInfoList);
		// 更多商品
		data.put("moreGoodsInfo", moreGoodsInfo);

		resp.setResponseData(data);
		return resp;
	}

	private Map<String,Object> getNavigationInfoWithResourceDolist(List<AfResourceDo> bannerResclist) {
		Map<String,Object> navigationInfo = new HashMap<String,Object> ();
		
		List<Object> navigationList = new ArrayList<Object>();
		
		Map<String,Object> backgroundInfo = new HashMap<String,Object> ();
		// 查询快速导航背景图信息
		List<AfResourceDo> bgRescList = afResourceService.getConfigByTypes(ResourceType.HOME_NAVIGATION_BACKGROUND.getCode());
		if(bgRescList != null && !bgRescList.isEmpty()){
			AfResourceDo afResourceDo = bgRescList.get(0);
			backgroundInfo.put("imageUrl", afResourceDo.getValue());
			backgroundInfo.put("type", AfResourceSecType.NAVIGATION_BACKGROUND.getCode());
		}
		
		for (AfResourceDo afResourceDo : bannerResclist) {
			String secType = afResourceDo.getSecType();
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("imageUrl", afResourceDo.getValue());
			data.put("titleName", afResourceDo.getName());
			if(afResourceDo.getType().equals(AfResourceType.HomeNavigation.getCode())){
				data.put("type", secType);
				// 对首页充值的版本兼容修改
				if (contextApp.getAppVersion() <= 365 && afResourceDo.getSecType().equals(AfResourceSecType.NAVIGATION_BOLUOME.getCode())){
					data.put("type", AfResourceSecType.NAVIGATION_MOBILE_CHARGE.getCode());
				}
			}else{
				data.put("type", afResourceDo.getValue1());
			}
			data.put("content", afResourceDo.getValue2());
			data.put("sort", afResourceDo.getSort());
			navigationList.add(data);
			
		}
		navigationInfo.put("navigationList",navigationList);
		navigationInfo.put("backgroundInfo",backgroundInfo);
		return navigationInfo;
	}

	private Map<String,Object> getCarouselToManyWithResourceDoList(List<AfResourceDo> rescList) {
		Map<String,Object> midBanner2Many = new HashMap<String,Object>();
		List<Map<String,Object>> midBannerList = new ArrayList<Map<String,Object>> ();
		List<Map<String,Object>> manyEntityList = new ArrayList<Map<String,Object>> ();
		for(AfResourceDo afResourceDo : rescList) {
			Map<String, Object> data = new HashMap<String,Object> ();
			String value3 = afResourceDo.getValue3();
			data.put("imageUrl", afResourceDo.getValue());
			data.put("titleName", afResourceDo.getName());
			if(afResourceDo.getType().equals(AfResourceType.HomeNavigation.getCode())){
				data.put("type", afResourceDo.getSecType());
				// 对首页充值的版本兼容修改
				if (contextApp.getAppVersion() <= 365 && afResourceDo.getSecType().equals(AfResourceSecType.NAVIGATION_BOLUOME.getCode())){
					data.put("type", AfResourceSecType.NAVIGATION_MOBILE_CHARGE.getCode());
				}
			}else{
				data.put("type", afResourceDo.getValue1());
			}
			data.put("content", afResourceDo.getValue2());
			data.put("sort", afResourceDo.getSort());
			if(ResourceType.HOME_CAROUSEL_IMAGE.getCode().equals(value3)) {
				midBannerList.add(data);
			} else if (ResourceType.HOME_N_CAROUSEL_IMAGE.getCode().equals(value3)) {
				manyEntityList.add(data);
			}
		}
		midBanner2Many.put("midBannerList", midBannerList);
		midBanner2Many.put("manyEntityList", manyEntityList);
		return midBanner2Many;
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

	private List<Object> getOne2TwoObjectWithResourceDolist(List<AfResourceDo> rescList) {
		List<Object> one2TwoInfoList = new ArrayList<Object>();
		for (AfResourceDo afResourceDo : rescList) {
			String secType = afResourceDo.getSecType();
			if("MAIN_IMAGE".equals(secType)) {
				String value4 = afResourceDo.getValue4();
				Map<String,Object> one2TwoInfo = new HashMap<String,Object>();
				List<Object> bannerList = new ArrayList<Object>();
				// 获取活动信息
				AfResourceDo activityInfo = afResourceService.getResourceByResourceId(Long.parseLong(value4));
				String name = activityInfo.getName(); //活动名称
				one2TwoInfo.put("titleName", name);
				for(AfResourceDo secResDo : rescList) {
					if(value4.equals(secResDo.getValue4())) {
						Map<String, Object> data = new HashMap<String, Object>();
						data.put("imageUrl", secResDo.getValue());
						data.put("titleName", secResDo.getName());
						String descs = secResDo.getDescription();
						if(!StringUtils.isEmpty(descs)) {
							String[] levelTitles = descs.split("\\|");
							if(levelTitles.length > 1) {
								data.put("oneLevelTitle", levelTitles[0]);
								data.put("twoLevelTitle", levelTitles[1]);
							}
						}
						
						if(secResDo.getType().equals(AfResourceType.HomeNavigation.getCode())){
							data.put("type", secResDo.getSecType());
							// 对首页充值的版本兼容修改
							if (contextApp.getAppVersion() <= 365 && secResDo.getSecType().equals(AfResourceSecType.NAVIGATION_BOLUOME.getCode())){
								data.put("type", AfResourceSecType.NAVIGATION_MOBILE_CHARGE.getCode());
							}
						}else{
							data.put("type", secResDo.getValue1());
						}
						data.put("content", secResDo.getValue2());
						data.put("sort", secResDo.getSort());
						bannerList.add(data);
						
					}
				}
				one2TwoInfo.put("bannerList", bannerList);
				one2TwoInfoList.add(one2TwoInfo);
			}
		}
		return one2TwoInfoList;
	}

	private List<Object> getObjectWithResourceDolist(List<AfResourceDo> bannerResclist) {
		List<Object> bannerList = new ArrayList<Object>();
		
		for (AfResourceDo afResourceDo : bannerResclist) {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("imageUrl", afResourceDo.getValue());
			data.put("titleName", afResourceDo.getName());
			if(afResourceDo.getType().equals(AfResourceType.HomeNavigation.getCode())){
				data.put("type", afResourceDo.getSecType());
				// 对首页充值的版本兼容修改
				if (contextApp.getAppVersion() <= 365 && afResourceDo.getSecType().equals(AfResourceSecType.NAVIGATION_BOLUOME.getCode())){
					data.put("type", AfResourceSecType.NAVIGATION_MOBILE_CHARGE.getCode());
				}
			}else{
				data.put("type", afResourceDo.getValue1());
			}
			data.put("content", afResourceDo.getValue2());
			data.put("sort", afResourceDo.getSort());

			bannerList.add(data);
		}

		return bannerList;
	}

}
