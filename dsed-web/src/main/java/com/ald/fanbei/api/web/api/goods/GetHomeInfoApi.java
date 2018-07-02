/**
 * 
 */
package com.ald.fanbei.api.web.api.goods;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.PickBrandCouponRequestBo;
import com.ald.fanbei.api.biz.service.AfAbtestDeviceNewService;
import com.ald.fanbei.api.biz.service.AfActivityGoodsService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserCouponService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.service.JpushService;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfResourceSecType;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.ResourceType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfAbtestDeviceNewDo;
import com.ald.fanbei.api.dal.domain.AfActivityGoodsDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;


/**
 * @author suweili
 *
 */
@Component("getHomeInfoApi")
public class GetHomeInfoApi implements ApiHandle {

	@Resource
	AfResourceService afResourceService;
	
	@Resource
	AfActivityGoodsService afActivityGoodsService;
	
	@Resource
	JpushService jpushService;
	
	@Resource 
	AfUserCouponService afUserCouponService;
	
	@Resource
	AfUserService afUserService;
	
	@Resource
	BizCacheUtil bizCacheUtil;
	@Resource
	AfAbtestDeviceNewService afAbtestDeviceNewService;
	
	private FanbeiContext contextApp;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		
		contextApp = context;
		Map<String, Object> data = new HashMap<String, Object>();
		String type = ConfigProperties.get(Constants.CONFKEY_INVELOMENT_TYPE);
		List<Object> bannerList = new ArrayList<Object>();
		List<Object> bannerSecList = new ArrayList<Object>();
		List<Object> one2OneList = new ArrayList<Object>();
		List<Object> one2ManyList = new ArrayList<Object>();
		List<Object> one2TwoList = new ArrayList<Object>();
		List<Object> one2TwoList2 = new ArrayList<Object>();
		List<Object> homeActivityList = new ArrayList<Object>();
		List<Object> navigationList = new ArrayList<Object>();
		Integer appVersion = context.getAppVersion();
		try{
			String userName = context.getUserName();
			Long userId = context.getUserId();
			if(userName != null && userId != null) {
				//获取后台配置的注册时间
				String regTime = "";
				List<AfResourceDo> regTimeList = afResourceService.getConfigByTypes(ResourceType.APP_UPGRADE_REGISTER_TIME.getCode());
				if(regTimeList != null && !regTimeList.isEmpty()) {
					AfResourceDo regTimeInfo  = regTimeList.get(0);
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
				if(resList != null && !resList.isEmpty()) {
					AfResourceDo versionInfoRes = resList.get(0);
					onOff = versionInfoRes.getValue1();
					String version = versionInfoRes.getValue();
					givenVersion = Integer.valueOf(version);
				}
				logger.info("GetHomeInfoApi userName=>" + userName);
				if(ltSaveObj == null) {
					long secs = DateUtil.getSecsEndOfDay();
					if(appVersion.compareTo(givenVersion) < 0 && "Y".equals(onOff)) {
						jpushService.jPushPopupWnd("LT_GIVEN_VERSION_WND",userName);
						logger.error("LT_GIVEN_VERSION_WND send success");
						bizCacheUtil.saveObject(ltStoreKey, "Y", secs); //单位:秒
					}
				}
				String gtStoreKey = "GET_HOME_INFO_GT" + userName;
				Object gtSaveObj = bizCacheUtil.getObject(gtStoreKey);
				// 获取后台配置的优惠券信息
				List<AfResourceDo>  couponsList= null;
				
				if(gmtCreate.after(regDate)) {
					// 新用户
					couponsList = afResourceService.getConfigByTypes(ResourceType.APP_UPDATE_COUPON_NEW.getCode());
				} else {
					// 老用户
					couponsList = afResourceService.getConfigByTypes(ResourceType.APP_UPDATE_COUPON.getCode());
				}
				if(couponsList != null && !couponsList.isEmpty()) {
					AfResourceDo couponInfoRes = couponsList.get(0);
					String couponIdStr = couponInfoRes.getValue();
					String[] couponIds = couponIdStr.split(",");
					boolean sended = false;
					for(String couponId : couponIds) {
						String[] tmp = couponId.split(":");
						if(tmp.length > 1) continue;
						int count = afUserCouponService.getUserCouponByUserIdAndCouponId(userId, Long.parseLong(couponId));
						if(count > 0 ) sended = true;
						break;
					}
					if(!sended && gtSaveObj == null) {
						if(appVersion.compareTo(givenVersion) >= 0 &&  "Y".equals(onOff)) {
							jpushService.jPushPopupWnd("GT_GIVEN_VERSION_WND",userName);
							logger.error("GT_GIVEN_VERSION_WND send success");
							bizCacheUtil.saveObject(gtStoreKey, "Y");
							for(String couponId : couponIds) {
								String[] tmp = couponId.split(":");
								// 用户发券
								try{
									if(tmp.length > 1) {
										String sceneId = tmp[0];
										grantBoluomiCoupon(Long.parseLong(sceneId), userId);
									} else {
										afUserCouponService.grantCoupon(userId, Long.parseLong(couponId), "updatePrize", "home");
									}
								} catch(Exception e) {
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
		} catch(Exception e) {
			logger.error("push wnd error=>" + e.getMessage());
		}
		//正式环境和预发布环境区分
		if (Constants.INVELOMENT_TYPE_ONLINE.equals(type) || Constants.INVELOMENT_TYPE_TEST.equals(type)) {
			bannerList = getObjectWithResourceDolist(
					afResourceService.getResourceHomeListByTypeOrderBy(AfResourceType.HomeBanner.getCode()));
		
			if(appVersion >= 363){
				bannerSecList = getObjectWithResourceDolist(
					afResourceService.getResourceHomeListByTypeOrderBy(AfResourceType.HomeSecondBanner.getCode()));
			}
            one2OneList = getObjectWithResourceDolist(
            		afResourceService.getOneToManyResourceOrderByBytype(AfResourceType.HomeOneImage.getCode()));
            		
            one2ManyList = getOne2ManyObjectWithResourceDolist(
            		afResourceService.getOneToManyResourceOrderByBytype(AfResourceType.HomeOneToMany.getCode()));
            		
            one2TwoList = getOne2ManyObjectWithResourceDolist(
            		afResourceService.getOneToManyResourceOrderByBytype(AfResourceType.HomeOneToTwo.getCode()));
            		
            one2TwoList2 = getOne2ManyObjectWithResourceDolist(
            		afResourceService.getOneToManyResourceOrderByBytype(AfResourceType.HomeOneToTwo2.getCode()));
            		
            homeActivityList = getOne2ManyObjectWithResourceDolist(
            		afResourceService.getOneToManyResourceOrderByBytype(AfResourceType.HomeActivity.getCode()));
            navigationList = getObjectWithResourceDolist(
				afResourceService.getHomeIndexListByOrderby(AfResourceType.HomeNavigation.getCode()));
		} else if (Constants.INVELOMENT_TYPE_PRE_ENV.equals(type) ){
			bannerList = getObjectWithResourceDolist(
				afResourceService.getResourceHomeListByTypeOrderByOnPreEnv(AfResourceType.HomeBanner.getCode()));
			if(context.getAppVersion() >= 363){
			     bannerSecList = getObjectWithResourceDolist(
				afResourceService.getResourceHomeListByTypeOrderByOnPreEnv(AfResourceType.HomeSecondBanner.getCode()));
		    }
        	one2OneList = getObjectWithResourceDolist(
        		afResourceService.getOneToManyResourceOrderByBytypeOnPreEnv(AfResourceType.HomeOneImage.getCode()));
        		
        	one2ManyList = getOne2ManyObjectWithResourceDolist(
        		afResourceService.getOneToManyResourceOrderByBytypeOnPreEnv(AfResourceType.HomeOneToMany.getCode()));
        		
        	one2TwoList = getOne2ManyObjectWithResourceDolist(
        		afResourceService.getOneToManyResourceOrderByBytypeOnPreEnv(AfResourceType.HomeOneToTwo.getCode()));
        		
        	one2TwoList2 = getOne2ManyObjectWithResourceDolist(
        		afResourceService.getOneToManyResourceOrderByBytypeOnPreEnv(AfResourceType.HomeOneToTwo2.getCode()));
        		
        	homeActivityList = getOne2ManyObjectWithResourceDolist(
        		afResourceService.getOneToManyResourceOrderByBytypeOnPreEnv(AfResourceType.HomeActivity.getCode()));
        		//预发线上未区分
        	navigationList = getObjectWithResourceDolist(
        		afResourceService.getHomeIndexListByOrderby(AfResourceType.HomeNavigation.getCode()));
		}
		
		data.put("bannerList", bannerList);
		data.put("bannerSecList", bannerSecList);
		data.put("homeActivityList",homeActivityList);
		data.put("one2ManyList", one2ManyList);
		data.put("one2TwoList", one2TwoList);
		data.put("one2OneList", one2OneList);
		data.put("navigationList", navigationList);
		data.put("one2TwoList2",one2TwoList2);
	
		resp.setResponseData(data);
		return resp;
	}
	
	private void grantBoluomiCoupon(Long sceneId, Long userId ) {
		logger.info(" pickBoluomeCoupon begin , sceneId = {}, userId = {}",sceneId, userId);
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
		if(url != null) {
			url = url.replace(" ", "");
		}
		String resultString = HttpUtil.doHttpPostJsonParam(url, JSONObject.toJSONString(bo));
		logger.info("pickBoluomeCoupon boluome bo = {}, resultString = {}", JSONObject.toJSONString(bo), resultString);
		JSONObject resultJson = JSONObject.parseObject(resultString);
		if (!"0".equals(resultJson.getString("code"))) {
			throw new FanbeiException(resultJson.getString("msg"));
		} else if (JSONArray.parseArray(resultJson.getString("data")).size() == 0){
			throw new FanbeiException("仅限领取一次，请勿重复领取！");
		}
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

	private List<Object> getOne2ManyObjectWithResourceDolist(List<AfResourceDo> resclist) {
		List<Object> bannerList = new ArrayList<Object>();
		String value4 = "-1";
		Map<String, Object> oneData = new HashMap<String, Object>();
		List<Map<String, Object>> manyData = new ArrayList<Map<String, Object>>();

		for (int i = 0; i < resclist.size(); i++) {
			AfResourceDo afResourceDo = resclist.get(i);
			if (!StringUtil.equals(value4, afResourceDo.getValue4()) ) {
				// 将多个对象加入数组中
				if (!StringUtil.equals(value4, "-1") ) {
					addListDataWithResource(oneData, manyData, bannerList);
				}
				value4 = afResourceDo.getValue4();
			}

			Map<String, Object> data = new HashMap<String, Object>();
			data.put("imageUrl", afResourceDo.getValue());
			data.put("titleName", afResourceDo.getName());
			data.put("type", afResourceDo.getValue1());
			data.put("content", afResourceDo.getValue2());
			data.put("sort", afResourceDo.getSort());
			// 1+2 模式新增时间字段的判断处理
			if(AfResourceType.HomeOneToTwo2.getCode().equals(afResourceDo.getType())){
				if("GOODS_ID".equals(afResourceDo.getValue1())){
					Long goodsId = NumberUtil.objToLong(afResourceDo.getValue2());
					AfActivityGoodsDo activityGoodsDo = afActivityGoodsService.getActivityGoodsByGoodsId(goodsId);
					if(activityGoodsDo != null){
						
						data.put("startTime", activityGoodsDo.getStartTime());
						data.put("validStart", activityGoodsDo.getValidStart());
						data.put("validEnd", activityGoodsDo.getValidEnd());
						data.put("currentTime", new Date());	
					}	
				}
				
			}
			
			if (StringUtil.equals(afResourceDo.getSecType(), AfResourceSecType.ResourceValue1MainImage.getCode())) {
				oneData = data;
			} else {
				manyData.add(data);
			}
		}
		if (manyData.size() > 0 || oneData.size() > 0) {
			addListDataWithResource(oneData, manyData, bannerList);
		}

		return bannerList;
	}

	private void addListDataWithResource(Map<String, Object> oneData, List<Map<String, Object>> manyData,
			List<Object> bannerList) {
		Map<String, Object> listData = new HashMap<String, Object>();
		List<Map<String, Object>> manyTemData = new ArrayList<Map<String, Object>>(manyData);

		Map<String, Object> oneTemData = new HashMap<String, Object>();
		oneTemData.putAll(oneData);

		listData.put("manyEntity", manyTemData);
		listData.put("oneEntity", oneTemData);
		bannerList.add(listData);
		manyData.clear();
		oneData.clear();
	}
	

}
