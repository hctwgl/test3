package com.ald.jsd.mgr.web.controller;


import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.vo.MgrBorrowInfoAnalysisVo;
import com.ald.fanbei.api.biz.vo.MgrDashboardCityInfoVo;
import com.ald.fanbei.api.biz.vo.MgrDashboardInfoVo;
import com.ald.fanbei.api.biz.vo.MgrTrendTodayInfoVo;
import com.ald.jsd.mgr.biz.service.MgrBorrowCashAnalysisService;
import com.ald.jsd.mgr.spring.NotNeedLogin;
import com.ald.jsd.mgr.web.dto.req.AnalysisReq;
import com.ald.jsd.mgr.web.dto.resp.Resp;

@NotNeedLogin
@Controller
@ResponseBody
@RequestMapping("/api/analysis")
public class AnalysisController extends BaseController {

    @Resource
    MgrBorrowCashAnalysisService mgrBorrowCashAnalysisService;

    @RequestMapping(value = {"/query.json"}, method = RequestMethod.POST)
    public Resp<MgrBorrowInfoAnalysisVo> getAnalysisInfo(@RequestBody AnalysisReq analysisReq) {
        MgrBorrowInfoAnalysisVo mgrBorrowInfoAnalysisVo = mgrBorrowCashAnalysisService.getBorrowInfoAnalysis(analysisReq);
        return Resp.succ(mgrBorrowInfoAnalysisVo, "");
    }

    @RequestMapping(value = {"/dashboard.json"}, method = RequestMethod.POST)
    public Resp<MgrDashboardInfoVo> getDashboardInfo() {
        MgrDashboardInfoVo mgrDashboardInfoVo = mgrBorrowCashAnalysisService.getBorrowInfoDashboard();
        return Resp.succ(mgrDashboardInfoVo, "成功");
    }
    @RequestMapping(value = {"/trendToday.json"}, method = RequestMethod.POST)
    public Resp<MgrTrendTodayInfoVo> getTrendTodayInfo() {
        MgrTrendTodayInfoVo mgrDashboardInfoVo = mgrBorrowCashAnalysisService.getBorrowInfoTrendToday();
        return Resp.succ(mgrDashboardInfoVo, "成功");
    }

    @RequestMapping(value = {"/dashboardCity.json"}, method = RequestMethod.POST)
    public Resp<MgrDashboardCityInfoVo> getdashboardCityInfo() {
        MgrDashboardCityInfoVo mgrDashboardCityInfoVo = mgrBorrowCashAnalysisService.getdashboardCityInfo();
        return Resp.succ(mgrDashboardCityInfoVo, "成功");
    }

}
