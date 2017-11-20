package com.ald.fanbei.api.web.apph5.controller;

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

import com.ald.fanbei.api.biz.service.AfCouponDouble12Service;
import com.ald.fanbei.api.biz.service.AfCouponService;
import com.ald.fanbei.api.biz.service.AfGoodsDouble12Service;
import com.ald.fanbei.api.biz.service.AfGoodsService;
import com.ald.fanbei.api.biz.service.AfUserCouponService;
import com.ald.fanbei.api.biz.service.AfUserService;
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
import com.ald.fanbei.api.dal.domain.AfGoodsDouble12Do;
import com.ald.fanbei.api.dal.domain.AfUserCouponDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.afCouponDouble12Vo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**  
 * @Title: AppH5Double12ActivityController.java
 * @Package com.ald.fanbei.api.web.apph5.controller
 * @Description: 双十二
 * @Copyright (c) 浙江阿拉丁电子商务股份有限公司 All Rights Reserved.
 * @author yanghailong
 * @date 2017年11月17日 下午1:14:31
 * @version V1.0  
 */
@RestController
@RequestMapping(value = "/activity/double12", produces = "application/json;charset=UTF-8")
public class AppH5Double12ActivityController extends BaseController{

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
			List<afCouponDouble12Vo> couponVoList = new ArrayList<afCouponDouble12Vo>();
			
			if(couponList!=null){
				for (AfCouponDouble12Do afCouponDouble12Do : couponList) {
					AfCouponDo coupon = afCouponService.getCoupon(afCouponDouble12Do.getCouponid());
					afCouponDouble12Vo afCouponDouble12Vo = new afCouponDouble12Vo();
					afCouponDouble12Vo.setId(coupon.getRid());
					afCouponDouble12Vo.setName(coupon.getName());
					afCouponDouble12Vo.setThreshold(coupon.getUseRule());
					if(new Date().before(afCouponDouble12Do.getStarttime())||new Date().after(afCouponDouble12Do.getEndtime())){	
						//不在活动时间内
						afCouponDouble12Vo.setIsShow("N");
						if(afCouponDouble12Do.getCount()!=afCouponDouble12Do.getTotal()){
							//重置count
							afCouponDouble12Service.updateReCountById(afCouponDouble12Do);
						};
					}else{
						afCouponDouble12Vo.setIsShow("Y");
					}
					
					couponVoList.add(afCouponDouble12Vo);
					
				}
			}
			
			logger.info(JSON.toJSONString(couponVoList));
			data.put("couponList", couponVoList);
			result = H5CommonResponse.getNewInstance(true, "获取优惠券列表成功", null, data).toString();
		
		} catch (Exception e) {
			logger.error("/activity/double12/couponHomePage error = {}", e.getStackTrace());
			return H5CommonResponse.getNewInstance(false, "获取优惠券列表失败", null, "").toString();
		}
		return result;
	}
	
	/**
	 * @Title: goodsHomePage
	 * @Description:  秒杀商品
	 * @return  String  
	 * @author yanghailong
	 * @data  2017年11月17日
	 */
	@RequestMapping(value = "/goodsHomePage", method = RequestMethod.POST)
	public String goodsHomePage(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> data = new HashMap<String, Object>();
		String result = "";
		try {
			doWebCheck(request, false);
			
			/*List<AfGoodsDouble12Do> goodsList = AfGoodsDouble12Service.getAfGoodsDouble12List();
			
			if(goodsList!=null){
				for (AfGoodsDouble12Do afGoodsDouble12Do : goodsList) {
					
				}
			}*/
			
			
		} catch (Exception e) {
			logger.error("/activity/double12/goodsHomePage error = {}", e.getStackTrace());
			return H5CommonResponse.getNewInstance(false, "获取秒杀商品列表失败", null, "").toString();
		}
		return result;
	}
	
	
	/**
	 * @Title: getCoupon
	 * @Description:  获取优惠券（验证登录）
	 * @return  String  
	 * @author yanghailong
	 * @data  2017年11月17日
	 */
	@RequestMapping(value = "/getCoupon", method = RequestMethod.POST)
	public String getCoupon(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> data = new HashMap<String, Object>();
		FanbeiWebContext context = new FanbeiWebContext();
		String result = "";
		try {
			context = doWebCheck(request, true);
			
			String userName = context.getUserName();
			Long couponId = NumberUtil.objToLong(request.getParameter("couponId"));
			logger.info("/activity/double12/getCoupon params: userName ={} , couponId = {}", userName, couponId);
			Long userId = convertUserNameToUserId(userName);
			
			AfCouponDouble12Do afCouponDouble12Do = afCouponDouble12Service.getCouponByCouponId(couponId);
			
			if(new Date().before(afCouponDouble12Do.getStarttime())){	
				//活动暂未开始
				return H5CommonResponse.getNewInstance(false, "活动暂未开始", null, "").toString();
			}
			if(new Date().after(afCouponDouble12Do.getEndtime())){
				//活动已结束
				return H5CommonResponse.getNewInstance(false, "活动已结束", null, "").toString();
			}
			
			Long count = afCouponDouble12Do.getCount();
			if(count > 0){
				//优惠券未抢完
				if(afUserCouponService.getUserCouponByUserIdAndCouponId(userId,couponId) == 0){
					//用户未领取
					
					afCouponDouble12Service.updateCountById(afCouponDouble12Do);
					
					AfUserCouponDo afUserCouponDo = new AfUserCouponDo();
					afUserCouponDo.setUserId(userId);
					afUserCouponDo.setCouponId(couponId);
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
		
		} catch (FanbeiException e) {
			if (e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_INVALID_SIGN_ERROR)
					|| e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_PARAM_TOKEN_ERROR)) {
				String loginUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative
						+ H5OpenNativeType.AppLogin.getCode();
				data.put("loginUrl", loginUrl);
			logger.error("/activity/double12/getCoupon" + context + "login error ");
			result = H5CommonResponse.getNewInstance(false, "没有登录", null, data).toString();
			}
		}catch (Exception e) {
			//e.printStackTrace();
			logger.error("/activity/double12/getCoupon error = {}", e.getStackTrace());
			return H5CommonResponse.getNewInstance(false, "领取优惠券失败", null, "").toString();
		}
		return result;
	}
	
	
	/**
	 * @Title: isbuy
	 * @Description:  秒杀商品校验（验证登录）
	 * @return  String  
	 * @author yanghailong
	 * @data  2017年11月17日
	 */
	@RequestMapping(value = "/isbuy", method = RequestMethod.POST)
	public String isbuy(HttpServletRequest request, HttpServletResponse response) {
		String result = "";
		
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
