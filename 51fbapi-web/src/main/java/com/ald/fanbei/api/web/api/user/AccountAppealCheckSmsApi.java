package com.ald.fanbei.api.web.api.user;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.ald.fanbei.api.biz.third.util.SmsUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.SmsType;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.dao.AfUserAppealLogDao;
import com.ald.fanbei.api.dal.domain.AfUserAppealLogDo;
import com.ald.fanbei.api.dal.domain.AfUserAppealLogDo.AfUserAppealLogStatusEnum;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * 账号申诉-验证新手机号
 * @author zhujiangfeng
 */

public class AccountAppealCheckSmsApi implements ApiHandle {

	@Resource
	SmsUtil smsUtil;
	
	@Resource
	AfUserAppealLogDao afUserAppealLogDao;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		Map<String, Object> params = requestDataVo.getParams();
		String smsCode = params.get("verifyCode").toString();
		String newMobile = params.get("newMobile").toString();
		
		if(StringUtils.isBlank(smsCode) || StringUtils.isBlank(newMobile) ) {
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.REQUEST_PARAM_ERROR);
		}
		
		smsUtil.checkSmsByMobileAndType(newMobile, smsCode, SmsType.MOBILE_BIND);
		
		afUserAppealLogDao.insert(AfUserAppealLogDo.generate(context.getUserId(), newMobile, AfUserAppealLogStatusEnum.ING));
		
		return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
	}

}
