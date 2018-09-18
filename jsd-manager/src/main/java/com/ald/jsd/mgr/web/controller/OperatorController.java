package com.ald.jsd.mgr.web.controller;

import java.io.UnsupportedEncodingException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.jsd.mgr.biz.service.MgrOperatorService;
import com.ald.jsd.mgr.dal.domain.MgrOperatorDo;
import com.ald.jsd.mgr.web.dto.req.OperatorEditReq;
import com.ald.jsd.mgr.web.dto.resp.Resp;


@Controller
@ResponseBody
@RequestMapping("/api/operator")
public class OperatorController extends BaseController {
	
	@Resource
	private MgrOperatorService mgrOperatorService;
	
	
    @RequestMapping(value = "/edit.json")
    public Resp<?> edit(@RequestBody @Valid OperatorEditReq params, HttpServletRequest request) {
    	return Resp.succ(null,"");
//        try {
//            MgrOperatorDo operatorDo = null;
//            if(StringUtil.isNotBlank(id)){//edit
//            	operatorDo = MgrOperatorService.getOperatorById(Long.parseLong(id));
//            	if(operatorDo == null){
//                    return CommonResponse.getNewInstance(false, "非法用户").toString();
//            	}
//                String status = ObjectUtils.toString(request.getParameter("status")).trim();
//                operatorDo.setStatus(Integer.parseInt(status));
//            }else{//add
//            	operatorDo = MgrOperatorService.getUserByUserName(userName);
//                if (null != operatorDo) {
//                    return CommonResponse.getNewInstance(false, "用户登录名已存在").toString();
//                }
//                operatorDo = new MgrOperatorDo();
//                operatorDo.setCreator(operator);
//                operatorDo.setStatus(1);
//                operatorDo.setUserName(userName);
//            }
//            this.setPassword(operatorDo, request, id);
//            operatorDo.setModifier(operator);
//            String email = ObjectUtils.toString(request.getParameter("email"));
//            operatorDo.setName(name);
//            operatorDo.setEmail(email);
//            operatorDo.setMobile(mobile);
//            operatorDo.setPhone("");
//            
//            if (StringUtil.isBlank(id)) {//add
//                MgrOperatorService.addUser(operatorDo);
//                dsedSysOptLogService.addOptLog(buildOptLog(getUser(request), "新增用户信息，名称："+operatorDo.getUserName()));
//            }else{
//            	MgrOperatorService.updateUser(operatorDo);
//            	dsedSysOptLogService.addOptLog(buildOptLog(getUser(request), "更新用户信息，名称："+operatorDo.getUserName()));
//            }
//            
//            String dealResult = this.dealWithOperatorRole(id, operatorDo, operator, roleType);
//            if(StringUtil.isNotBlank(dealResult)){
//            	return dealResult;
//            }
//            logger.info("do add role success with operatorDo="+operatorDo);
//            return CommonResponse.getNewInstance(true, "编辑成功", "listOperator.htm", null).toString();
//        } catch (Exception e) {
//            logger.error("do add role exception", e);
//        }
//        return CommonResponse.getNewInstance(false, "编辑失败").toString();
    }
    
    private void setPassword(MgrOperatorDo operatorDO, HttpServletRequest request, String id) throws UnsupportedEncodingException{
//    	String rawPwd = Constants.DEFAULT_PASSWORD;
//    	if (StringUtil.isNotBlank(id)) {//edit
//        	String resetPass = ObjectUtils.toString(request.getParameter("resetPass")).trim();
//            if("1".equals(resetPass)){
//                byte[] saltBytes = DigestUtil.generateSalt(Constants.DEFAULT_BYTES_SIZE);
//                String salt = DigestUtil.encodeHex(saltBytes);
//                byte[] pwdBytes = DigestUtil.digestString(rawPwd.getBytes("UTF-8"), saltBytes,
//                        Constants.DEFAULT_DIGEST_TIMES, Constants.SHA1);
//                operatorDO.setPassword(DigestUtil.encodeHex(pwdBytes));
//                operatorDO.setSalt(salt);
//            }    		
//    	}else{//add
//            String password = ObjectUtils.toString(request.getParameter("password")).trim();
//            rawPwd = StringUtils.isBlank(password)?Constants.DEFAULT_PASSWORD:password;
//            byte[] saltBytes = DigestUtil.generateSalt(Constants.DEFAULT_BYTES_SIZE);
//            String salt = DigestUtil.encodeHex(saltBytes);
//            byte[] pwdBytes = DigestUtil.digestString(rawPwd.getBytes("UTF-8"), saltBytes,Constants.DEFAULT_DIGEST_TIMES, Constants.SHA1);
//            operatorDO.setPassword(DigestUtil.encodeHex(pwdBytes));
//            operatorDO.setSalt(salt);
//    	}
    }
    
}
