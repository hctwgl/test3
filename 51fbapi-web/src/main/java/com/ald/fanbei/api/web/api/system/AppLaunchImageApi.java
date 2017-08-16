package com.ald.fanbei.api.web.api.system;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
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
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,
			FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse response = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
	
		AfResourceDo resourceDo = afResourceService.getSingleResourceBytype(RESOURCE_TYPE);
		if(resourceDo == null){
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.FAILED);
		}
		JSONObject data = new JSONObject();
		data.put("imageUrl", resourceDo.getValue());
		data.put("advertiseUrl", resourceDo.getValue1());	

		response.setResponseData(data);
		return response;
	}

}
