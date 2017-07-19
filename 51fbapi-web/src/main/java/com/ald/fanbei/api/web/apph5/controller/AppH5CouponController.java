package com.ald.fanbei.api.web.apph5.controller;
 
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

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
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.enums.ResourceType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.AfCouponCategoryDo;
import com.ald.fanbei.api.dal.domain.AfCouponDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
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
    			jsonObj.put("bannerImage", bannerImage);
    			jsonObj.put("bannerUrl", bannerUrl);
    		}
    		// 查询所有优惠券分类
    		List<AfCouponCategoryDo> afCouponCategoryList = afCouponCategoryService.listAllCouponCategory();
    		List <Map<String,Object>> couponCategoryList = new ArrayList<Map<String,Object>>();
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
        			couponInfoMap.put("shopUrl", afCouponCategoryDo.getUrl());
        			couponInfoMap.put("couponId", afCouponDo.getRid());
        			couponInfoMap.put("name", afCouponDo.getName());
        			couponInfoMap.put("useRule", afCouponDo.getUseRule());
        			couponInfoMap.put("type", afCouponDo.getType());
        			couponInfoMap.put("amount", afCouponDo.getAmount());
        			couponInfoMap.put("useRange", afCouponDo.getUseRange());
        			couponInfoMap.put("gmtStart", afCouponDo.getGmtStart().getTime());
        			couponInfoMap.put("gmtEnd", afCouponDo.getGmtEnd().getTime());
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
        				if(context.isLogin() && "N".equals(couponInfoMap.get("isDraw"))) {
        					couponInfoMap.put("isOver", "N");
        				} else {
        					couponInfoMap.put("isOver", "Y");
        				}
        			} else {
        				couponInfoMap.put("isOver", "N");
        			}
        			couponInfoList.add(couponInfoMap);
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
    public String doProcess(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest httpServletRequest) {
        return null;
    }
 
}