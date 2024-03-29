package com.ald.jsd.mgr.biz.service;

import com.ald.fanbei.api.biz.vo.MgrBorrowInfoAnalysisVo;
import com.ald.fanbei.api.biz.vo.MgrDashboardCityInfoVo;
import com.ald.fanbei.api.biz.vo.MgrDashboardInfoVo;
import com.ald.fanbei.api.biz.vo.MgrTrendTodayInfoVo;
import com.ald.jsd.mgr.web.dto.req.AnalysisReq;

/**
 * 极速贷数据分析Service
 *
 * @author guoshuaiqiang
 * @version 1.0.0 初始化
 * @date 2018-08-22 16:18:06
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface MgrBorrowCashAnalysisService {

    MgrBorrowInfoAnalysisVo getBorrowInfoAnalysis(AnalysisReq analysisReq);

    MgrDashboardInfoVo getBorrowInfoDashboard();

    MgrTrendTodayInfoVo getBorrowInfoTrendToday();

    MgrDashboardCityInfoVo getdashboardCityInfo();
}
