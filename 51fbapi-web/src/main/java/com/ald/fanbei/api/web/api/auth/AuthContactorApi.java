package com.ald.fanbei.api.web.api.auth;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 *@类现描述：紧急联系人api
 *@author hexin 2017年3月28日 下午14:48:44
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("authContactorApi")
public class AuthContactorApi implements ApiHandle {

	@Resource
	AfUserAuthService afUserAuthService;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
		String contactorName = ObjectUtils.toString(requestDataVo.getParams().get("contactorName"));
		String contactorMobile = ObjectUtils.toString(requestDataVo.getParams().get("contactorMobile"));
		String contactorType = ObjectUtils.toString(requestDataVo.getParams().get("contactorType"));
		if(StringUtil.isBlank(contactorName)||StringUtil.isBlank(contactorMobile)||StringUtil.isBlank(contactorType)){
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PARAM_ERROR);
		}
		AfUserAuthDo authDo = new AfUserAuthDo();
		authDo.setContactorStatus(YesNoStatus.YES.getCode());
		authDo.setContactorName(contactorName);
		authDo.setContactorMobile(contactorMobile);
		authDo.setContactorType(contactorType);
		authDo.setUserId(context.getUserId());
		if(afUserAuthService.updateUserAuth(authDo)>0){
			resp.addResponseData("allowConsume",afUserAuthService.getConsumeStatus(context.getUserId(),context.getAppVersion()));
			return resp;
		}
		throw new FanbeiException(FanbeiExceptionCode.FAILED);
	}

}
