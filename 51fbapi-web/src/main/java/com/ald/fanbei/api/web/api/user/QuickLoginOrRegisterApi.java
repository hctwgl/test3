package com.ald.fanbei.api.web.api.user;

import com.ald.fanbei.api.biz.bo.TokenBo;
import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.biz.third.util.SmsUtil;
import com.ald.fanbei.api.biz.third.util.TongdunUtil;
import com.ald.fanbei.api.biz.third.util.baiqishi.BaiQiShiUtils;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.biz.util.TokenCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.*;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.*;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.AfUserVo;
import com.alibaba.fastjson.JSONObject;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.util.security.Credential.MD5;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 
 * @类描述：
 * 
 * @author Xiaotianjian 2017年1月19日下午1:48:59
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("quickLoginOrRegisterApi")
public class QuickLoginOrRegisterApi implements ApiHandle {

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
	// @Resource
	// AfGameChanceService afGameChanceService;
	@Resource
	TongdunUtil tongdunUtil;
	@Resource
	BaiQiShiUtils baiQiShiUtils;
	@Resource
	BizCacheUtil bizCacheUtil;
	@Resource
	JpushService jpushService;
	@Resource
	RiskUtil riskUtil;
	@Resource
	AfUserToutiaoService afUserToutiaoService;
	@Resource
	AfSmsRecordService afSmsRecordService;
	@Resource
	SmsUtil smsUtil;
	@Resource
	AfPromotionChannelPointService afPromotionChannelPointService;
	@Resource
	AfAbtestDeviceNewService afAbtestDeviceNewService;
	@Resource
	AfBoluomeActivityService afBoluomeActivityService;
	@Resource
	AfUserBankcardService afUserBankcardService;
	@Resource
	private AfCouponService afCouponService;
	@Resource
	private AfUserCouponService afUserCouponService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		String SUCC = "1";
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		final String userName = context.getUserName();
		final String requestId=requestDataVo.getId();
		String loginChannel=requestId.substring(requestId.lastIndexOf("_")+1);
		String osType = ObjectUtils.toString(requestDataVo.getParams().get("osType"));
		String phoneType = ObjectUtils.toString(requestDataVo.getParams().get("phoneType"));
		String uuid = ObjectUtils.toString(requestDataVo.getParams().get("uuid"));
		// 滴滴风控相关信息
		String wifiMac = ObjectUtils.toString(requestDataVo.getParams().get("wifi_mac"));
		String ip = CommonUtil.getIpAddr(request);

		// String inputPassSrc =
		// ObjectUtils.toString(requestDataVo.getParams().get("password"));
		String blackBox = ObjectUtils.toString(requestDataVo.getParams().get("blackBox"));
		String bqsBlackBox = ObjectUtils.toString(requestDataVo.getParams().get("bqsBlackBox"));
		String networkType = ObjectUtils.toString(requestDataVo.getParams().get("networkType"));
		String loginType = ObjectUtils.toString(requestDataVo.getParams().get("loginType"));
		String verifyCode = ObjectUtils.toString(requestDataVo.getParams().get("verifyCode"));// 验证码
		AfUserDo afUserDo = afUserService.getUserByUserName(userName);
		AfUserLoginLogDo login = new AfUserLoginLogDo();
		login.setAppVersion(Integer.parseInt(ObjectUtils.toString(requestDataVo.getSystem().get("appVersion"))));
		login.setLoginIp(ip);
		login.setOsType(osType);
		login.setPhoneType(phoneType);
		login.setUserName(userName);
		login.setUuid(uuid);
		if (afUserDo == null){
			login.setResult("quick register start");
			afUserLoginLogService.addUserLoginLog(login);//埋点
			smsUtil.checkSmsByMobileAndType(context.getUserName(), verifyCode, SmsType.QUICK_REGIST);// 短信验证码判断
		}else {
			login.setResult("quick login start");
			afUserLoginLogService.addUserLoginLog(login);//埋点
			smsUtil.checkSmsByMobileAndType(context.getUserName(), verifyCode, SmsType.QUICK_LOGIN);// 短信验证码判断
		}

		if (afUserDo == null) {
			afUserDo = quickRegister(requestDataVo, context, request);
		}
		Long userId = afUserDo.getRid();
		if (StringUtils.equals(afUserDo.getStatus(), UserStatus.FROZEN.getCode())) {
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_FROZEN_ERROR);
		}

		Integer failCount = afUserDo.getFailCount();
		// add user login record
		AfUserLoginLogDo loginDo = new AfUserLoginLogDo();
		loginDo.setAppVersion(Integer.parseInt(ObjectUtils.toString(requestDataVo.getSystem().get("appVersion"))));
		loginDo.setLoginIp(ip);
		loginDo.setOsType(osType);
		loginDo.setPhoneType(phoneType);
		loginDo.setUserName(userName);
		loginDo.setUuid(uuid);
		//ToutiaoAdActive(requestDataVo, context, afUserDo);
		// check login failed count,if count greater than 5,lock specify hours
		AfResourceDo lockHourResource = afResourceService
				.getSingleResourceBytype(Constants.RES_APP_LOGIN_FAILED_LOCK_HOUR);
		if (DateUtil.afterDay(DateUtil.addHoures(afUserDo.getGmtModified(),
				lockHourResource == null ? 2 : Integer.parseInt(lockHourResource.getValue())), new Date())) {
			if (afUserDo.getFailCount() > 5) {
				loginDo.setResult("quick login false:err count too max" + afUserDo.getFailCount());
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


		AfUserDo temp = new AfUserDo();
		temp.setFailCount(0);
		temp.setRid(afUserDo.getRid());
		temp.setUserName(userName);
		afUserService.updateUser(temp);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String loginTime = sdf.format(new Date(System.currentTimeMillis()));

		boolean isNeedRisk = true;// 是否为手机号未验证注册的用户

		if(loginChannel.indexOf("borrowSuperman")!=-1){
			long successTime=afUserLoginLogService.getCountByUserNameAndResultSupermanTrue(userName);
			if(successTime < 1){
				AfResourceDo afResourceDo=afResourceService.getSingleResourceBytype(AfResourceType.LOGIN_SUPERMAN_COUPON.getCode());
				//开关打开
				if(StringUtil.equals(afResourceDo.getValue(),"1")){
					AfCouponDo afCouponDo=afCouponService.getCouponById(Long.valueOf(afResourceDo.getValue1()));
					if(afCouponDo!=null){
						Long totalCount = afCouponDo.getQuota();
						if(totalCount <= afCouponDo.getQuotaAlready()){
							resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_COUPON_PICK_OVER_ERROR);
						}
						AfUserCouponDo afUserCouponDo=new AfUserCouponDo();
						afUserCouponDo.setGmtStart(afCouponDo.getGmtStart());
						afUserCouponDo.setGmtEnd(afCouponDo.getGmtEnd());
						afUserCouponDo.setUserId(userId);
						afUserCouponDo.setStatus(CouponStatus.NOUSE.getCode());
						afUserCouponDo.setCouponId(afCouponDo.getRid());
						afUserCouponDo.setSourceType(afResourceDo.getType());
						if(afUserCouponService.addUserCoupon(afUserCouponDo)==1){
							AfCouponDo couponDo=new AfCouponDo();
							couponDo.setRid(afCouponDo.getRid());
							couponDo.setQuotaAlready(1);
							afCouponService.updateCouponquotaAlreadyById(afCouponDo);
							try {
								smsUtil.sendSmsToDhst(afUserDo.getMobile(),afCouponDo.getName());
							} catch (Exception e) {
								logger.error("sendLoginSupermanCouponMsg is Fail.",e);
							}
						}
					}
				}
			}
			loginDo.setResult("quick login true,"+loginChannel);
			}else {
			loginDo.setResult("quick login true");
		}
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
		jo.put("allowConsume", afUserAuthService.getConsumeStatus(afUserDo.getRid(), context.getAppVersion()));

		String loginWifiMacKey = Constants.CACHEKEY_USER_LOGIN_WIFI_MAC + afUserDo.getRid();
		bizCacheUtil.saveObject(loginWifiMacKey, wifiMac);
		if (afUserDo.getFailCount() == -1 && (afUserDo.getPassword() == null || "".equals(afUserDo.getPassword()))) {
			jo.put("flag", "Y");
		} else {
			jo.put("flag", "N");
		}
		// 3.7.6 对于未结款的用户在登录后，结款按钮高亮显示
		Boolean isBorrowed = bizCacheUtil.isRedisSetValue(Constants.HAVE_BORROWED, String.valueOf(afUserDo.getRid()));
		if (Boolean.TRUE.equals(isBorrowed)) {
			jo.put("borrowed", "Y");
		} else {
			jo.put("borrowed", "N");
		}

		// jo.put("firstLogin", afUserDo.getFailCount() == -1?1:0);
		if (context.getAppVersion() >= 340) {
			if (StringUtils.isBlank(blackBox)) {
				return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PARAM_ERROR);

			}
			tongdunUtil.getLoginResult(requestDataVo.getId(), blackBox, ip, userName, userName, "1", "");
			try {
				AfUserAccountDo accountDo = afUserAccountService.getUserAccountByUserId(afUserDo.getRid());
				String idNumber = "";
				String openId = "";
				String cardNumber = "";
				if (accountDo != null){
					idNumber = accountDo.getIdNumber();
					openId = accountDo.getOpenId();
				}
				AfUserBankcardDo bank = afUserBankcardService.getUserMainBankcardByUserId(afUserDo.getRid());
				if (bank != null){
					cardNumber = bank.getCardNumber();
				}
				baiQiShiUtils.getLoginResult(requestDataVo.getId(),bqsBlackBox, ip, afUserDo.getMobile(),afUserDo.getRealName(),idNumber,cardNumber,openId);
			}catch (Exception e){
				logger.error("baiQiShiUtils getLoginResult error => {}",e);
			}
		}
		if (context.getAppVersion() >= 381) {
			riskUtil.verifyASyLogin(ObjectUtils.toString(afUserDo.getRid(), ""), userName, blackBox, uuid, loginType,
					loginTime, ip, phoneType, networkType, osType, SUCC, Constants.EVENT_LOGIN_ASY,bqsBlackBox);
		}

		resp.setResponseData(jo);
		 //吃玩住行活动被邀请的新用户登录送券
		try{
			  afBoluomeActivityService.sentNewUserBoluomeCouponForDineDash(afUserDo);
			  logger.info("sentNewUserBoluomeCouponForDineDash success");
		   }catch (Exception e){
			  logger.error("sentNewUserBoluomeCouponForDineDash error",e.getMessage());
		   }
		if (failCount == -1) {
			new Timer().schedule(new TimerTask() {
				public void run() {
					jpushService.jPushCoupon("COUPON_POPUPS", userName);
					this.cancel();
				}
			}, 1000 * 5);// 一分钟
		}

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
		} catch (Exception e) {
			// ignore error.
		}
		return resp;
	}

	private AfUserDo quickRegister(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		String requestId = requestDataVo.getId();
		String nick = ObjectUtils.toString(requestDataVo.getParams().get("nick"), "");
		ClientTypeEnum clientType = StringUtil.judgeClientType(requestDataVo.getId());
		String registerChannelPointId = ObjectUtils.toString(requestDataVo.getParams().get("channelPointId"), "");
		String majiabaoName = requestId.substring(requestId.lastIndexOf("_") + 1, requestId.length());
		String userName = context.getUserName();

		String ip = CommonUtil.getIpAddr(request);
		String blackBox = ObjectUtils.toString(requestDataVo.getParams().get("blackBox"));
		String bqsBlackBox = ObjectUtils.toString(requestDataVo.getParams().get("bqsBlackBox"));
		String uuid = ObjectUtils.toString(requestDataVo.getParams().get("uuid"));
		String phoneType = ObjectUtils.toString(requestDataVo.getParams().get("phoneType"));
		String networkType = ObjectUtils.toString(requestDataVo.getParams().get("networkType"));
		String osType = ObjectUtils.toString(requestDataVo.getParams().get("osType"));
		AfUserDo userDo = new AfUserDo();
		String salt = UserUtil.getSalt();
		// String password = UserUtil.getPassword(passwordSrc, salt);
		userDo.setSalt(salt);
		userDo.setUserName(userName);
		userDo.setMobile(userName);
		userDo.setNick(nick);

		if (registerChannelPointId != null) {
			AfPromotionChannelPointDo channelPointDo = afPromotionChannelPointService.getPoint("Andriod",
					registerChannelPointId);
			AfPromotionChannelPointDo channelPointDoIOS = afPromotionChannelPointService.getPoint("IOS",
					registerChannelPointId);
			// 根据设备来源区分处理

			String clientTypeCode = clientType != null ? clientType.getCode() : "";
			if (ClientTypeEnum.ANDROID.getCode().equals(clientTypeCode)) {
				if (channelPointDo != null) {
					userDo.setRegisterChannelPointId(channelPointDo.getId());
					userDo.setRegisterChannelId(channelPointDo.getChannelId());
				} else if (channelPointDoIOS != null) {
					userDo.setRegisterChannelPointId(channelPointDoIOS.getId());
					userDo.setRegisterChannelId(channelPointDoIOS.getChannelId());
					logger.warn(
							"setRegisterPwdApi setRegisterChannelId type not match,source is android,set into ios.registerChannelPointId="
									+ registerChannelPointId);
				}
			} else if (ClientTypeEnum.IOS.getCode().equals(clientTypeCode)) {
				if (channelPointDoIOS != null) {
					userDo.setRegisterChannelPointId(channelPointDoIOS.getId());
					userDo.setRegisterChannelId(channelPointDoIOS.getChannelId());
				} else if (channelPointDo != null) {
					userDo.setRegisterChannelPointId(channelPointDo.getId());
					userDo.setRegisterChannelId(channelPointDo.getChannelId());
					logger.warn(
							"setRegisterPwdApi setRegisterChannelId type not match,source is ios,set into android.registerChannelPointId="
									+ registerChannelPointId);
				}
			} else {
				// 走默认之前的处理
				if (channelPointDo != null) {
					userDo.setRegisterChannelPointId(channelPointDo.getId());
					userDo.setRegisterChannelId(channelPointDo.getChannelId());
				} else if (channelPointDoIOS != null) {
					userDo.setRegisterChannelPointId(channelPointDoIOS.getId());
					userDo.setRegisterChannelId(channelPointDoIOS.getChannelId());
				}
				logger.warn(
						"setRegisterPwdApi setRegisterChannelId source is not found,set into default.registerChannelPointId="
								+ registerChannelPointId);
			}
		}

		userDo.setRecommendId(0l);
		userDo.setMajiabaoName(majiabaoName);
		userDo.setPassword("");
		userDo.setAddress("");
		userDo.setAvatar("");
		userDo.setBirthday("");
		userDo.setCity("");
		userDo.setCounty("");
		userDo.setEmail("");
		userDo.setProvince("");
		userDo.setGender("");
		userDo.setRealName("");
		userDo.setFailCount(-1);
		userDo.setRecommendCode("");
		userDo.setStatus("NORMAL");
		afUserService.addUser(userDo, "Q");

		Long invteLong = Constants.INVITE_START_VALUE + userDo.getRid();
		String inviteCode = Long.toString(invteLong, 36);
		userDo.setRecommendCode(inviteCode);
		afUserService.updateUser(userDo);

		// 注册完成,给用户发送注册成功的短信
		// smsUtil.sendRegisterSuccessSms(userDo.getUserName());

		AfUserDo afUserDo = afUserService.getUserByUserName(userName);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String registerTime = sdf.format(new Date(System.currentTimeMillis()));
		// 风控可信异步通知
		if (context.getAppVersion() >= 381) {
			riskUtil.verifyASyRegister(ObjectUtils.toString(afUserDo.getRid(), ""), userName, blackBox, uuid,
					registerTime, ip, phoneType, networkType, osType, Constants.EVENT_RIGISTER_ASY,bqsBlackBox);
		}
		return afUserDo;
	}

	private void ToutiaoAdActive(RequestDataVo requestDataVo, FanbeiContext context, AfUserDo afUserDo) {
		try {
			String imei = ObjectUtils.toString(requestDataVo.getParams().get("IMEI"), null);
			String androidId = ObjectUtils.toString(requestDataVo.getParams().get("AndroidID"), null);
			String idfa = ObjectUtils.toString(requestDataVo.getParams().get("IDFA"), null);
			String imeiMd5 = "";
			logger.error("toutiaoactive para:" + imei + "," + androidId + "," + idfa);
			if (StringUtils.isNotBlank(imei)) {
				imeiMd5 = getMd5(imei);
			}
			if (StringUtils.isNotBlank(imei) || StringUtils.isNotBlank(androidId) || StringUtils.isNotBlank(idfa)) {
				AfUserToutiaoDo tdo = afUserToutiaoService.getUserActive(imeiMd5, androidId, idfa);
				if (tdo != null) {
					Date tdate = tdo.getGmtModified();
					Date udate = afUserDo.getGmtCreate();
					if (tdate.getTime() < udate.getTime()) {
						String callbackUrl = tdo.getCallbackUrl();
						if (callbackUrl.indexOf("&event_type") == -1) {
							callbackUrl += "&event_type=1";
						}
						String result = HttpUtil.doGet(callbackUrl, 20);
				/*		if (result.indexOf("success") > -1) {
							Long rid = tdo.getRid();
							Long userIdToutiao = context.getUserId() == null ? -1l : context.getUserId();
							String userNameToutiao = context.getUserName() == null ? "" : context.getUserName();
							afUserToutiaoService.uptUserActive(rid, userIdToutiao, userNameToutiao);
						}
						logger.error("toutiaoactive:update success,active=1,callbacr_url=" + callbackUrl + ",result="
								+ result);*/
					}
				}
			}
		} catch (Exception e) {
			logger.error("toutiaoactive:catch error", e.getMessage());
		}
	}

	/**
	 * 用于获取一个String的md5值
	 * 
	 * @param string
	 * @return
	 */
	public static String getMd5(String str) throws Exception {
		MessageDigest md5 = MessageDigest.getInstance("MD5");

		byte[] bs = md5.digest(str.getBytes());
		StringBuilder sb = new StringBuilder(40);
		for (byte x : bs) {
			if ((x & 0xff) >> 4 == 0) {
				sb.append("0").append(Integer.toHexString(x & 0xff));
			} else {
				sb.append(Integer.toHexString(x & 0xff));
			}
		}
		return sb.toString();
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

	/**
	 * 是否是白名单
	 * 
	 * @param userName
	 * @return
	 */
	private boolean isInWhiteList(String userName) {
		AfResourceDo resDo = afResourceService.getSingleResourceBytype(AfResourceType.LOGIN_WHITE_LIST.getCode());
		if (resDo == null) {
			return false;
		}
		if (StringUtils.isNotBlank(resDo.getValue3())) {
			String orignStr = resDo.getValue3().replace("，", ","); // ，改为,
			String whites[] = orignStr.split(",");
			for (int i = 0; i < whites.length; i++) {
				if (userName.equals(whites[i])) {
					return true;
				}
			}
		}
		return false;
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
