package com.ald.jsd.mgr.web.controller;


import com.ald.fanbei.api.biz.service.JsdUserAuthService;
import com.ald.fanbei.api.dal.domain.JsdUserAuthDo;
import com.ald.fanbei.api.dal.query.JsdUserAuthQuery;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@ResponseBody
@RequestMapping("/api/auth")
public class UserAuthContriller extends BaseController{


    @Resource
    JsdUserAuthService jsdUserAuthService;

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


}
