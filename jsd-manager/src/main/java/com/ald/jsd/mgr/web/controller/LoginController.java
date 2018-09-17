package com.ald.fanbei.admin.web.sys.controller;

import com.ald.fanbei.admin.biz.service.DsedSysRoleService;
import com.ald.fanbei.admin.biz.service.DsedSysOperatorService;
import com.ald.fanbei.admin.biz.service.util.BizCacheUtil;
import com.ald.fanbei.admin.biz.service.util.SmsUtil;
import com.ald.fanbei.admin.biz.service.util.YFSmsUtil;
import com.ald.fanbei.admin.common.*;
import com.ald.fanbei.admin.dal.domain.DsedSysOperatorDo;
import com.ald.fanbei.admin.dal.domain.DsedSysRoleDo;
import com.ald.fanbei.admin.dal.util.OperatorUtil;
import com.ald.fanbei.admin.web.common.BaseController;
import com.ald.fanbei.admin.web.common.CommonResponse;
import jodd.util.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/**
 * 
 *@类描述：登录
 *@author 何鑫 2017年1月18日  12:51:33
 *@注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping("/api")
public class LoginController extends BaseController {

    @Resource
    private DsedSysOperatorService sysUserService;
    @Resource
    private DsedSysRoleService dsedSysRoleService;
    @Resource
    BizCacheUtil bizCacheUtil;
    
    /**
     * 用户密码登陆
     * @param request
     * @param model
     * @return
     */
    @RequestMapping(value = "/login.json", method = RequestMethod.GET)
    public String login(HttpServletRequest request, ModelMap model) {
        try {
            String userName = OperatorUtil.getUserName(request);
            if (userName == null || StringUtils.isBlank(userName)) {
            	Cookie cookie = CookieUtil.getCookie(request, Constants.LOGIN_NAME_COOKIE);
            	if(null != cookie){
            		model.put("cookieUserName", cookie.getValue());
            	}
                return "login";
            }
            int roleType = OperatorUtil.getRoleType(request, -1);
            if (roleType < 0) {
                return "login";
            }
            dsedSysOptLogService.addOptLog(buildOptLog(userName, "登录系统，用户名："+userName));
            return "redirect:/operator/main.htm?userName=" + userName;
        } catch (Exception e) {
            logger.error("login failed", e);
            return "login";
        }

    }

    /**
     * 短信验证码登录
     *
     * @param request
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/smsLogin.json", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String doSmsLogin(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
        DsedSysRoleDo roleDO = null;
        String userName = request.getParameter("userName");
        try {
            String verifyCode = request.getParameter("password");
            if (StringUtils.isBlank(userName)) {
                return CommonResponse.getNewInstance(false, "用户名不能为空").toString();
            }
            if (StringUtils.isBlank(verifyCode)) {
                return CommonResponse.getNewInstance(false, "验证码不能为空").toString();
            }
            DsedSysOperatorDo userDO = sysUserService.getUserByUserName(userName);
            if (null == userDO) {
                return CommonResponse.getNewInstance(false, "非法用户").toString();
            }
            Object smsObj = bizCacheUtil.getObject(Constants.LOGIN_SMS_KEY+userName);
            if (smsObj == null){
                return CommonResponse.getNewInstance(false, "验证码已过期").toString();
            }
            if (!StringUtils.equals(verifyCode, (String)smsObj)) {
                return CommonResponse.getNewInstance(false, "验证码错误").toString();
            }
            roleDO = dsedSysRoleService.getRoleByUserName(userName);
            if (null == roleDO) {
                return CommonResponse.getNewInstance(false, "非法用户角色").toString();
            }
            // 写入cookie
            CookieUtil.writeCookie(response,
                    Constants.SID,
                    CookieUtil.encodeCookieVal(userDO.getUserName() + "|" +
                            roleDO.getId()+"|"+roleDO.getRoleType()
                    ),
                    Constants.COOKIE_ALIVE_TIME);
            //uniform-blank：忘记账号 1：记住账号
            String uniform = request.getParameter("uniform");
            if(StringUtil.isBlank(uniform)){
                CookieUtil.removeCookie(Constants.LOGIN_NAME_COOKIE, response);
            }else{
                // 写入用户名，给页面显示用
                CookieUtil.writeCookieEncode(response, Constants.LOGIN_NAME_COOKIE, userDO.getUserName(),
                        Constants.COOKIE_REMEMBER_TIME);
            }
            request.setAttribute(Constants.CUR_USER_NAME,userName);
            //删除验证码
            bizCacheUtil.delCache(Constants.LOGIN_SMS_KEY+userName);
            dsedSysOptLogService.addOptLog(buildOptLog(userName, "登录系统，用户名："+userName));
        } catch (Exception e) {
            logger.error("login error", e);
            dsedSysOptLogService.addOptLog(buildOptLog(userName, "登录失败，用户名："+userName));
            return CommonResponse.getNewInstance(false, "非法请求", "/error.htm", null).toString();
        }
        return CommonResponse.getNewInstance(true, "登录成功", "/operator/main.htm", null).toString();
    }
    /**
     * 获取验证码
     * @return
     */
    @RequestMapping(value = "/verifyCode.json", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyCode(HttpServletRequest request, HttpServletResponse response){
        try {
            String userName = request.getParameter("userName");
            if (!CommonUtil.isMobile(userName)) {
                return CommonResponse.getNewInstance(false, "手机号不正确", "", null).toString();
            }
            Object smsObj = bizCacheUtil.getObject(Constants.LOGIN_SMS_KEY+userName);
            if (smsObj != null && !Constants.INVELOMENT_TYPE_TEST.equals(ConfigProperties.get(Constants.CONF_KEY_INVELOMENT_TYPE))) {
                logger.info(Constants.LOGIN_SMS_KEY + userName + " verifyCode is :" + smsObj.toString());
                return CommonResponse.getNewInstance(true, "三十分钟内有效", "", null).toString();
            }
            DsedSysOperatorDo userDO = sysUserService.getUserByUserName(userName);
            if (null == userDO || StringUtils.isBlank(userDO.getPassword())) {
                return CommonResponse.getNewInstance(false, "非法用户").toString();
            }
            String verifyCode = "888888";
            if (!Constants.INVELOMENT_TYPE_TEST.equals(ConfigProperties.get(Constants.CONF_KEY_INVELOMENT_TYPE))){
                verifyCode = CommonUtil.getRandomNumber(6);
                YFSmsUtil.sendAsyn(userName, SmsUtil.LOGIN_SMS_CONTENT.replace("sms",verifyCode),YFSmsUtil.VERIFYCODE);
            }
            //删除验证码
            bizCacheUtil.delCache(Constants.LOGIN_SMS_KEY+userName);
            bizCacheUtil.saveObject(Constants.LOGIN_SMS_KEY+userName,verifyCode,Constants.LOGIN_SMS_KEY_EXPIRE);
            return CommonResponse.getNewInstance(true, "发送成功", "", null).toString();
        }catch (Exception e){
            logger.error("verifyCode error={}", e);
            return CommonResponse.getNewInstance(false, "发送失败", "", null).toString();
        }

    }
    
    /**
     * 注销
     * @return
     */
    @RequestMapping(value = "/logout.json")
    public String logout(HttpServletRequest request, HttpServletResponse response){
    	String userName = getUser(request);
        //CookieUtil.removeAllCookie(request, response);
    	CookieUtil.removeCookie(Constants.SID, response);
        HttpSession session = request.getSession(false);
        if (null != session) {
            session.invalidate();
        }
        dsedSysOptLogService.addOptLog(buildOptLog(userName, "登出系统，用户名："+userName));
        return "redirect:/login.htm";
    }

}
