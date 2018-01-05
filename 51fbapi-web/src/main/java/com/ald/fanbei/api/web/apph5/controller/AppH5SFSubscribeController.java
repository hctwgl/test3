package com.ald.fanbei.api.web.apph5.controller;

import static org.hamcrest.CoreMatchers.nullValue;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ald.fanbei.api.biz.service.AfCouponCategoryService;
import com.ald.fanbei.api.biz.service.AfCouponService;
import com.ald.fanbei.api.biz.service.AfGoodsDoubleEggsService;
import com.ald.fanbei.api.biz.service.AfGoodsDoubleEggsUserService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfShopService;
import com.ald.fanbei.api.biz.service.AfUserCouponService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.enums.CouponWebFailStatus;
import com.ald.fanbei.api.common.enums.H5OpenNativeType;
import com.ald.fanbei.api.common.enums.SpringFestivalActivityEnum;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfCouponCategoryDo;
import com.ald.fanbei.api.dal.domain.AfCouponDo;
import com.ald.fanbei.api.dal.domain.AfGoodsDoubleEggsDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfSFgoodsVo;
import com.ald.fanbei.api.dal.domain.AfShopDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.AfCouponDouble12Vo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * @Title: AppH5SFSubscribe.java
 * @Package com.ald.fanbei.api.web.apph5.controller
 * @Description: TODO(用一句话描述该文件做什么)
 * @Copyright (c) 浙江阿拉丁电子商务股份有限公司 All Rights Reserved.
 * @author qiao
 * @date 2018年1月2日 上午11:17:14
 * @version V1.0
 */
@RestController
@RequestMapping(value = "/appH5SF", produces = "application/json;charset=UTF-8")
public class AppH5SFSubscribeController extends BaseController {

	@Resource
	AfResourceService afResourceService;
	@Resource
	AfGoodsDoubleEggsService afGoodsDoubleEggsService;
	@Resource
	AfGoodsDoubleEggsUserService afGoodsDoubleEggsUserService;
	@Resource
	AfUserService afUserService;
	@Resource
	AfShopService afShopService;
	@Resource
	AfCouponCategoryService afCouponCategoryService;
	@Resource
	AfCouponService afCouponService;
	@Resource
	AfUserCouponService afUserCouponService;

	String opennative = "/fanbei-web/opennative?name=";

	/**
	 * 
	 * @Title: convertUserNameToUserId @Description: @param userName @return
	 *         Long @throws
	 */
	private Long convertUserNameToUserId(String userName) {
		Long userId = null;
		if (!StringUtil.isBlank(userName)) {
			AfUserDo user = afUserService.getUserByUserName(userName);
			if (user != null) {
				userId = user.getRid();
			}

		}
		return userId;
	}

	/**
	 * 
	 * @Title: initHomePage @author qiao @date 2018年1月5日
	 * 下午1:39:36 @Description: @param request @param response @return @return
	 * String @throws
	 */
	@RequestMapping(value = "/initHomePage", method = RequestMethod.POST)
	public String initHomePage(HttpServletRequest request, HttpServletResponse response) {
		String result = "";
		FanbeiWebContext context = new FanbeiWebContext();
		java.util.Map<String, Object> data = new HashMap<>();

		try {
			context = doWebCheck(request, false);
			if (context != null) {

				String userName = context.getUserName();
				// init the userId for the interface : getFivePictures
				Long userId = 0L;

				// if login then
				if (StringUtil.isNotBlank(userName)) {
					userId = convertUserNameToUserId(userName);
				}

				// get goods to subscribe
				List<AfSFgoodsVo> goodsList = afGoodsDoubleEggsService.getFivePictures(userId);
				data.put("goodsList", goodsList);
				logger.info("/appH5SF/initHomePage userId={} , goodsList={} ", new Object[] { userId, goodsList });
				// get configuration from afresource
				AfResourceDo resourceDo = afResourceService.getConfigByTypesAndSecType("SPRING_FESTIVAL_ACTIVITY",
						"INIT_HOME_PAGE");
				if (resourceDo != null) {
					data.put("describtion", resourceDo.getValue2());
					data.put("strategy_img", resourceDo.getValue());
					data.put("strategy_redirect_img", resourceDo.getValue1());
				}
				logger.info("/appH5SF/initHomePage userId={} , goodsList={} ,resourceDo = {}",
						new Object[] { userId, goodsList, resourceDo });
				result = H5CommonResponse.getNewInstance(true, "初始化成功", "", data).toString();
			}

		} catch (Exception exception) {
			result = H5CommonResponse.getNewInstance(false, "初始化失败", "", exception.getMessage()).toString();
			logger.error("初始化数据失败  e = {} , resultStr = {}", exception, result);
			doMaidianLog(request, H5CommonResponse.getNewInstance(false, "fail"), result);
		}
		return result;
	}

	@RequestMapping(value = "/getBrandId", method = RequestMethod.POST)
	public String getBrandId(HttpServletRequest request, HttpServletResponse response) {
		String resultStr = "";
		java.util.Map<String, Object> data = new HashMap<>();

		try {

			String huoche = "HUOCHE";
			String jipiao = "JIPIAO";

			// get shop id for plane and train
			AfShopDo huocheDo = new AfShopDo();
			huocheDo.setType(huoche);
			huocheDo = afShopService.getShopInfoBySecTypeOpen(huocheDo);
			if (huocheDo != null) {
				data.put("trainShopId", huocheDo.getRid());
			}

			AfShopDo jipiaoDo = new AfShopDo();
			jipiaoDo.setType(jipiao);
			jipiaoDo = afShopService.getShopInfoBySecTypeOpen(jipiaoDo);
			if (jipiaoDo != null) {
				data.put("planeShopId", jipiaoDo.getRid());
			}

			logger.info("/appH5SF/getBrandId param: huocheDo={} , JipiaoDo ={}", huocheDo.toString(),
					jipiaoDo.toString());
			resultStr = H5CommonResponse.getNewInstance(true, "获取波罗蜜url成功", "", data).toString();

		} catch (Exception exception) {
			resultStr = H5CommonResponse.getNewInstance(false, "获取波罗蜜url失败", "", exception.getMessage()).toString();
			logger.error("获取波罗蜜url失败  e = {} , resultStr = {}", exception, resultStr);
			doMaidianLog(request, H5CommonResponse.getNewInstance(false, "fail"), resultStr);
		}
		return resultStr;
	}

	/**
	 * 
	 * @Title: getBrandUrl @author qiao @date 2018年1月5日
	 * 下午1:39:29 @Description: @param request @param response @return @return
	 * String @throws
	 */
	@RequestMapping(value = "/getBrandUrl", method = RequestMethod.POST)
	public String getBrandUrl(HttpServletRequest request, HttpServletResponse response) {
		String resultStr = "";
		FanbeiWebContext context = new FanbeiWebContext();
		java.util.Map<String, Object> data = new HashMap<>();

		try {
			context = doWebCheck(request, true);
			if (context != null) {

				Long shopId = NumberUtil.objToLongDefault(request.getParameter("shopId"), null);

				if (shopId == null) {
					logger.error("shopId is empty");
					return H5CommonResponse.getNewInstance(false, "参数shopId为空").toString();
				}

				AfShopDo shopInfo = afShopService.getShopById(shopId);
				if (shopInfo == null) {
					logger.error("shopId is invalid");
					return H5CommonResponse.getNewInstance(false, "参数shopId无效").toString();
				}
				String userName = context.getUserName();
				Long userId = convertUserNameToUserId(userName);
				String shopUrl = afShopService.parseBoluomeUrl(shopInfo.getShopUrl(), shopInfo.getPlatformName(),
						shopInfo.getType(), userId, userName);

				data.put("shopUrl", shopUrl);
				logger.info("/appH5SF/getBrandUrl param: userId={} , shopUrl ={}", userId, shopUrl);
				resultStr = H5CommonResponse.getNewInstance(true, "获取波罗蜜url成功", "", data).toString();
			}

		} catch (FanbeiException e) {
			if (e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_INVALID_SIGN_ERROR)) {
				data = new HashMap<>();
				String loginUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative
						+ H5OpenNativeType.AppLogin.getCode();
				data.put("loginUrl", loginUrl);
				resultStr = H5CommonResponse.getNewInstance(false, "没有登录", "", data).toString();
				return resultStr.toString();
			}
		} catch (Exception exception) {
			resultStr = H5CommonResponse.getNewInstance(false, "获取波罗蜜url失败", "", exception.getMessage()).toString();
			logger.error("获取波罗蜜url失败  e = {} , resultStr = {}", exception, resultStr);
			doMaidianLog(request, H5CommonResponse.getNewInstance(false, "fail"), resultStr);
		}
		return resultStr;
	}
	
	@RequestMapping(value = "/initTigerMachine", method = RequestMethod.POST)
	public String couponHomePage(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> data = new HashMap<String, Object>();
		FanbeiWebContext context = new FanbeiWebContext();
		String result = "";

		try {
			context = doWebCheck(request, false);
			Long userId = convertUserNameToUserId(context.getUserName());
			
			Long activityId = NumberUtil.objToLong(request.getParameter("activityId"));
			
			if (activityId == null ) {
				return H5CommonResponse.getNewInstance(false, "参数会场id获取失败").toString();
			}
			
			// 未登录初始化数据
			String tag = SpringFestivalActivityEnum.findTagByActivityId(activityId);  
			
			
			AfCouponCategoryDo couponCategory = afCouponCategoryService.getCouponCategoryByTag(tag);
			String coupons = couponCategory.getCoupons();
			if(StringUtil.isBlank(coupons)){
				return H5CommonResponse.getNewInstance(false, "改会场没有优惠券").toString();
			}
			JSONArray couponsArray = (JSONArray) JSONArray.parse(coupons);

			List<AfCouponDouble12Vo> couponVoList = new ArrayList<AfCouponDouble12Vo>();
			
			for (int i = 0; i < couponsArray.size(); i++) {
				String couponId = (String) couponsArray.getString(i);
				AfCouponDo afCouponDo = afCouponService.getCouponById(Long.parseLong(couponId));
				if (afCouponDo != null) {
					AfCouponDouble12Vo afCouponDouble12Vo = new AfCouponDouble12Vo();
					afCouponDouble12Vo.setId(afCouponDo.getRid());
					afCouponDouble12Vo.setName(afCouponDo.getName());
					afCouponDouble12Vo.setThreshold(afCouponDo.getUseRule());
					afCouponDouble12Vo.setAmount(afCouponDo.getAmount());
					afCouponDouble12Vo.setLimitAmount(afCouponDo.getLimitAmount());

					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					// 当前时间
					Date currentTime = new Date();
					
					//new way to get Field isShow

					AfResourceDo afResourceDo = afResourceService.getConfigByTypesAndSecType("SPRING_FESTIVAL_ACTIVITY", "START_END_TIME");
					if (afResourceDo == null) {
						return H5CommonResponse.getNewInstance(false, "获取活动时间失败").toString();
					}
					
					String startTime = afResourceDo.getValue();
					String endTime = afResourceDo.getValue1();
					
					if (currentTime.before(dateFormat.parse(startTime))) {
						afCouponDouble12Vo.setIsShow("N");// 活动未开始
					}
					
					if (currentTime.after(dateFormat.parse(endTime))) {
						afCouponDouble12Vo.setIsShow("E");// 活动已经结束
					}
					
					String tenMinute = startTime.split(" ")[1];
					String currentHourMinute = DateUtil.convertDateToString(DateUtil.SHORT_MATCH_PATTERN,currentTime);
					
					if(currentHourMinute.compareTo(tenMinute) < 0 ){
						afCouponDouble12Vo.setIsShow("N");// 活动未开始
					}else{
						afCouponDouble12Vo.setIsShow("Y");// 在活动时间内
					}
					if (userId == null) {
						afCouponDouble12Vo.setIsGet("N");// 未领取

					} else {
						if (afUserCouponService.getUserCouponByUserIdAndCouponId(userId, afCouponDo.getRid()) != 0) {
							afCouponDouble12Vo.setIsGet("Y");// 已领取
						} else {
							afCouponDouble12Vo.setIsGet("N");// 未领取
						}

					}
					if (afCouponDo.getQuota() > afCouponDo.getQuotaAlready()) {
						afCouponDouble12Vo.setIshas("Y");// 优惠券还有
					} else {
						afCouponDouble12Vo.setIshas("N");// 优惠券已领完
					}
					couponVoList.add(afCouponDouble12Vo);
				}
			}

			logger.info(JSON.toJSONString(couponVoList));
			data.put("couponList", couponVoList);
			result = H5CommonResponse.getNewInstance(true, "获取优惠券列表成功", null, data).toString();
		
		} 
		catch (Exception e) {
			logger.error("/appH5DoubleEggs/initCoupons error = {}", e.getStackTrace());
			return H5CommonResponse.getNewInstance(false, "获取优惠券列表失败", null, "").toString();
		}
		return result;
	}
	
	
	

	@Override
	public String checkCommonParam(String reqData, HttpServletRequest request, boolean isForQQ) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RequestDataVo parseRequestData(String requestData, HttpServletRequest request) {
		try {
			RequestDataVo reqVo = new RequestDataVo();

			JSONObject jsonObj = JSON.parseObject(requestData);
			reqVo.setId(jsonObj.getString("id"));
			reqVo.setMethod(request.getRequestURI());
			reqVo.setSystem(jsonObj);

			return reqVo;
		} catch (Exception e) {
			throw new FanbeiException("参数格式错误" + e.getMessage(), FanbeiExceptionCode.REQUEST_PARAM_ERROR);
		}

	}

	@Override
	public BaseResponse doProcess(RequestDataVo requestDataVo, FanbeiContext context,
			HttpServletRequest httpServletRequest) {
		// TODO Auto-generated method stub
		return null;
	}

}
