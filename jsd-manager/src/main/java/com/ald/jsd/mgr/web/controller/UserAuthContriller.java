package com.ald.jsd.mgr.web.controller;


import com.ald.fanbei.api.biz.service.JsdUserAuthService;
import com.ald.fanbei.api.dal.domain.JsdUserAuthDo;
import com.ald.fanbei.api.dal.query.UserAuthQuery;
import com.ald.jsd.mgr.dal.domain.dto.UserAuthDto;
import com.ald.jsd.mgr.web.dto.resp.Resp;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Controller
@ResponseBody
@RequestMapping("/api/auth")
public class UserAuthContriller extends BaseController{

    @Resource
    JsdUserAuthService jsdUserAuthService;

    @RequestMapping("/getUserAuthInfo.json")
    public Resp<UserAuthQuery> getUserAuthInfo(UserAuthQuery query, HttpServletRequest request){
        query.setFull(true);
        query.setList(jsdUserAuthService.getListUserAuth(query));
        return Resp.succ(query,"");
    }

}
