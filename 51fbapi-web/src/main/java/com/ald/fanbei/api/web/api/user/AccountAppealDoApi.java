package com.ald.fanbei.api.web.api.user;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.dao.AfUserAppealLogDao;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAppealLogDo;
import com.ald.fanbei.api.dal.domain.AfUserAppealLogDo.AfUserAppealLogStatusEnum;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * 账号申诉-申诉并判定是否申诉成功
 * @author zhujiangfeng
 */
@Component("accountAppealDoApi")
public class AccountAppealDoApi implements ApiHandle {
	
	@Resource
	BizCacheUtil bizCacheUtil;
	
	@Resource
	AfUserAppealLogDao afUserAppealLogDao;
	@Resource
	AfUserAccountService afUserAccountService;
	@Resource
	AfUserService afUserService;
	
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		Map<String, Object> params = requestDataVo.getParams();
		final String password = params.get("password").toString();
		final String oldMobile = params.get("oldMobile").toString();
		
		if(StringUtils.isBlank(password) || StringUtils.isBlank(oldMobile)) {
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.REQUEST_PARAM_ERROR);
		}
		
		String mobileInfoStr = bizCacheUtil.hget(Constants.CACHEKEY_REAL_AUTH_MOBILE_INFO, oldMobile);
		logger.info("AccountAppealDoApi,getMobileInfo from cache,info=" + mobileInfoStr);
		final Long userId = Long.valueOf(mobileInfoStr.split(",")[1]);
		
		AfUserAppealLogDo appealLog = afUserAppealLogDao.getLatestByUserId(userId);
		if(appealLog == null) {return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.REQUEST_PARAM_SYSTEM_NOT_EXIST);}
		
		final String newMobile = appealLog.getNewMobile();
		String citizen_id = bizCacheUtil.getObject(Constants.CACHEKEY_REAL_AUTH_CITIZEN_CARD_PREFFIX + oldMobile).toString();
		String realname = bizCacheUtil.getObject(Constants.CACHEKEY_REAL_AUTH_REAL_NAME_PREFFIX + oldMobile).toString();
		Object facePassFlag = bizCacheUtil.getObject(Constants.CACHEKEY_REAL_AUTH_PASS_PREFFIX + oldMobile);
		logger.info("AccountAppealDoApi,userName=" + oldMobile + ",newMobile=" + newMobile + ",citizenId=" + citizen_id + ",realName=" + realname + ",faccePassFlag" + facePassFlag);
		appealLog.setRealName(realname);
		appealLog.setCitizenId(citizen_id);
		
		AfUserAccountDo userAccount = afUserAccountService.getUserAccountByUserId(userId);
		if(facePassFlag == null || !realname.equals(userAccount.getRealName()) || !citizen_id.equals(userAccount.getIdNumber())) {
			appealLog.setStatus(AfUserAppealLogStatusEnum.FAIL.name());
			appealLog.setMsg(FanbeiExceptionCode.USER_CARD_INFO_ATYPISM_ERROR.getErrorMsg());
			afUserAppealLogDao.update(appealLog);
			
			clearReferCache(oldMobile);//操作失败也清理所有缓存，强制流程从头开始
			
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_CARD_INFO_ATYPISM_ERROR);
		}
		
		//更改用户手机号和登陆密码
		afUserService.updateUserCoreInfo(userId, newMobile, password);
		clearReferCache(oldMobile);
		
		try {
			appealLog.setStatus(AfUserAppealLogStatusEnum.SUCCESS.name());
			afUserAppealLogDao.update(appealLog);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		
		return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
	}
	
	private void clearReferCache(String oldMobile) {
		bizCacheUtil.delCache(Constants.CACHEKEY_REAL_AUTH_CITIZEN_CARD_PREFFIX + oldMobile);
		bizCacheUtil.delCache(Constants.CACHEKEY_REAL_AUTH_REAL_NAME_PREFFIX + oldMobile);
		bizCacheUtil.delCache(Constants.CACHEKEY_REAL_AUTH_PASS_PREFFIX + oldMobile);
		bizCacheUtil.hdel(Constants.CACHEKEY_REAL_AUTH_MOBILE_INFO, oldMobile);
	}

}
