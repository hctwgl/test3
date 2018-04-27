package com.ald.fanbei.api.web.api.system;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.util.DateUtil;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.CheckVersionBo;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfResourceSecType;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSONArray;


@Component("getTabbarInforApi")
public class GetTabbarInforApi implements ApiHandle {

	@Resource
	AfResourceService afResourceService;

	@Resource
	AfOrderService afOrderService;
	@Resource
	BizCacheUtil bizCacheUtil;
	
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,
			FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		//4.1.3之前的版本用原先的，之后的用新的
		List<AfResourceDo> resourceList = new  ArrayList<AfResourceDo>();
		Integer appVersion = context.getAppVersion();
		if (appVersion < 413) {
			resourceList = afResourceService.getResourceListByTypeOrderBy(AfResourceType.HomeTabbar.getCode());
		} else{
			resourceList = afResourceService.getResourceListByTypeOrderBy(AfResourceType.ASJHomeTabbar.getCode());
		}
	
		
		Map<String, Object> tabbarInfor= getObjectWithResourceDolist(context,resourceList,requestDataVo);
		//resp.setResponseData(tabbarInfor);

		//查询用户登录后未支付订单数
		Long userId = context.getUserId();
		if(userId != null){
			//查看redis中是否有登录时间,有说明不是第一次登录,判断与上次登录时间是否是同一天
			String getTime = (String)bizCacheUtil.getObject(Constants.NO_FINISH_ORDER + userId);
			//记录用户当前登录时间
			String nowLogin = DateUtil.getShortNow(new Date());
			//不是第一次登录
			if(getTime != null){
				//当前登录时间和redis中存储的时间是否是同一天
				if(!nowLogin.equals(getTime)){
					//根据用户id查询用户未支付订单数
					int noFinishOrderCount = afOrderService.getNoFinishOrderCount(userId);
					bizCacheUtil.saveObject(Constants.NO_FINISH_ORDER+userId,nowLogin,Constants.SECOND_OF_ONE_DAY);
					tabbarInfor.put("noFinishOrderCount",noFinishOrderCount);
					resp.setResponseData(tabbarInfor);
					return resp;
				}else {
					tabbarInfor.put("noFinishOrderCount",0);
					if("Y".equals((String)bizCacheUtil.getObject(Constants.FIRST_TIME + userId))){
						bizCacheUtil.saveObject(Constants.FIRST_TIME+userId,nowLogin,0);
						int noFinishOrderCount = afOrderService.getNoFinishOrderCount(userId);
						tabbarInfor.put("noFinishOrderCount",noFinishOrderCount);
					}
					//bizCacheUtil.saveObject(Constants.NO_FINISH_ORDER+userId,nowLogin,0);
					resp.setResponseData(tabbarInfor);
					return resp;
				}
			}
			//第一次登录
			//记录用户当前登录时间,存到redis中
			bizCacheUtil.saveObject(Constants.NO_FINISH_ORDER+userId,nowLogin,Constants.SECOND_OF_ONE_DAY);
			//根据用户id查询用户未支付订单数
			int noFinishOrderCount = afOrderService.getNoFinishOrderCount(userId);
			if(noFinishOrderCount == 0){
				bizCacheUtil.saveObject(Constants.FIRST_TIME+userId,"Y",Constants.SECOND_OF_ONE_DAY);
			}
			tabbarInfor.put("noFinishOrderCount",noFinishOrderCount);
			resp.setResponseData(tabbarInfor);
			return resp;
		}
		tabbarInfor.put("noFinishOrderCount",0);
		resp.setResponseData(tabbarInfor);
		return resp;
	}
	
	private Map<String, Object> getObjectWithResourceDolist(FanbeiContext context,List<AfResourceDo> tabbarlist,RequestDataVo requestDataVo) {
		//是否使用后台传来的图片，否的话菜单图片用app本地图片
		Integer appVersion = context.getAppVersion();
		
		AfResourceDo useImgDo = new AfResourceDo();
		if (appVersion >= 413) {
			useImgDo = afResourceService
					.getConfigByTypesAndSecType(AfResourceType.ASJ_IS_USE_IMG.getCode(), AfResourceSecType.ASJ_IS_USE_IMG.getCode());
		}else{
			useImgDo = afResourceService
					.getConfigByTypesAndSecType(AfResourceType.IS_USE_IMG.getCode(), AfResourceSecType.IS_USE_IMG.getCode());
		}
		
		//app 在appstore 审核信息，如果还未审核，则用后台传的图片
		AfResourceDo resourceInfo = afResourceService.getSingleResourceBytype(Constants.RES_IS_FOR_AUTH);
		boolean use = this.useImg(useImgDo, requestDataVo, context);
		Map<String, Object> index = new HashMap<String, Object>();
		for (AfResourceDo afResourceDo : tabbarlist) {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("title", afResourceDo.getName());
			if(use){
			data.put("imageUrl", afResourceDo.getValue());
			}
			data.put("titleColor", afResourceDo.getValue1());
			//兼容原版本
//			if (appVersion >= 413) {
//			 data.put("content", afResourceDo.getValue3());
//			}
			if(StringUtils.equals(afResourceDo.getSecType(), "HOME_NOMAL")){
				index.put("homeNomal", data);
			}
			if(StringUtils.equals(afResourceDo.getSecType(), "HOME_SELECTED")){
				index.put("homeSelected", data);
			}
			if(StringUtils.equals(afResourceDo.getSecType(), "BRAND_NOMAL")){
				index.put("brandNomal", data);
			}
			if(StringUtils.equals(afResourceDo.getSecType(), "BRAND_SELECTED")){
				index.put("brandSelected", data);
			}
			if(StringUtils.equals(afResourceDo.getSecType(), "STAGING_NOMAL")){
					handleIosStaging(context,requestDataVo,data,resourceInfo);
				index.put("stagingNomal", data);
			}
			if(StringUtils.equals(afResourceDo.getSecType(), "STAGING_SELECTED")){
				handleIosStaging(context,requestDataVo,data,resourceInfo);
				index.put("stagingSelected", data);
			}
			if(StringUtils.equals(afResourceDo.getSecType(), "MAIN_NOMAL")){
				index.put("mainNomal", data);
			}
			if(StringUtils.equals(afResourceDo.getSecType(), "MAIN_SELECTED")){
				index.put("mainSelected", data);
			}
			if(StringUtils.equals(afResourceDo.getSecType(), "BORROW_NOMAL")){
				handleIosBorow(context,requestDataVo,data,resourceInfo);
				index.put("borrowNomal", data);
			}
			if(StringUtils.equals(afResourceDo.getSecType(), "BORROW_SELECTED")){
				handleIosBorow(context,requestDataVo,data,resourceInfo);
				index.put("borrowSelected", data);
			}
			if(StringUtils.equals(afResourceDo.getSecType(), "ASJ_OPERATION_POSITION")){
				data.put("content", afResourceDo.getValue3());
				data.put("status", afResourceDo.getValue2());
				index.put("operationPositionNomal", data);
			}
			if(StringUtils.equals(afResourceDo.getSecType(), "ASJ_OPERATION_POSITION_SELECTED")){
				index.put("operationPositionSelected", data);
			}
			if(StringUtils.equals(afResourceDo.getSecType(), "ASJ_LIFE")){
				index.put("asjLifeNomal", data);
			}
			if(StringUtils.equals(afResourceDo.getSecType(), "ASJ_LIFE_SELECTED")){
				index.put("asjLifeSelected", data);
			}
			if(StringUtils.equals(afResourceDo.getSecType(), "ASJ_FIND")){
				index.put("asjFindNomal", data);
			}
			if(StringUtils.equals(afResourceDo.getSecType(), "ASJ_FIND_SELECTED")){
				index.put("asjFindSelected", data);
			}
			if(StringUtils.equals(afResourceDo.getSecType(), "ASJ_HOME_NOMAL")){
				index.put("asjHomeNomal", data);
			}
			if(StringUtils.equals(afResourceDo.getSecType(), "ASJ_HOME_SELECTED")){
				index.put("asjHomeSelected", data);
			}
			if(StringUtils.equals(afResourceDo.getSecType(), "ASJ_MAIN_NOMAL")){
				index.put("asjMainNomal", data);
			}
			if(StringUtils.equals(afResourceDo.getSecType(), "ASJ_MAIN_SELECTED")){
				index.put("asjMainSelected", data);
			}
			if(StringUtils.equals(afResourceDo.getSecType(), "BORROW_HIGHLIGHT")){
				handleIosBorow(context,requestDataVo,data,resourceInfo);
				index.put("borrowHighLight", data);
			}
			
		}

		return index;
	}
	/**
	 * 针对IOS 在appStore审核中时，处理底部tab借钱模块为搜呗
	 * @param context
	 * @param requestDataVo
	 * @param data
	 */
	private void handleIosBorow(FanbeiContext context,RequestDataVo requestDataVo,Map<String, Object> data,AfResourceDo resourceInfo) {
		
		 Map<String, Object> params = requestDataVo.getParams();
	        String channelCode = ObjectUtils.toString(params.get("channelCode"), null);
	        if (resourceInfo == null) {
	        	return;
	        } 
		 //需要打开为了审核的相关版本
        //VALUE是为了IOS审核
        if(requestDataVo.getId().startsWith("i")) {
        	String iosCheckVersion = resourceInfo.getValue();
        	if (!StringUtils.isBlank(iosCheckVersion)) {
        		List<CheckVersionBo> array = JSONArray.parseArray(iosCheckVersion, CheckVersionBo.class);
        		CheckVersionBo desVersion = new CheckVersionBo(channelCode, context.getAppVersion());
        		data.put("title", array.contains(desVersion) ? "搜呗" : data.get("title"));
        	}
        } else {
        //VALUE2是为了Android审核
        	String androidCheckVersion = resourceInfo.getValue2();
        	if (!StringUtils.isBlank(androidCheckVersion)) {
        		List<CheckVersionBo> array = JSONArray.parseArray(androidCheckVersion, CheckVersionBo.class);
        		CheckVersionBo desVersion = new CheckVersionBo(channelCode, context.getAppVersion());
        		data.put("title", array.contains(desVersion) ? "搜呗" : data.get("title"));
        	}
        }
	}

	private void handleIosStaging(FanbeiContext context,RequestDataVo requestDataVo,Map<String, Object> data,AfResourceDo resourceInfo) {

		Map<String, Object> params = requestDataVo.getParams();
		String channelCode = ObjectUtils.toString(params.get("channelCode"), null);
		if (resourceInfo == null) {
        	return;
        } 
		//需要打开为了审核的相关版本
		//VALUE是为了IOS审核
		if(requestDataVo.getId().startsWith("i")) {
			String iosCheckVersion = resourceInfo.getValue();
			if (!StringUtils.isBlank(iosCheckVersion)) {
				List<CheckVersionBo> array = JSONArray.parseArray(iosCheckVersion, CheckVersionBo.class);
				CheckVersionBo desVersion = new CheckVersionBo(channelCode, context.getAppVersion());
				data.put("title", array.contains(desVersion) ? "服务" : data.get("title"));
			}
		} else {
			//VALUE2是为了Android审核
			String androidCheckVersion = resourceInfo.getValue2();
			if (!StringUtils.isBlank(androidCheckVersion)) {List<CheckVersionBo> array = JSONArray.parseArray(androidCheckVersion, CheckVersionBo.class);
				CheckVersionBo desVersion = new CheckVersionBo(channelCode, context.getAppVersion());
				data.put("title", array.contains(desVersion) ? "服务" : data.get("title"));
			}
		}
	}
	
	/**
	 * 是否使用后台传来的图片做菜单栏的图片
	 * @param useImgDo 是否使用后台传图片
	 * @return true:用后台的 ，false:用app本地的
	 */
	private boolean useImg(AfResourceDo useImgDo,RequestDataVo requestDataVo,FanbeiContext context){
		boolean use = true;
		if(useImgDo!=null){
			 use = "1".equals(useImgDo.getValue())?true:false;
		}
		return use;
	}

}
