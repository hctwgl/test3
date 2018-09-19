package com.ald.jsd.mgr.web.controller;

import java.nio.charset.Charset;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.util.CommonUtil;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.DigestUtil;
import com.ald.jsd.mgr.biz.service.MgrRoleService;
import com.ald.jsd.mgr.dal.dao.MgrOperatorDao;
import com.ald.jsd.mgr.dal.domain.MgrOperatorDo;
import com.ald.jsd.mgr.spring.NotNeedLogin;
import com.ald.jsd.mgr.web.LocalConstants;
import com.ald.jsd.mgr.web.Sessions;
import com.ald.jsd.mgr.web.dto.req.LoginReq;
import com.ald.jsd.mgr.web.dto.resp.Resp;

@Controller
@ResponseBody
@RequestMapping("/api/login")
public class LoginController extends BaseController {

	@Resource
	private MgrOperatorDao mgrOperatorDao;
    @Resource
    private MgrRoleService mgrRoleService;
    @Resource
    private BizCacheUtil bizCacheUtil;
    
    /**
     * 用户密码登陆
     * @param request
     * @param model
     * @return
     */
    @NotNeedLogin
    @RequestMapping(value = "/in.json", method = RequestMethod.POST)
    public Resp<?> doLogin(@RequestBody @Valid LoginReq loginReq, HttpServletRequest request) {
        MgrOperatorDo userDO = mgrOperatorDao.getByUsername(loginReq.username);
        if (null == userDO) {
            return Resp.fail("用户不存在");
        }
        
        byte[] saltBytes = DigestUtil.decodeHex(userDO.getSalt());
        byte[] reqPwdBytes = DigestUtil.digestString(loginReq.passwd.getBytes(Charset.forName("UTF-8")), saltBytes, Constants.DEFAULT_DIGEST_TIMES, Constants.SHA1);
        String reqPwd = DigestUtil.encodeHex(reqPwdBytes);
        String dbPwd = userDO.getPassword();
        
        if (!StringUtils.equals(reqPwd, dbPwd)) {
        	return Resp.fail("密码错误");
        }
        
        Sessions.setUserMo(request, userDO);
        return Resp.succ();
    }

    /**
     * 短信验证码登录
     *
     * @param request
     * @param model
     * @return
     * @throws Exception
     */
    @NotNeedLogin
    @RequestMapping(value = "/smsIn.json", method = RequestMethod.POST)
    public Resp<?> doSmsLogin(@RequestBody @Valid LoginReq loginReq, HttpServletRequest request) {
    	MgrOperatorDo userDO = mgrOperatorDao.getByUsername(loginReq.username);
        if (null == userDO) {
            return Resp.fail("用户不存在");
        }
        
    	String cacheKey = LocalConstants.CACHE_KEY_LOGIN_SMS + loginReq.username;
        Object smsObj = bizCacheUtil.getObject(cacheKey);
        if (!StringUtils.equals(loginReq.verifyCode, (String)smsObj)) {
        	return Resp.fail("验证码错误");
        }
        bizCacheUtil.delCache(cacheKey);
        
        Sessions.setUserMo(request, userDO);
        return Resp.succ();
    }
    /**
     * 获取验证码
     * @return
     */
    @NotNeedLogin
    @RequestMapping(value = "/sendSms.json", method = RequestMethod.POST)
    public Resp<?> verifyCode(@RequestBody @Valid LoginReq loginReq, HttpServletResponse response){
    	MgrOperatorDo userDO = mgrOperatorDao.getByUsername(loginReq.username);
        if (null == userDO) {
            return Resp.fail(null, 900, "用户不存在");
        }
    	
        String verifyCode = "888888";
        if (!Constants.INVELOMENT_TYPE_TEST.equals(ConfigProperties.get(Constants.CONFKEY_INVELOMENT_TYPE))){
            verifyCode = CommonUtil.getRandomNumber(6);
        }
        
        String cacheKey = LocalConstants.CACHE_KEY_LOGIN_SMS + loginReq.username;
        bizCacheUtil.delCache(cacheKey);
        bizCacheUtil.saveObject(cacheKey, verifyCode, LocalConstants.CACHE_KEY_LOGIN_SMS_EXPIRE_SECS);
        return Resp.succ();
    }
    
    /**
     * 注销
     * @return
     */
    @RequestMapping(value = "/logout.json")
    public Resp<?> logout(HttpServletRequest request){
    	Sessions.empty(request);
        return Resp.succ();
    }

}
