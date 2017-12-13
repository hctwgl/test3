package com.ald.fanbei.api.web.api.auth;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * 
 * @类描述： 获取是人脸识别SDK类型 或者为依图或者为face++
 * @author zjf
 */
@Component("getFaceTypeFreeApi")
public class GetFaceTypeFreeApi implements ApiHandle {

	@Resource
	GetFaceTypeApi getFaceTypeApi;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		return getFaceTypeApi.process(requestDataVo, context, request);
	}

}
