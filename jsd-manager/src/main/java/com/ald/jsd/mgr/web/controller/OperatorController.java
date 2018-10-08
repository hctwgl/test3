package com.ald.jsd.mgr.web.controller;

import java.nio.charset.Charset;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.util.DigestUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.jsd.mgr.dal.dao.MgrOperatorDao;
import com.ald.jsd.mgr.dal.domain.MgrOperatorDo;
import com.ald.jsd.mgr.spring.NotNeedLogin;
import com.ald.jsd.mgr.web.LocalConstants;
import com.ald.jsd.mgr.web.Sessions;
import com.ald.jsd.mgr.web.dto.req.ModPwdReq;
import com.ald.jsd.mgr.web.dto.req.OperatorEditReq;
import com.ald.jsd.mgr.web.dto.resp.Resp;

@Controller
@ResponseBody
@RequestMapping("/api/operator")
public class OperatorController extends BaseController {
	
	@Resource
	private MgrOperatorDao mgrOperatorDao;
	
	@NotNeedLogin
    @RequestMapping(value = "/edit.json")
    public Resp<?> edit(@RequestBody @Valid OperatorEditReq params, HttpServletRequest request) {
    	String operator = "byInvoke";
        MgrOperatorDo operatorDo = null;
        if(params.id != null){	//edit
        	operatorDo = mgrOperatorDao.getById(params.id);
        	if(operatorDo == null) return Resp.fail("用户不存在");
        }else{					//add
        	operatorDo = mgrOperatorDao.getByUsername(params.username);
            if (null != operatorDo) return Resp.fail("用户名已存在");
            operatorDo = new MgrOperatorDo();
            operatorDo.setCreator(operator);
            operatorDo.setStatus(1);
            operatorDo.setUserName(params.username);
        }
        
        this.setPassword(operatorDo, params.passwd);
        operatorDo.setModifier(operator);
        operatorDo.setName(params.realName);
        operatorDo.setEmail(params.email);
        operatorDo.setMobile(params.phone);
        operatorDo.setPhone(params.phone);
        
        if (params.id != null) {//edit
        	mgrOperatorDao.updateById(operatorDo);
        	logger.info("do update operator success with operatorDo="+operatorDo);
        }else{					//add
        	mgrOperatorDao.saveRecord(operatorDo);
        	logger.info("do add operator success with operatorDo="+operatorDo);
        }
        
        return Resp.succ();
    }
    
    /**
     * 修改密码
     * @return
     */
    @RequestMapping(value = "/modPwd.json")
    public Resp<?> modPwd(@RequestBody @Valid ModPwdReq params, HttpServletRequest request){
    	Long uid = Sessions.getUid(request);
    	
    	MgrOperatorDo optDo = mgrOperatorDao.getById(uid);
        byte[] saltBytes = DigestUtil.decodeHex(optDo.getSalt());
        
        byte[] oriPwdBytes = DigestUtil.digestString(params.oriPasswd.getBytes(LocalConstants.UTF_8), saltBytes, Constants.DEFAULT_DIGEST_TIMES, Constants.SHA1);
        String oriPwd = DigestUtil.encodeHex(oriPwdBytes);
        
        String dbPwd = optDo.getPassword();
        if (!StringUtils.equals(oriPwd, dbPwd)) {
        	return Resp.fail("原密码错误！");
        }
        
        byte[] newPwdBytes = DigestUtil.digestString(params.newPasswd.getBytes(LocalConstants.UTF_8), saltBytes, Constants.DEFAULT_DIGEST_TIMES, Constants.SHA1);
        String newPwd = DigestUtil.encodeHex(newPwdBytes);
        
        optDo.setPassword(newPwd);
        mgrOperatorDao.updateById(optDo);
        
        return Resp.succ();
    }
    
    private void setPassword(MgrOperatorDo operatorDo, String passwd){
    	String pwd = LocalConstants.DEFAULT_PASSWD;
    	if (operatorDo.getRid() != null) {	//edit
    	}else{								//add
    		if(StringUtil.isNotBlank(passwd)) {
    			pwd = passwd;
    		}
            byte[] saltBytes = DigestUtil.generateSalt(8);
            String salt = DigestUtil.encodeHex(saltBytes);
            byte[] pwdBytes = DigestUtil.digestString(pwd.getBytes(Charset.forName("UTF-8")), saltBytes, Constants.DEFAULT_DIGEST_TIMES, Constants.SHA1);
            operatorDo.setPassword(DigestUtil.encodeHex(pwdBytes));
            operatorDo.setSalt(salt);
    	}
    }
    
}
