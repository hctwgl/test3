package com.ald.fanbei.api.web.apph5.controller;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.enums.H5OpenNativeType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.AfCouponDouble12Vo;
import com.ald.fanbei.api.web.vo.AfDouble12GoodsTimeTypeVo;
import com.ald.fanbei.api.web.vo.AfDouble12GoodsVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Description: 有得卖三方 回收业务
 * @author weiqingeng
 * @date 2018年2月26日 下午4:14:31
 */
@RestController
@RequestMapping(value = "/fanbei/ydm")
public class AppH5RecycleController extends BaseController{

	@Autowired
	private AfCouponDouble12Service afCouponDouble12Service;
	@Autowired
	private BizCacheUtil bizCacheUtil;
	@Autowired
	private AfUserCouponService afUserCouponService;
	@Autowired
	private AfCouponService afCouponService;
	@Autowired
	private AfUserService afUserService;

	/**
	 * 获取优惠券 有得卖三方 推送过来的优惠券
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/getCoupon", method = RequestMethod.POST)
	public String getCoupon(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> data = new HashMap<String, Object>();
		FanbeiWebContext context = new FanbeiWebContext();
		String result = "";
		String key = "";
		try {
			context = doWebCheck(request, true);
			String userName = context.getUserName();
			Long couponId = NumberUtil.objToLong(request.getParameter("couponId"));
			logger.info("/activity/double12/getCoupon params: userName ={} , couponId = {}", userName, couponId);
			Long userId = convertUserNameToUserId(userName);
			key = Constants.CACHKEY_GET_COUPON_LOCK + ":" + userId + ":" + couponId;
			boolean isNotLock = bizCacheUtil.getLockTryTimes(key, "1", 1000);
			if (isNotLock) {
				AfCouponDouble12Do afCouponDouble12Do = afCouponDouble12Service.getCouponByCouponId(couponId);
				Long count = afCouponDouble12Do.getCount();
				if(count > 0){
					//优惠券未抢完
					if(afUserCouponService.getUserCouponByUserIdAndCouponId(userId,couponId) == 0){
						//用户未领取
						afCouponDouble12Service.updateCountById(afCouponDouble12Do);
						AfCouponDo afCoupon = afCouponService.getCoupon(couponId);
						AfUserCouponDo afUserCouponDo = new AfUserCouponDo();
						afUserCouponDo.setUserId(userId);
						afUserCouponDo.setCouponId(couponId);
						afUserCouponDo.setGmtStart(afCoupon.getGmtStart());
						afUserCouponDo.setGmtEnd(afCoupon.getGmtEnd());
						afUserCouponDo.setStatus("NOUSE");//未使用
						afUserCouponDo.setSourceType("SPECIAL");//专场
						afUserCouponService.addUserCoupon(afUserCouponDo);
						data.put("result", "Y");
						result = H5CommonResponse.getNewInstance(true, "领取优惠券成功", null, data).toString();
					}else {
						//用户已领取
						data.put("result", "N");
						result = H5CommonResponse.getNewInstance(true, "用户已领过该优惠券", null, data).toString();
					}
				}else{
					//优惠券已抢完
					data.put("result", "O");
					result = H5CommonResponse.getNewInstance(true, "优惠券已领完", null, data).toString();
				}
			}
		} catch (FanbeiException e) {
			if (e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_INVALID_SIGN_ERROR)
					|| e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_PARAM_TOKEN_ERROR)) {
			logger.error("/fanbei/ydm/getCoupon" + context + "login error ");
			result = H5CommonResponse.getNewInstance(false, "没有登录", null, data).toString();
			}
		}catch (Exception e) {
			//e.printStackTrace();
			logger.error("/fanbei/ydm/getCoupon error = {}", e.getStackTrace());
			return H5CommonResponse.getNewInstance(false, "领取优惠券失败", null, "").toString();
		} finally {
			bizCacheUtil.delCache(key);
		}
		return result;
	}


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
