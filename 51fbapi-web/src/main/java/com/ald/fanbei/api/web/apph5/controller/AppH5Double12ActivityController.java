package com.ald.fanbei.api.web.apph5.controller;

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
import com.ald.fanbei.api.common.util.DateUtil;
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
import com.ald.fanbei.api.web.vo.AfDouble12GoodsTimeTypeVo;
import com.ald.fanbei.api.web.vo.AfDouble12GoodsVo;
import com.ald.fanbei.api.web.vo.AfCouponDouble12Vo;
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
	@Resource
	BizCacheUtil bizCacheUtil;
	@Resource
	AfResourceService afResourceService;
	@Resource
	AfCouponCategoryService afCouponCategoryService;
	
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
		FanbeiWebContext context = new FanbeiWebContext();
		String result = "";
		
		try {
			context = doWebCheck(request, false);
			
//			String userName = context.getUserName();
//			logger.info("/activity/double12/couponHomePage params: userName ={}", userName);
//			Long userId = convertUserNameToUserId(userName);
			
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
					afCouponDouble12Vo.setIsGet("N");//未领取
					
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
    				
					/*if(afUserCouponService.getUserCouponByUserIdAndCouponId(userId,afCouponDo.getRid()) != 0){
						afCouponDouble12Vo.setIsGet("Y");//已领取
					}else{
						afCouponDouble12Vo.setIsGet("N");//未领取
					}*/
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
		Map<String, AfDouble12GoodsTimeTypeVo> goodsMap = new HashMap<String, AfDouble12GoodsTimeTypeVo>();
		String result = "";
		try {
			doWebCheck(request, false);
			
			List<AfGoodsDouble12Do> goodsList = AfGoodsDouble12Service.getAfGoodsDouble12List();
			
			if(goodsList!=null){
				for (AfGoodsDouble12Do afGoodsDouble12Do : goodsList) {
					//将秒杀开始时间具体日作为map的key
					Calendar c =Calendar.getInstance();
					c.setTime(afGoodsDouble12Do.getStartTime());
					String key = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
					//根据秒杀开始的时间，封装秒杀商品List到map
					switch (key) {
					case "5":
						getGoodsMap(goodsMap, afGoodsDouble12Do, key);
						break;
					case "6":
						getGoodsMap(goodsMap, afGoodsDouble12Do, key);
						break;
					case "7":
						getGoodsMap(goodsMap, afGoodsDouble12Do, key);
						break;
					case "8":
						getGoodsMap(goodsMap, afGoodsDouble12Do, key);
						break;
					case "9":
						getGoodsMap(goodsMap, afGoodsDouble12Do, key);
						break;
					case "10":
						getGoodsMap(goodsMap, afGoodsDouble12Do, key);
						break;
					case "11":
						getGoodsMap(goodsMap, afGoodsDouble12Do, key);
						break;
					case "12":
						getGoodsMap(goodsMap, afGoodsDouble12Do, key);
						break;
					}
				}
			}
			logger.info(JSON.toJSONString(goodsMap));
			if(goodsMap.isEmpty()){
				return H5CommonResponse.getNewInstance(false, "获取秒杀商品列表为空", null, "").toString();
			}
			data.put("goodsMap", goodsMap);
			result = H5CommonResponse.getNewInstance(true, "获取秒杀商品列表成功", null, data).toString();
			
		} catch (Exception e) {
			//e.printStackTrace();
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
		} finally {
			bizCacheUtil.delCache(key);
		}
		return result;
	}
	
	
	/**
	 * @Title: startTime
	 * @Description:  获取红包雨开始时间
	 * @return  String  
	 * @author yanghailong
	 * @data  2017年11月17日
	 */
	@RequestMapping(value = "/startTime", method = RequestMethod.POST)
	public String startTime(HttpServletRequest request, HttpServletResponse response) {
		String result = "";
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			// 当前时间
			Date currentTime = new Date();
			
//			String testTime = "2017-12-13 10:00:00";
//			Date currentTime = dateFormat.parse(testTime);
			
			// 开始时间
			Date startTime = null;
			
			AfResourceDo afResourceDo = afResourceService.getSingleResourceBytype("DOUBLE12");
			if(afResourceDo==null){
				return H5CommonResponse.getNewInstance(false, "获取活动时间失败").toString();
			}
			String[] startTimeStrs = afResourceDo.getValue().split(",");
			
			if(currentTime.before(dateFormat.parse(startTimeStrs[0]))){
				//2017-12-10 10:00号之前
				startTime = dateFormat.parse(startTimeStrs[0]);
			}
			
			/*if(currentTime.after(dateFormat.parse(startTimeStrs[0]))&&currentTime.before(dateFormat.parse(startTimeStrs[1]))){
				//2017-12-10 10:00号  —— 2017-12-10 14:00号
				startTime = dateFormat.parse(startTimeStrs[1]);
			}*/
			if(startTime==null){
				for (int i = 0; i < startTimeStrs.length-1; i++) {
					if(currentTime.after(dateFormat.parse(startTimeStrs[i]))&&currentTime.before(dateFormat.parse(startTimeStrs[i+1]))){
						startTime = dateFormat.parse(startTimeStrs[i+1]);
					}
				}
			}
			if(startTime==null){
				startTime=currentTime;
			}
			map.put("startTime", startTime);
			map.put("currentTime", currentTime);

			if(currentTime.after(dateFormat.parse(startTimeStrs[8]))){
				//2017-12-12 20:00号之后  最后一场，无下一场时间
				map.put("startTime", 0);
			}
			
			result = H5CommonResponse.getNewInstance(true, "获取活动时间成功", null, map).toString();
		} catch (Exception e) {
			//e.printStackTrace();
			logger.error("/activity/double12/startTime" + "error = {}", e.getStackTrace());
			result = H5CommonResponse.getNewInstance(false, "获取活动时间失败").toString();
		}
		return result;
	}
	
	
	
	/**
	 * 
	 * @Title: getGoodsMap
	 * @Description:  将秒杀商品加到map中
	 * @return  void  
	 * @author yanghailong
	 * @data  2017年11月21日
	 */
	private void getGoodsMap(Map<String, AfDouble12GoodsTimeTypeVo> goodsMap,
			AfGoodsDouble12Do afGoodsDouble12Do, String key) {
		if(goodsMap.get(key)!=null){
			goodsMap.get(key).getGoodsList().add(getAfDouble12GoodsVo(afGoodsDouble12Do));
		}else{
			List<AfDouble12GoodsVo> double12GoodsVoList = new ArrayList<AfDouble12GoodsVo>();
			double12GoodsVoList.add(getAfDouble12GoodsVo(afGoodsDouble12Do));
			
			Date starttime = afGoodsDouble12Do.getStartTime();
			AfDouble12GoodsTimeTypeVo afDouble12TypeVo = new AfDouble12GoodsTimeTypeVo();
			afDouble12TypeVo.setGoodsList(double12GoodsVoList);
			if(new Date().before(afGoodsDouble12Do.getStartTime())){
				afDouble12TypeVo.setType("N");//秒杀未开始
			}else if(new Date().after(afGoodsDouble12Do.getEndTime())){
				afDouble12TypeVo.setType("E");//秒杀已结束
			}else {
				afDouble12TypeVo.setType("O");//秒杀进行中
			}
			
			goodsMap.put(key, afDouble12TypeVo);
		}
	}
	
	/**
	 * 
	 * @Title: getAfDouble12GoodsVo
	 * @Description:  将商品信息数据封装到Vo
	 * @return  AfDouble12GoodsVo  
	 * @author yanghailong
	 * @data  2017年11月21日
	 */
	private AfDouble12GoodsVo getAfDouble12GoodsVo(AfGoodsDouble12Do afGoodsDouble12Do){
		//根据goodsId查询商品信息
		AfGoodsDo afGoodsDo = afGoodsService.getGoodsById(afGoodsDouble12Do.getGoodsId());
		AfDouble12GoodsVo afDouble12GoodsVo = new AfDouble12GoodsVo();
		if(afGoodsDo!=null){
			afDouble12GoodsVo.setNumId(String.valueOf(afGoodsDo.getRid()));
			afDouble12GoodsVo.setSaleAmount(afGoodsDo.getPriceAmount().toString());
			afDouble12GoodsVo.setRealAmount(afGoodsDo.getSaleAmount().toString());
			afDouble12GoodsVo.setRebateAmount(afGoodsDo.getRebateAmount().toString());
			afDouble12GoodsVo.setGoodsName(afGoodsDo.getName());
			afDouble12GoodsVo.setGoodsIcon(afGoodsDo.getGoodsIcon());
			afDouble12GoodsVo.setThumbnailIcon(afGoodsDo.getThumbnailIcon());
			afDouble12GoodsVo.setGoodsUrl(afGoodsDo.getGoodsDetail().split(";")[0]);
			afDouble12GoodsVo.setOpenId(afGoodsDo.getOpenId());
			afDouble12GoodsVo.setSource(afGoodsDo.getSource());
			afDouble12GoodsVo.setStockCount(afGoodsDo.getStockCount());
			Integer goodsDouble12Count = Integer.parseInt(afGoodsDo.getStockCount())-afGoodsDouble12Do.getAlreadyCount();//秒杀商品余量
			if(goodsDouble12Count<0){
				afDouble12GoodsVo.setCount("0");
			}else{
				afDouble12GoodsVo.setCount(goodsDouble12Count.toString());
			}
		}
		return afDouble12GoodsVo;
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
