package com.ald.fanbei.api.web.api.auth;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

@Component("authFaceApi")
public class AuthFaceApi implements ApiHandle {
	
	private final static String  RESULT_AUTH_TRUE = "T";
//	private final static String  RESULT_AUTH_FALSE = "F";

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
//		String result = (String)requestDataVo.getParams().get("result");
		String resultAuth = (String)requestDataVo.getParams().get("resultAuth");
		
		if(StringUtil.equals(resultAuth, RESULT_AUTH_TRUE)){
			
		}
		
		return resp;
	}

}
