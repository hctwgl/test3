package com.ald.fanbei.api.web.api.user;

import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.ald.fanbei.api.biz.service.*;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.util.security.Credential.MD5;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.TokenBo;
import com.ald.fanbei.api.biz.third.util.TongdunUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.biz.util.TokenCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.UserStatus;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.CommonUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.UserUtil;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.AfUserLoginLogDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.AfUserVo;
import com.alibaba.fastjson.JSONObject;

/**
 * 
 * @类描述：
 * 
 * @author Xiaotianjian 2017年1月19日下午1:48:59
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("loginApi")
public class LoginApi implements ApiHandle {

	@Resource
	AfUserService afUserService;
	@Resource
	AfUserLoginLogService afUserLoginLogService;
	@Resource
	TokenCacheUtil tokenCacheUtil;
	@Resource
	AfResourceService afResourceService;
	@Resource
	AfUserAccountService afUserAccountService;
	@Resource
	AfUserAuthService afUserAuthService;
	@Resource
	AfGameChanceService afGameChanceService;
	@Resource
	TongdunUtil tongdunUtil;
	@Resource
	JpushService jpushService;
	@Resource
	BizCacheUtil bizCacheUtil;
	
	

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		String userName = context.getUserName();
		String osType = ObjectUtils.toString(requestDataVo.getParams().get("osType"));
		String phoneType = ObjectUtils.toString(requestDataVo.getParams().get("phoneType"));
		String uuid = ObjectUtils.toString(requestDataVo.getParams().get("uuid"));
		String ip = CommonUtil.getIpAddr(request);

		String inputPassSrc = ObjectUtils.toString(requestDataVo.getParams().get("password"));
		String blackBox = ObjectUtils.toString(requestDataVo.getParams().get("blackBox"));
		
		if (StringUtils.isBlank(inputPassSrc)) {
			logger.error("inputPassSrc can't be empty");
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PARAM_ERROR);
		}
		AfUserDo afUserDo = afUserService.getUserByUserName(userName);
		if (afUserDo == null) {
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_NOT_EXIST_ERROR);
		}
		if (StringUtils.equals(afUserDo.getStatus(), UserStatus.FROZEN.getCode())) {
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_FROZEN_ERROR);
		}
		// add user login record
		AfUserLoginLogDo loginDo = new AfUserLoginLogDo();
		loginDo.setAppVersion(Integer.parseInt(ObjectUtils.toString(requestDataVo.getSystem().get("appVersion"))));
		loginDo.setLoginIp(ip);
		loginDo.setOsType(osType);
		loginDo.setPhoneType(phoneType);
		loginDo.setUserName(userName);
		loginDo.setUuid(uuid);

		// check login failed count,if count greater than 5,lock specify hours
		AfResourceDo lockHourResource = afResourceService
				.getSingleResourceBytype(Constants.RES_APP_LOGIN_FAILED_LOCK_HOUR);
		if (DateUtil.afterDay(DateUtil.addHoures(afUserDo.getGmtModified(),
				lockHourResource == null ? 2 : Integer.parseInt(lockHourResource.getValue())), new Date())) {
			if (afUserDo.getFailCount() > 5) {
				loginDo.setResult("false:err count too max" + afUserDo.getFailCount());
				afUserLoginLogService.addUserLoginLog(loginDo);
				return new ApiHandleResponse(requestDataVo.getId(),
						FanbeiExceptionCode.USER_PASSWORD_ERROR_GREATER_THAN5);
			}
		} else {
			// current time is over the expired time, then clear the login error
			// count
			AfUserDo temp = new AfUserDo();
			temp.setRid(afUserDo.getRid());
			temp.setFailCount(0);
			temp.setUserName(userName);
			afUserDo.setFailCount(0);
			afUserService.updateUser(temp);
		}

		// check password
		String inputPassword = UserUtil.getPassword(inputPassSrc, afUserDo.getSalt());
		if (!StringUtils.equals(inputPassword, afUserDo.getPassword())) {
			// fail count add 1
			AfUserDo temp = new AfUserDo();
			Integer errorCount = afUserDo.getFailCount();
			temp.setRid(afUserDo.getRid());
			temp.setFailCount(errorCount + 1);
			temp.setUserName(userName);
			afUserService.updateUser(temp);
			loginDo.setResult("false:password error");
			afUserLoginLogService.addUserLoginLog(loginDo);
			FanbeiExceptionCode errorCode = getErrorCountCode(errorCount + 1);
			return new ApiHandleResponse(requestDataVo.getId(), errorCode);
		}
		if(afUserDo.getRecommendId() > 0l && afUserLoginLogService.getCountByUserName(userName) == 0){
			afGameChanceService.updateInviteChance(afUserDo.getRecommendId());
			//向推荐人推送消息
			AfUserDo user = afUserService.getUserById(afUserDo.getRecommendId());
			jpushService.gameShareSuccess(user.getUserName());
		}
		loginDo.setResult("true");
		afUserLoginLogService.addUserLoginLog(loginDo);
		// reset fail count to 0 and record login ip phone msg
		AfUserDo temp = new AfUserDo();
		temp.setFailCount(0);
		temp.setRid(afUserDo.getRid());
		temp.setUserName(userName);
		afUserService.updateUser(temp);

		// save token to cache
		String token = UserUtil.generateToken(userName);
		TokenBo tokenBo = new TokenBo();
		tokenBo.setLastAccess(System.currentTimeMillis() + "");
		tokenBo.setToken(token);
		tokenBo.setUserId(userName);
		tokenCacheUtil.saveToken(userName, tokenBo);
		// set return user info and generate token
		AfUserVo userVo = parseUserVo(afUserDo);
		JSONObject jo = new JSONObject();
		jo.put("user", userVo);
		jo.put("token", token);
		jo.put("allowConsume", afUserAuthService.getConsumeStatus(afUserDo.getRid(),context.getAppVersion()));
		
		//3.7.6 对于未结款的用户在登录后，结款按钮高亮显示
		Boolean isBorrowed =  bizCacheUtil.isRedisSetValue(Constants.HAVE_BORROWED, String.valueOf(afUserDo.getRid()));
		if(Boolean.TRUE.equals(isBorrowed)){
			jo.put("borrowed", "Y");
		}else{
			jo.put("borrowed", "N");
		}
		
		
		// jo.put("firstLogin", afUserDo.getFailCount() == -1?1:0);
		if (context.getAppVersion() >= 340) {
			if (StringUtils.isBlank(blackBox)) {
				return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PARAM_ERROR);

			}
			tongdunUtil.getLoginResult(requestDataVo.getId(), blackBox, ip, userName, userName, "1", "");
		}

		resp.setResponseData(jo);

		return resp;
	}

	private AfUserVo parseUserVo(AfUserDo afUserDo) {
		AfUserVo vo = new AfUserVo();
		vo.setUserId(afUserDo.getRid());
		vo.setUserName(afUserDo.getUserName());
		vo.setNick(afUserDo.getNick());
		vo.setAvatar(afUserDo.getAvatar());
		vo.setMobile(afUserDo.getMobile());
		vo.setFailCount(afUserDo.getFailCount());
		return vo;
	}

	public FanbeiExceptionCode getErrorCountCode(Integer errorCount) {
		if (errorCount == 0) {
			return FanbeiExceptionCode.USER_PASSWORD_ERROR_ZERO;
		} else if (errorCount == 1) {
			return FanbeiExceptionCode.USER_PASSWORD_ERROR_FIRST;
		} else if (errorCount == 2) {
			return FanbeiExceptionCode.USER_PASSWORD_ERROR_SECOND;
		} else if (errorCount == 3) {
			return FanbeiExceptionCode.USER_PASSWORD_ERROR_THIRD;
		} else if (errorCount == 4) {
			return FanbeiExceptionCode.USER_PASSWORD_ERROR_FOURTH;
		} else if (errorCount == 5) {
			return FanbeiExceptionCode.USER_PASSWORD_ERROR_FIFTH;
		} else if (errorCount == 6) {
			return FanbeiExceptionCode.USER_PASSWORD_ERROR_GREATER_THAN5;
		} else {
			return FanbeiExceptionCode.USER_PASSWORD_ERROR_GREATER_THAN5;
		}
	}

	public static void main(String[] args) {
		System.out.println(UserUtil.getPassword(MD5.digest("123456"), "d229b3462c0b8a94"));
	}
}
