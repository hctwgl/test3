package com.ald.fanbei.api.web.api.user;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.ald.fanbei.api.biz.third.util.baiqishi.BaiQiShiUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.TokenBo;
import com.ald.fanbei.api.biz.service.AfAbTestDeviceService;
import com.ald.fanbei.api.biz.service.AfAbtestDeviceNewService;
import com.ald.fanbei.api.biz.service.AfBoluomeActivityService;
import com.ald.fanbei.api.biz.service.AfSmsRecordService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.service.AfUserLoginLogService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.service.JpushService;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.biz.third.util.TongdunUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.biz.util.TokenCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.SmsType;
import com.ald.fanbei.api.common.enums.UserStatus;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.CommonUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.common.util.UserUtil;
import com.ald.fanbei.api.dal.domain.AfAbTestDeviceDo;
import com.ald.fanbei.api.dal.domain.AfAbtestDeviceNewDo;
import com.ald.fanbei.api.dal.domain.AfSmsRecordDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.AfUserLoginLogDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.AfUserVo;
import com.alibaba.fastjson.JSONObject;

/**
 * 
 * @类描述: 可信登录验证短信验证码
 *
 * @auther caihuan 2017年8月30日
 * @注意:本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("checkLoginVerifyCodeApi")
public class CheckLoginVerifyCodeApi implements ApiHandle{

	@Resource
	AfUserService afUserService;
	@Resource
	AfUserLoginLogService afUserLoginLogService;
	@Resource
	BizCacheUtil bizCacheUtil;
	@Resource
	TongdunUtil tongdunUtil;
	@Resource
	TokenCacheUtil tokenCacheUtil;
	@Resource
	AfUserAuthService afUserAuthService;
	@Resource
	AfSmsRecordService afSmsRecordService;
	@Resource
	RiskUtil riskUtil;
	@Resource
	AfBoluomeActivityService afBoluomeActivityService;
	@Resource
	AfAbtestDeviceNewService afAbtestDeviceNewService;
	@Resource
	JpushService jpushService;
	@Resource
	BaiQiShiUtils baiQiShiUtils;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		String SUCC = "1";
		String FAIL = "0";
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		final String userName = context.getUserName();
		String osType = ObjectUtils.toString(requestDataVo.getParams().get("osType"));
		String phoneType = ObjectUtils.toString(requestDataVo.getParams().get("phoneType"));
		String ip = CommonUtil.getIpAddr(request);

		String uuid = ObjectUtils.toString(requestDataVo.getParams().get("uuid"));
		String networkType = ObjectUtils.toString(requestDataVo.getParams().get("networkType"));
		String verifyCode = ObjectUtils.toString(requestDataVo.getParams().get("verifyCode"));
		String inputPassSrc = ObjectUtils.toString(requestDataVo.getParams().get("password"));
		String blackBox = ObjectUtils.toString(requestDataVo.getParams().get("blackBox"));
		String bqsBlackBox = ObjectUtils.toString(requestDataVo.getParams().get("bqsBlackBox"));
		
		if (StringUtils.isBlank(inputPassSrc)) {
			logger.error("可信登录：inputPassSrc can't be empty");
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PARAM_ERROR);
		}
		AfUserDo afUserDo = afUserService.getUserByUserName(userName);
		if (afUserDo == null) {
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_NOT_EXIST_ERROR);
		}
		Long userId = afUserDo.getRid();
		if (StringUtils.equals(afUserDo.getStatus(), UserStatus.FROZEN.getCode())) {
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_FROZEN_ERROR);
		}
		
		AfUserLoginLogDo loginDo = new AfUserLoginLogDo();
		loginDo.setAppVersion(Integer.parseInt(ObjectUtils.toString(requestDataVo.getSystem().get("appVersion"))));
		loginDo.setLoginIp(ip);
		loginDo.setOsType(osType);
		loginDo.setPhoneType(phoneType);
		loginDo.setUserName(userName);
		loginDo.setUuid(uuid);
		
		String inputPassword = UserUtil.getPassword(inputPassSrc, afUserDo.getSalt());
		if (!StringUtils.equals(inputPassword, afUserDo.getPassword())) {
			// fail count add 1
			AfUserDo temp = new AfUserDo();
			Integer errorCount = afUserDo.getFailCount();
			temp.setRid(afUserDo.getRid());
			temp.setFailCount(errorCount + 1);
			temp.setUserName(userName);
			afUserService.updateUser(temp);
			loginDo.setResult("false:可信登录密码不对");
			afUserLoginLogService.addUserLoginLog(loginDo);
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_PASSWORD_ERROR);
		}
		
		AfSmsRecordDo smsDo = afSmsRecordService.getLatestByUidType(context.getUserName(), SmsType.LOGIN.getCode());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String loginTime = sdf.format(new Date(System.currentTimeMillis()));
        if(smsDo == null ){
        	return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_LOGIN_SMS_NOTEXIST);
        }
        if(smsDo.getIsCheck() == 1){
        	return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_REGIST_SMS_ALREADY_ERROR);
        }
        //判断验证码是否一致并且验证码是否已经做过验证
        String realCode = smsDo.getVerifyCode();
        if(!StringUtils.equals(verifyCode, realCode)){
        	riskUtil.verifyASyLogin(ObjectUtils.toString(afUserDo.getRid(), ""), userName, blackBox, uuid, "0", 
        			loginTime, ip, phoneType, networkType, osType,FAIL,Constants.EVENT_LOGIN_ASY,bqsBlackBox);
        	// 更新为已经验证
         	//afSmsRecordService.updateSmsIsCheck(smsDo.getRid());
         	loginDo.setResult("false:可信登录短信验证码不正确");
			afUserLoginLogService.addUserLoginLog(loginDo);
        	return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_LOGIN_SMS_WRONG_ERROR);
        }
        //判断验证码是否过期
        if(DateUtil.afterDay(new Date(), DateUtil.addMins(smsDo.getGmtCreate(), Constants.MINITS_OF_HALF_HOUR))){
        	return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_REGIST_SMS_OVERDUE);
        }
        // 更新为已经验证
     	afSmsRecordService.updateSmsIsCheck(smsDo.getRid());
     	
     	loginDo.setResult("true:验证短信验证码通过");
		afUserLoginLogService.addUserLoginLog(loginDo);
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
			try {
				baiQiShiUtils.getLoginResult(requestDataVo.getId(),bqsBlackBox, ip, afUserDo.getMobile(),afUserDo.getRealName(),null,null,null);
			}catch (Exception e){
				logger.info("checkLoginVerifyCodeApi baiQiShiUtils error =>{}",e.getMessage());
			}
		}

		riskUtil.verifyASyLogin(ObjectUtils.toString(afUserDo.getRid(), ""), userName, blackBox, uuid, "0",
				loginTime, ip, phoneType, networkType, osType,SUCC,Constants.EVENT_LOGIN_ASY,bqsBlackBox);
		resp.setResponseData(jo);
		//逛逛点亮活动新用户送券第一版(下架)
//		try{
//		  int  result =  afBoluomeActivityService.sentNewUserBoluomeCoupon(afUserDo);
//		  if(result==0){
//		      logger.info("sentNewUserBoluomeCoupon success");
//		    }
//		}catch (Exception e){
//			logger.error("sentNewUserBoluomeCoupon error",e.getMessage());
//		}
		
		//吃玩住行活动被邀请的新用户登录送券（第二版）
		try{
			    afBoluomeActivityService.sentNewUserBoluomeCouponForDineDash(afUserDo);
			    logger.info("sentNewUserBoluomeCouponForDineDash success");
			
			}catch (Exception e){
					logger.error("sentNewUserBoluomeCouponForDineDash error",e.getMessage());
			}
//		//首次登陆，弹窗
//		long successTime =  afUserLoginLogService.getCountByUserNameAndResultTrue(userName);
//		if(successTime <= 1){
//				  new Timer().schedule(new TimerTask() {
//				   public void run() {
//				        	jpushService.jPushCoupon("COUPON_POPUPS", userName);
//				        	this.cancel();
//				        }
//				    }, 1000 * 5);// 一分钟
//		}
//		
		// 记录用户设备信息
		try {
			String deviceId = ObjectUtils.toString(requestDataVo.getParams().get("deviceId"));
			if (StringUtils.isNotEmpty(deviceId)) {
			  //String deviceIdTail = StringUtil.getDeviceTailNum(deviceId);
				AfAbtestDeviceNewDo abTestDeviceDo = new AfAbtestDeviceNewDo();
				abTestDeviceDo.setUserId(userId);
				abTestDeviceDo.setDeviceNum(deviceId);
				// 通过唯一组合索引控制数据不重复
				afAbtestDeviceNewService.addUserDeviceInfo(abTestDeviceDo);
			}
		}  catch (Exception e) {
			// ignore error.
		}
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
	

}
