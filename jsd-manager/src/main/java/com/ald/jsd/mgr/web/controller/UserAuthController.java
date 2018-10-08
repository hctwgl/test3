package com.ald.jsd.mgr.web.controller;


import com.ald.fanbei.api.biz.service.JsdUserAuthService;
import com.ald.fanbei.api.dal.query.UserAuthQuery;
import com.ald.jsd.mgr.web.dto.resp.Resp;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

@Controller
@ResponseBody
@RequestMapping("/api/auth/")
public class UserAuthController extends BaseController{

    @Resource
    JsdUserAuthService jsdUserAuthService;

    @RequestMapping(value = {"getUserAuthInfo.json"})
    public Resp<UserAuthQuery> getUserAuthInfo(@RequestBody UserAuthQuery query, HttpServletRequest request){
        query.setFull(true);
        query.setSubmitPersonNum(jsdUserAuthService.getSubmitPersonNum());
        query.setPassPersonNum(jsdUserAuthService.getPassPersonNum());
        BigDecimal num=new BigDecimal("100");
        if(query.getSubmitPersonNum()==0){
            query.setPassRate(BigDecimal.ZERO);
        }else if(query.getPassPersonNum()==query.getSubmitPersonNum()){
            query.setPassRate(num);
        }else{
            query.setPassRate(new BigDecimal(query.getPassPersonNum()).divide(new BigDecimal(query.getSubmitPersonNum()),2,BigDecimal.ROUND_HALF_UP).multiply(num));
        }
        query.setList(jsdUserAuthService.getListUserAuth(query));
        return Resp.succ(query,"");
    }

}
