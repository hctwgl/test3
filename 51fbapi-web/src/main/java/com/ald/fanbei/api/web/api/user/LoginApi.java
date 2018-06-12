package com.ald.fanbei.api.web.api.user;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.ald.fanbei.api.biz.third.util.SmsUtil;
import com.ald.fanbei.api.biz.third.util.YSSmsUtil;
import com.ald.fanbei.api.biz.third.util.baiqishi.BaiQiShiUtils;
import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.common.enums.AfResourceSecType;
import com.ald.fanbei.api.common.enums.CouponStatus;
import com.ald.fanbei.api.dal.domain.*;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.util.security.Credential.MD5;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.TokenBo;
import com.ald.fanbei.api.biz.service.AfAbtestDeviceNewService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.service.AfUserLoginLogService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.service.AfUserToutiaoService;
import com.ald.fanbei.api.biz.service.JpushService;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.biz.third.util.TongdunUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.biz.util.TokenCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.UserStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.CommonUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.common.util.UserUtil;
import com.ald.fanbei.api.dal.domain.AfAbtestDeviceNewDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.AfUserLoginLogDo;
import com.ald.fanbei.api.dal.domain.AfUserToutiaoDo;
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
	// @Resource
	// AfGameChanceService afGameChanceService;
	@Resource
	TongdunUtil tongdunUtil;
	@Resource
	BaiQiShiUtils baiQiShiUtils;
	// @Resource
	// JpushService jpushService;
	@Resource
	BizCacheUtil bizCacheUtil;
	@Resource
	JpushService jpushService;
	@Resource
	RiskUtil riskUtil;
	@Resource
	SmsUtil smsUtil;
	@Resource
	YSSmsUtil ysSmsUtil;
	@Resource
	AfUserToutiaoService afUserToutiaoService;
	@Resource
	private AfUserCouponService afUserCouponService;
	@Resource
	AfUserBankcardService afUserBankcardService;
	@Resource
	AfAbtestDeviceNewService afAbtestDeviceNewService;
	@Resource
	private AfCouponService afCouponService;

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

		String inputPassSrc = ObjectUtils.toString(requestDataVo.getParams().get("password"));
		String blackBox = ObjectUtils.toString(requestDataVo.getParams().get("blackBox"));
		String bqsBlackBox = ObjectUtils.toString(requestDataVo.getParams().get("bqsBlackBox"));
		String networkType = ObjectUtils.toString(requestDataVo.getParams().get("networkType"));
		String loginType = ObjectUtils.toString(requestDataVo.getParams().get("loginType"));

		if (StringUtils.isBlank(inputPassSrc)) {
			logger.error("inputPassSrc can't be empty");
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
		Integer failCount = afUserDo.getFailCount();
		// add user login record
		AfUserLoginLogDo loginDo = new AfUserLoginLogDo();
		loginDo.setAppVersion(Integer.parseInt(ObjectUtils.toString(requestDataVo.getSystem().get("appVersion"))));
		loginDo.setLoginIp(ip);
		loginDo.setOsType(osType);
		loginDo.setPhoneType(phoneType);
		loginDo.setUserName(userName);
		loginDo.setUuid(uuid);
//		ToutiaoAdActive(requestDataVo, context, afUserDo);
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
		// if(afUserDo.getRecommendId() > 0l &&
		// afUserLoginLogService.getCountByUserName(userName) == 0){
		// afGameChanceService.updateInviteChance(afUserDo.getRecommendId());
		// //向推荐人推送消息
		// AfUserDo user = afUserService.getUserById(afUserDo.getRecommendId());
		// jpushService.gameShareSuccess(user.getUserName());
		// }
		// reset fail count to 0 and record login ip phone msg

		AfUserDo temp = new AfUserDo();
		temp.setFailCount(0);
		temp.setRid(afUserDo.getRid());
		temp.setUserName(userName);
		afUserService.updateUser(temp);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String loginTime = sdf.format(new Date(System.currentTimeMillis()));

		boolean isNeedRisk = true;
		if ("1".equals(loginType)) {
			Date gmtCreateDate = afUserDo.getGmtCreate();
			Date date = new Date();
			long hours = DateUtil.getNumberOfHoursBetween(gmtCreateDate, date);
			if (hours <= 2) { // 防止部分非新注册用户直接登录绕过风控可信接口
				isNeedRisk = false;
			}
		}

		//首次登陆，弹窗
		if(loginChannel.indexOf("borrowSuperman")==-1) {
			long successTime =  afUserLoginLogService.getCountByUserNameAndResultTrue(userName);
			if(successTime < 1){
				new Timer().schedule(new TimerTask() {
					public void run() {
						jpushService.jPushCoupon("COUPON_POPUPS", userName);
						this.cancel();
					}
				}, 1000 * 5);// 一分钟
			}
		}
		
		// 调用风控可信接口
		if (context.getAppVersion() >= 381 && isNeedRisk && !isInWhiteList(userName)) {

			boolean riskSucc = false;
			try {
				riskSucc = riskUtil.verifySynLogin(ObjectUtils.toString(afUserDo.getRid(), ""), userName, blackBox,
						uuid, loginType, loginTime, ip, phoneType, networkType, osType,bqsBlackBox);
			} catch (Exception e) {
				if (e instanceof FanbeiException) {
					logger.error("用户登录调风控可信验证失败", e);
					throw e;
				} else {
					logger.error("用户登录调风控可信验证发生预期外异常userName:" + userName, e);
					riskSucc = false;
				}
			}

			if (!riskSucc) {
				loginDo.setResult("false:需要验证登录短信");
				afUserLoginLogService.addUserLoginLog(loginDo);
				JSONObject jo = new JSONObject();
				jo.put("needVerify", "Y");
				resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_LOGIN_UNTRUST_ERROW);
				resp.setResponseData(jo); // 失败了返回需要短信验证
				return resp;
			}
			loginType = "2"; // 可信登录验证通过，变可信
		}
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
								String content="恭喜您获得一张“"+afCouponDo.getName()+"”，有效期为“"+afCouponDo.getGmtEnd()+"”，请登录借款超人app，在还款时选择使用；";
								ysSmsUtil.send(afUserDo.getMobile(),content,ysSmsUtil.NOTITION_YS);
							} catch (Exception e) {
								logger.error("sendLoginSupermanCouponMsg is Fail.",e);
							}
						}
					}
				}
			}
			loginDo.setResult("true,"+loginChannel);
		}else {
			loginDo.setResult("true");
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
		if (failCount == -1) {
			jo.put("flag", "Y");
		} else {
			jo.put("flag", "N");
		}
		String loginWifiMacKey = Constants.CACHEKEY_USER_LOGIN_WIFI_MAC + afUserDo.getRid();
		bizCacheUtil.saveObject(loginWifiMacKey, wifiMac);

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
				if (bank == null){
					cardNumber = bank.getCardNumber();
				}
				baiQiShiUtils.getLoginResult(requestDataVo.getId(),bqsBlackBox, ip, afUserDo.getMobile(),afUserDo.getRealName(),idNumber,cardNumber,openId);
			}catch (Exception e){
				logger.error("loginApi baiQiShiUtils getLoginResult error => {}",e.getMessage());
			}
		}
		if (context.getAppVersion() >= 381) {
			riskUtil.verifyASyLogin(ObjectUtils.toString(afUserDo.getRid(), ""), userName, blackBox, uuid, loginType,
					loginTime, ip, phoneType, networkType, osType, SUCC, Constants.EVENT_LOGIN_ASY,bqsBlackBox);
		}

		resp.setResponseData(jo);

//		if (failCount == -1) {
//			new Timer().schedule(new TimerTask() {
//				public void run() {
//					jpushService.jPushCoupon("COUPON_POPUPS", userName);
//					this.cancel();
//				}
//			}, 1000 * 5);// 一分钟
//		}
		
//		//首次登陆，弹窗
//		long successTime =  afUserLoginLogService.getCountByUserNameAndResultTrue(userName);
//		if(successTime <= 1){
//		        new Timer().schedule(new TimerTask() {
//		        public void run() {
//		        	jpushService.jPushCoupon("COUPON_POPUPS", userName);
//		        	this.cancel();
//		        	}
//		        }, 1000 * 5);// 一分钟
//		}
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
						if (result.indexOf("success") > -1) {
							Long rid = tdo.getRid();
							Long userIdToutiao = context.getUserId() == null ? -1l : context.getUserId();
							String userNameToutiao = context.getUserName() == null ? "" : context.getUserName();
							afUserToutiaoService.uptUserActive(rid, userIdToutiao, userNameToutiao);
						}
						logger.error("toutiaoactive:update success,active=1,callbacr_url=" + callbackUrl + ",result="
								+ result);
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
	 * @param str
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
		System.out.println(UserUtil.getPassword(MD5.digest("123456"), "698b6fc6fc689bed"));
	}
}
