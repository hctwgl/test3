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

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.dal.domain.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.PickBrandCouponRequestBo;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.CacheConstants;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfResourceSecType;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.InterestfreeCode;
import com.ald.fanbei.api.common.enums.ResourceType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.dto.AfEncoreGoodsDto;
import com.ald.fanbei.api.web.cache.Cache;
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

	@Resource
	JpushService jpushService;

	@Resource
	AfUserCouponService afUserCouponService;

	@Resource
	AfUserService afUserService;
	@Resource
	AfAbtestDeviceNewService afAbtestDeviceNewService;

	@Resource
	BizCacheUtil bizCacheUtil;
	
	@Resource
	Cache schedeledCache;

	private FanbeiContext contextApp;

	@Resource
	AfSeckillActivityService afSeckillActivityService;
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		contextApp = context;

		String deviceType = ObjectUtils.toString(requestDataVo.getParams().get("deviceType"));

		Integer appVersion = context.getAppVersion();
		try {
			String userName = context.getUserName();
			Long userId = context.getUserId();
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
				logger.info("GetHomeInfoApi userName=>" + userName);
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
				try {
					String deviceId = ObjectUtils.toString(requestDataVo.getParams().get("deviceId"));
					if (StringUtils.isNotEmpty(deviceId)) {
					  //String deviceIdTail = StringUtil.getDeviceTailNum(deviceId);
						AfAbtestDeviceNewDo abTestDeviceDo = new AfAbtestDeviceNewDo();
						abTestDeviceDo.setUserId(userId);
						abTestDeviceDo.setDeviceNum(deviceId);
						// 通过唯一组合索引控制数据不重复
						afAbtestDeviceNewService.addUserDeviceInfo(abTestDeviceDo);
					}
				}  catch (Exception e) {
					// ignore error.
				}
				
				
			}
		} catch (Exception e) {
			logger.error("push wnd error=>" + e.getMessage());
		}
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("homePageType", "OLD");
		String type = ConfigProperties.get(Constants.CONFKEY_INVELOMENT_TYPE);
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
		// 正式环境和预发布环境区分
		if (Constants.INVELOMENT_TYPE_ONLINE.equals(type) || Constants.INVELOMENT_TYPE_TEST.equals(type)) {
			// 新版,旧版,banner图不一样
			String homeBanner = AfResourceType.HomeBannerNew.getCode();
			if (StringUtils.equals(deviceType, "IPHONEX")) {
				homeBanner = AfResourceType.HomeBannerNewMostiPhoneX.getCode();
			} else {
				if (contextApp.getAppVersion() >= 394) {
					homeBanner = AfResourceType.HomeBannerNewMost.getCode();
				}
			}

			topBannerList = getObjectWithResourceDolist(afResourceService.getResourceHomeListByTypeOrderBy(homeBanner));
		} else if (Constants.INVELOMENT_TYPE_PRE_ENV.equals(type)) {
			// 新版,旧版,banner图不一样
			String homeBanner = AfResourceType.HomeBannerNew.getCode();
			if (StringUtils.equals(deviceType, "IPHONEX")) {
				homeBanner = AfResourceType.HomeBannerNewMostiPhoneX.getCode();
			} else {
				if (contextApp.getAppVersion() >= 394) {
					homeBanner = AfResourceType.HomeBannerNewMost.getCode();
				}
			}
			topBannerList = getObjectWithResourceDolist(
					afResourceService.getResourceHomeListByTypeOrderByOnPreEnv(homeBanner));
		}
		// 顶部轮播
		data.put("topBannerList", topBannerList);
		
		// 快速导航信息
		Map<String, Object> navigationInfo = getNavigationInfoWithResourceDolist(
				afResourceService.getHomeIndexListByOrderby(AfResourceType.HomeNavigation.getCode()));

		// 1大左2小右
		// 如果是394之前的版本用旧的,394以后是新版本
		String beforeHomeOneToTwo = AfResourceType.HomeOneToTwo.getCode();
		if (contextApp.getAppVersion() >= 394) {
			beforeHomeOneToTwo = AfResourceType.NewHomeOneToTwo.getCode();
		}
		List<Object> one2TwoInfoList = getOne2TwoObjectWithResourceDolist(
				afResourceService.getOneToManyResourceOrderByBytype(beforeHomeOneToTwo));

		// 获取轮播 +N信息 旧的394之前版本
		String beforeImage = AfResourceType.HomeCarouseToMany.getCode();
		if (contextApp.getAppVersion() >= 394) {
			beforeImage = AfResourceType.NewHomeCarouseToMany.getCode();
		}
		Map<String, Object> midBanner2Many = getCarouselToManyWithResourceDoList(
				afResourceService.getCarouselToManyResourceOrderByType(beforeImage));

		// 新增运营位1,快捷导航上方活动专场
		List<Object> navigationUpOne = getNavigationUpOneResourceDoList(
				afResourceService.getNavigationUpOneResourceDoList(AfResourceType.HomeNavigationUpOne.getCode()));
		// 新增运营位2,快捷导航下方活动专场
		List<Object> navigationDownOne = getNavigationDownTwoResourceDoList(
				afResourceService.getNavigationDownTwoResourceDoList(AfResourceType.HomeNavigationDownTwo.getCode()));
		// 3/6/9运营位
		Map<String, Object> manyPricutres = getManyPricutresResourceDoList(
				afResourceService.getManyPricutresResourceDoList(AfResourceType.ManeyPictrues.getCode()));
		// 获取首页活动信息
		// 获取借款分期配置信息
		AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE,
				Constants.RES_BORROW_CONSUME);
		JSONArray array = JSON.parseArray(resource.getValue());
		// 删除2分期
		if (array == null) {
			throw new FanbeiException(FanbeiExceptionCode.BORROW_CONSUME_NOT_EXIST_ERROR);
		}
		// removeSecondNper(array);
		List<Map<String, Object>> activityInfoList = bizCacheUtil.getObjectList(CacheConstants.HOME_PAGE.GET_HOME_INFO_V1_ACTIVITY_INFO_LIST.getCode());
		if(activityInfoList == null) {
			// redis取不到，则从一级缓存获取
			activityInfoList = (List<Map<String, Object>>) schedeledCache.getObject(CacheConstants.HOME_PAGE.GET_HOME_INFO_V1_ACTIVITY_INFO_LIST.getCode());
		}
		if(activityInfoList == null) {
			// 一级缓存获取不到，则从数据库获取
			activityInfoList = getHomeActivityList(resource, array);
			bizCacheUtil.saveListForever(CacheConstants.HOME_PAGE.GET_HOME_INFO_V1_ACTIVITY_INFO_LIST.getCode(), activityInfoList);
		}
		// 更多商品
		Map<String, Object> moreGoodsInfo = (Map<String, Object>) bizCacheUtil.getMap(CacheConstants.HOME_PAGE.GET_HOME_INFO_V1_MORE_GOODS_INFO.getCode());
		if(moreGoodsInfo == null) {
			moreGoodsInfo = (Map<String, Object>) schedeledCache.getObject(CacheConstants.HOME_PAGE.GET_HOME_INFO_V1_MORE_GOODS_INFO.getCode());
		}
		if(moreGoodsInfo == null) {
			moreGoodsInfo = getMoreGoodsInfo(resource, array);
			bizCacheUtil.saveMapForever(CacheConstants.HOME_PAGE.GET_HOME_INFO_V1_MORE_GOODS_INFO.getCode(), moreGoodsInfo);
		}

		// 背景图配置
		List<AfResourceDo> backgroundList = afResourceService
				.getBackGroundByType(ResourceType.HOMEPAGE_BACKGROUND.getCode());

		// 快速导航
		data.put("navigationInfo", navigationInfo);
		// 1大左2小右
		data.put("one2TwoInfoList", one2TwoInfoList);
		// 新增运营位1,快捷导航上方活动专场
		data.put("navigationUpOne", navigationUpOne);
		// 新增运营位2,快捷导航下方活动专场
		data.put("navigationDownOne", navigationDownOne);
		// 轮播+N
		data.put("midBanner2Many", midBanner2Many);
		// 3/6/9运营位
		data.put("manyPricutres", manyPricutres);
		// 首页活动商品
		data.put("activityInfoList", activityInfoList);
		// 更多商品
		data.put("moreGoodsInfo", moreGoodsInfo);
		// 首页背景图
		data.put("backgroundSet", backgroundList);
		resp.setResponseData(data);
		return resp;
	}

	public Map<String, Object> getMoreGoodsInfo(AfResourceDo resource, JSONArray array) {
		Map<String, Object> moreGoodsInfo = new HashMap<String, Object>();
		AfActivityDo moreActivity = afActivityService.getHomeMoreActivity();
		List<Map<String, Object>> moreGoodsList = new ArrayList<Map<String, Object>>();
		List<AfEncoreGoodsDto> moreGoodsDoList = afActivityGoodsService
				.listHomeActivityGoodsByActivityId(moreActivity.getId());
		//判断商品是否处于活动中
		List<AfSeckillActivityGoodsDo> activityGoodsDos = new ArrayList<>();
		List<Long> goodsIdList = new ArrayList<>();
		for (AfGoodsDo goodsDo : moreGoodsDoList) {
			goodsIdList.add(goodsDo.getRid());
		}
		if(goodsIdList!=null&&goodsIdList.size()>0){
			activityGoodsDos =afSeckillActivityService.getActivityGoodsByGoodsIds(goodsIdList);
		}
		for (AfEncoreGoodsDto goodsDo : moreGoodsDoList) {
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
			String doubleRebate = goodsDo.getDoubleRebate();
			goodsInfo.put("doubleRebate", "0".equals(doubleRebate) ? "N" : "Y");
			goodsInfo.put("goodsType", "0");
			goodsInfo.put("remark", StringUtil.null2Str(goodsDo.getRemark()));
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

			moreGoodsList.add(goodsInfo);
		}
		moreGoodsInfo.put("moreGoodsList", moreGoodsList);
		moreGoodsInfo.put("titleName", "更多商品");
		return moreGoodsInfo;
	}

	public List<Map<String, Object>> getHomeActivityList(AfResourceDo resource, JSONArray array) {
		List<AfActivityDo> activityList = afActivityService.listAllHomeActivity();
		List<Map<String, Object>> activityInfoList = new ArrayList<Map<String, Object>>();
		for (AfActivityDo afActivityDo : activityList) {
			Map<String, Object> activityData = new HashMap<String, Object>();
			activityData.put("titleName", afActivityDo.getName());
			activityData.put("imageUrl", afActivityDo.getIconUrl());
			activityData.put("type", afActivityDo.getLinkType());
			activityData.put("content", afActivityDo.getLinkContent());
			// 活动商品
			List<Map<String, Object>> goodsList = new ArrayList<Map<String, Object>>();
			// 获取活动商品
			List<AfEncoreGoodsDto> activityGoodsDoList = afActivityGoodsService
					.listHomeActivityGoodsByActivityId(afActivityDo.getId());
			//判断商品是否处于活动中
			List<AfSeckillActivityGoodsDo> activityGoodsDos = new ArrayList<>();
			List<Long> goodsIdList = new ArrayList<>();
			for (AfEncoreGoodsDto goodsDo : activityGoodsDoList) {
				goodsIdList.add(goodsDo.getRid());
			}
			if(goodsIdList!=null&&goodsIdList.size()>0){
				activityGoodsDos =afSeckillActivityService.getActivityGoodsByGoodsIds(goodsIdList);
			}
			for (AfEncoreGoodsDto goodsDo : activityGoodsDoList) {
				Map<String, Object> goodsInfo = new HashMap<String, Object>();
			try{
				//改变活动价格
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
				String doubleRebate = goodsDo.getDoubleRebate();
				goodsInfo.put("doubleRebate", "0".equals(doubleRebate) ? "N" : "Y");
				goodsInfo.put("goodsType", "0");
				goodsInfo.put("remark", StringUtil.null2Str(goodsDo.getRemark()));
				// 如果是分期免息商品，则计算分期
				Long goodsId = goodsDo.getRid();
				AfSchemeGoodsDo schemeGoodsDo = null;
				try {
					schemeGoodsDo = afSchemeGoodsService.getSchemeGoodsByGoodsId(goodsId);
				} catch (Exception e) {
					logger.error(e.toString());
				}
				logger.info("getHomeActivityList schemeGoodsDo = "+JSON.toJSONString(schemeGoodsDo));
				JSONArray interestFreeArray = null;
				if (schemeGoodsDo != null) {
					AfInterestFreeRulesDo interestFreeRulesDo = afInterestFreeRulesService
							.getById(schemeGoodsDo.getInterestFreeId());
				logger.info("getHomeActivityList interestFreeRulesDo = "+JSON.toJSONString(interestFreeRulesDo));
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

				goodsList.add(goodsInfo);
			}catch (Exception e){
				logger.error("getHomeActivityList error:" + JSON.toJSONString(e));
				logger.info("getHomeActivityList is error = "+e+"goodsDo="+JSON.toJSONString(goodsDo)+"goodsInfo = "+JSON.toJSONString(goodsInfo));
			}
		}
			activityData.put("goodsList", goodsList);
			activityInfoList.add(activityData);
	}
		return activityInfoList;
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

	private Map<String, Object> getNavigationInfoWithResourceDolist(List<AfResourceDo> bannerResclist) {
		Map<String, Object> navigationInfo = new HashMap<String, Object>();
		AfResourceDo afResourceDoNine = new AfResourceDo();
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
		int lsize = bannerResclist.size();
		// for (AfResourceDo afResourceDo : bannerResclist) {
		for (int i = 0; i < lsize; i++) {
			if (lsize > 5 && lsize < 10) {
				if (i >= 5) {
					break;
				}
			} else if (lsize > 10) {
				if (i >= 10) {
					break;
				}
			}
			AfResourceDo afResourceDo = bannerResclist.get(i);
			String secType = afResourceDo.getSecType();
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("imageUrl", afResourceDo.getValue());
			data.put("titleName", afResourceDo.getName());
			if (afResourceDo.getType().equals(AfResourceType.HomeNavigation.getCode())) {
				data.put("type", secType);
				// 对首页充值的版本兼容修改
				if (contextApp.getAppVersion() <= 365
						&& afResourceDo.getSecType().equals(AfResourceSecType.NAVIGATION_BOLUOME.getCode())) {
					data.put("type", AfResourceSecType.NAVIGATION_MOBILE_CHARGE.getCode());
				}
			} else {
				data.put("type", afResourceDo.getValue1());
			}
			// 对首页商品分类的版本兼容修改
			if (contextApp.getAppVersion() <= 393
					&& afResourceDo.getSecType().equals(AfResourceSecType.NAVIGATION_CATEGORY.getCode())) {
				data.put("type", "NAVIGATION_NINE");
				afResourceDoNine = afResourceService.getConfigByTypesAndSecType(AfResourceType.HomeNavigation.getCode(),
						"NAVIGATION_NINE");
				data.put("content", afResourceDoNine.getValue2());
				data.put("sort", afResourceDoNine.getSort());
				data.put("color", afResourceDoNine.getValue3());
				data.put("imageUrl", afResourceDoNine.getValue());
				data.put("titleName", afResourceDoNine.getName());
			} else {
				data.put("content", afResourceDo.getValue2());
				data.put("sort", afResourceDo.getSort());
				data.put("color", afResourceDo.getValue3());
			}
			navigationList.add(data);

		}
		navigationInfo.put("navigationList", navigationList);
		navigationInfo.put("backgroundInfo", backgroundInfo);
		return navigationInfo;
	}

	private Map<String, Object> getCarouselToManyWithResourceDoList(List<AfResourceDo> rescList) {
		Map<String, Object> midBanner2Many = new HashMap<String, Object>();
		List<Map<String, Object>> midBannerList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> manyEntityList = new ArrayList<Map<String, Object>>();
		for (AfResourceDo afResourceDo : rescList) {
			Map<String, Object> data = new HashMap<String, Object>();
			String value3 = afResourceDo.getValue3();
			data.put("imageUrl", afResourceDo.getValue());
			data.put("titleName", afResourceDo.getName());
			if (afResourceDo.getType().equals(AfResourceType.HomeNavigation.getCode())) {
				data.put("type", afResourceDo.getSecType());
				// 对首页充值的版本兼容修改
				if (contextApp.getAppVersion() <= 365
						&& afResourceDo.getSecType().equals(AfResourceSecType.NAVIGATION_BOLUOME.getCode())) {
					data.put("type", AfResourceSecType.NAVIGATION_MOBILE_CHARGE.getCode());
				}
			} else {
				data.put("type", afResourceDo.getValue1());
			}
			data.put("content", afResourceDo.getValue2());
			data.put("sort", afResourceDo.getSort());
			String type1 = ResourceType.HOME_CAROUSEL_IMAGE.getCode();
			String type2 = ResourceType.HOME_N_CAROUSEL_IMAGE.getCode();
			if (contextApp.getAppVersion() >= 394) {
				type1 = ResourceType.NEW_HOME_CAROUSEL_IMAGE.getCode();
				type2 = ResourceType.NEW_HOME_N_CAROUSEL_IMAGE.getCode();
			}
			if (type1.equals(value3)) {
				midBannerList.add(data);
			} else if (type2.equals(value3)) {
				manyEntityList.add(data);
			}
		}
		midBanner2Many.put("midBannerList", midBannerList);
		midBanner2Many.put("manyEntityList", manyEntityList);
		return midBanner2Many;
	}

	private Map<String, Object> getManyPricutresResourceDoList(List<AfResourceDo> rescList) {
		Map<String, Object> manyPricutres = new HashMap<String, Object>();
		List<Map<String, Object>> manyPricutresList = new ArrayList<Map<String, Object>>();
		if (rescList == null) {
			manyPricutres.put("manyPricutresList", manyPricutresList);
			return manyPricutres;
		}
		if (rescList.size() < 3) {
			manyPricutres.put("manyPricutresList", manyPricutresList);
			return manyPricutres;
		}
		if (rescList.size() >= 3 && rescList.size() < 6) {
			if (rescList.size() == 5) {
				rescList.remove(4);
			}
			if (rescList.size() == 4) {
				rescList.remove(3);
			}
		}
		if (rescList.size() >= 6 && rescList.size() < 9) {
			if (rescList.size() == 8) {
				rescList.remove(7);
			}
			if (rescList.size() == 7) {
				rescList.remove(6);
			}
		}
		for (AfResourceDo afResourceDo : rescList) {
			Map<String, Object> data = new HashMap<String, Object>();
			String value3 = afResourceDo.getValue3();
			if (StringUtils.isNotBlank(value3)) {
				data.put("imageUrl", afResourceDo.getValue());
				data.put("titleName", afResourceDo.getName());
				if (afResourceDo.getType().equals(AfResourceType.HomeNavigation.getCode())) {
					data.put("type", afResourceDo.getSecType());
					// 对首页充值的版本兼容修改
					if (contextApp.getAppVersion() <= 365
							&& afResourceDo.getSecType().equals(AfResourceSecType.NAVIGATION_BOLUOME.getCode())) {
						data.put("type", AfResourceSecType.NAVIGATION_MOBILE_CHARGE.getCode());
					}
				} else {
					data.put("type", afResourceDo.getValue1());
				}
				data.put("content", afResourceDo.getValue2());
				data.put("sort", afResourceDo.getSort());
				manyPricutresList.add(data);
			}
		}
		manyPricutres.put("manyPricutresList", manyPricutresList);
		return manyPricutres;
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

	private List<Object> getOne2TwoObjectWithResourceDolist(List<AfResourceDo> rescList) {
		List<Object> one2TwoInfoList = new ArrayList<Object>();
		for (AfResourceDo afResourceDo : rescList) {
			String secType = afResourceDo.getSecType();
			if ("MAIN_IMAGE".equals(secType)) {
				String value4 = afResourceDo.getValue4();
				Map<String, Object> one2TwoInfo = new HashMap<String, Object>();
				List<Object> bannerList = new ArrayList<Object>();
				// 获取活动信息
				AfResourceDo activityInfo = afResourceService.getResourceByResourceId(Long.parseLong(value4));
				String name = activityInfo.getName(); // 活动名称
				one2TwoInfo.put("titleName", name);
				for (AfResourceDo secResDo : rescList) {
					if (value4.equals(secResDo.getValue4())) {
						Map<String, Object> data = new HashMap<String, Object>();
						data.put("imageUrl", secResDo.getValue());
						data.put("titleName", secResDo.getName());
						String descs = secResDo.getDescription();
						if (!StringUtils.isEmpty(descs)) {
							String[] levelTitles = descs.split("\\|");
							if (levelTitles.length > 1) {
								data.put("oneLevelTitle", levelTitles[0]);
								data.put("twoLevelTitle", levelTitles[1]);
							}
						}

						if (secResDo.getType().equals(AfResourceType.HomeNavigation.getCode())) {
							data.put("type", secResDo.getSecType());
							// 对首页充值的版本兼容修改
							if (contextApp.getAppVersion() <= 365
									&& secResDo.getSecType().equals(AfResourceSecType.NAVIGATION_BOLUOME.getCode())) {
								data.put("type", AfResourceSecType.NAVIGATION_MOBILE_CHARGE.getCode());
							}
						} else {
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
			if (afResourceDo.getType().equals(AfResourceType.HomeNavigation.getCode())) {
				data.put("type", afResourceDo.getSecType());
				// 对首页充值的版本兼容修改
				if (contextApp.getAppVersion() <= 365
						&& afResourceDo.getSecType().equals(AfResourceSecType.NAVIGATION_BOLUOME.getCode())) {
					data.put("type", AfResourceSecType.NAVIGATION_MOBILE_CHARGE.getCode());
				}
			} else {
				data.put("type", afResourceDo.getValue1());
			}
			data.put("content", afResourceDo.getValue2());
			data.put("sort", afResourceDo.getSort());

			bannerList.add(data);
		}

		return bannerList;
	}

	private List<Object> getNavigationUpOneResourceDoList(List<AfResourceDo> navigationUplist) {
		List<Object> navigationUpOne = new ArrayList<Object>();

		for (AfResourceDo afResourceDo : navigationUplist) {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("imageUrl", afResourceDo.getValue());
			data.put("titleName", afResourceDo.getName());
			if (afResourceDo.getType().equals(AfResourceType.HomeNavigation.getCode())) {
				data.put("type", afResourceDo.getSecType());
				// 对首页充值的版本兼容修改
				if (contextApp.getAppVersion() <= 365
						&& afResourceDo.getSecType().equals(AfResourceSecType.NAVIGATION_BOLUOME.getCode())) {
					data.put("type", AfResourceSecType.NAVIGATION_MOBILE_CHARGE.getCode());
				}
			} else {
				data.put("type", afResourceDo.getValue1());
			}
			data.put("content", afResourceDo.getValue2());
			data.put("sort", afResourceDo.getSort());

			navigationUpOne.add(data);
		}
		return navigationUpOne;
	}

	private List<Object> getNavigationDownTwoResourceDoList(List<AfResourceDo> navigationUplist) {
		List<Object> navigationList = new ArrayList<Object>();

		for (AfResourceDo afResourceDo : navigationUplist) {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("imageUrl", afResourceDo.getValue());
			data.put("titleName", afResourceDo.getName());
			if (afResourceDo.getType().equals(AfResourceType.HomeNavigation.getCode())) {
				data.put("type", afResourceDo.getSecType());
				// 对首页充值的版本兼容修改
				if (contextApp.getAppVersion() <= 365
						&& afResourceDo.getSecType().equals(AfResourceSecType.NAVIGATION_BOLUOME.getCode())) {
					data.put("type", AfResourceSecType.NAVIGATION_MOBILE_CHARGE.getCode());
				}
			} else {
				data.put("type", afResourceDo.getValue1());
			}
			data.put("content", afResourceDo.getValue2());
			data.put("sort", afResourceDo.getSort());

			navigationList.add(data);
		}
		return navigationList;
	}

}
