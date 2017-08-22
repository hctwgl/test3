package com.ald.fanbei.api.web.api.system;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

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
	
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,
			FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		List<AfResourceDo> resourceList = afResourceService.getResourceListByTypeOrderBy(AfResourceType.HomeTabbar.getCode());
		Map<String, Object> tabbarInfor= getObjectWithResourceDolist(context,resourceList,requestDataVo);
		resp.setResponseData(tabbarInfor);
		return resp;
	}
	
	private Map<String, Object> getObjectWithResourceDolist(FanbeiContext context,List<AfResourceDo> tabbarlist,RequestDataVo requestDataVo) {
		//是否使用后台传来的图片，否的话菜单图片用app本地图片
		AfResourceDo useImgDo = afResourceService
				.getConfigByTypesAndSecType(AfResourceType.IS_USE_IMG.getCode(), AfResourceSecType.IS_USE_IMG.getCode());
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
				index.put("stagingNomal", data);
			}
			if(StringUtils.equals(afResourceDo.getSecType(), "STAGING_SELECTED")){
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
	        	data.put("title", "借钱");
	        } 
		 //需要打开为了审核的相关版本
        //VALUE是为了IOS审核
        if(requestDataVo.getId().startsWith("i")) {
        	String iosCheckVersion = resourceInfo.getValue();
        	if (StringUtils.isBlank(iosCheckVersion)) {
        		data.put("title", "借钱");
        	} else {
        		List<CheckVersionBo> array = JSONArray.parseArray(iosCheckVersion, CheckVersionBo.class);
        		CheckVersionBo desVersion = new CheckVersionBo(channelCode, context.getAppVersion());
        		data.put("title", array.contains(desVersion) ? "搜呗" : "借钱");
        	}
        } else {
        //VALUE2是为了Android审核
        	String androidCheckVersion = resourceInfo.getValue2();
        	if (StringUtils.isBlank(androidCheckVersion)) {
        		data.put("title", "借钱");
        	} else {
        		List<CheckVersionBo> array = JSONArray.parseArray(androidCheckVersion, CheckVersionBo.class);
        		CheckVersionBo desVersion = new CheckVersionBo(channelCode, context.getAppVersion());
        		data.put("title", array.contains(desVersion) ? "搜呗" : "借钱");
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
