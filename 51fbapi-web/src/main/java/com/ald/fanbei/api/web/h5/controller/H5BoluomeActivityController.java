package com.ald.fanbei.api.web.h5.controller;


import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.bo.TokenBo;
import com.ald.fanbei.api.biz.service.AfH5BoluomeActivityService;
import com.ald.fanbei.api.biz.service.AfPromotionChannelPointService;
import com.ald.fanbei.api.biz.service.AfPromotionChannelService;
import com.ald.fanbei.api.biz.service.AfPromotionLogsService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfSmsRecordService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.service.AfUserLoginLogService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.third.util.SmsUtil;
import com.ald.fanbei.api.biz.third.util.TongdunUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.CookieUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.SmsType;
import com.ald.fanbei.api.common.enums.UserStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.CommonUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.common.util.UserUtil;
import com.ald.fanbei.api.dal.dao.AfCouponDao;
import com.ald.fanbei.api.dal.dao.AfUserCouponDao;
import com.ald.fanbei.api.dal.domain.AfBoluomeActivityUserLoginDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfSmsRecordDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.AfUserLoginLogDo;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.AfUserVo;
import com.alibaba.fastjson.JSON;
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
SmsUtil smsUtil;

@Resource
AfH5BoluomeActivityService afH5BoluomeActivityService;


	//菠萝觅活动登录
	@RequestMapping(value = "/boluomeActivityLogin", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String boluomeActivityLogin(HttpServletRequest request,HttpServletResponse response ,ModelMap model){
		
		
		String userName = ObjectUtils.toString(request.getParameter("userName"), "").toString();
		String password = ObjectUtils.toString(request.getParameter("password"),"").toString();
		Long boluomeActivityId = NumberUtil.objToLong(request.getParameter("activityId"));
		String refUseraName = ObjectUtils.toString(request.getParameter("refUserName"),"").toString();
		AfUserDo UserDo = afUserService.getUserByUserName(userName);
		AfUserDo refUserDo = afUserService.getUserByUserName(refUseraName);
		
		String cacheKey = Constants.BOLUOME_LOGIN_ERROR_TIMES + userName;
		int errorCount =  NumberUtil.objToIntDefault((bizCacheUtil.getObject(cacheKey)), 0);
		if(errorCount<5){
		
		//被邀请者登录验证
		if (userName == null || userName.isEmpty()) {
			return H5CommonResponse.getNewInstance(false, "请输入账号", "", "").toString();
		}
		if (password == null || password.isEmpty()) {
			return H5CommonResponse.getNewInstance(false, "请输入密码", "", "").toString();
		}
		
		if (UserDo == null) {
			return H5CommonResponse.getNewInstance(false, "用户不存在", "", "").toString();
		}
		if (StringUtils.equals(UserDo.getStatus(), UserStatus.FROZEN.getCode())) {
			return H5CommonResponse.getNewInstance(false, "用户账号冻结", "", "").toString();
		}
		// check password
		String inputPassword = UserUtil.getPassword(password, UserDo.getSalt());
		
		
		if (!StringUtils.equals(inputPassword, UserDo.getPassword())) {
			// fail count add 1
				errorCount = errorCount+1; 
				bizCacheUtil.saveObject(cacheKey, errorCount, Constants.SECOND_OF_HALF_HOUR);
				FanbeiExceptionCode code =  getErrorCountCode(errorCount);
				return H5CommonResponse.getNewInstance(false, code.getErrorMsg(), "", "").toString();
		}	
		
		        bizCacheUtil.delCache(cacheKey);
				// save token to cache
				String token = UserUtil.generateToken(userName);
			   				
				CookieUtil.writeCookie(response, "userName", userName, Constants.MINITS_OF_HALF_HOUR);
				CookieUtil.writeCookie(response, "token", token, Constants.MINITS_OF_HALF_HOUR);
				
				//绑定关系refUserDo
				AfBoluomeActivityUserLoginDo afBoluomeActivityUserLogin  = new AfBoluomeActivityUserLoginDo(); 
				afBoluomeActivityUserLogin.setUserId(UserDo.getRid());
				afBoluomeActivityUserLogin.setUserName(UserDo.getUserName());
				afBoluomeActivityUserLogin.setBoluomeActivityId(boluomeActivityId);
				afBoluomeActivityUserLogin.setRefUserId(refUserDo.getRid());
				afBoluomeActivityUserLogin.setRefUserName(refUserDo.getUserName());
			    int saveInfo = afH5BoluomeActivityService.saveUserLoginInfo(afBoluomeActivityUserLogin);
		        
		}else{
			return H5CommonResponse.getNewInstance(false, "账号已经锁定", "", "").toString();
		}
		return H5CommonResponse.getNewInstance(true, "登录成功", "", "").toString();
	}
	
	
	//提交菠萝觅活动注册
	@ResponseBody
	@RequestMapping(value = "/commitBouomeActivityRegister", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	public String commitRegister(HttpServletRequest request, ModelMap model) throws IOException {
		Calendar calStart = Calendar.getInstance();
		String resultStr = "";
		
		try {
			String mobile = ObjectUtils.toString(request.getParameter("registerMobile"), "").toString();
			String verifyCode = ObjectUtils.toString(request.getParameter("smsCode"), "").toString();
			String passwordSrc = ObjectUtils.toString(request.getParameter("password"), "").toString();
			String recommendCode = ObjectUtils.toString(request.getParameter("recommendCode"), "").toString();
			String token = ObjectUtils.toString(request.getParameter("token"), "").toString();
			
			
			AfUserDo eUserDo = afUserService.getUserByUserName(mobile);
			if (eUserDo != null) {
				return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_REGIST_ACCOUNT_EXIST.getDesc(), "", null).toString();

			}
			AfSmsRecordDo smsDo = afSmsRecordService.getLatestByUidType(mobile, SmsType.REGIST.getCode());
			if (smsDo == null) {
				logger.error("sms record is empty");
				resultStr = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.SMS_MOBILE_ERROR.getDesc(), "", null).toString();
				return resultStr;
			}

			String realCode = smsDo.getVerifyCode();
			if (!StringUtils.equals(verifyCode, realCode)) {
				logger.error("verifyCode is invalid");
				resultStr =  H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_REGIST_SMS_ERROR.getDesc(), "", null).toString();
				return resultStr;
			}
			if (smsDo.getIsCheck() == 1) {
				logger.error("verifyCode is already invalid");
				resultStr =  H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_REGIST_SMS_ALREADY_ERROR.getDesc(), "", null).toString();
				return resultStr;
			}
			// 判断验证码是否过期
			if (DateUtil.afterDay(new Date(), DateUtil.addMins(smsDo.getGmtCreate(), Constants.MINITS_OF_HALF_HOUR))) {
				resultStr =  H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_REGIST_SMS_OVERDUE.getDesc(), "", null).toString();
				return resultStr;

			}
			try {
				tongdunUtil.getPromotionResult(token,null,null,CommonUtil.getIpAddr(request),mobile, mobile, "");
			} catch (Exception e) {
				resultStr = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.TONGTUN_FENGKONG_REGIST_ERROR.getDesc(), "", null).toString();
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
			if (!StringUtils.isBlank(recommendCode)) {
				AfUserDo userRecommendDo = afUserService.getUserByRecommendCode(recommendCode);
				userDo.setRecommendId(userRecommendDo.getRid());
			}
			afUserService.addUser(userDo);

			Long invteLong = Constants.INVITE_START_VALUE + userDo.getRid();
			String inviteCode = Long.toString(invteLong, 36);
			userDo.setRecommendCode(inviteCode);
			afUserService.updateUser(userDo);

			// 获取邀请分享地址
			AfResourceDo resourceCodeDo = afResourceService.getSingleResourceBytype(AfResourceType.AppDownloadUrl.getCode());
			String appDownLoadUrl = "";
			if (resourceCodeDo != null) {
				appDownLoadUrl = resourceCodeDo.getValue();
			}
			resultStr = H5CommonResponse.getNewInstance(true, "成功", appDownLoadUrl, null).toString();
			
			// 注册成功给用户发送注册短信
//			smsUtil.sendRegisterSuccessSms(userDo.getUserName());
			return resultStr;

		}catch(FanbeiException e){
			logger.error("commitRegister fanbei exception"+e.getMessage());
			resultStr = H5CommonResponse.getNewInstance(false, "失败", "", null).toString();
			return resultStr;
		} catch (Exception e) {
			logger.error("commitRegister exception",e);
			resultStr = H5CommonResponse.getNewInstance(false, "失败", "", null).toString();
			return resultStr;
		}finally{
			doLog(request, resultStr,"", Calendar.getInstance().getTimeInMillis()-calStart.getTimeInMillis(),request.getParameter("registerMobile"));
		}

	}
	
	//菠萝觅活动忘记密码
	@ResponseBody
	@RequestMapping(value = "/boluomeActivityForgetPwd", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	public String boluomeActivityForgetPwd(HttpServletRequest request, ModelMap model) throws IOException {
		String mobile = ObjectUtils.toString(request.getParameter("mobile"), "").toString();
		AfUserDo afUserDo = new AfUserDo(); 
		afUserDo = afUserService.getUserByUserName(mobile);
		String resultStr = "";
		if (afUserDo == null) {
			resultStr = H5CommonResponse.getNewInstance(false, "用户不存在", "", null).toString();
		}

		boolean resultForget = smsUtil.sendBoluomeForgetPwdVerifyCode(mobile, afUserDo.getRid());
		if (!resultForget) {
			
			resultStr = H5CommonResponse.getNewInstance(false, "用户发送验证码失败", "", null).toString();
		}
		return resultStr;
	}
	//菠萝觅活动重置密码
	@ResponseBody
	@RequestMapping(value = "/boluomeActivityResetPwd", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	public String boluomeActivityResetPwd(HttpServletRequest request, ModelMap model) throws IOException {
	//	String mobile = ObjectUtils.toString(request.getParameter("mobile"), "").toString();
		String userName = ObjectUtils.toString(request.getParameter("mobile"), "").toString();
		String passwordSrc = ObjectUtils.toString(request.getParameter("password"), "").toString();
		String verifyCode = ObjectUtils.toString(request.getParameter("verifyCode"), "").toString();
		String resultStr = "";
		
		if(StringUtil.isBlank(passwordSrc)){
			resultStr = H5CommonResponse.getNewInstance(false, "参数错误", "", null).toString();
        }
        AfSmsRecordDo smsDo = afSmsRecordService.getLatestByUidType(userName, SmsType.BOLUOOME_FORGET_PASS.getCode());
        if(smsDo == null){
    		resultStr = H5CommonResponse.getNewInstance(false, "参数错误", "", null).toString();
        }
        //判断验证码是否一致并且验证码是否已经做过验证
        String realCode = smsDo.getVerifyCode();
        if(!StringUtils.equals(verifyCode, realCode) || smsDo.getIsCheck() == 0){
        	resultStr = H5CommonResponse.getNewInstance(false, "参数错误", "", null).toString();
        }
        //判断验证码是否过期
        if(DateUtil.afterDay(new Date(), DateUtil.addMins(smsDo.getGmtCreate(), Constants.MINITS_OF_HALF_HOUR))){
        	resultStr = H5CommonResponse.getNewInstance(false, "验证码已经过期", "", null).toString();
        }
        
        AfUserDo afUserDo = afUserService.getUserByUserName(userName);
        if(afUserDo == null){
        	resultStr = H5CommonResponse.getNewInstance(false,"用户不存在", "", null).toString();
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
		return resultStr;
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
              throw new FanbeiException("参数格式错误"+e.getMessage(), FanbeiExceptionCode.REQUEST_PARAM_ERROR);
          }
    }
    
    @Override
    public String checkCommonParam(String reqData, HttpServletRequest request, boolean isForQQ) {
        return null;
    }
 
    @Override
    public String doProcess(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest httpServletRequest) {
        return null;
    }

}
