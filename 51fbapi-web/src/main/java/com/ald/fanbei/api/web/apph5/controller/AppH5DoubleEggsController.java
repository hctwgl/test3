package com.ald.fanbei.api.web.apph5.controller;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.text.SimpleDateFormat;

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
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

import org.apache.commons.lang.ObjectUtils;
import org.eclipse.jetty.server.UserIdentity;

import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.util.CollectionUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfCouponCategoryDo;
import com.ald.fanbei.api.dal.domain.AfCouponDo;
import com.ald.fanbei.api.dal.domain.AfGoodsDoubleEggsDo;
import com.ald.fanbei.api.dal.domain.AfGoodsDoubleEggsUserDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.web.vo.AfCouponDouble12Vo;
import com.alibaba.fastjson.JSONArray;

/**
 * @Title: AppH5DoubleEggsController.java
 * @Package com.ald.fanbei.api.web.apph5.controller
 * @Description: TODO(用一句话描述该文件做什么)
 * @Copyright (c) 浙江阿拉丁电子商务股份有限公司 All Rights Reserved.
 * @author qiao
 * @date 2017年12月7日 下午1:26:54
 * @version V1.0
 */
@RestController
@RequestMapping(value = "/appH5DoubleEggs",method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
public class AppH5DoubleEggsController extends BaseController {
	@Resource
	AfCouponCategoryService afCouponCategoryService;
	@Resource
	AfCouponService afCouponService;
	@Resource
	AfResourceService afResourceService;
	@Resource
	AfUserCouponService afUserCouponService;
	@Resource
	AfUserService afUserService;
	@Resource
	AfGoodsDoubleEggsService afGoodsDoubleEggsService;
	
	@Resource
	AfGoodsDoubleEggsUserService afGoodsDoubleEggsUserService;
	/**
	 * 
	* @Title: initHomePage
	* @author qiwei
	* @date 2017年12月7日 下午1:58:31
	* @Description: 主页面的两张图片和url，此接口H5和AppH5通用，无需登录
	* @param request
	* @param response
	* @return    
	* @return String   
	* @throws
	 */
	@RequestMapping(value = "/initHomePage")
	public String initHomePage(HttpServletRequest request,HttpServletResponse response){
		String result = "";
		try {
			java.util.Map<String, Object> data = new HashMap<>();
			//TODO:get info from afResource;
			
			
			result = H5CommonResponse.getNewInstance(true, "初始化成功", "", data).toString();
		} catch (Exception exception) {
			result = H5CommonResponse.getNewInstance(false, "初始化失败", "", exception.getMessage()).toString();
			logger.error("初始化数据失败  e = {} , resultStr = {}", exception, result);
			doMaidianLog(request, H5CommonResponse.getNewInstance(false, "fail"),result);
		}
		return result;
	}
	
	/**
	 * 
	* @Title: initOnsaleGoods
	* @author qiwei
	* @date 2017年12月7日 下午2:04:33
	* @Description: 初始化特卖商品
	* @param request
	* @param response
	* @return    
	* @return String   
	* @throws
	 */
	@RequestMapping(value = "/initOnsaleGoods")
	public String initOnsaleGoods(HttpServletRequest request,HttpServletResponse response){
		String result = "";
		try {
			java.util.Map<String, Object> data = new HashMap<>();
			//TODO:get info from afResource;
			
			result = H5CommonResponse.getNewInstance(true, "特卖商品初始化成功", "", data).toString();
		} catch (Exception exception) {
			result = H5CommonResponse.getNewInstance(false, "特卖商品初始化失败", "", exception.getMessage()).toString();
			logger.error("特卖商品初始化数据失败  e = {} , resultStr = {}", exception, result);
			doMaidianLog(request, H5CommonResponse.getNewInstance(false, "fail"),result);
		}
		return result;
	}
	
	/**
	 * 
	* @Title: getOnSaleGoods
	* @author qiwei
	* @date 2017年12月7日 下午2:04:40
	* @Description: 获得特卖商品（点击tap进行不同的跳转）
	* @param request
	* @param response
	* @return    
	* @return String   
	* @throws
	 */
	@RequestMapping(value = "/getOnSaleGoods")
	public String getOnSaleGoods(HttpServletRequest request,HttpServletResponse response){
		String result = "";
		try {
			java.util.Map<String, Object> data = new HashMap<>();
			//TODO:get info from afResource;
			
			
			result = H5CommonResponse.getNewInstance(true, "特卖商品初始化成功", "", data).toString();
			} catch (Exception exception) {
				result = H5CommonResponse.getNewInstance(false, "特卖商品初始化失败", "", exception.getMessage()).toString();
				logger.error("特卖商品初始化数据失败  e = {} , resultStr = {}", exception, result);
				doMaidianLog(request, H5CommonResponse.getNewInstance(false, "fail"),result);
			}
			return result;
		}

	@Override
	public String checkCommonParam(String reqData, HttpServletRequest request, boolean isForQQ) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	/**
	 * @Title: initCoupons
	 * @Description:  优惠券
	 * @return  String  
	 * @author chenqiwei
	 * @data  2017年12月7日
	 */
	@RequestMapping(value = "/initCoupons", method = RequestMethod.POST)
	public String couponHomePage(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> data = new HashMap<String, Object>();
		FanbeiWebContext context = new FanbeiWebContext();
		String result = "";
		
		try {
			context = doWebCheck(request, false);
			Long userId = convertUserNameToUserId(context.getUserName());
			//未登录初始化数据
			String tag = "_DOUBLE_EGGS_";
			AfCouponCategoryDo couponCategory = afCouponCategoryService.getCouponCategoryByTag(tag);
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
						
						SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						// 当前时间
						Date currentTime = new Date();
											
						AfResourceDo afResourceDo = afResourceService.getSingleResourceBytype("DOUBLE_EGGS_COUPON_TIME");
						if(afResourceDo==null){
							return H5CommonResponse.getNewInstance(false, "获取活动时间失败").toString();
						}
						String[] times = afResourceDo.getValue2().split(",");
						
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
			if (userId == null) {
				afCouponDouble12Vo.setIsGet("N");//未领取
				
			}else{
				if(afUserCouponService.getUserCouponByUserIdAndCouponId(userId,afCouponDo.getRid()) != 0){
					afCouponDouble12Vo.setIsGet("Y");//已领取
				}else{
					afCouponDouble12Vo.setIsGet("N");//未领取
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
		
		} 
		catch (Exception e) {
			logger.error("/activity/double12/couponHomePage error = {}", e.getStackTrace());
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
	
	/**
	 * 
	* @Title: getSecondKillGoodsList
	* @author qiao
	* @date 2017年12月7日 下午2:27:13
	* @Description: 获得秒杀商品
	* @param request
	* @param response
	* @return    
	* @return String   
	* @throws
	 */
	@RequestMapping(value = "/getSecondKillGoodsList")
	public String getSecondKillGoodsList(HttpServletRequest request,HttpServletResponse response){
		String result = "";
		FanbeiWebContext context = new FanbeiWebContext();
		try {
			context = doWebCheck(request, false);
			
			List<AfGoodsDoubleEggsDo> goodsList = afGoodsDoubleEggsService.getAvalibleGoodsList();
			if (CollectionUtil.isNotEmpty(goodsList)) {
				java.util.Map<String, Object> data = new HashMap<>();
				//change to the view model

				
				// if this user has already login in then add status to goods.
				String userName = context.getUserName();
				long userId = 0L;
				if (StringUtil.isNotBlank(userName) && convertUserNameToUserId(userName) != null) {
					
					//change status according to different users
					
				} 
				
				
				
				result = H5CommonResponse.getNewInstance(true, "初始化成功", "", data).toString();
			}
			result = H5CommonResponse.getNewInstance(false, "初始化失败").toString();
			
			
		} catch (Exception exception) {
			result = H5CommonResponse.getNewInstance(false, "初始化失败", "", exception.getMessage()).toString();
			logger.error("初始化数据失败  e = {} , resultStr = {}", exception, result);
			doMaidianLog(request, H5CommonResponse.getNewInstance(false, "fail"),result);
		}
		return result;
	}
	
	/**
	 * 
	* @Title: subscribe
	* @author qiao
	* @date 2017年12月7日 下午2:26:48
	* @Description: 预约
	* @param request
	* @param response
	* @return    
	* @return String   
	* @throws
	 */
	@RequestMapping(value = "/subscribe")
	public String subscribe(HttpServletRequest request, HttpServletResponse response) {
		String result = "";
		FanbeiWebContext context = new FanbeiWebContext();
		try {
			context = doWebCheck(request, true);
			if (context != null) {
				Long userId = convertUserNameToUserId(context.getUserName());
				java.util.Map<String, Object> data = new HashMap<>();

				Long goodsId = NumberUtil.objToLong(request.getParameter("goodsId"));
				AfGoodsDoubleEggsDo goodsDo = afGoodsDoubleEggsService.getByGoodsId(goodsId);

				if (goodsDo != null) {
					
					// TODO:get 10 minutes from afResource;
					String time = "10";
					int preTime = Integer.parseInt(time);
					Date now = new Date();
					
					// if now + preTime >= goods start time then throw error"time分钟内无需预约"
					if(DateUtil.addMins(now, preTime).after(goodsDo.getStartTime())){
						result = H5CommonResponse.getNewInstance(false,"抱歉" + preTime + "分钟内无法预约！").toString();
						return result;
					}
					
					long doubleGoodsId = goodsDo.getRid();
					// to check if this user already subscribed this goods if yes then "已经预约不能重复预约"else"预约成功"
					if (afGoodsDoubleEggsUserService.isExist(doubleGoodsId,userId)) {
						result = H5CommonResponse.getNewInstance(false, "已经预约过不能重复预约！").toString();
						return result;
					}
					
					AfGoodsDoubleEggsUserDo userDo = new AfGoodsDoubleEggsUserDo();
					userDo.setDoubleEggsId(doubleGoodsId);
					userDo.setGmtCreate(now);
					userDo.setGmtModified(now);
					userDo.setIsOrdered(1);
					userDo.setUserId(userId);
					
					afGoodsDoubleEggsUserService.saveRecord(userDo);

					result = H5CommonResponse.getNewInstance(true, "预约成功", "", data).toString();
				}
			}
			result = H5CommonResponse.getNewInstance(false, "预约失败").toString();

		} catch (Exception exception) {
			result = H5CommonResponse.getNewInstance(false, "预约化失败", "", exception.getMessage()).toString();
			logger.error("预约失败 context = {},  e = {} ", context, exception);
			doMaidianLog(request, H5CommonResponse.getNewInstance(false, "fail"), result);
		}
		return result;
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
