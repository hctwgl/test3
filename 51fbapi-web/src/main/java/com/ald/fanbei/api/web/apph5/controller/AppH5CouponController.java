package com.ald.fanbei.api.web.apph5.controller;
 
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.service.AfCouponCategoryService;
import com.ald.fanbei.api.biz.service.AfCouponService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserCouponService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.enums.H5OpenNativeType;
import com.ald.fanbei.api.common.enums.ResourceType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.Base64;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfCouponCategoryDo;
import com.ald.fanbei.api.dal.domain.AfCouponDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserCouponDto;
import com.ald.fanbei.api.dal.domain.query.AfUserCouponQuery;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.AfUserCouponVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
 
/**
 * @类描述：
 * 返场活动
 * @author 江荣波 2017年7月17日下午1:41:05
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping("/fanbei-web")
public class AppH5CouponController extends BaseController {
	
	@Resource
	AfResourceService afResourceService;
	@Resource
	AfCouponService afCouponService;
	@Resource
	AfCouponCategoryService  afCouponCategoryService;
    @Resource
    AfUserCouponService afUserCouponService;
    @Resource 
	AfUserService afUserService;
    
    private String opennative = "/fanbei-web/opennative?name=";
    
    private final static int EXPIRE_DAY = 2;
    
    @RequestMapping(value = "couponCategoryInfo", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
    public String couponCategoryInfo(HttpServletRequest request, ModelMap model) throws IOException {
    	try{
    		FanbeiWebContext context = doWebCheck(request, false);
    		JSONObject jsonObj = new JSONObject();
    		// 获取Banner信息
    		List<AfResourceDo> bannerInfoList = afResourceService.getConfigByTypes(ResourceType.COUPON_CENTER_BANNER.getCode());
    		if(bannerInfoList != null && !bannerInfoList.isEmpty()) {
    			AfResourceDo bannerInfo = bannerInfoList.get(0);
    			String bannerImage = bannerInfo.getValue();
    			String bannerUrl = bannerInfo.getValue2();
    			String isOpen = bannerInfo.getValue4();
    			if("O".equals(isOpen)) {
    				jsonObj.put("bannerImage", bannerImage);
        			jsonObj.put("bannerUrl", bannerUrl);
    			}
    		}
    		// 查询所有优惠券分类
    		List<AfCouponCategoryDo> afCouponCategoryList = afCouponCategoryService.listAllCouponCategory();
    		List <Map<String,Object>> couponCategoryList = new ArrayList<Map<String,Object>>();
    		
    		Map<String,Object> allCouponMap = new HashMap<String,Object>();
    		List<HashMap<String,Object>> allCouponInfoList = new ArrayList<HashMap<String,Object>>();
    		allCouponMap.put("couponInfoList",allCouponInfoList);
    		couponCategoryList.add(allCouponMap);
    		allCouponMap.put("name", "推荐");
    		AfCouponCategoryDo couponCategoryAll = afCouponCategoryService.getCouponCategoryAll();
    		if(couponCategoryAll != null ) {
    			String couponsAll = couponCategoryAll.getCoupons();
        		JSONArray arrayAll = (JSONArray) JSONArray.parse(couponsAll);
        		for(int i = 0; i < arrayAll.size(); i++){
        			HashMap<String, Object> couponInfoMap = new HashMap<String, Object>();
        			String couponId = (String)arrayAll.getString(i);
        			AfCouponDo afCouponDo = afCouponService.getCouponById(Long.parseLong(couponId));
        			if(afCouponDo == null) continue;
        			couponInfoMap.put("shopUrl", couponCategoryAll.getUrl());
        			couponInfoMap.put("couponId", afCouponDo.getRid());
        			couponInfoMap.put("name", afCouponDo.getName());
        			couponInfoMap.put("useRule", afCouponDo.getUseRule());
        			couponInfoMap.put("type", afCouponDo.getType());
        			couponInfoMap.put("amount", afCouponDo.getAmount());
        			couponInfoMap.put("useRange", afCouponDo.getUseRange());
        			couponInfoMap.put("limitAmount", afCouponDo.getLimitAmount());
        			Date gmtStart = afCouponDo.getGmtStart();
        			if( gmtStart != null){
        				couponInfoMap.put("gmtStart", gmtStart.getTime());
        			} else {
        				couponInfoMap.put("gmtStart", 0);
        			}
        			Date gmtEnd = afCouponDo.getGmtEnd();
        			if (gmtEnd != null) {
        				couponInfoMap.put("gmtEnd", gmtEnd.getTime());
        			} else {
        				couponInfoMap.put("gmtEnd", 0);
        			}
        			
        			couponInfoMap.put("currentTime", System.currentTimeMillis());
        			if (!context.isLogin()) {
        				couponInfoMap.put("isDraw", "Y");
        			} else {
        				// 获取用户信息
        				String userName = context.getUserName();
            			AfUserDo user = afUserService.getUserByUserName(userName);
            			// 判断是否领取优惠券
            			int userCouponCount = afUserCouponService.getUserCouponByUserIdAndCouponId(user.getRid(), Long.parseLong(couponId));
            			if(userCouponCount < afCouponDo.getLimitCount()){
            				couponInfoMap.put("isDraw", "Y");
            			} else {
            				couponInfoMap.put("isDraw", "N");
            			}
        			}
        			// 判断优惠券是否领完
        			Long quota = afCouponDo.getQuota();
        			Integer quotaAlready = afCouponDo.getQuotaAlready();
        			if(quota != 0 && quota != -1 && quota.intValue() <= quotaAlready.intValue()){
        				if(!context.isLogin()) {
        					couponInfoMap.put("isOver", "N");
        				} else {
        				 	couponInfoMap.put("isOver", "Y");
        				}
        			} else {
        				couponInfoMap.put("isOver", "N");
        			}
        			allCouponInfoList.add(new HashMap<String, Object>(couponInfoMap));
        		}
    		}
    		
    		for(AfCouponCategoryDo afCouponCategoryDo: afCouponCategoryList) {
    			Map<String,Object> couponCategoryMap = new HashMap<String,Object>();
    			couponCategoryMap.put("name", afCouponCategoryDo.getName());
    			List <Map<String,Object>> couponInfoList = new ArrayList<Map<String,Object>>();
    			
    			String coupons = afCouponCategoryDo.getCoupons();
    			JSONArray array = (JSONArray) JSONArray.parse(coupons);
        		for(int i = 0; i < array.size(); i++){
        			HashMap<String, Object> couponInfoMap = new HashMap<String, Object>();
        			String couponId = (String)array.getString(i);
        			AfCouponDo afCouponDo = afCouponService.getCouponById(Long.parseLong(couponId));
        			if(afCouponDo == null) continue;
        			couponInfoMap.put("shopUrl", afCouponCategoryDo.getUrl());
        			couponInfoMap.put("couponId", afCouponDo.getRid());
        			couponInfoMap.put("name", afCouponDo.getName());
        			couponInfoMap.put("useRule", afCouponDo.getUseRule());
        			couponInfoMap.put("type", afCouponDo.getType());
        			couponInfoMap.put("amount", afCouponDo.getAmount());
        			couponInfoMap.put("useRange", afCouponDo.getUseRange());
        			couponInfoMap.put("limitAmount", afCouponDo.getLimitAmount());
        			Date gmtStart = afCouponDo.getGmtStart();
        			if( gmtStart != null){
        				couponInfoMap.put("gmtStart", gmtStart.getTime());
        			} else {
        				couponInfoMap.put("gmtStart", 0);
        			}
        			Date gmtEnd = afCouponDo.getGmtEnd();
        			if (gmtEnd != null) {
        				couponInfoMap.put("gmtEnd", gmtEnd.getTime());
        			} else {
        				couponInfoMap.put("gmtEnd", 0);
        			}
        			
        			couponInfoMap.put("currentTime", System.currentTimeMillis());
        			if (!context.isLogin()) {
        				couponInfoMap.put("isDraw", "Y");
        			} else {
        				// 获取用户信息
        				String userName = context.getUserName();
            			AfUserDo user = afUserService.getUserByUserName(userName);
            			// 判断是否领取优惠券
            			int userCouponCount = afUserCouponService.getUserCouponByUserIdAndCouponId(user.getRid(), Long.parseLong(couponId));
            			if(userCouponCount < afCouponDo.getLimitCount()){
            				couponInfoMap.put("isDraw", "Y");
            			} else {
            				couponInfoMap.put("isDraw", "N");
            			}
        			}
        			// 判断优惠券是否领完
        			Long quota = afCouponDo.getQuota();
        			Integer quotaAlready = afCouponDo.getQuotaAlready();
        			if(quota != 0 && quota != -1 && quota.intValue() <= quotaAlready.intValue()){
        				if(!context.isLogin()) {
        					couponInfoMap.put("isOver", "N");
        				} else {
        				 	couponInfoMap.put("isOver", "Y");
        				}
        			} else {
        				couponInfoMap.put("isOver", "N");
        			}
        			couponInfoList.add(couponInfoMap);
        			allCouponInfoList.add(new HashMap<String,Object>(couponInfoMap));
        		}
        		couponCategoryMap.put("couponInfoList", couponInfoList);
        		couponCategoryList.add(couponCategoryMap);
    		}
    		jsonObj.put("couponCategoryList", couponCategoryList);
        	return H5CommonResponse.getNewInstance(true, FanbeiExceptionCode.SUCCESS.getDesc(),"",jsonObj).toString(); 
    	} catch (Exception e){
    		e.printStackTrace();
    		return H5CommonResponse.getNewInstance(false, "请求失败，错误信息" + e.toString()).toString();
    	}
    	
    }
    
    @RequestMapping(value = "getMineCouponInfo", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
 	@ResponseBody
    public String getMineCouponInfo(HttpServletRequest request, ModelMap model) throws IOException {
    	try{
    		// FIXME
    		FanbeiWebContext context = doWebCheck(request, false);
    		context = doWebCheck(request, false);
    		JSONObject data = new JSONObject();
    		String userName = context.getUserName();
    		AfUserDo userDo = afUserService.getUserByUserName(userName);
			if(userDo == null) {
				String loginUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative + H5OpenNativeType.AppLogin.getCode();
				data.put("loginUrl", loginUrl);
			} else {
				Long userId = userDo.getRid();
				Integer pageNo = NumberUtil.objToIntDefault(request.getParameter("pageNo"), 1);
		        String status = ObjectUtils.toString(request.getParameter("status"));
		        
		        logger.info("userId=" + userId + ",pageNo=" + pageNo + ",status=" + status);
		        
		        AfUserCouponQuery query = new AfUserCouponQuery();
		        query.setPageNo(pageNo);
		        query.setUserId(userId);
		        query.setStatus(status);
		        List<AfUserCouponDto> couponList = afUserCouponService.getUserCouponByUser(query);
		        List<AfUserCouponVo> couponVoList = new ArrayList<AfUserCouponVo>();
		        for (AfUserCouponDto afUserCouponDto : couponList) {
		        	AfUserCouponVo couponVo = getUserCouponVo(afUserCouponDto);
		        	Date gmtEnd = couponVo.getGmtEnd();
		        	// 如果当前时间离到期时间小于48小时,则显示即将过期
		        	Calendar cal = Calendar.getInstance();
		        	cal.add(Calendar.DAY_OF_YEAR, EXPIRE_DAY);
		        	Date twoDay = cal.getTime();
		        	if(gmtEnd != null){
		        		if(twoDay.after(gmtEnd)) {
		            		couponVo.setWillExpireStatus("Y");
		            	} else {
		            		couponVo.setWillExpireStatus("N");
		            	}
		        	} else {
		        		couponVo.setWillExpireStatus("N");
		        	}
		        	// 查询优惠券所在分类
		        	List <AfCouponCategoryDo> couponCategoryList = afCouponCategoryService.getCouponCategoryByCouponId(afUserCouponDto.getCouponId());
		        	if(couponCategoryList != null && !couponCategoryList.isEmpty()) {
		        		logger.info("couponCategoryList info=>" + couponCategoryList.toString());
		        		AfCouponCategoryDo afCouponCategoryDo = couponCategoryList.get(0);
		        		String shopUrl = afCouponCategoryDo.getUrl();
		        		couponVo.setShopUrl(shopUrl);
		        	}
		        	couponVoList.add(couponVo);
				}
		        
		        data.put("pageNo", pageNo);
				data.put("couponList", couponVoList);
			}
    		return H5CommonResponse.getNewInstance(true, FanbeiExceptionCode.SUCCESS.getDesc(),"",data).toString(); 
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    		return H5CommonResponse.getNewInstance(false, "请求失败，错误信息" + e.toString()).toString();
    	}
    	
    	
    	
    }
    
    private AfUserCouponVo getUserCouponVo(AfUserCouponDto afUserCouponDto){
		AfUserCouponVo couponVo = new AfUserCouponVo();
		couponVo.setAmount(afUserCouponDto.getAmount());
		couponVo.setGmtEnd(afUserCouponDto.getGmtEnd());
		couponVo.setGmtStart(afUserCouponDto.getGmtStart());
		couponVo.setLimitAmount(afUserCouponDto.getLimitAmount());
		couponVo.setName(afUserCouponDto.getName());
		couponVo.setStatus(afUserCouponDto.getStatus());
		couponVo.setUseRule(afUserCouponDto.getUseRule());
		couponVo.setType(afUserCouponDto.getType());
		couponVo.setUseRange(afUserCouponDto.getUseRange());
		return couponVo;
	}
    
    @RequestMapping(value = "activityCouponInfo", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
    public String activityCouponInfo(HttpServletRequest request, ModelMap model) throws IOException {
    	try{
    		// FanbeiWebContext context = doWebCheck(request, false);
    		// context = doWebCheck(request, false);
    		JSONObject jsonObj = new JSONObject();
    		// 获取活动优惠券组信息
    		String groupId = ObjectUtils.toString(request.getParameter("groupId"), null).toString();
    		if(groupId == null) {
    			throw new FanbeiException("groupId can't be null or empty.");
    		}
    		// 根据Id获取分组优惠券
    		AfCouponCategoryDo couponCategory = afCouponCategoryService.getCouponCategoryById(groupId);
    		String coupons = couponCategory.getCoupons();
    		
    		JSONArray couponsArray = (JSONArray) JSONArray.parse(coupons);
    		for(int i = 0; i < couponsArray.size(); i++){
    			HashMap<String, Object> couponInfoMap = new HashMap<String, Object>();
    			String couponId = (String)couponsArray.getString(i);
    			AfCouponDo afCouponDo = afCouponService.getCouponById(Long.parseLong(couponId));
    			couponInfoMap.put("shopUrl", couponCategory.getUrl());
    			couponInfoMap.put("couponId", afCouponDo.getRid());
    			couponInfoMap.put("name", afCouponDo.getName());
    			couponInfoMap.put("useRule", afCouponDo.getUseRule());
    			couponInfoMap.put("type", afCouponDo.getType());
    			couponInfoMap.put("amount", afCouponDo.getAmount());
    			couponInfoMap.put("useRange", afCouponDo.getUseRange());
    			couponInfoMap.put("limitAmount", afCouponDo.getLimitAmount());
    			Date gmtStart = afCouponDo.getGmtStart();
    			if( gmtStart != null){
    				couponInfoMap.put("gmtStart", gmtStart.getTime());
    			} else {
    				couponInfoMap.put("gmtStart", 0);
    			}
    			Date gmtEnd = afCouponDo.getGmtEnd();
    			if (gmtEnd != null) {
    				couponInfoMap.put("gmtEnd", gmtEnd.getTime());
    			} else {
    				couponInfoMap.put("gmtEnd", 0);
    			}
    		}
    		return H5CommonResponse.getNewInstance(true, FanbeiExceptionCode.SUCCESS.getDesc(),"",jsonObj).toString(); 
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    		return H5CommonResponse.getNewInstance(false, "请求失败，错误信息" + e.toString()).toString();
    	}
    }
    
    
    @Override
    public String checkCommonParam(String reqData, HttpServletRequest request, boolean isForQQ) {
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
              throw new FanbeiException("参数格式错误"+e.getMessage(), FanbeiExceptionCode.REQUEST_PARAM_ERROR);
          }
    }
 
    @Override
    public BaseResponse doProcess(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest httpServletRequest) {
        return null;
    }
 
}