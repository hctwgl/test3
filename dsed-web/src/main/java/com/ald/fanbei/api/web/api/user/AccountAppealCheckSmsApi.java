package com.ald.fanbei.api.web.api.user;

import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.third.util.SmsUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.SmsType;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.dal.dao.AfUserAppealLogDao;
import com.ald.fanbei.api.dal.domain.AfUserAppealLogDo;
import com.ald.fanbei.api.dal.domain.AfUserAppealLogDo.AfUserAppealLogStatusEnum;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * 账号申诉-验证新手机号
 * 注：免登
 * @author zhujiangfeng
 */

@Component("accountAppealCheckSmsApi")
public class AccountAppealCheckSmsApi implements ApiHandle {

	@Resource
	SmsUtil smsUtil;
	@Resource
	BizCacheUtil bizCacheUtil;
	
	@Resource
	AfUserAppealLogDao afUserAppealLogDao;
	
	@Resource
	AfUserService afUserService;
	@Resource
	AfUserAuthService afUserAuthService;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		Map<String, Object> params = requestDataVo.getParams();
		String smsCode = params.get("verifyCode").toString();
		String oldMobile = params.get("oldMobile").toString();
		String newMobile = params.get("newMobile").toString();
		
		if(StringUtils.isBlank(smsCode) || StringUtils.isBlank(newMobile) || StringUtils.isBlank(oldMobile)) {
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.REQUEST_PARAM_ERROR);
		}
		
		// 校验旧手机号是否为系统有效账号
		AfUserDo oldUserDo = afUserService.getUserByUserName(oldMobile);
		if(oldUserDo == null) {
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_INVALID_MOBILE_NO);
		}
		
		// 检查该用户是否实名认证过
		AfUserAuthDo authDo = afUserAuthService.getUserAuthInfoByUserId(oldUserDo.getRid());
		if(YesNoStatus.NO.getCode().equals(authDo.getRealnameStatus())) {
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_CARD_INFO_EXIST_ERROR);
		}
		
		// 校验新手机号是否已经注册
		AfUserDo newUserDo = afUserService.getUserByUserName(newMobile);
        if(newUserDo != null) {
        	return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_HAS_REGIST_ERROR);
        }
		
		smsUtil.checkSmsByMobileAndType(newMobile, smsCode, SmsType.MOBILE_BIND);
		
		// 短信校验成功以后
		afUserAppealLogDao.insert(AfUserAppealLogDo.generate(oldUserDo.getRid(), oldMobile, newMobile, AfUserAppealLogStatusEnum.ING));
		// 前置条件全部通过，以oldMobile为key缓存newMobile+uid，有效期一天
		bizCacheUtil.hset(Constants.CACHEKEY_REAL_AUTH_MOBILE_INFO, oldMobile, newMobile+","+oldUserDo.getRid(), DateUtil.getEndOfDate(new Date()));
		
		return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
	}

}
