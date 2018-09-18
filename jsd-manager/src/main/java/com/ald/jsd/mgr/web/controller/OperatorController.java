package com.ald.jsd.mgr.web.controller;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.util.DigestUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.jsd.mgr.biz.service.MgrOperatorService;
import com.ald.jsd.mgr.dal.domain.MgrOperatorDo;
import com.ald.jsd.mgr.dal.domain.MgrOperatorRoleDo;
import com.ald.jsd.mgr.dal.domain.MgrRoleDo;
import com.ald.jsd.mgr.web.dto.req.OperatorEditReq;
import com.ald.jsd.mgr.web.dto.resp.Resp;
import com.alibaba.fastjson.JSON;

import jodd.util.StringUtil;


@Controller
@ResponseBody
@RequestMapping("/api/operator")
public class OperatorController extends BaseController {
	
	@Resource
	private MgrOperatorService mgrOperatorService;
	
	
    @RequestMapping(value = "/edit.json")
    public Resp<?> edit(@RequestBody @Valid OperatorEditReq params, HttpServletRequest request) {
        try {
            MgrOperatorDo operatorDo = null;
            if(StringUtil.isNotBlank(id)){//edit
            	operatorDo = MgrOperatorService.getOperatorById(Long.parseLong(id));
            	if(operatorDo == null){
                    return CommonResponse.getNewInstance(false, "非法用户").toString();
            	}
                String status = ObjectUtils.toString(request.getParameter("status")).trim();
                operatorDo.setStatus(Integer.parseInt(status));
            }else{//add
            	operatorDo = MgrOperatorService.getUserByUserName(userName);
                if (null != operatorDo) {
                    return CommonResponse.getNewInstance(false, "用户登录名已存在").toString();
                }
                operatorDo = new MgrOperatorDo();
                operatorDo.setCreator(operator);
                operatorDo.setStatus(1);
                operatorDo.setUserName(userName);
            }
            this.setPassword(operatorDo, request, id);
            operatorDo.setModifier(operator);
            String email = ObjectUtils.toString(request.getParameter("email"));
            operatorDo.setName(name);
            operatorDo.setEmail(email);
            operatorDo.setMobile(mobile);
            operatorDo.setPhone("");
            
            if (StringUtil.isBlank(id)) {//add
                MgrOperatorService.addUser(operatorDo);
                dsedSysOptLogService.addOptLog(buildOptLog(getUser(request), "新增用户信息，名称："+operatorDo.getUserName()));
            }else{
            	MgrOperatorService.updateUser(operatorDo);
            	dsedSysOptLogService.addOptLog(buildOptLog(getUser(request), "更新用户信息，名称："+operatorDo.getUserName()));
            }
            
            String dealResult = this.dealWithOperatorRole(id, operatorDo, operator, roleType);
            if(StringUtil.isNotBlank(dealResult)){
            	return dealResult;
            }
            logger.info("do add role success with operatorDo="+operatorDo);
            return CommonResponse.getNewInstance(true, "编辑成功", "listOperator.htm", null).toString();
        } catch (Exception e) {
            logger.error("do add role exception", e);
        }
        return CommonResponse.getNewInstance(false, "编辑失败").toString();
    }
    
    private String checkBaseParam(String id,int roleType,String mobile,String userName,String name){
        if (roleType < 0) {
            return CommonResponse.getNewInstance(false, "非法角色").toString();
        }
        if (StringUtils.isBlank(id) && StringUtils.isBlank(userName)) {
            return CommonResponse.getNewInstance(false, "用户登录名不能为空").toString();
        }
        if (StringUtils.isBlank(name)) {
            return CommonResponse.getNewInstance(false, "用户姓名不能为空").toString();
        }
        if (StringUtils.isBlank(mobile)) {
            return CommonResponse.getNewInstance(false, "手机号不能为空").toString();
        }
        return "";
    }
    
    private void setPassword(MgrOperatorDo operatorDO,HttpServletRequest request,String id) throws UnsupportedEncodingException{
    	String rawPwd = Constants.DEFAULT_PASSWORD;
    	if (StringUtil.isNotBlank(id)) {//edit
        	String resetPass = ObjectUtils.toString(request.getParameter("resetPass")).trim();
            if("1".equals(resetPass)){
                byte[] saltBytes = DigestUtil.generateSalt(Constants.DEFAULT_BYTES_SIZE);
                String salt = DigestUtil.encodeHex(saltBytes);
                byte[] pwdBytes = DigestUtil.digestString(rawPwd.getBytes("UTF-8"), saltBytes,
                        Constants.DEFAULT_DIGEST_TIMES, Constants.SHA1);
                operatorDO.setPassword(DigestUtil.encodeHex(pwdBytes));
                operatorDO.setSalt(salt);
            }    		
    	}else{//add
            String password = ObjectUtils.toString(request.getParameter("password")).trim();
            rawPwd = StringUtils.isBlank(password)?Constants.DEFAULT_PASSWORD:password;
            byte[] saltBytes = DigestUtil.generateSalt(Constants.DEFAULT_BYTES_SIZE);
            String salt = DigestUtil.encodeHex(saltBytes);
            byte[] pwdBytes = DigestUtil.digestString(rawPwd.getBytes("UTF-8"), saltBytes,Constants.DEFAULT_DIGEST_TIMES, Constants.SHA1);
            operatorDO.setPassword(DigestUtil.encodeHex(pwdBytes));
            operatorDO.setSalt(salt);
    	}
    }
    
    private String dealWithOperatorRole(String id,MgrOperatorDo operatorDO,String operator,Integer roleType){
        if (StringUtil.isBlank(id)) {//add
            MgrOperatorRoleDo operatorRoleDO = new MgrOperatorRoleDo();
            MgrRoleDo roleDO = dsedSysRoleService.getRoleByRoleType(roleType);
            if (null == roleDO || null == roleDO.getId()) {
                return CommonResponse.getNewInstance(false, "编辑失败，角色非法").toString();
            }
            operatorRoleDO.setCreator(operator);
            operatorRoleDO.setModifier(operator);
            operatorRoleDO.setRoleId(roleDO.getId());
            operatorRoleDO.setRoleType(roleType);
            operatorRoleDO.setOperatorId(operatorDO.getId());
            sysOperatorRoleService.insertOrUpdate(operatorRoleDO);
        }else{
//        	MgrOperatorService.updateUser(operatorDO);
        	MgrOperatorDto userDto = MgrOperatorService.getOperatorById(Long.parseLong(id));
        	if(roleType != userDto.getRoleType()){
                MgrOperatorRoleDo optRole = new MgrOperatorRoleDo();
                optRole.setId(userDto.getUrId());
                MgrRoleDo roleDO = dsedSysRoleService.getRoleByRoleType(roleType);
                if (null == roleDO || null == roleDO.getId()) {
                    return CommonResponse.getNewInstance(false, "编辑失败，角色非法").toString();
                }
                optRole.setModifier(operator);
                optRole.setRoleId(roleDO.getId());
                optRole.setOperatorId(operatorDO.getId());
                optRole.setRoleType(roleType);
                sysOperatorRoleService.insertOrUpdate(optRole);
        	}
        }
        
        return "";
    }
    
    
    @RequestMapping(value = "/deleteOperator", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String deleteOperator(ModelMap model, HttpServletRequest request) {
        try {
            Long id = NumberUtil.objToLongWithDefault(request.getParameter("id"), -1l);
            MgrOperatorService.deleteUserById(id);
            dsedSysOptLogService.addOptLog(buildOptLog(getUser(request), "删除用户信息，id："+id));
            logger.info("do add role with id="+id+" by userName="+getUser(request));
        } catch (Exception e) {
            logger.error("do delete role exception", e);
        }
        return CommonResponse.getNewInstance(true, "删除用户成功", "listOperator.htm", null).toString();
    }

    @RequestMapping(value = "/editPassword", method = RequestMethod.GET)
    public void editPassword(ModelMap model, HttpServletRequest request) {
    	
    }
    
    @RequestMapping(value = "/checkPassword", method = RequestMethod.POST)
    @ResponseBody
    public String checkPassword(ModelMap model, HttpServletRequest request) {
    	Map<String,Object> map = new HashMap<String,Object>();
    	try {
    		String userName = OperatorUtil.getUserName(request);
        	String password = request.getParameter("password");
            MgrOperatorDo userDO = MgrOperatorService.getUserByUserName(userName);
            String salt = userDO.getSalt();
            password = changeStr(salt, password);
            if(userDO.getPassword().equals(password)){
            	map.put("result", true);
            }else{
            	map.put("result", false);
            }
		} catch (Exception e) {
            logger.error("do select Operator exception", e);
        	map.put("result", false);
		}
        return JSON.toJSONString(map);
    }
    
    private String changeStr(String salt,String password) throws UnsupportedEncodingException{
    	byte[] pwdBytes = DigestUtil.digestString(password.getBytes("UTF-8"), DigestUtil.decodeHex(salt),
                Constants.DEFAULT_DIGEST_TIMES, Constants.SHA1);
        String pwdStr = DigestUtil.encodeHex(pwdBytes);
    	return pwdStr;
    }
    
    @RequestMapping(value = "/updatePassword", method = RequestMethod.POST)
    @ResponseBody
    public String updatePassword(ModelMap model, HttpServletRequest request) {
    	try {
    		String userName = OperatorUtil.getUserName(request);
        	String password = request.getParameter("password");
            MgrOperatorDo userDO = MgrOperatorService.getUserByUserName(userName);
            String salt = userDO.getSalt();
            password = changeStr(salt, password);
            userDO.setPassword(password);
            int updateCount = MgrOperatorService.updateUserInfoByName(userDO);
            if(updateCount>0){
                logger.info("do update password success with userName="+userName);
                dsedSysOptLogService.addOptLog(buildOptLog(getUser(request), "修改密码成功，id："+userDO.getUserName()));
            	return CommonResponse.getNewInstance(true, "修改密码成功", "main.htm", "").toString();
            }else{
                logger.info("do update password failed with userName="+userName);
                return CommonResponse.getNewInstance(false, "修改密码失败").toString();
            }
		} catch (Exception e) {
            logger.error("do update password exception", e);
            return CommonResponse.getNewInstance(false, "操作失败").toString();
		}
    }
}
