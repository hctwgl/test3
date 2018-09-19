package com.ald.jsd.mgr.web.controller;


import com.ald.fanbei.api.biz.service.JsdUserAuthService;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.dal.query.UserAuthQuery;
import com.ald.jsd.mgr.web.dto.resp.Resp;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Controller
@ResponseBody
@RequestMapping("/api/auth/")
public class UserAuthContriller extends BaseController{

    @Resource
    JsdUserAuthService jsdUserAuthService;

    @RequestMapping(value = {"getUserAuthInfo.json"})
    public Resp<UserAuthQuery> getUserAuthInfo(UserAuthQuery query, HttpServletRequest request){
        query.setFull(true);
        query.setSubmitPersonNum(jsdUserAuthService.getSubmitPersonNum());
        query.setPassPersonNum(jsdUserAuthService.getPassPersonNum());
        query.setPassRate(BigDecimalUtil.divide(query.getPassPersonNum(),query.getSubmitPersonNum()));
        query.setList(jsdUserAuthService.getListUserAuth(query));
        return Resp.succ(query,"");
    }

}
