package com.ald.fanbei.api.web.apph5.controller;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ald.fanbei.api.biz.service.AfActivityService;
import com.ald.fanbei.api.biz.service.AfCouponCategoryService;
import com.ald.fanbei.api.biz.service.AfCouponService;
import com.ald.fanbei.api.biz.service.AfGoodsCategoryService;
import com.ald.fanbei.api.biz.service.AfGoodsDoubleEggsService;
import com.ald.fanbei.api.biz.service.AfGoodsDoubleEggsUserService;
import com.ald.fanbei.api.biz.service.AfGoodsService;
import com.ald.fanbei.api.biz.service.AfInterestFreeRulesService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfSchemeGoodsService;
import com.ald.fanbei.api.biz.service.AfSubjectService;
import com.ald.fanbei.api.biz.service.AfUserCouponService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.enums.H5OpenNativeType;
import com.ald.fanbei.api.common.enums.HttpType;
import com.ald.fanbei.api.common.enums.InterestfreeCode;
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
import com.ald.fanbei.api.dal.domain.AfGoodsBuffer;
import com.ald.fanbei.api.dal.domain.AfGoodsDo;
import com.ald.fanbei.api.dal.domain.AfGoodsDoubleEggsDo;
import com.ald.fanbei.api.dal.domain.AfGoodsDoubleEggsUserDo;
import com.ald.fanbei.api.dal.domain.AfInterestFreeRulesDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfSchemeGoodsDo;
import com.ald.fanbei.api.dal.domain.AfSubjectDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.GoodsForDate;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.InterestFreeUitl;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.AfCouponDouble12Vo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

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
@RequestMapping(value = "/appH5DoubleEggs", produces = "application/json;charset=UTF-8")
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
	@Resource
	AfGoodsCategoryService afGoodsCategoryService;
	@Resource
	AfSchemeGoodsService afSchemeGoodsService;
	@Resource
	AfInterestFreeRulesService  afInterestFreeRulesService;
	@Resource
	AfGoodsService afGoodsService;
	@Resource
	AfSubjectService afSubjectService;
	@Resource
	AfActivityService afActivityService;
	
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
	@RequestMapping(value = "/initHomePage",method = RequestMethod.POST )
	public String initHomePage(HttpServletRequest request, HttpServletResponse response) {
		String result = "";
		try {
			java.util.Map<String, Object> data = new HashMap<>();
			// get info from afResource;
			AfResourceDo afResourceDo = afResourceService.getConfigByTypesAndSecType("DOUBLE_EGGS", "INI_HOME_PAGE");
			if(afResourceDo != null){
			    data.put("eggsPic", afResourceDo.getValue());
			    data.put("eggsUrl", afResourceDo.getValue1());
			    data.put("freshManPic", afResourceDo.getValue2());
			    data.put("freshManUrl", afResourceDo.getValue3());
			}
			
			result = H5CommonResponse.getNewInstance(true, "初始化成功", "", data).toString();
		} catch (Exception exception) {
			result = H5CommonResponse.getNewInstance(false, "初始化失败", "", exception.getMessage()).toString();
			logger.error("初始化数据失败  e = {} , resultStr = {}", exception, result);
			doMaidianLog(request, H5CommonResponse.getNewInstance(false, "fail"), result);
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
	@RequestMapping(value = "/initOnsaleGoods",method = RequestMethod.POST)
	public String initOnsaleGoods(HttpServletRequest request,HttpServletResponse response){
		String result = "";
		try {
			java.util.Map<String, Object> data = new HashMap<>();
			List<Map<String,Object>> goodsList = new ArrayList<Map<String,Object>>();
			List<Map<String,Object>> firstCategoryList = new ArrayList<Map<String,Object>>();
			
			 List<AfSubjectDo> listAllParentSubjectByTag = afSubjectService.listAllParentSubjectByTag("DOUBLE_EGG");
			if(listAllParentSubjectByTag != null &&listAllParentSubjectByTag.size()>0){
			    //long parentId =   afGoodsCategoryDo.getId();
			    //初始化时查该parentId下的三级分类的排序最前的商品列表
			  
			    //查询size为0的商品
			    long parentId =   listAllParentSubjectByTag.get(0).getId();
			    
			    List<AfGoodsDo> afGoodsList = afGoodsService.listGoodsListByParentIdFromSubjectGoods(parentId);
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
								goodsDo.getSaleAmount(), resource.getValue1(), resource.getValue2(),goodsId,"0");
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
			    
			     for(AfSubjectDo afFirstSubjectDo:listAllParentSubjectByTag){
				      AfSubjectDo queryAfSubject = new AfSubjectDo();
				      queryAfSubject.setParentId(afFirstSubjectDo.getId());
				      queryAfSubject.setLevel(2);
				      List<AfSubjectDo> afSecondSubjectList = afSubjectService.listByParentIdAndLevel(queryAfSubject);
				      List<Map<String,Object>> secondSubjectList = new ArrayList<Map<String,Object>>();
				      //遍历parent_id = i.id
				      for(AfSubjectDo afSecondSubjectDo:afSecondSubjectList){
          			               Map<String, Object> secondSubject = new HashMap<String, Object>();
          			                secondSubject.put("secondCategoryId",afSecondSubjectDo.getId());
          			                secondSubject.put("secondCategoryName",afSecondSubjectDo.getName());
          			                secondSubjectList.add(secondSubject);
				      }
			      Map<String, Object> firstSubject = new HashMap<String, Object>();
			      firstSubject.put("firstCategoryId",afFirstSubjectDo.getId());
			      firstSubject.put("firstCategoryName",afFirstSubjectDo.getName());
			      firstSubject.put("secondCategoryList",secondSubjectList);
			      firstCategoryList.add(firstSubject);
			    }
			    
			}
			   data.put("goodsList", goodsList);
		           data.put("firstCategoryList", firstCategoryList);
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
	 * @author qiwei @date 2017年12月7日
	 * 下午2:04:40 
	 * @Description: 获得特卖商品（点击tap进行不同的跳转） 
	 * @param request @param
	 * response 
	 * @return @return String 
	 * @throws
	 */
	@RequestMapping(value = "/getOnSaleGoods",method = RequestMethod.POST)
	public String getOnSaleGoods(HttpServletRequest request, HttpServletResponse response) {
		String result = "";
		try {
			java.util.Map<String, Object> data = new HashMap<>();

			Long subjectId = NumberUtil.objToLong(request.getParameter("secondCategoryId"));
			if(subjectId == null){
			    return H5CommonResponse.getNewInstance(false, "参数异常", "", data).toString();
			}
			List<Map<String,Object>> goodsList = new ArrayList<Map<String,Object>>();
			
			    List<AfGoodsDo> afGoodsList = afGoodsService.listGoodsListBySubjectId(subjectId);
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
								goodsDo.getSaleAmount(), resource.getValue1(), resource.getValue2(),goodsId,"0");
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
			result = H5CommonResponse.getNewInstance(true, "获取特卖商品成功", "", data).toString();
			} catch (Exception exception) {
				result = H5CommonResponse.getNewInstance(false, "获取特卖商品失败", "", exception.getMessage()).toString();
				logger.error("获取特卖商品失败  e = {} , resultStr = {}", exception, result);
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
	 * @Description: 优惠券初始化
	 * @return String
	 * @author chenqiwei
	 * @data 2017年12月7日
	 */
	@RequestMapping(value = "/initCoupons", method = RequestMethod.POST)
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

	public static void main(String[] args) {
		Date currentTime = new Date();
		String currentHourMinute = DateUtil.convertDateToString(DateUtil.SHORT_MATCH_PATTERN,currentTime);
		System.out.println(currentHourMinute.compareTo("10:10"));;
		System.out.println(currentHourMinute);
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
	@RequestMapping(value = "/getSecondKillGoodsList",method = RequestMethod.POST)
	public String getSecondKillGoodsList(HttpServletRequest request, HttpServletResponse response) {
		String result = "";
		FanbeiWebContext context = new FanbeiWebContext();
		try {
			context = doWebCheck(request, false);
			
			//get tag from activityId then get goods from different tag
			Long activityId = NumberUtil.objToLong(request.getParameter("activityId"));
			if (activityId == null) {
				return H5CommonResponse.getNewInstance(false, "没有配置此分会场！").toString();
			}

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			
			String paramDate = request.getParameter("startDate");
			
			
			
			Date startDate = new Date();
			
			if (StringUtil.isNotBlank(paramDate)) {
				startDate = sdf.parse(paramDate);
			}
			String log = String.format("/appH5DoubleEggs/getSecondKillGoodsList parameter : activityId = %d", activityId);
			//find the name from activityId
			String tag = SpringFestivalActivityEnum.findTagByActivityId(activityId);
			if (StringUtil.isBlank(tag)) {
				return H5CommonResponse.getNewInstance(false, "没有配置此分会场！").toString();
			}
			
			
			Date now = new Date() ;

			//get dateList start from the config of specific activity
			List<Date> dateList = afActivityService.getDateListByName(tag);
			
			
			if (CollectionUtil.isNotEmpty(dateList)) {
				
				log = log + String.format("middle params dateList.size() = %s ,startDate = %s", dateList.size(),startDate.toString());
				logger.info(log);
				
				if(startDate.before(dateList.get(0))){
					startDate = dateList.get(0);
				}
				
				
				
			//AfGoodsForSecondKill afGoodsForSecondKill = new AfGoodsForSecondKill();
				List<AfGoodsBuffer> goodsList = new ArrayList<>();
				
				//for (Date startDate : dateList) {
					
					List<GoodsForDate> goodsListForDate = afGoodsDoubleEggsService.getGOodsByDate(startDate,tag);
					if (CollectionUtil.isNotEmpty(goodsListForDate)) {
						
						AfGoodsBuffer goodsBuffer = new AfGoodsBuffer();
						
						// if this user has already login in then add status to goods. goodsListForDate
						String userName = context.getUserName();
						
						long userId = 0L;
						if (StringUtil.isNotBlank(userName) && convertUserNameToUserId(userName) != null) {
							for(GoodsForDate goodsForDate :goodsListForDate){
								userId = convertUserNameToUserId(userName);
								// change status according to different users(change the status for goodsListForDate)
								int num = afGoodsDoubleEggsUserService.isSubscribed(userId,goodsForDate.getDoubleGoodsId());
								
								log = log + String.format("num = %s",num);
								logger.info(log);
								
								//only to make sure if this user has already subscribed this goods
								if (now.before(startDate) && num > 0 ) {
									goodsForDate.setStatus(1);
								}
							}
						}
						
						log = log + String.format("goodsListForDate=%s, userName = %s,startDate = %s",goodsListForDate.toString(), userName,startDate);
						logger.info(log);
						
						if (startDate != null) {

							Date temStartDate = DateUtil.formatDateToYYYYMMdd(startDate);
							Date temNow = DateUtil.formatDateToYYYYMMdd(new Date());
							if (DateUtil.afterDay(temStartDate, temNow)) {
								goodsBuffer.setStatus(0);
							}else if (DateUtil.beforeDay(temStartDate, temNow)){
								goodsBuffer.setStatus(2);
							}else {
								goodsBuffer.setStatus(1);
							}

							// format to the fix form
							
							goodsBuffer.setStartDate(sdf.format(startDate));
							goodsBuffer.setStartTime(startDate);
							goodsBuffer.setGoodsListForDate(goodsListForDate);
							goodsList.add(goodsBuffer);

						}
						
					}
		
				//}
				
				
				java.util.Map<String, Object> data = new HashMap<>();
				

				data.put("goodsList", goodsList);
				data.put("serviceDate", new Date());

				log = log + String.format("goodsList = %s",goodsList.toString());
				logger.info(log);
				
				result = H5CommonResponse.getNewInstance(true, "初始化成功", "", data).toString();
				return result;
				
			}
			result = H5CommonResponse.getNewInstance(false, "初始化失败").toString();

		} catch (Exception exception) {
			result = H5CommonResponse.getNewInstance(false, "初始化失败", "", exception.getMessage()).toString();
			logger.error("初始化数据失败  e = {} , resultStr = {}", exception, result);
			doMaidianLog(request, H5CommonResponse.getNewInstance(false, "fail"), result);
		}
		return result;
	}

	/**
	 * 
	* @Title: getSecondKillGoodsListV1
	* @author qiao
	* @date 2018年3月2日 下午3:39:32
	* @Description: 适合一天有多个场次
	* @param request
	* @param response
	* @return    
	* @return String   
	* @throws
	 */
	@RequestMapping(value = "/getSecondKillGoodsListV1",method = RequestMethod.POST)
	public String getSecondKillGoodsListV1(HttpServletRequest request, HttpServletResponse response) {
		String result = "";
		
		String httpType = request.getParameter("httpType");
		
		if (StringUtil.isBlank(httpType)) {
			FanbeiException exception = new FanbeiException(FanbeiExceptionCode.REQUEST_PARAM_NOT_EXIST);
			result = H5CommonResponse.getNewInstance(false, "httpType is empty", "", exception.getMessage()).toString();
			return result;
		}
		
		//get tag from activityId then get goods from different tag
		Long activityId = NumberUtil.objToLong(request.getParameter("meetingId"));
		
		if ( activityId == null) {
			return H5CommonResponse.getNewInstance(false, "meetingId is empty ！").toString();
		}
		
		String log = String.format("/appH5DoubleEggs/getSecondKillGoodsList parameter : activityId = %d", activityId);
		
		List<GoodsForDate> goodsList = afGoodsDoubleEggsService.getGoodsListByActivityId(activityId);
		
		if (CollectionUtil.isNotEmpty(goodsList)) {
			for(GoodsForDate goodsForDate : goodsList){
				Integer alreadyCount = 0;
		        alreadyCount = afGoodsDoubleEggsService.getAlreadyCount(goodsForDate.getGoodsId());
		        Integer intstock= goodsForDate.getStockCount() - alreadyCount;
		        goodsForDate.setCount(intstock<0?0:intstock);
			}
		}
		
		log = log + String.format("goodsList = %s",goodsList.toString());
		logger.info(log);
		
		if (httpType.equals(HttpType.H5.getCode().toString())) {
			
			//like without login in AppH5
			java.util.Map<String, Object> data = new HashMap<>();
			
			data.put("goodsList", goodsList);
			data.put("serviceDate", new Date());
			
			return H5CommonResponse.getNewInstance(true, "初始化成功", "", data).toString();
		}
		
		//if httpType is appH5 then judge if it is login or subscribed already 
		FanbeiWebContext context = new FanbeiWebContext();
		try{
			context = doWebCheck(request, false);
			
			// if this user has already login in then add status to goods. goodsListForDate
			String userName = context.getUserName();
			
			long userId = 0L;
			if (StringUtil.isNotBlank(userName) && convertUserNameToUserId(userName) != null) {
				
				if (CollectionUtil.isNotEmpty(goodsList)) {
					
					int status = goodsList.get(0).getStatus();
					
					if (status == 0 || status == 2) {
						//check if already subscribed or if already bought 
						
						for(GoodsForDate goodsForDate :goodsList){
							
							userId = convertUserNameToUserId(userName);
							
							if (status == 0) {
								//check if already subscribed
								int num = afGoodsDoubleEggsUserService.isSubscribed(userId,goodsForDate.getDoubleGoodsId());
								
								log = log + String.format("num = %s",num);
								logger.info(log);
								if (num > 0 ) {
									goodsForDate.setStatus(1);
								}
							}
						}
					}
					

				}
				

			}
			
			
			//like without login in AppH5
			java.util.Map<String, Object> data = new HashMap<>();
			
			data.put("goodsList", goodsList);
			data.put("serviceDate", new Date());
			
			return H5CommonResponse.getNewInstance(true, "初始化成功", "", data).toString();
			
		}catch (Exception exception) {
			result = H5CommonResponse.getNewInstance(false, "初始化失败", "", exception.getMessage()).toString();
			logger.error("初始化数据失败  e = {} , resultStr = {}", exception, result);
			doMaidianLog(request, H5CommonResponse.getNewInstance(false, "fail"), result);
	
		
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
	@RequestMapping(value = "/subscribe",method = RequestMethod.POST)
	public String subscribe(HttpServletRequest request, HttpServletResponse response) {
		String result = "";
		FanbeiWebContext context = new FanbeiWebContext();
		try {
			
			String log = "/appH5DoubleEggs/subscribe";
			
			context = doWebCheck(request, true);
			if (context != null) {
				Long userId = convertUserNameToUserId(context.getUserName());
				java.util.Map<String, Object> data = new HashMap<>();

				Long doubleGoodsId = NumberUtil.objToLong(request.getParameter("doubleGoodsId"));
				
				log = log + String.format("doubleGoodsId = %s",doubleGoodsId);
				logger.info(log);
				
				AfGoodsDoubleEggsDo tempDo = afGoodsDoubleEggsService.getByDoubleGoodsId(doubleGoodsId);
				if (tempDo == null ) {
					result = H5CommonResponse.getNewInstance(false, "商品不存在！").toString();
				}
		
				log = log + String.format("AfGoodsDoubleEggsDo = %s",tempDo.toString());
				logger.info(log);
				
				//根据goodsId查询商品信息
				AfGoodsDo afGoodsDo = afGoodsService.getGoodsById(tempDo.getGoodsId());
				int goodsDouble12Count = (int) (Integer.parseInt(afGoodsDo.getStockCount())-tempDo.getAlreadyCount());//秒杀商品余量
				
				log = log + String.format("goodsDouble12Count = %d",goodsDouble12Count);
				logger.info(log);
				
				if(goodsDouble12Count <= 0){
					//报错提示秒杀商品已售空
					throw new FanbeiException(FanbeiExceptionCode.NO_DOUBLE12GOODS_ACCEPTED);
				}
				
				if (tempDo != null) {

					//String time = "10";
					//Integer.parseInt(time);
					int preTime = 10;
					Date now = new Date();

					// if now + preTime >= goods start time then throw
					// error"time分钟内无需预约"
					if (DateUtil.addMins(now, preTime).after(tempDo.getStartTime())) {
						return  H5CommonResponse.getNewInstance(false, "抱歉" + preTime + "分钟内无法预约！").toString();
						
					}

					
					// to check if this user already subscribed this goods if
					// yes then "已经预约不能重复预约"else"预约成功"
					if (afGoodsDoubleEggsUserService.isSubscribed(tempDo.getRid(), userId) > 0) {
						return H5CommonResponse.getNewInstance(false, "已经预约过不能重复预约！").toString();
						 
					}

					AfGoodsDoubleEggsUserDo userDo = new AfGoodsDoubleEggsUserDo();
					userDo.setDoubleEggsId(doubleGoodsId);
					userDo.setGmtCreate(now);
					userDo.setGmtModified(now);
					//for spring festival
					userDo.setIsOrdered(2);
					userDo.setUserId(userId);
					
					log = log + String.format("afGoodsDoubleEggsUserDo for saving = %s",userDo.toString());
					logger.info(log);
					
					afGoodsDoubleEggsUserService.saveRecord(userDo);
					
					//Long fakeNumber = goodsDo.getAlreadyCount();
					
					//int numberForSF = afGoodsDoubleEggsUserService.getSpringFestivalNumber(goodsId);
					//data.put("number", numberForSF + fakeNumber);
					result = H5CommonResponse.getNewInstance(true, "预约成功", "", data).toString();
					return result;
				}
			}
			result = H5CommonResponse.getNewInstance(false, "预约失败").toString();

		}catch (FanbeiException e) {
			if (e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_INVALID_SIGN_ERROR) || e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_PARAM_TOKEN_ERROR)) {
				Map<String, Object> data = new HashMap<>();
				String loginUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative
						+ H5OpenNativeType.AppLogin.getCode();
				data.put("loginUrl", loginUrl);
				result = H5CommonResponse.getNewInstance(false, "没有登录", "", data).toString();
				return result.toString();
			}
			result = H5CommonResponse.getNewInstance(false, "初始化失败", "", e.getErrorCode().getDesc()).toString();
			logger.error("resultStr = {}", result);
			logger.error("初始化数据失败  e = {} , resultStr = {}", e, result);
		}
		
		catch (Exception exception) {
			result = H5CommonResponse.getNewInstance(false, "预约化失败", "", exception.getMessage()).toString();
			logger.error("预约失败 context = {},  e = {} ", context, exception);
			doMaidianLog(request, H5CommonResponse.getNewInstance(false, "fail"), result);
		}
		return result;
	}
	String opennative = "/fanbei-web/opennative?name=";
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
	 private void removeSecondNper(JSONArray array) {
	        if (array == null) {
	            return;
	        }
	        Iterator<Object> it = array.iterator();
	        while (it.hasNext()) {
	            JSONObject json = (JSONObject) it.next();
	            if (json.getString(Constants.DEFAULT_NPER).equals("2")) {
	                it.remove();
	                break;
	            }
	        }
	    }

}
