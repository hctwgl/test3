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

import com.ald.fanbei.api.biz.service.AfCouponService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.AfCouponDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
 
/**
 * @类描述：
 * 返场活动
 * @author 江荣波 2017年6月20日下午1:41:05
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping("/fanbei-web")
public class AppH5GiftController extends BaseController {
	
	@Resource
	AfResourceService afResourceService;
	@Resource
	AfCouponService afCouponService;
    
    @RequestMapping(value = "newUserGift", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
    public String newUserGift(HttpServletRequest request, ModelMap model) throws IOException {
    	try{
    		FanbeiWebContext context = doWebCheck(request, false);
    		String userName = context.getUserName();
    		JSONObject jsonObj = new JSONObject();
    		if(userName != null) {
    			jsonObj.put("userName", userName);
    		}
    		// 获取优惠券配置信息
    		List<AfResourceDo> afResourceList = afResourceService.getConfigByTypes(AfResourceType.NewUserCouponGift.getCode());
    		if(afResourceList == null || afResourceList.isEmpty()) {
    			return H5CommonResponse.getNewInstance(false, "请配置新手礼包优惠券信息").toString();
    		}
    		AfResourceDo afResourceDo = afResourceList.get(0);
    		String couponIdValue = afResourceDo.getValue();
    		String[] couponIdAndTypes = couponIdValue.split(",");
    		List<HashMap<String,Object>> couponInfoList = new ArrayList<HashMap<String,Object>>();
    		for(String couponIdAndType : couponIdAndTypes) {
    			String[] coupontInfos = couponIdAndType.split(":");
    			HashMap<String,Object> couponInfoMap = new HashMap<String,Object>();
    			if(coupontInfos.length == 1){
    				String couponId = coupontInfos[0];
        			// 查询优惠券信息
        			AfCouponDo afCouponDo = afCouponService.getCouponById(Long.parseLong(couponId));
        			if(afCouponDo == null){
        				return H5CommonResponse.getNewInstance(false, "优惠券信息不存在:id=>" + couponId).toString();
        			}
        			String name = afCouponDo.getName(); 
        			String[] couponInfos = name.split("元");
        			couponInfoMap.put("name", couponInfos[0]);
        			couponInfoMap.put("couponId", couponId);
        			couponInfoMap.put("type", "1");
        			couponInfoMap.put("desc", afCouponDo.getUseRule());
        			couponInfoMap.put("remark", "全场券");
    			} else {
    				String couponId = coupontInfos[0];
        			String couponType = coupontInfos[1];
        			AfResourceDo resourceDo = afResourceService.getResourceByResourceId(Long.parseLong(couponId));
        			String name = resourceDo.getName();
        			couponInfoMap.put("couponId", couponId);
        			String[] couponInfos = name.split("元");
        			couponInfoMap.put("name", couponInfos[0]);
        			couponInfoMap.put("type", "2");
        			couponInfoMap.put("desc", afResourceDo.getValue1());
        			couponInfoMap.put("remark", afResourceDo.getValue2());
    			}
    			
    			couponInfoList.add(couponInfoMap);
    		}
    		jsonObj.put("couponInfoList", couponInfoList);
        	return H5CommonResponse.getNewInstance(true, FanbeiExceptionCode.SUCCESS.getDesc(),"",jsonObj).toString(); 
    	} catch (Exception e){
    		return H5CommonResponse.getNewInstance(false, "请求失败，错误信息" + e.toString()).toString();
    	}
    	
    }
    
    @RequestMapping(value = "superCouponList", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
    public String superCouponList(HttpServletRequest request, ModelMap model) throws IOException {
    	try{
    		FanbeiWebContext context = doWebCheck(request, false);
    		String userName = context.getUserName();
    		JSONObject jsonObj = new JSONObject();
    		if(userName != null) {
    			jsonObj.put("userName", userName);
    		}
    		
    		List<AfResourceDo> afResourceList = afResourceService.getConfigByTypes(AfResourceType.SuperCouponGift.getCode());
    		if(afResourceList == null || afResourceList.isEmpty()) {
    			return H5CommonResponse.getNewInstance(false, "").toString();
    		}
    		AfResourceDo afResourceDo = afResourceList.get(0);
    		String couponIdValue = afResourceDo.getValue();
    		String[] couponIds = couponIdValue.split(",");
    		List<HashMap<String,Object>> couponInfoList = new ArrayList<HashMap<String,Object>>();
    		for(String couponId : couponIds) {
    			HashMap<String,Object> couponInfoMap = new HashMap<String,Object>();
    			// 查询优惠券信息
    			AfCouponDo afCouponDo = afCouponService.getCouponById(Long.parseLong(couponId));
    			if(afCouponDo == null){
    				return H5CommonResponse.getNewInstance(false, "优惠券信息不存在:id=>" + couponId).toString();
    			}
    			String name = afCouponDo.getName(); 
    			couponInfoMap.put("name", name);
    			couponInfoMap.put("couponId", couponId);
    			couponInfoMap.put("type", "1");
    			couponInfoMap.put("desc", afCouponDo.getUseRule());
    			couponInfoMap.put("remark", "全场通用");
    			couponInfoList.add(couponInfoMap);
    		}
    		jsonObj.put("couponInfoList", couponInfoList);
        	return H5CommonResponse.getNewInstance(true, FanbeiExceptionCode.SUCCESS.getDesc(),"",jsonObj).toString(); 
    	} catch (Exception e){
    		return H5CommonResponse.getNewInstance(false, "请求失败，错误信息" + e.toString()).toString();
    	}
    }
    
    @RequestMapping(value = "activityCouponList", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
    public String activityCouponList(HttpServletRequest request, ModelMap model) throws IOException {
    	try{
    		FanbeiWebContext context = doWebCheck(request, false);
    		String userName = context.getUserName();
    		JSONObject jsonObj = new JSONObject();
    		if(userName != null) {
    			jsonObj.put("userName", userName);
    		}
    		List<AfResourceDo> afResourceList = afResourceService.getConfigByTypes(AfResourceType.ActivityCouponGift.getCode());
    		if(afResourceList == null || afResourceList.isEmpty()) {
    			return H5CommonResponse.getNewInstance(false, "").toString();
    		}
    		AfResourceDo afResourceDo = afResourceList.get(0);
    		String couponIdValue = afResourceDo.getValue();
    		String[] couponIds = couponIdValue.split(",");
    		List<HashMap<String,Object>> couponInfoList = new ArrayList<HashMap<String,Object>>();
    		for(String couponId : couponIds) {
    			HashMap<String,Object> couponInfoMap = new HashMap<String,Object>();
    			// 查询优惠券信息
    			AfCouponDo afCouponDo = afCouponService.getCouponById(Long.parseLong(couponId));
    			if(afCouponDo == null){
    				return H5CommonResponse.getNewInstance(false, "优惠券信息不存在:id=>" + couponId).toString();
    			}
    			String name = afCouponDo.getName(); 
    			couponInfoMap.put("name", name);
    			couponInfoMap.put("amount", afCouponDo.getAmount());
    			couponInfoMap.put("limitAmount", afCouponDo.getLimitAmount());
    			couponInfoMap.put("couponId", couponId);
    			couponInfoMap.put("type", "1");
    			couponInfoMap.put("desc", afCouponDo.getUseRule());
    			couponInfoMap.put("remark", "全场通用");
    			couponInfoList.add(couponInfoMap);
    		}
    		jsonObj.put("couponInfoList", couponInfoList);
        	return H5CommonResponse.getNewInstance(true, FanbeiExceptionCode.SUCCESS.getDesc(),"",jsonObj).toString(); 
    	} catch (Exception e){
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