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

import org.apache.commons.lang.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ald.fanbei.api.biz.service.AfCouponCategoryService;
import com.ald.fanbei.api.biz.service.AfCouponDouble12Service;
import com.ald.fanbei.api.biz.service.AfCouponService;
import com.ald.fanbei.api.biz.service.AfGoodsDouble12Service;
import com.ald.fanbei.api.biz.service.AfGoodsService;
import com.ald.fanbei.api.biz.service.AfResourceService;
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
import com.ald.fanbei.api.dal.domain.AfCouponCategoryDo;
import com.ald.fanbei.api.dal.domain.AfCouponDo;
import com.ald.fanbei.api.dal.domain.AfCouponDouble12Do;
import com.ald.fanbei.api.dal.domain.AfGoodsDo;
import com.ald.fanbei.api.dal.domain.AfGoodsDouble12Do;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
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
	@Resource
	AfCouponCategoryService afCouponCategoryService;
	@Resource
	AfResourceService afResourceService;
	
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
			
			// 获取活动优惠券组信息
    		String groupId = ObjectUtils.toString(request.getParameter("groupId"), null).toString();
    		if(groupId == null) {
    			return H5CommonResponse.getNewInstance(false, "groupId can't be null or empty.", null, "").toString();
    		}
			
    		AfCouponCategoryDo couponCategory = afCouponCategoryService.getCouponCategoryById(groupId);
    		String coupons = couponCategory.getCoupons();
    		JSONArray couponsArray = (JSONArray) JSONArray.parse(coupons);
    		
    		List<AfCouponDouble12Vo> couponVoList = new ArrayList<AfCouponDouble12Vo>();
    		
    		for (int i = 0; i < couponsArray.size(); i++) {
    			String couponId = (String)couponsArray.getString(i);
    			AfCouponDo afCouponDo = afCouponService.getCouponById(Long.parseLong(couponId));
    			if(afCouponDo!=null){
    				AfCouponDouble12Vo afCouponDouble12Vo = new AfCouponDouble12Vo();
    				afCouponDouble12Vo.setId(afCouponDo.getRid());
    				afCouponDouble12Vo.setName(afCouponDo.getName());
    				afCouponDouble12Vo.setThreshold(afCouponDo.getUseRule());
    				afCouponDouble12Vo.setAmount(afCouponDo.getAmount());
    				afCouponDouble12Vo.setLimitAmount(afCouponDo.getLimitAmount());
    				afCouponDouble12Vo.setIsGet("N");
    				
    				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					// 当前时间
					Date currentTime = new Date();
										
					AfResourceDo afResourceDo = afResourceService.getSingleResourceBytype("DOUBLE12_COUPON_TIME");
					if(afResourceDo==null){
						return H5CommonResponse.getNewInstance(false, "获取活动时间失败").toString();
					}
					String[] times = afResourceDo.getValue().split(",");
					
					if(currentTime.before(dateFormat.parse(times[0]))){
						//2017-12-5 10:00号之前
						afCouponDouble12Vo.setIsShow("N");//活动未开始
					}
					
					if(afCouponDouble12Vo.getIsShow()==null){
						for (int j = 0; j < times.length-1; j=j+2) {
							if(afCouponDouble12Vo.getIsShow()==null){
								if(currentTime.after(dateFormat.parse(times[times.length-1]))){
									afCouponDouble12Vo.setIsShow("E");//活动已结束
								}
							}
							if(afCouponDouble12Vo.getIsShow()==null){
								if(currentTime.after(dateFormat.parse(times[j]))&&currentTime.before(dateFormat.parse(times[j+1]))){
									afCouponDouble12Vo.setIsShow("Y");//在活动时间内
								}
							}
							if(afCouponDouble12Vo.getIsShow()==null){
								if(currentTime.after(dateFormat.parse(times[j+1]))&&currentTime.before(dateFormat.parse(times[j+2]))){
									afCouponDouble12Vo.setIsShow("N");//活动未开始
								}
							}
						}
					}
    				
    				if(afCouponDo.getQuota() > afCouponDo.getQuotaAlready()){
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
