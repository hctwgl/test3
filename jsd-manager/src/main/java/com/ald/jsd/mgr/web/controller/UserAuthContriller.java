package com.ald.jsd.mgr.web.controller;


import com.ald.fanbei.api.biz.service.JsdUserAuthService;
import com.ald.fanbei.api.biz.service.JsdUserService;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.dal.domain.JsdUserAuthDo;
import com.ald.fanbei.api.dal.domain.JsdUserDo;
import com.ald.fanbei.api.dal.query.JsdUserAuthQuery;
import com.ald.jsd.mgr.web.dto.req.UserAuthReq;
import com.ald.jsd.mgr.web.dto.resp.Resp;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

@Controller
@ResponseBody
@RequestMapping("/api/auth/")
public class UserAuthContriller extends BaseController{


    @Resource
    JsdUserAuthService jsdUserAuthService;

    @Resource
    private JsdUserService jsdUserService;

    @RequestMapping("/getUserAuthInfo")
    public String getJsdUserAuthInfo(@RequestBody JSONObject data, HttpServletRequest request, ModelMap model, JsdUserAuthQuery query){
        String riskStatus = data.getString("riskStatus");
        String searchContent = data.getString("searchContent");
        int pageIndex = data.getInteger("pageIndex");
        int pageSize = data.getInteger("pageSize");
        JsdUserAuthQuery jsdUserAuthQuery=new JsdUserAuthQuery();
        jsdUserAuthQuery.setPageIndex(pageIndex);
        jsdUserAuthQuery.setPageSize(pageSize);
        jsdUserAuthQuery.setRiskStatus(riskStatus);
        jsdUserAuthQuery.setSearchContent(searchContent);
        List<JsdUserAuthDo> list=jsdUserAuthService.getListJsdUserAuth(query);
        return null;
    }


    @RequestMapping(value = {"synUserAuth.json"})
    public String synUserAuth(UserAuthReq req){
        try {
            JsdUserDo userDo=jsdUserService.getByOpenId(req.getOpenId());
            if(userDo==null){
                return Resp.fail(req,306,"用户为空！").toString();
            }
            JsdUserAuthDo userAuthDo=new JsdUserAuthDo();
            userAuthDo.setGmtCreate(new Date());
            userAuthDo.setRiskAmount(req.getRiskAmount());
            userAuthDo.setGmtRisk(DateUtil.stringToDate(req.getRiskTime()));
            userAuthDo.setRiskStatus(req.getRiskStatus());
            userAuthDo.setRiskNo(req.getRiskNo());
            userAuthDo.setUserId(userDo.getRid());
            userAuthDo.setRiskRate(req.getRiskRate());
            jsdUserAuthService.saveRecord(userAuthDo);
            return Resp.fail(req,000,"成功").toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
