package com.ald.fanbei.api.web.apph5.controller;

import java.util.ArrayList;
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
import com.ald.fanbei.api.biz.service.AfUserCouponService;
import com.ald.fanbei.api.biz.service.AfUserCouponTigerMachineService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.enums.H5OpenNativeType;
import com.ald.fanbei.api.common.enums.SpringFestivalActivityEnum;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfCouponCategoryDo;
import com.ald.fanbei.api.dal.domain.AfCouponDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.AfCouponDouble12Vo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * @Title: AppH5SFController.java
 * @Package com.ald.fanbei.api.web.apph5.controller
 * @Description: TODO(用一句话描述该文件做什么)
 * @Copyright (c) 浙江阿拉丁电子商务股份有限公司 All Rights Reserved.
 * @author qiao
 * @date 2018年1月5日 下午4:54:57
 * @version V1.0
 */
@RestController
@RequestMapping(value = "/H5SF", produces = "application/json;charset=UTF-8")
public class AppH5SFController extends BaseController {
	@Resource
	AfCouponCategoryService afCouponCategoryService;
	@Resource
	AfCouponService afCouponService;
	@Resource
	AfUserCouponService afUserCouponService;
	@Resource
	AfResourceService afResourceService;
	@Resource
	AfGoodsDoubleEggsService afGoodsDoubleEggsService;
	@Resource
	AfGoodsDoubleEggsUserService afGoodsDoubleEggsUserService;
	@Resource
	AfUserService afUserService;
	@Resource
	AfUserCouponTigerMachineService afUserCouponTigerMachineService;

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
	* @Title: initTigerMachine
	* @author qiao
	* @date 2018年1月5日 下午5:43:05
	* @Description: 老虎机页面初始化
	* @param request
	* @param response
	* @return    
	* @return String   
	* @throws
	 */
	@RequestMapping(value = "/initTigerMachine", method = RequestMethod.POST)
	public String initTigerMachine(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> data = new HashMap<String, Object>();
		FanbeiWebContext context = new FanbeiWebContext();
		String result = "";

		try {
			context = doWebCheck(request, true);
			
			Long userId = convertUserNameToUserId(context.getUserName());

			Long activityId = 10L;
			
			//get conpons
			String tag = SpringFestivalActivityEnum.findTagByActivityId(activityId);

			AfCouponCategoryDo couponCategory = afCouponCategoryService.getCouponCategoryByTag(tag);
			String coupons = couponCategory.getCoupons();
			if (StringUtil.isBlank(coupons)) {
				return H5CommonResponse.getNewInstance(false, "次活动奖品已经领完").toString();
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

					if (afCouponDo.getQuota() > afCouponDo.getQuotaAlready()) {
						afCouponDouble12Vo.setIshas("Y");// 优惠券还有
					} else {
						afCouponDouble12Vo.setIshas("N");// 优惠券已领完
					}
					couponVoList.add(afCouponDouble12Vo);
				}
			}
			
			//get total userTimes
			int times = afUserCouponTigerMachineService.getTotalTimesByUserId(userId);
			data.put("times", times);
			logger.info(JSON.toJSONString(couponVoList));
			data.put("couponList", couponVoList);
			result = H5CommonResponse.getNewInstance(true, "获取优惠券列表成功", null, data).toString();

		} catch (FanbeiException e) {
			if (e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_INVALID_SIGN_ERROR)) {
				data = new HashMap<>();
				String loginUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative
						+ H5OpenNativeType.AppLogin.getCode();
				data.put("loginUrl", loginUrl);
				return result = H5CommonResponse.getNewInstance(false, "没有登录", "", data).toString();
				
			}
		} 
		
		catch (Exception e) {
			logger.error("/appH5DoubleEggs/initCoupons error = {}", e.getStackTrace());
			return H5CommonResponse.getNewInstance(false, "获取优惠券列表失败", null, "").toString();
		}
		return result;
	}

	/**
	 * 
	* @Title: pickUpTigerMachineCoupon
	* @author qiao
	* @date 2018年1月5日 下午5:47:05
	* @Description: 老虎机领券
	* @param request
	* @param response
	* @return    
	* @return String   
	* @throws
	 */
	@RequestMapping(value = "/pickUpTigerMachineCoupon", method = RequestMethod.POST)
	public String pickUpTigerMachineCoupon(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> data = new HashMap<String, Object>();
		FanbeiWebContext context = new FanbeiWebContext();
		String result = "";

		try {
			context = doWebCheck(request, true);
			Long userId = convertUserNameToUserId(context.getUserName());
			Long activityId = 10L;
			
			//get total userTimes
			int times = afUserCouponTigerMachineService.getTotalTimesByUserId(userId);
			if (times < 1) {
				return H5CommonResponse.getNewInstance(false, "您暂无抽奖机会，快去购物获取抽奖机会吧").toString();
			}
			//get conpons
			String tag = SpringFestivalActivityEnum.findTagByActivityId(activityId);

			AfCouponCategoryDo couponCategory = afCouponCategoryService.getCouponCategoryByTag(tag);
			String coupons = couponCategory.getCoupons();
			if (StringUtil.isBlank(coupons)) {
				return H5CommonResponse.getNewInstance(false, "次活动奖品已经领完").toString();
			}
			JSONArray couponsArray = (JSONArray) JSONArray.parse(coupons);

			List<Long> avalibleCouponIdList = new ArrayList<>();

			for (int i = 0; i < couponsArray.size(); i++) {
				String couponId = (String) couponsArray.getString(i);
				AfCouponDo afCouponDo = afCouponService.getCouponById(Long.parseLong(couponId));
				if (afCouponDo != null) {

					if (afCouponDo.getQuota() > afCouponDo.getQuotaAlready()) {
						avalibleCouponIdList.add(afCouponDo.getRid());
					} 
				}
			}
			
			//random get one coupon Id
			int hitIndex = (int) (Math.random() * (avalibleCouponIdList.size() - 0) + 0);
			
			Long couponId = avalibleCouponIdList.get(hitIndex);
			data.put("couponId", couponId);
			
			//decrease the times for once.
			//(fist decrease the daily time one then the shopping one)+grant coupons
			
			boolean isSuccess = afUserCouponTigerMachineService.grandCoupon(couponId,userId);
			if (!isSuccess) {
				return H5CommonResponse.getNewInstance(false, "老虎机抽取礼物失败，可以重新来一次哦~").toString();
			}
			
			
			data.put("times", times-1);
			result = H5CommonResponse.getNewInstance(true, "获取优惠券列表成功", null, data).toString();

		} catch (FanbeiException e) {
			if (e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_INVALID_SIGN_ERROR)) {
				data = new HashMap<>();
				String loginUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative
						+ H5OpenNativeType.AppLogin.getCode();
				data.put("loginUrl", loginUrl);
				return result = H5CommonResponse.getNewInstance(false, "没有登录", "", data).toString();
				
			}
		} 
		
		catch (Exception e) {
			logger.error("/H5SF/pickUpTigerMachineCoupon error = {}", e.getStackTrace());
			return H5CommonResponse.getNewInstance(false, "老虎机抽取礼物失败，可以重新来一次哦~").toString();
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
