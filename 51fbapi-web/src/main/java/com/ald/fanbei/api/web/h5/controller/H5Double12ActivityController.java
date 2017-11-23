package com.ald.fanbei.api.web.h5.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

import com.ald.fanbei.api.biz.service.AfCouponDouble12Service;
import com.ald.fanbei.api.biz.service.AfCouponService;
import com.ald.fanbei.api.biz.service.AfGoodsDouble12Service;
import com.ald.fanbei.api.biz.service.AfGoodsService;
import com.ald.fanbei.api.biz.service.AfUserCouponService;
import com.ald.fanbei.api.biz.service.AfUserService;
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
import com.ald.fanbei.api.dal.domain.AfCouponDo;
import com.ald.fanbei.api.dal.domain.AfCouponDouble12Do;
import com.ald.fanbei.api.dal.domain.AfGoodsDo;
import com.ald.fanbei.api.dal.domain.AfGoodsDouble12Do;
import com.ald.fanbei.api.dal.domain.AfUserCouponDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.AfDouble12GoodsVo;
import com.ald.fanbei.api.web.vo.AfCouponDouble12Vo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**  
 * @Title: H5Double12ActivityController.java
 * @Package com.ald.fanbei.api.web.h5.controller
 * @Description: 双十二 H5
 * @Copyright (c) 浙江阿拉丁电子商务股份有限公司 All Rights Reserved.
 * @author yanghailong
 * @date 2017年11月17日 下午1:14:31
 * @version V1.0  
 */
@RestController
@RequestMapping(value = "/activityH5/double12", produces = "application/json;charset=UTF-8")
public class H5Double12ActivityController extends BaseController{

	@Resource
	AfCouponDouble12Service afCouponDouble12Service;
	@Resource
	AfGoodsDouble12Service AfGoodsDouble12Service;
	@Resource
	AfCouponService afCouponService;
	@Resource
	AfUserCouponService afUserCouponService;
	@Resource
	AfUserService afUserService;
	@Resource
	AfGoodsService afGoodsService;
	@Resource
	BizCacheUtil bizCacheUtil;
	
	String opennative = "/fanbei-web/opennative?name=";
	
	/**
	 * @Title: couponHomePage
	 * @Description:  优惠券
	 * @return  String  
	 * @author yanghailong
	 * @data  2017年11月17日
	 */
	@RequestMapping(value = "/couponHomePage", method = RequestMethod.POST)
	public String couponHomePage(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> data = new HashMap<String, Object>();
		String result = "";
		
		try {
			doWebCheck(request, false);
			
			List<AfCouponDouble12Do> couponList = afCouponDouble12Service.getCouponList();
			List<AfCouponDouble12Vo> couponVoList = new ArrayList<AfCouponDouble12Vo>();
			
			if(couponList!=null){
				for (AfCouponDouble12Do afCouponDouble12Do : couponList) {
					AfCouponDo coupon = afCouponService.getCoupon(afCouponDouble12Do.getCouponid());
					AfCouponDouble12Vo afCouponDouble12Vo = new AfCouponDouble12Vo();
					afCouponDouble12Vo.setId(coupon.getRid());
					afCouponDouble12Vo.setName(coupon.getName());
					afCouponDouble12Vo.setThreshold(coupon.getUseRule());
					afCouponDouble12Vo.setAmount(coupon.getAmount());
					afCouponDouble12Vo.setIsGet("N");
					if(new Date().before(afCouponDouble12Do.getStarttime())||new Date().after(afCouponDouble12Do.getEndtime())){	
						afCouponDouble12Vo.setIsShow("N");//不在活动时间内
					}else{
						afCouponDouble12Vo.setIsShow("Y");//在活动时间内
					}
					if(afCouponDouble12Do.getCount() > 0){
						afCouponDouble12Vo.setIshas("Y");//优惠券还有
					}else {
						afCouponDouble12Vo.setIshas("N");//优惠券已领完
					}
					
					couponVoList.add(afCouponDouble12Vo);
					
				}
			}
			
			logger.info(JSON.toJSONString(couponVoList));
			data.put("couponList", couponVoList);
			result = H5CommonResponse.getNewInstance(true, "获取优惠券列表成功", null, data).toString();
		
		} catch (Exception e) {
			logger.error("/activityH5/double12/couponHomePage error = {}", e.getStackTrace());
			return H5CommonResponse.getNewInstance(false, "获取优惠券列表失败", null, "").toString();
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
