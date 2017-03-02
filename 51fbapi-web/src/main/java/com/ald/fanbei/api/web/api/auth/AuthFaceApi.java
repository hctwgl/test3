package com.ald.fanbei.api.web.api.auth;

import java.math.BigDecimal;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfAuthYdService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfAuthYdDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSONObject;
/**
 * 
 *@类现描述：人脸识别认证结果，app端做完人脸识别之后把结果发给服务端
 *
 *@author chenjinhu 2017年2月15日 下午3:34:46
 *@version 2.0 重构版本
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("authFaceApi")
public class AuthFaceApi implements ApiHandle {
	
	private final static String  RESULT_AUTH_TRUE = "T";
	
	@Resource
	AfUserAuthService afUserAuthService;
	@Resource
	AfAuthYdService afAuthYdService;
	

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
		String result = (String)requestDataVo.getParams().get("result");
		String resultAuth = (String)requestDataVo.getParams().get("resultAuth");
		
		if(StringUtil.equals(resultAuth, RESULT_AUTH_TRUE)){
			AfUserAuthDo userAuth = new AfUserAuthDo();
			userAuth.setUserId(context.getUserId());
			userAuth.setYdStatus(YesNoStatus.YES.getCode());
			userAuth.setFacesStatus(YesNoStatus.YES.getCode());
			userAuth.setSimilarDegree(new BigDecimal((String)JSONObject.parseObject(result).get("be_idcard")));
			afUserAuthService.updateUserAuth(userAuth);
		}
		
		AfAuthYdDo afAuthYdDo = new AfAuthYdDo();
		afAuthYdDo.setType("FACE_APP");
		afAuthYdDo.setUserId(context.getUserId());
		afAuthYdDo.setAuthParam("");
		afAuthYdDo.setAuthResult(result);
		afAuthYdService.addAuthYd(afAuthYdDo);
		
		return resp;
	}

}
