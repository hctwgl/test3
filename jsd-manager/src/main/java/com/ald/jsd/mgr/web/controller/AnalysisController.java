package com.ald.jsd.mgr.web.controller;


import com.ald.fanbei.api.biz.bo.assetpush.JsdBorrowInfoAnalysisVo;
import com.ald.fanbei.api.biz.service.JsdBorrowCashService;
import com.ald.fanbei.api.biz.service.JsdUserAuthService;
import com.ald.fanbei.api.dal.query.UserAuthQuery;
import com.ald.jsd.mgr.biz.service.JsdBorrowCashAnalysisService;
import com.ald.jsd.mgr.biz.service.MgrBorrowCashService;
import com.ald.jsd.mgr.spring.NotNeedLogin;
import com.ald.jsd.mgr.web.dto.req.AnalysisReq;
import com.ald.jsd.mgr.web.dto.resp.Resp;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
@NotNeedLogin
@Controller
@ResponseBody
@RequestMapping("/api/analysis")
public class AnalysisController extends BaseController{

    @Resource
    JsdBorrowCashAnalysisService jsdBorrowCashAnalysisService;

    @RequestMapping(value = {"/query.json"},method = RequestMethod.POST)
    public Resp<JsdBorrowInfoAnalysisVo> getUserAuthInfo(@RequestBody AnalysisReq analysisReq){
        JsdBorrowInfoAnalysisVo jsdBorrowInfoAnalysisVo = jsdBorrowCashAnalysisService.getBorrowInfoAnalysisInfo(analysisReq.days);
        return Resp.succ(jsdBorrowInfoAnalysisVo,"");
    }

}
