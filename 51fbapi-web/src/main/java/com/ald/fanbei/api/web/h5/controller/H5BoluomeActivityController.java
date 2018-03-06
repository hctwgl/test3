package com.ald.fanbei.api.web.h5.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.bo.BoluomeCouponResponseBo;
import com.ald.fanbei.api.biz.bo.BoluomeCouponResponseParentBo;
import com.ald.fanbei.api.biz.bo.BrandActivityCouponResponseBo;
import com.ald.fanbei.api.biz.bo.PickBrandCouponRequestBo;
import com.ald.fanbei.api.biz.bo.ThirdResponseBo;
import com.ald.fanbei.api.biz.service.AfBoluomeActivityCouponService;
import com.ald.fanbei.api.biz.service.AfBoluomeOneYuanRegisterService;
import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfSmsRecordService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.service.AfUserLoginLogService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.service.boluome.BoluomeUtil;
import com.ald.fanbei.api.biz.third.util.SmsUtil;
import com.ald.fanbei.api.biz.third.util.TongdunUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.CookieUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.SmsType;
import com.ald.fanbei.api.common.enums.UserStatus;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.CommonUtil;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.common.util.UserUtil;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.dal.domain.AfBoluomeActivityCouponDo;
import com.ald.fanbei.api.dal.domain.AfBoluomeActivityUserLoginDo;
import com.ald.fanbei.api.dal.domain.AfBoluomeOneYuanRegisterDo;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfSmsRecordDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * @类描述：
 * 
 * @author :chenqiwei
 * @version ：2017年8月3日 下午14:00:28
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping("/H5GGShare")
public class H5BoluomeActivityController extends BaseController {

    @Resource
    AfUserService afUserService;
    @Resource
    AfUserLoginLogService afUserLoginLogService;
    @Resource
    AfUserAuthService afUserAuthService;
    @Resource
    BizCacheUtil bizCacheUtil;
    @Resource
    AfResourceService afResourceService;
    @Resource
    AfSmsRecordService afSmsRecordService;
    @Resource
    TongdunUtil tongdunUtil;
    @Resource
    AfOrderService afOrderService;
    @Resource
    SmsUtil smsUtil;
    @Resource
    AfBoluomeActivityCouponService afBoluomeActivityCouponService;
    @Resource
    AfBoluomeOneYuanRegisterService  afBoluomeOneYuanRegisterService;
    @Resource
    BoluomeUtil boluomeUtil;
    
    
    private static String couponUrl = null;

    // 菠萝觅活动登录
    @RequestMapping(value = "/boluomeActivityLogin", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String boluomeActivityLogin(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
	String loginSource = ObjectUtils.toString(request.getParameter("urlName"), "").toString();
	String userName = ObjectUtils.toString(request.getParameter("userName"), "").toString();
	String password = ObjectUtils.toString(request.getParameter("password"), "").toString();
	String tongduanToken = ObjectUtils.toString(request.getParameter("token"), "").toString();
//	String typeFrom = ObjectUtils.toString(request.getParameter("typeFrom"), "").toString();
//      String typeFromNum = ObjectUtils.toString(request.getParameter("typeFromNum"), "").toString();

     try{
	AfUserDo UserDo = afUserService.getUserByUserName(userName);
//	  AfUserDo refUserDo = new AfUserDo();
//	if (refUseraName != null && StringUtil.isNotBlank(refUseraName)) {
//	     refUserDo = afUserService.getUserByUserName(refUseraName);
//	}
//
	if (loginSource == null || "".equals(loginSource)) {
	    if (CookieUtil.getCookie(request, "urlName") != null) {
		loginSource = CookieUtil.getCookie(request, "urlName").getValue();
	    }
	}

	String cacheKey = Constants.BOLUOME_LOGIN_ERROR_TIMES + userName;
	int errorCount = NumberUtil.objToIntDefault((bizCacheUtil.getObject(cacheKey)), 0);
	if (errorCount < 5) {

	    // 被邀请者登录验证
	    if (userName == null || userName.isEmpty()) {
		return H5CommonResponse.getNewInstance(false, "请输入账号", "Login", "").toString();

	    }
	    if (password == null || password.isEmpty()) {
		return H5CommonResponse.getNewInstance(false, "请输入密码", "Login", "").toString();
	    }

	    if (UserDo == null) {
		return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_NOT_EXIST_ERROR.getDesc(), "DownLoad", "").toString();
	    }
	    if (StringUtils.equals(UserDo.getStatus(), UserStatus.FROZEN.getCode())) {
		return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_FROZEN_ERROR.getDesc(), "Login", "").toString();
	    }
	    try {
		tongdunUtil.getPromotionLoginResult(tongduanToken, null, null, CommonUtil.getIpAddr(request), userName, userName, "");
	    } catch (Exception e) {
		return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.TONGTUN_FENGKONG_LOGIN_ERROR.getDesc(), "Login", null).toString();

	    }
	    // check password
	    String inputPassword = UserUtil.getPassword(password, UserDo.getSalt());

	    if (!StringUtils.equals(inputPassword, UserDo.getPassword())) {
		// fail count add 1
		errorCount = errorCount + 1;
		bizCacheUtil.saveObject(cacheKey, errorCount, Constants.SECOND_OF_HALF_HOUR);
		FanbeiExceptionCode code = getErrorCountCode(errorCount);
		return H5CommonResponse.getNewInstance(false, code.getDesc(), "Login", "").toString();
	    }

	    bizCacheUtil.delCache(cacheKey);
	    // save token to cache
	    String token = UserUtil.generateToken(userName);
	    String tokenKey = Constants.H5_CACHE_USER_TOKEN_COOKIES_KEY + userName;
	    CookieUtil.writeCookie(response, Constants.H5_USER_NAME_COOKIES_KEY, userName, Constants.SECOND_OF_HALF_HOUR_INT);
	    CookieUtil.writeCookie(response, Constants.H5_USER_TOKEN_COOKIES_KEY, token, Constants.SECOND_OF_HALF_HOUR_INT);
	    bizCacheUtil.saveObject(tokenKey, token, Constants.SECOND_OF_HALF_HOUR);
 
	} else {
	    return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_PASSWORD_ERROR_GREATER_THAN5.getDesc(), "Login", "").toString();
	}
     }catch (Exception e){
		logger.error("boluomeActivityLogin error",e.getMessage());
	}
	return H5CommonResponse.getNewInstance(true, "登录成功", "", "").toString();
    }

    // 提交菠萝觅活动注册
    @ResponseBody
    @RequestMapping(value = "/boluomeActivityRegisterLogin", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    public String bouomeActivityRegisterLogin(HttpServletRequest request, HttpServletResponse response, ModelMap model) throws IOException {
	
	String resultStr = "";
	try {
	    String mobile = ObjectUtils.toString(request.getParameter("registerMobile"), "").toString();
  	    String inviteer = ObjectUtils.toString(request.getParameter("inviteer"), "").toString();
	    String verifyCode = ObjectUtils.toString(request.getParameter("smsCode"), "").toString();
	    String passwordSrc = ObjectUtils.toString(request.getParameter("password"), "").toString();
	    String recommendCode = ObjectUtils.toString(request.getParameter("recommendCode"), "").toString();
	    String token = ObjectUtils.toString(request.getParameter("token"), "").toString();
	    Long boluomeActivityId = NumberUtil.objToLong(request.getParameter("activityId"));
	    String typeFrom = ObjectUtils.toString(request.getParameter("typeFrom"), "").toString();
	    String typeFromNum = ObjectUtils.toString(request.getParameter("typeFromNum"), "").toString();
	    	
	    String log = "/H5GGShare/boluomeActivityRegisterLogin";
	   
	    
	    AfUserDo eUserDo = afUserService.getUserByUserName(mobile);
	    log = log + String.format("mobile and inviteer and recommendCode %s", mobile+"inviteer = "+inviteer+"recommendCode = "+recommendCode);
	    logger.info(log);
	    if (eUserDo != null) {
		logger.error("boluomeActivityRegisterLogin user regist account exist",mobile);
		return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_REGIST_ACCOUNT_EXIST.getDesc(), "Register", null).toString();

	    }
	    AfSmsRecordDo smsDo = afSmsRecordService.getLatestByUidType(mobile, SmsType.REGIST.getCode());
	    if (smsDo == null) {
		logger.error("boluomeActivityRegisterLogin sms record is empty",mobile);
		resultStr = H5CommonResponse.getNewInstance(false, "手机号与验证码不匹配", "Register", null).toString();
		return resultStr;
	    }

	    String realCode = smsDo.getVerifyCode();
	    if (!StringUtils.equals(verifyCode, realCode)) {
		logger.error("boluomeActivityRegisterLogin verifyCode is invalid",mobile);
		resultStr = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_REGIST_SMS_ERROR.getDesc(), "Register", null).toString();
		return resultStr;
	    }
	    if (smsDo.getIsCheck() == 1) {
		logger.error("boluomeActivityRegisterLogin verifyCode is already invalid",mobile);
		resultStr = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_REGIST_SMS_ALREADY_ERROR.getDesc(), "Register", null).toString();
		return resultStr;
	    }
	    // 判断验证码是否过期
	    if (DateUtil.afterDay(new Date(), DateUtil.addMins(smsDo.getGmtCreate(), Constants.MINITS_OF_HALF_HOUR))) {
		logger.error("boluomeActivityRegisterLogin user regist sms overdue",mobile);
		resultStr = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_REGIST_SMS_OVERDUE.getDesc(), "Register", null).toString();
		return resultStr;

	    }
	    try {
		tongdunUtil.getPromotionResult(token, null, null, CommonUtil.getIpAddr(request), mobile, mobile, "");
	    } catch (Exception e) {
		logger.error("boluomeActivityRegisterLogin tongtun fengkong error",mobile);
		resultStr = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.TONGTUN_FENGKONG_REGIST_ERROR.getDesc(), "Register", null).toString();
		return resultStr;
	    }

	    // 更新为已经验证
	    afSmsRecordService.updateSmsIsCheck(smsDo.getRid());

	    String salt = UserUtil.getSalt();
	    String password = UserUtil.getPassword(passwordSrc, salt);

	    AfUserDo userDo = new AfUserDo();
	    userDo.setSalt(salt);
	    userDo.setUserName(mobile);
	    userDo.setMobile(mobile);
	    userDo.setNick("");
	    userDo.setPassword(password);
	    userDo.setRecommendId(0l);
	    //邀请码
	    if (!StringUtils.isBlank(recommendCode)) {
		AfUserDo userRecommendDo = afUserService.getUserByRecommendCode(recommendCode);
		userDo.setRecommendId(userRecommendDo.getRid());
	    }
	    if (!StringUtils.isBlank(inviteer)) {
		AfUserDo user = afUserService.getUserByUserName(inviteer);
		if(user != null){
		 userDo.setRecommendId(user.getRid());
		}
	    }
	    logger.info("boluomeActivityRegisterLogin userDo = "+JSONObject.toJSONString(userDo));
	    String source = "oneYuan";
	    Long userId = afUserService.toAddUser(userDo,source);
	    logger.info("boluomeActivityRegisterLogin userId = "+userId+" mobile = "+mobile);
	    //渠道
	    try{
	    if(1000L == boluomeActivityId.longValue()){
		if(!StringUtils.isBlank(typeFrom)){
		    AfBoluomeOneYuanRegisterDo register   = new AfBoluomeOneYuanRegisterDo();
		    register.setGmtCreate(new Date());
		    register.setGmtModified(new Date());
		    register.setMobile(mobile);
		    register.setTypeFrom(typeFrom);
		    register.setTypeFromNum(typeFromNum);
		    register.setInviter(inviteer);
		    if (!StringUtils.isBlank(recommendCode)) {
			AfUserDo userRecommendDo = afUserService.getUserByRecommendCode(recommendCode);
			if(userRecommendDo != null){
			     register.setInviter(userRecommendDo.getUserName());
			  }
		    }
		    
		    afBoluomeOneYuanRegisterService.saveRecord(register);
		}
	    }
	    }catch (FanbeiException e) {
        	logger.error("save qudao info exception" + e.getMessage());
     		  
            } 
	    
	    Long invteLong = Constants.INVITE_START_VALUE + userId;
	    String inviteCode = Long.toString(invteLong, 36);
	    userDo.setRecommendCode(inviteCode);
	    afUserService.updateUser(userDo);
	    // 获取邀请分享地址
	    String appDownLoadUrl = "";
//	    AfResourceDo resourceCodeDo = afResourceService.getSingleResourceBytype(AfResourceType.AppDownloadUrl.getCode());
//	    if (resourceCodeDo != null) {
//		appDownLoadUrl = resourceCodeDo.getValue();
//	    }
	    resultStr = H5CommonResponse.getNewInstance(true, "注册成功", appDownLoadUrl, null).toString();
	
	    // save token to cache
            String  token1 = UserUtil.generateToken(mobile);
	    String tokenKey = Constants.H5_CACHE_USER_TOKEN_COOKIES_KEY + mobile;
	    CookieUtil.writeCookie(response, Constants.H5_USER_NAME_COOKIES_KEY, mobile, Constants.SECOND_OF_HALF_HOUR_INT);
	    CookieUtil.writeCookie(response, Constants.H5_USER_TOKEN_COOKIES_KEY, token, Constants.SECOND_OF_HALF_HOUR_INT);
	    bizCacheUtil.saveObject(tokenKey, token1, Constants.SECOND_OF_HALF_HOUR);


	    log = log + String.format(mobile+"注册成功"+"inviteer:"+inviteer);
//	    try{
//		//绑定关系开关
//		  AfResourceDo biddingSwitch =   afResourceService.getConfigByTypesAndSecType("GG_ACTIVITY","BIDDING_SWITCH");
//		    if(biddingSwitch != null){
//			if("O".equals(biddingSwitch.getValue())){
//			    if (inviteer != null && !"".equals(inviteer)){
//				if (!inviteer.equals(mobile)) {
//        		  	       	// 绑定关系mobile
//        			        AfUserDo afUserDo =  afUserService.getUserByUserName(mobile);
//        			        AfUserDo refUserDo =  afUserService.getUserByUserName(inviteer);
//        			        logger.info("/H5GGShare/boluomeActivityRegisterLogin afUserDo = {}, refUserDo = {}",JSONObject.toJSONString(afUserDo),JSONObject.toJSONString(refUserDo));
//        			        if(StringUtils.isEmpty(boluomeActivityId.toString())){
//        			            boluomeActivityId  = 1000L;
//        			        }
//        		  		if(afUserDo !=  null && refUserDo != null){
//                		  		AfBoluomeActivityUserLoginDo afBoluomeActivityUserLogin = new AfBoluomeActivityUserLoginDo();
//                		  		afBoluomeActivityUserLogin.setUserId(afUserDo.getRid());
//                		  		afBoluomeActivityUserLogin.setUserName(afUserDo.getUserName());
//                		  		afBoluomeActivityUserLogin.setBoluomeActivityId(boluomeActivityId);
//                		  		afBoluomeActivityUserLogin.setRefUserId(refUserDo.getRid());
//                		  		afBoluomeActivityUserLogin.setRefUserName(refUserDo.getUserName());
//                		  		afH5BoluomeActivityService.saveUserLoginInfo(afBoluomeActivityUserLogin);
//                		  		logger.info("/H5GGShare/boluomeActivityRegisterLogin saveUserLoginInfo afBoluomeActivityUserLogin = {}",JSONObject.toJSONString(afBoluomeActivityUserLogin));
//        		  		}
//        		    }
//        	    }
//		}
//	      }
//	    }catch (FanbeiException e) {
//        	logger.error("save boluomeActivity user binding exception" + e.getMessage());
//        		  
//            } 
	    
	    return resultStr;

	} catch (FanbeiException e) {
	    logger.error("commitRegister fanbei exception" + e.getMessage());
	    resultStr = H5CommonResponse.getNewInstance(false, "失败", "Register", null).toString();
	    return resultStr;
	} catch (Exception e) {
	    logger.error("commitRegister exception", e);
	    resultStr = H5CommonResponse.getNewInstance(false, "失败", "Register", null).toString();
	    return resultStr;
	} finally {

	}

    }


    
    // 菠萝觅活动忘记密码获取短信验证码
    @ResponseBody
    @RequestMapping(value = "/boluomeActivityForgetPwd", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    public String boluomeActivityForgetPwd(HttpServletRequest request, ModelMap model) throws IOException {
	String resultStr = "";
	try {
	    String mobile = ObjectUtils.toString(request.getParameter("mobile"), "").toString();
	    String verifyImgCode = ObjectUtils.toString(request.getParameter("verifyImgCode"), "").toString();
	    String token = ObjectUtils.toString(request.getParameter("token"), "").toString();

	    AfUserDo afUserDo = new AfUserDo();
	    afUserDo = afUserService.getUserByUserName(mobile);

	    if (afUserDo == null) {
		resultStr = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_NOT_EXIST_ERROR.getDesc(), "ForgetPwd", null).toString();
	    }
	    try {
		tongdunUtil.getPromotionForgetPwdSmsResult(token, null, null, CommonUtil.getIpAddr(request), mobile, mobile, "");
	    } catch (Exception e) {
		resultStr = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.TONGTUN_FENGKONG_REGISTER_PWD_ERROR.getDesc(), "Register", null).toString();
		return resultStr;
	    }
	    // 发送短信前,加入图片验证码验证
	    String realCode = bizCacheUtil.getObject(Constants.CACHEKEY_CHANNEL_IMG_CODE_PREFIX + mobile).toString();

	    if (!realCode.toLowerCase().equals(verifyImgCode.toLowerCase())) {// 图片验证码不正确

		resultStr = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_REGIST_IMAGE_ERROR.getDesc(), "", null).toString();
		return resultStr;
	    }
	    bizCacheUtil.delCache(Constants.CACHEKEY_CHANNEL_IMG_CODE_PREFIX + mobile);
	    if (afUserDo != null) {
		boolean resultForget = smsUtil.sendForgetPwdVerifyCode(mobile, afUserDo.getRid());
		if (!resultForget) {
		    resultStr = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_SEND_SMS_ERROR.getDesc(), "ForgetPwd", null).toString();
		} else {
		    resultStr = H5CommonResponse.getNewInstance(true, "用户发送验证码成功", "ForgetPwd", null).toString();
		}
	    }
	} catch (Exception e) {
	    resultStr = H5CommonResponse.getNewInstance(false, e.getMessage(), "ForgetPwd", null).toString();
	    logger.error("boluomeActivityForgetPwd fanbei exception" + e.getMessage());
	}
	return resultStr;
    }

    // 菠萝觅校验验证码
    @ResponseBody
    @RequestMapping(value = "/boluomeActivityCheckVerifyCode", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    public String checkVerifyCode(HttpServletRequest request, ModelMap model) throws IOException {
	String resultStr = "";
	try {
	    String verifyCode = ObjectUtils.toString(request.getParameter("verifyCode"), "").toString();
	    String userName = ObjectUtils.toString(request.getParameter("mobile"), "").toString();
	    AfSmsRecordDo smsDo = afSmsRecordService.getLatestByUidType(userName, SmsType.FORGET_PASS.getCode());
	    if (smsDo == null) {
		resultStr = H5CommonResponse.getNewInstance(false, "手机号与验证码不匹配", "ForgetPwd", null).toString();
		return resultStr;
	    }
	    // 判断验证码是否一致并且验证码是否已经做过验证
	    String realCode = smsDo.getVerifyCode();
	    if (!StringUtils.equals(verifyCode, realCode) || smsDo.getIsCheck() == 1) {
		resultStr = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_REGIST_SMS_ERROR.getDesc(), "ForgetPwd", null).toString();
		return resultStr;
	    }
	    // 判断验证码是否过期
	    if (DateUtil.afterDay(new Date(), DateUtil.addMins(smsDo.getGmtCreate(), Constants.MINITS_OF_HALF_HOUR))) {
		resultStr = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_REGIST_SMS_OVERDUE.getDesc(), "ForgetPwd", null).toString();
		return resultStr;
	    }

	} catch (Exception e) {
	    return H5CommonResponse.getNewInstance(false, "boluomeActivityCheckVerifyCode fanbei exception", "ForgetPwd", null).toString();
	}
	return H5CommonResponse.getNewInstance(true, "验证码校验成功", "ResetPwd", null).toString();
    }

    // 菠萝觅活动重置密码
    @ResponseBody
    @RequestMapping(value = "/boluomeActivityResetPwd", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    public String boluomeActivityResetPwd(HttpServletRequest request, ModelMap model) throws IOException {
	// String mobile = ObjectUtils.toString(request.getParameter("mobile"),
	// "").toString();
	String userName = ObjectUtils.toString(request.getParameter("mobile"), "").toString();
	String passwordSrc = ObjectUtils.toString(request.getParameter("password"), "").toString();
	String verifyCode = ObjectUtils.toString(request.getParameter("verifyCode"), "").toString();
	String resultStr = "";

	if (StringUtil.isBlank(passwordSrc)) {
	    resultStr = H5CommonResponse.getNewInstance(false, "密码为空", "ResetPwd", null).toString();
	    return resultStr;
	}

	AfSmsRecordDo smsDo = afSmsRecordService.getLatestByUidType(userName, SmsType.FORGET_PASS.getCode());
	if (smsDo == null) {
	    resultStr = H5CommonResponse.getNewInstance(false, "手机号与验证码不匹配", "ResetPwd", null).toString();
	    return resultStr;
	}

	// 判断验证码是否一致并且验证码是否已经做过验证
	String realCode = smsDo.getVerifyCode();
	if (!StringUtils.equals(verifyCode, realCode) || smsDo.getIsCheck() == 1) {
	    resultStr = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_REGIST_SMS_ERROR.getDesc(), "ResetPwd", null).toString();
	    return resultStr;
	}
	// 判断验证码是否过期
	if (DateUtil.afterDay(new Date(), DateUtil.addMins(smsDo.getGmtCreate(), Constants.MINITS_OF_HALF_HOUR))) {
	    resultStr = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_REGIST_SMS_OVERDUE.getDesc(), "ResetPwd", null).toString();
	    return resultStr;
	}

	AfUserDo afUserDo = afUserService.getUserByUserName(userName);
	if (afUserDo == null) {
	    resultStr = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_NOT_EXIST_ERROR.getDesc(), "Register", null).toString();
	    return resultStr;
	}

	String salt = UserUtil.getSalt();
	String password = UserUtil.getPassword(passwordSrc, salt);
	AfUserDo userDo = new AfUserDo();
	userDo.setRid(afUserDo.getRid());
	userDo.setSalt(salt);
	userDo.setPassword(password);
	userDo.setFailCount(0);
	userDo.setUserName(userName);
	afUserService.updateUser(userDo);
	resultStr = H5CommonResponse.getNewInstance(true, "重置密码成功", "Login", null).toString();
	// 验证码改为已验证
	afSmsRecordService.updateSmsIsCheck(smsDo.getRid());
	return resultStr;
    }

    
    // 提交菠萝觅活动注册
    @ResponseBody
    @RequestMapping(value = "/commitBouomeActivityRegister", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    public String commitRegister(HttpServletRequest request, ModelMap model) throws IOException {
	String resultStr = "";
     
	try {
	    String mobile = ObjectUtils.toString(request.getParameter("registerMobile"), "").toString();
	    String verifyCode = ObjectUtils.toString(request.getParameter("smsCode"), "").toString();
	    String passwordSrc = ObjectUtils.toString(request.getParameter("password"), "").toString();
	    String recommendCode = ObjectUtils.toString(request.getParameter("recommendCode"), "").toString();
	    String token = ObjectUtils.toString(request.getParameter("token"), "").toString();
	    String registerSource = ObjectUtils.toString(request.getParameter("urlName"), "").toString();
	    
	    if (registerSource == null || "".equals(registerSource)) {
		if (CookieUtil.getCookie(request, "urlName") != null) {
		    registerSource = CookieUtil.getCookie(request, "urlName").getValue();
		}
	    }

	    AfUserDo eUserDo = afUserService.getUserByUserName(mobile);
	    if (eUserDo != null) {
		return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_REGIST_ACCOUNT_EXIST.getDesc(), "Register", null).toString();

	    }
	    AfSmsRecordDo smsDo = afSmsRecordService.getLatestByUidType(mobile, SmsType.REGIST.getCode());
	    if (smsDo == null) {
		logger.error("sms record is empty");
		resultStr = H5CommonResponse.getNewInstance(false, "手机号与验证码不匹配", "Register", null).toString();
		return resultStr;
	    }

	    String realCode = smsDo.getVerifyCode();
	    if (!StringUtils.equals(verifyCode, realCode)) {
		logger.error("verifyCode is invalid");
		resultStr = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_REGIST_SMS_ERROR.getDesc(), "Register", null).toString();
		return resultStr;
	    }
	    if (smsDo.getIsCheck() == 1) {
		logger.error("verifyCode is already invalid");
		resultStr = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_REGIST_SMS_ALREADY_ERROR.getDesc(), "Register", null).toString();
		return resultStr;
	    }
	    // 判断验证码是否过期
	    if (DateUtil.afterDay(new Date(), DateUtil.addMins(smsDo.getGmtCreate(), Constants.MINITS_OF_HALF_HOUR))) {
		resultStr = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_REGIST_SMS_OVERDUE.getDesc(), "Register", null).toString();
		return resultStr;

	    }
	    try {
		tongdunUtil.getPromotionResult(token, null, null, CommonUtil.getIpAddr(request), mobile, mobile, "");
	    } catch (Exception e) {
		resultStr = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.TONGTUN_FENGKONG_REGIST_ERROR.getDesc(), "Register", null).toString();
		return resultStr;
	    }

	    // 更新为已经验证
	    afSmsRecordService.updateSmsIsCheck(smsDo.getRid());

	    String salt = UserUtil.getSalt();
	    String password = UserUtil.getPassword(passwordSrc, salt);

	    AfUserDo userDo = new AfUserDo();
	    userDo.setSalt(salt);
	    userDo.setUserName(mobile);
	    userDo.setMobile(mobile);
	    userDo.setNick("");
	    userDo.setPassword(password);
	    userDo.setRecommendId(0l);
	    //邀请码
	    if (!StringUtils.isBlank(recommendCode)) {
		AfUserDo userRecommendDo = afUserService.getUserByRecommendCode(recommendCode);
		userDo.setRecommendId(userRecommendDo.getRid());
	    }
	    long userId =   afUserService.addUser(userDo);

	    Long invteLong = Constants.INVITE_START_VALUE + userId;
	    String inviteCode = Long.toString(invteLong, 36);
	    userDo.setRecommendCode(inviteCode);
	    afUserService.updateUser(userDo);

	    // 获取邀请分享地址
	   // AfResourceDo resourceCodeDo = afResourceService.getSingleResourceBytype(AfResourceType.AppDownloadUrl.getCode());
	    String appDownLoadUrl = "";

	    resultStr = H5CommonResponse.getNewInstance(true, "成功", appDownLoadUrl, null).toString();

 	    return resultStr;

	} catch (FanbeiException e) {
	    logger.error("commitRegister fanbei exception" + e.getMessage());
	    resultStr = H5CommonResponse.getNewInstance(false, "失败", "Register", null).toString();
	    return resultStr;
	} catch (Exception e) {
	    logger.error("commitRegister exception", e);
	    resultStr = H5CommonResponse.getNewInstance(false, "失败", "Register", null).toString();
	    return resultStr;
	} finally {

	}

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

    @Override
    public RequestDataVo parseRequestData(String requestData, HttpServletRequest request) {
	try {
	    RequestDataVo reqVo = new RequestDataVo();

	    JSONObject jsonObj = JSON.parseObject(requestData);
	    reqVo.setId(jsonObj.getString("id"));
	    reqVo.setMethod(request.getRequestURI());
	    reqVo.setSystem(jsonObj);
	    return reqVo;
	} catch (Exception e) {
	    throw new FanbeiException("参数格式错误" + e.getMessage(), FanbeiExceptionCode.REQUEST_PARAM_ERROR);
	}
    }

    @Override
    public String checkCommonParam(String reqData, HttpServletRequest request, boolean isForQQ) {
	return null;
    }

    @Override
    public BaseResponse doProcess(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest httpServletRequest) {
	return null;
    }

}
