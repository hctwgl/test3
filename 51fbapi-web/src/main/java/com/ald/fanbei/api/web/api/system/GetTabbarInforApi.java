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
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.YesNoStatus;
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
		Map<String, Object> index = new HashMap<String, Object>();
		for (AfResourceDo afResourceDo : tabbarlist) {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("title", afResourceDo.getName());
			data.put("imageUrl", afResourceDo.getValue());
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
				handleIosBorow(context,requestDataVo,data);
				index.put("borrowNomal", data);
			}
			if(StringUtils.equals(afResourceDo.getSecType(), "BORROW_SELECTED")){
				handleIosBorow(context,requestDataVo,data);
				index.put("borrowSelected", data);
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
	private void handleIosBorow(FanbeiContext context,RequestDataVo requestDataVo,Map<String, Object> data) {
		if(requestDataVo.getId().startsWith("i")){
			AfResourceDo resourceInfo = afResourceService.getSingleResourceBytype(Constants.RES_IS_FOR_AUTH);
			if(resourceInfo != null){
				String iosCheckVersion = resourceInfo.getValue();
	        	if (StringUtils.isNotBlank(iosCheckVersion)) {
	        		 Map<String, Object> params = requestDataVo.getParams();
	        	     String channelCode = ObjectUtils.toString(params.get("channelCode"), null);
	        		 List<CheckVersionBo> array = JSONArray.parseArray(iosCheckVersion, CheckVersionBo.class);
	        		 CheckVersionBo desVersion = new CheckVersionBo(channelCode, context.getAppVersion());
	        		 if(array.contains(desVersion)){
	        			data.put("title", "搜呗");
	        		 }
	        	}
			}
		}
	}

}
