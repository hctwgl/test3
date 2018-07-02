package com.ald.fanbei.api.web.apph5.controller;

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

import com.ald.fanbei.api.biz.service.AfActivityService;
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
import com.ald.fanbei.api.common.util.CollectionUtil;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfCouponCategoryDo;
import com.ald.fanbei.api.dal.domain.AfCouponDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserCouponTigerMachineDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.AfCouponDouble12Vo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.math.BigDecimal;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ald.fanbei.api.biz.service.AfGoodsService;
import com.ald.fanbei.api.biz.service.AfInterestFreeRulesService;
import com.ald.fanbei.api.biz.service.AfModelH5ItemService;
import com.ald.fanbei.api.biz.service.AfSchemeGoodsService;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.enums.CouponActivityType;
import com.ald.fanbei.api.common.enums.InterestfreeCode;
import com.ald.fanbei.api.dal.domain.AfGoodsDo;
import com.ald.fanbei.api.dal.domain.AfInterestFreeRulesDo;
import com.ald.fanbei.api.dal.domain.AfModelH5ItemDo;
import com.ald.fanbei.api.dal.domain.AfSchemeGoodsDo;
import com.ald.fanbei.api.dal.domain.dto.SecondKillDateVo;
import com.ald.fanbei.api.web.common.InterestFreeUitl;




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
@RequestMapping(value = "/appH5SFMain", produces = "application/json;charset=UTF-8")
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
	@Resource
	AfActivityService afActivityService;
	
	@Resource
	BizCacheUtil bizCacheUtil;
	@Resource
	AfGoodsService afGoodsService;
	@Resource
	AfSchemeGoodsService afSchemeGoodsService;
	@Resource
	AfInterestFreeRulesService afInterestFreeRulesService;
	@Resource
	AfModelH5ItemService afModelH5ItemService;


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
	* @Title: getDateList
	* @author qiao
	* @date 2018年3月2日 上午9:53:10
	* @Description: 每天一场秒杀的获取时间。
	* @param request
	* @param response
	* @return    
	* @return String   
	* @throws
	 */

	@RequestMapping(value = "/getDateList", method = RequestMethod.POST)
	public String getDateList(HttpServletRequest request, HttpServletResponse response) {
		String result = "";
		try {

			// get tag from activityId then get goods from different tag
			Long activityId = NumberUtil.objToLong(request.getParameter("activityId"));
			if (activityId == null) {
				return H5CommonResponse.getNewInstance(false, "没有配置此分会场！").toString();
			}

			// find the name from activityId
			String tag = SpringFestivalActivityEnum.findTagByActivityId(activityId);
			if (StringUtil.isBlank(tag)) {
				return H5CommonResponse.getNewInstance(false, "没有配置此分会场！").toString();
			}

			// get dateList start from the config of specific activity
			List<Date> dateListe = afActivityService.getDateListByName(tag);
			List<SecondKillDateVo> dateList = new ArrayList<SecondKillDateVo>();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			
			if(CollectionUtil.isNotEmpty(dateListe)){
				for(Date date:dateListe){
					String dateStr = sdf.format(date);
					SecondKillDateVo vo = new SecondKillDateVo();
					vo.setStartDate(dateStr);
					vo.setStartTime(date);
					
					date = DateUtil.formatDateToYYYYMMdd(date);
					
					Date temNow = DateUtil.formatDateToYYYYMMdd(new Date());
					
					
					if (DateUtil.afterDay(date, temNow)) {
						vo.setStatus(0);
					}else if (DateUtil.beforeDay(date, temNow)){
						vo.setStatus(2);
					}else {
						
						vo.setStatus(1);
					}
					
					dateList.add(vo);
					
					
				}
			}

			java.util.Map<String, Object> data = new HashMap<>();

			data.put("dateList", dateList);
			data.put("serviceDate", new Date());

			return H5CommonResponse.getNewInstance(true, "初始化成功", "", data).toString();

		} catch (

		Exception exception) {
			result = H5CommonResponse.getNewInstance(false, "初始化失败", "", exception.getMessage()).toString();
			logger.error("初始化数据失败  e = {} , resultStr = {}", exception, result);
			doMaidianLog(request, H5CommonResponse.getNewInstance(false, "fail"), result);
		}
		return result;
	}
	

	/**
	 * 
	* @Title: getDateList
	* @author qiao
	* @date 2018年3月2日 上午9:53:10
	* @Description: 每天一场秒杀多场的活动日期获取
	* @param request
	* @param response
	* @return    
	* @return String   
	* @throws
	 */
	@RequestMapping(value = "/getDateListV1", method = RequestMethod.POST)
	public String getDateListV1(HttpServletRequest request, HttpServletResponse response) {
		String result = "";
		try {

			// get tag from activityId then get goods from different tag
			Long activityId = NumberUtil.objToLong(request.getParameter("activityId"));
			if (activityId == null) {
				return H5CommonResponse.getNewInstance(false, "没有配置此分会场！").toString();
			}

			// find the name from activityId
			String tag = SpringFestivalActivityEnum.findTagByActivityId(activityId);
			if (StringUtil.isBlank(tag)) {
				return H5CommonResponse.getNewInstance(false, "没有配置此分会场！").toString();
			}

			// get dateList start from the config of specific activity
			List<SecondKillDateVo> dateList = afActivityService.getDateVoListByName(tag);
			SimpleDateFormat sdf = new SimpleDateFormat("MM月dd号 HH:mm");
			
			if(CollectionUtil.isNotEmpty(dateList)){
				for(SecondKillDateVo date:dateList){
					if(date.getStartTime() != null){
						date.setStartDate(sdf.format(date.getStartTime()));
					}
					
					
				}
			}

			java.util.Map<String, Object> data = new HashMap<>();

			data.put("dateList", dateList);
			data.put("serviceDate", new Date());

			return H5CommonResponse.getNewInstance(true, "初始化成功", "", data).toString();

		} catch (

		Exception exception) {
			result = H5CommonResponse.getNewInstance(false, "初始化失败", "", exception.getMessage()).toString();
			logger.error("初始化数据失败  e = {} , resultStr = {}", exception, result);
			doMaidianLog(request, H5CommonResponse.getNewInstance(false, "fail"), result);
		}
		return result;
	}
	
	/**
	 * 
	* @Title:  carefullyChosen
	* @Description: 
	* @param request
	* @param response
	* @return    
	* @return String   
	* @throws
	 */
	@ResponseBody
	@RequestMapping(value = "/carefullyChosen", method = RequestMethod.POST,produces = "text/html;charset=UTF-8")
	public String carefullyChosen(HttpServletRequest request, HttpServletResponse response) {
		
		String result = "";
		try {
			doWebCheck(request, false);
			String cacheKey =  "carefullyChosen:goods";  
			List<Map<String,Object>> itemList = bizCacheUtil.getObjectList(cacheKey);
			List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
			String tag = ObjectUtils.toString(request.getParameter("tag"), null);
			if(tag == null || "".equals(tag)) {
			    result = H5CommonResponse.getNewInstance(false, "tag不能为空！").toString();
				return result;
			}
			
			if (itemList  == null) {
			
			//根据tag
			//String type = "PARTACTIVITY_H5_TEMPLATE";
			List<AfModelH5ItemDo>  afModelH5ItemList = afModelH5ItemService.getModelH5ItemCategoryByModelTag(tag);
			
		
			for(AfModelH5ItemDo afModelH5ItemDo:afModelH5ItemList){
			    String itemName = afModelH5ItemDo.getItemValue();
			    Map<String, Object> data = new HashMap<String, Object>();
			    List<Map<String,Object>> goodsList = new ArrayList<Map<String,Object>>();
			    List<AfGoodsDo> afGoodsList = afGoodsService.getGoodsByModelId( Long.valueOf(afModelH5ItemDo.getRid()));
			  
			    
			    if(afGoodsList.size()>0){
				//获取借款分期配置信息
			        AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE, Constants.RES_BORROW_CONSUME);
			        JSONArray array = JSON.parseArray(resource.getValue());
			        //删除2分期
			        if (array == null) {
			            throw new FanbeiException(FanbeiExceptionCode.BORROW_CONSUME_NOT_EXIST_ERROR);
			        }
			       // removeSecondNper(array);
				
				for(AfGoodsDo goodsDo : afGoodsList) {
				        
				        
		    			Map<String, Object> goodsInfo = new HashMap<String, Object>();
		    			goodsInfo.put("goodName",goodsDo.getName());
		    			goodsInfo.put("rebateAmount", goodsDo.getRebateAmount());
		    			goodsInfo.put("saleAmount", goodsDo.getSaleAmount());
		    			goodsInfo.put("priceAmount", goodsDo.getPriceAmount());
		    			goodsInfo.put("goodsIcon", goodsDo.getGoodsIcon());
		    			goodsInfo.put("goodsId", goodsDo.getRid());
		    			goodsInfo.put("goodsUrl", goodsDo.getGoodsUrl());
		    			goodsInfo.put("thumbnailIcon", goodsDo.getThumbnailIcon());
		    			goodsInfo.put("source", goodsDo.getSource());
		    			goodsInfo.put("goodsType", "0");
		    			goodsInfo.put("remark", StringUtil.null2Str(goodsDo.getRemark()));
		    			// 如果是分期免息商品，则计算分期
		    			Long goodsId = goodsDo.getRid();
						AfSchemeGoodsDo  schemeGoodsDo = null;
						try {
							schemeGoodsDo = afSchemeGoodsService.getSchemeGoodsByGoodsId(goodsId);
						} catch(Exception e){
							logger.error(e.toString());
						}
						JSONArray interestFreeArray = null;
						if(schemeGoodsDo != null){
							AfInterestFreeRulesDo  interestFreeRulesDo = afInterestFreeRulesService.getById(schemeGoodsDo.getInterestFreeId());
							String interestFreeJson = interestFreeRulesDo.getRuleJson();
							if (StringUtils.isNotBlank(interestFreeJson) && !"0".equals(interestFreeJson)) {
								interestFreeArray = JSON.parseArray(interestFreeJson);
							}
						}
						List<Map<String, Object>> nperList = InterestFreeUitl.getConsumeList(array, interestFreeArray, BigDecimal.ONE.intValue(),
							goodsDo.getSaleAmount(), resource.getValue1(), resource.getValue2(),0l,"0");
						if(nperList!= null){
							goodsInfo.put("goodsType", "1");
							Map<String, Object> nperMap = nperList.get(nperList.size() - 1);
							String isFree = (String)nperMap.get("isFree");
							if(InterestfreeCode.NO_FREE.getCode().equals(isFree)) {
								nperMap.put("freeAmount", nperMap.get("amount"));
							}
							goodsInfo.put("nperMap", nperMap);
						}
						
						goodsList.add(goodsInfo);
		    		}				
			 }
			         data.put("goodsList", goodsList);
				 data.put("itemName",itemName);
				 list.add(data);
				 itemList = list;
				bizCacheUtil.saveObjectListExpire(cacheKey, itemList,  Constants.SECOND_OF_TEN_MINITS);
				 
			       
			 }
		    }
			 
			result = H5CommonResponse.getNewInstance(true, "获取商品成功", "", itemList).toString();
			} catch (Exception exception) {
				result = H5CommonResponse.getNewInstance(false, "获取商品失败", "", exception.getMessage()).toString();
				logger.error("获取商品失败  e = {} , resultStr = {}", exception, result);
				doMaidianLog(request, H5CommonResponse.getNewInstance(false, "fail"),result);
			}
			return result;
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
				
				int tigerTimes = 1;
				if (userId != null && !userId.equals(0L)) {
					//login then get the total times or insert one time daily one
					AfUserCouponTigerMachineDo machineDo = new AfUserCouponTigerMachineDo();
					machineDo.setUserId(userId);
					AfUserCouponTigerMachineDo resultDo = afUserCouponTigerMachineService.getByCommonCondition(machineDo);
					if (resultDo != null) {
						tigerTimes = afUserCouponTigerMachineService.getTotalTimesByUserId(userId);
					}else{
						afUserCouponTigerMachineService.addOneTime(userId, "DAILY");
					}
				}else{
					userId = 0L;
				}
				
				data.put("tigerTimes", tigerTimes);
				logger.info("/appH5SFMain/initHomePage userId={} , tigerTimes={} ", new Object[] { userId, tigerTimes });
				
				// get configuration from afresource
				AfResourceDo resourceDo = afResourceService.getConfigByTypesAndSecType("SPRING_FESTIVAL_ACTIVITY",
						"MAIN_DESCRIBTION");
				if (resourceDo != null) {
					data.put("describtion", resourceDo.getValue2());
				}
				logger.info("/appH5SFMain/initHomePage userId={} , tigerTimes={} ,resourceDo = {}",
						new Object[] { userId, tigerTimes, resourceDo });
				result = H5CommonResponse.getNewInstance(true, "初始化成功", "", data).toString();
			}

		} catch (Exception exception) {
			result = H5CommonResponse.getNewInstance(false, "初始化失败", "", exception.getMessage()).toString();
			logger.error("初始化数据失败  e = {} , resultStr = {}", exception, result);
			doMaidianLog(request, H5CommonResponse.getNewInstance(false, "fail"), result);
		}
		return result;
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
			
			String log = String.format("initTigerMachine finished doWebCheck and the context is %s", context.toString());
			logger.info(log);
			
			String userName = context.getUserName();

			log =log + String.format("userName %s", userName);
			logger.info(log);
			
			Long userId = convertUserNameToUserId(userName);
			
			log =log + String.format("userName %s", userName);
			logger.info(log);
			
			//get conpons
			String tag = "_TIGER_MACHINE_";

			AfCouponCategoryDo couponCategory = afCouponCategoryService.getCouponCategoryByTag(tag);
			
			log =log + String.format("couponCategory %s", couponCategory.toString());
			logger.info(log);
			
			if(couponCategory == null){
				return H5CommonResponse.getNewInstance(false, "老虎机优惠券没有配置").toString();
			}
			
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
			
			log =log + String.format("couponVoList %s", couponVoList.toString());
			logger.info(log);
			
			//get total userTimes
			int times = afUserCouponTigerMachineService.getTotalTimesByUserId(userId);
			data.put("times", times);
			
			log =log + String.format("couponVoList %s", couponVoList);
			logger.info(log);
			
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
		
			
			//get total userTimes
			int times = afUserCouponTigerMachineService.getTotalTimesByUserId(userId);
			
			String log = String.format("/appH5SFMain/pickUpTigerMachineCoupon middle params: times = %d", times);
			logger.info(log);
			
			if (times < 1) {
				return H5CommonResponse.getNewInstance(false, "您暂无抽奖机会，快去购物获取抽奖机会吧").toString();
			}
			//get conpons
			String tag ="_TIGER_MACHINE_";

			AfCouponCategoryDo couponCategory = afCouponCategoryService.getCouponCategoryByTag(tag);
			
			log = log + String.format(" couponCategory = %s", couponCategory.toString());
			logger.info(log);
			
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
			
			log = log + String.format("hitIndex = %d", hitIndex);
			logger.info(log);
			
			Long couponId = avalibleCouponIdList.get(hitIndex);
			
			log = log + String.format("couponId = %d", couponId);
			logger.info(log);
			
			data.put("couponId", couponId);
			
			//decrease the times for once.
			//(fist decrease the daily time one then the shopping one)+grant coupons
			
			boolean isSuccess = afUserCouponTigerMachineService.grandCoupon(couponId,userId);
			
			log = log + String.format("isSuccess = %b", isSuccess);
			logger.info(log);
			
			if (!isSuccess) {
				return H5CommonResponse.getNewInstance(false, "老虎机抽取礼物失败，可以重新来一次哦~").toString();
			}
			
			times = afUserCouponTigerMachineService.getTotalTimesByUserId(userId);
			
			log = log + String.format("final times = %d", times);
			logger.info(log);
			
			data.put("times", times);
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
