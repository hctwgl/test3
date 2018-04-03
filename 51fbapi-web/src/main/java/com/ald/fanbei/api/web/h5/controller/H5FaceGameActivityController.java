package com.ald.fanbei.api.web.h5.controller;

import java.util.Date;

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

import com.ald.fanbei.api.biz.service.AfSmsRecordService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.third.util.TongdunUtil;
import com.ald.fanbei.api.biz.third.util.baiqishi.BaiQiShiUtils;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.CookieUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.SmsType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.CommonUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.UserUtil;
import com.ald.fanbei.api.dal.domain.AfSmsRecordDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
@Controller
@RequestMapping("/fanbei_api/faceScore")
public class H5FaceGameActivityController extends BaseController {
	
	@Resource
	private AfUserService afUserService;
	@Resource
	private AfSmsRecordService afSmsRecordService;
	@Resource
	private TongdunUtil tongdunUtil;
	@Resource
	private BaiQiShiUtils baiQiShiUtils;
	@Resource
    BizCacheUtil bizCacheUtil;
	 // 菠萝觅活动登录
	@ResponseBody
    @RequestMapping(value = "/registerWithCash", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    public String registerWithCash(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		 String resultStr = "";
		 String referer = request.getHeader("referer"); 
		try {
		    String moblie = ObjectUtils.toString(request.getParameter("registerMobile"), "").toString();
		    String verifyCode = ObjectUtils.toString(request.getParameter("smsCode"), "").toString();
		    String passwordSrc = ObjectUtils.toString(request.getParameter("password"), "").toString();
		    String token = ObjectUtils.toString(request.getParameter("token"), "").toString();
		    String bsqToken = ObjectUtils.toString(request.getParameter("bsqToken"), "").toString();

		    AfUserDo eUserDo = afUserService.getUserByUserName(moblie);
		    if (eUserDo != null) {
			return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_REGIST_ACCOUNT_EXIST.getDesc(), "Register", null).toString();

		    }
		    AfSmsRecordDo smsDo = afSmsRecordService.getLatestByUidType(moblie, SmsType.REGIST.getCode());
		    if (smsDo == null) {
			logger.error("sms record is empty");
			resultStr = H5CommonResponse.getNewInstance(false, "手机号与验证码不匹配", "", null).toString();
			return resultStr;
		    }

		    String realCode = smsDo.getVerifyCode();
		    if (!StringUtils.equals(verifyCode, realCode)) {
			logger.error("verifyCode is invalid");
			resultStr = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_REGIST_SMS_ERROR.getDesc(), "", null).toString();
			return resultStr;
		    }
		    if (smsDo.getIsCheck() == 1) {
			logger.error("verifyCode is already invalid");
			resultStr = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_REGIST_SMS_ALREADY_ERROR.getDesc(), "", null).toString();
			return resultStr;
		    }
		    // 判断验证码是否过期
		    if (DateUtil.afterDay(new Date(), DateUtil.addMins(smsDo.getGmtCreate(), Constants.MINITS_OF_HALF_HOUR))) {
			resultStr = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_REGIST_SMS_OVERDUE.getDesc(), "", null).toString();
			return resultStr;

		    }
		    try {
			tongdunUtil.getPromotionResult(token, null, null, CommonUtil.getIpAddr(request), moblie, moblie, "");
		    } catch (Exception e) {
			resultStr = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.TONGTUN_FENGKONG_REGIST_ERROR.getDesc(), "", null).toString();
			return resultStr;
		    }
			try {
				baiQiShiUtils.getRegistResult("h5",bsqToken,CommonUtil.getIpAddr(request),moblie,"","","","");
			}catch (Exception e){
				logger.error("h5Common commitRegisterLogin baiQiShiUtils getRegistResult error => {}",e.getMessage());
			}
		    // 更新为已经验证
		    afSmsRecordService.updateSmsIsCheck(smsDo.getRid());

		    String salt = UserUtil.getSalt();
		    String password = UserUtil.getPassword(passwordSrc, salt);

		    AfUserDo userDo = new AfUserDo();
		    userDo.setSalt(salt);
		    userDo.setUserName(moblie);
		    userDo.setMobile(moblie);
		    userDo.setNick("");
		    userDo.setPassword(password);
		    userDo.setRecommendId(0l);
		    
		    long userId = afUserService.addUser(userDo);

		    String appDownLoadUrl = "";
		    resultStr = H5CommonResponse.getNewInstance(true, "注册成功", appDownLoadUrl, null).toString();
		    // save token to cache 记住登录状态
		    String userName = moblie;
		    String newtoken = UserUtil.generateToken(userName);
		    String tokenKey = Constants.H5_CACHE_USER_TOKEN_COOKIES_KEY + userName;
		    CookieUtil.writeCookie(response, Constants.H5_USER_NAME_COOKIES_KEY, moblie, Constants.SECOND_OF_HALF_HOUR_INT);
		    CookieUtil.writeCookie(response, Constants.H5_USER_TOKEN_COOKIES_KEY, token, Constants.SECOND_OF_HALF_HOUR_INT);
		  //  bizCacheUtil.saveObject(tokenKey, newtoken, Constants.SECOND_OF_HALF_HOUR);
		    //埋点
		    //doMaidianLog(request, H5CommonResponse.getNewInstance(true, "succ"),referer);
		    bizCacheUtil.saveObject(tokenKey, newtoken, Constants.SECOND_OF_HALF_DAY);
		    return H5CommonResponse.getNewInstance(true, "登录成功", "",newtoken).toString();

		} catch (FanbeiException e) {
		    logger.error("commitRegister fanbei exception" + e.getMessage());
		    resultStr = H5CommonResponse.getNewInstance(false, "失败", "", null).toString();
		    return resultStr;
		} catch (Exception e) {
		    logger.error("commitRegister exception", e);
		    resultStr = H5CommonResponse.getNewInstance(false, "失败", "", null).toString();
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
	public String checkCommonParam(String reqData, HttpServletRequest request,
			boolean isForQQ) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RequestDataVo parseRequestData(String requestData,
			HttpServletRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BaseResponse doProcess(RequestDataVo requestDataVo,
			FanbeiContext context, HttpServletRequest httpServletRequest) {
		// TODO Auto-generated method stub
		return null;
	}

}
