package com.ald.jsd.mgr.web.controller;

import java.nio.charset.Charset;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.util.DigestUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.jsd.mgr.dal.dao.MgrOperatorDao;
import com.ald.jsd.mgr.dal.domain.MgrOperatorDo;
import com.ald.jsd.mgr.web.LocalConstants;
import com.ald.jsd.mgr.web.dto.req.OperatorEditReq;
import com.ald.jsd.mgr.web.dto.resp.Resp;


@Controller
@ResponseBody
@RequestMapping("/api/operator")
public class OperatorController extends BaseController {
	
	@Resource
	private MgrOperatorDao mgrOperatorDao;
	
	
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
