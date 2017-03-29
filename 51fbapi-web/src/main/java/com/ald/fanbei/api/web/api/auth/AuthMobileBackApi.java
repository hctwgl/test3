package com.ald.fanbei.api.web.api.auth;

import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.MobileStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 *@类现描述：手机运营商认证返回
 *@author hexin 2017年3月24日 下午18:11:42
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("authMobileBackApi")
public class AuthMobileBackApi implements ApiHandle {

	@Resource
	private AfUserAuthService afUserAuthService;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
		AfUserAuthDo authDo = new AfUserAuthDo();
		authDo.setUserId(context.getUserId());
		authDo.setGmtMobile(new Date());
		authDo.setMobileStatus(MobileStatus.WAIT.getCode());
		if(afUserAuthService.updateUserAuth(authDo)>0){
			return resp;
		}
		throw new FanbeiException(FanbeiExceptionCode.FAILED);
	}

}
