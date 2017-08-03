package com.ald.fanbei.api.web.h5.controller;


import java.io.IOException;
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
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.service.AfUserLoginLogService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.CookieUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.UserStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.UserUtil;
import com.ald.fanbei.api.dal.domain.AfBoluomeActivityUserLoginDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
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
@RequestMapping("/")
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


AfH5BoluomeActivityService afH5BoluomeActivityService;

	@RequestMapping(value = "boluomeActivityLogin", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String boluomeActivityLogin(HttpServletRequest request,HttpServletResponse response ,ModelMap model){
		
		
		String userName = ObjectUtils.toString(request.getParameter("userName"), "").toString();
		String password = ObjectUtils.toString(request.getParameter("password"),"").toString();
		Long boluomeActivityId = NumberUtil.objToLong(request.getParameter("boluomeActivityId"));
		String refUseraName = ObjectUtils.toString(request.getParameter("refUseraName"),"").toString();
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
