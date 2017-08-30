package com.ald.fanbei.api.web.api.system;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.third.util.TongdunUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.CommonUtil;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSONObject;


@Component("AppLaunchImageApi")
public class AppLaunchImageApi implements ApiHandle{

	private static final String RESOURCE_TYPE = "APP_LAUNCH_IMAGE";
	
	@Resource
	AfResourceService afResourceService;
	@Resource
	TongdunUtil tongdunUtil;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,
			FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse response = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
		String blackBox = ObjectUtils.toString(requestDataVo.getParams().get("blackBox"));
		String appVersion = context.getAppVersion()!=null?context.getAppVersion()+"":"";
		
		AfResourceDo resourceDo = afResourceService.getSingleResourceBytype(RESOURCE_TYPE);
		if(resourceDo == null){
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.FAILED);
		}
		JSONObject data = new JSONObject();
		data.put("imageUrl", resourceDo.getValue());
		data.put("advertiseUrl", resourceDo.getValue1());	

		response.setResponseData(data);
		
		//同盾处理
		try {
			if (StringUtils.isNotBlank(blackBox)) {
				tongdunUtil.activeOperate(request,requestDataVo.getId(), blackBox, CommonUtil.getIpAddr(request),appVersion, "","",true);
			}
		} catch (Exception e) {
			logger.error("appLaunchImageApi activeOperate error",e);
		}
		
		return response;
	}

}
