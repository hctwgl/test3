package com.ald.jsd.mgr.biz.service.impl;

import com.ald.fanbei.api.biz.vo.MgrBorrowInfoAnalysisVo;
import com.ald.fanbei.api.biz.vo.MgrDashboardInfoVo;
import com.ald.fanbei.api.biz.vo.MgrTrendTodayInfoVo;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashDo;
import com.ald.jsd.mgr.biz.service.MgrBorrowCashAnalysisService;
import com.ald.jsd.mgr.biz.service.MgrBorrowCashService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 极速贷ServiceImpl
 *
 * @author yanghailong
 * @version 1.0.0 初始化
 * @date 2018-08-22 16:18:06
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */

@Service("jsdBorrowCashAnalysisService")
public class MgrBorrowCashAnalysisServiceImpl implements MgrBorrowCashAnalysisService {

    @Resource
    private MgrBorrowCashService mgrBorrowCashService;
    @Resource
    private MgrUserAuthService mgrUserAuthService;

    @Override
    public MgrBorrowInfoAnalysisVo getBorrowInfoAnalysis(Integer days) {
        List<JsdBorrowCashDo> jsdBorrowCashDoList = mgrBorrowCashService.getBorrowCashLessThanDays(days);
        MgrBorrowInfoAnalysisVo mgrBorrowInfoAnalysisVo = new MgrBorrowInfoAnalysisVo();
        BigDecimal totalLoanAmount = BigDecimal.ZERO;
        BigDecimal returnedRate = BigDecimal.ZERO;//回款率
        BigDecimal returnAmount = BigDecimal.ZERO;//回款金额
        BigDecimal overdueAmount = BigDecimal.ZERO;//逾期金额
        BigDecimal dueAmount = BigDecimal.ZERO;//到期金额
        Integer borrowMans = 0;//放款人数
        BigDecimal repeatBorrowRate = BigDecimal.ZERO;//复借率
        BigDecimal overdueRate = BigDecimal.ZERO;//逾期率
        BigDecimal profitRate = BigDecimal.ZERO;//不良率
        BigDecimal riskPassRate = BigDecimal.ZERO;//认证通过率
        BigDecimal borrowPassRate = BigDecimal.ZERO;//借款通过率
        borrowMans = jsdBorrowCashDoList.stream().map(JsdBorrowCashDo::getUserId).collect(Collectors.toSet()).size();//去重放贷人数

        for (JsdBorrowCashDo borrow : jsdBorrowCashDoList) {
            totalLoanAmount = totalLoanAmount.add(borrow.getAmount());
            if (StringUtils.equals(borrow.getStatus(), "FINSH")) {
                returnAmount.add(borrow.getRepayAmount());
            }
            if (StringUtils.equals(borrow.getOverdueStatus(), "Y")) {
                overdueAmount.add(borrow.getAmount().subtract(borrow.getRepayAmount().subtract(borrow.getSumRepaidInterest())
                        .subtract(borrow.getSumRepaidOverdue()).subtract(borrow.getSumRepaidPoundage())));
            }
            if (borrow.getGmtPlanRepayment().before(new Date())) {
                dueAmount.add(borrow.getAmount());
            }
        }
        int applyBorrowCashNum = mgrBorrowCashService.getApplyBorrowCashByDays(days);//申请借款人数
        if (dueAmount != BigDecimal.ZERO){
            overdueRate = overdueAmount.divide(dueAmount,2,BigDecimal.ROUND_HALF_UP);
            returnedRate = returnAmount.divide(dueAmount,2,BigDecimal.ROUND_HALF_UP);//回款金额
        }
        if (applyBorrowCashNum != 0){
            borrowPassRate = new BigDecimal(borrowMans).divide(new BigDecimal(applyBorrowCashNum),4,BigDecimal.ROUND_HALF_UP);//借款通过人数
        }
        if (totalLoanAmount != BigDecimal.ZERO){
            profitRate = (returnAmount.subtract(overdueAmount)).divide(totalLoanAmount,4,BigDecimal.ROUND_HALF_UP);
        }
        int allUserNum = mgrUserAuthService.getPassPersonNumByStatusAndDays("", days);
        int paseUserNum = mgrUserAuthService.getPassPersonNumByStatusAndDays("Y", days);
        if (allUserNum  != 0){
            riskPassRate = new BigDecimal(paseUserNum).divide(new BigDecimal(allUserNum),4,BigDecimal.ROUND_HALF_UP).subtract(BigDecimal.ONE);
        }
        mgrBorrowInfoAnalysisVo.setRiskPassRate(riskPassRate);
        mgrBorrowInfoAnalysisVo.setTotalLoanAmount(totalLoanAmount);
        mgrBorrowInfoAnalysisVo.setBorrowMans(borrowMans);
        mgrBorrowInfoAnalysisVo.setBorrowPassRate(borrowPassRate);
        mgrBorrowInfoAnalysisVo.setReturnedRate(returnedRate);
        mgrBorrowInfoAnalysisVo.setOverdueRate(overdueRate);
        mgrBorrowInfoAnalysisVo.setProfitRate(profitRate);
        return mgrBorrowInfoAnalysisVo;
    }

    @Override
    public MgrDashboardInfoVo getBorrowInfoDashboard() {
        List<JsdBorrowCashDo> todayBorrowCashDoList = mgrBorrowCashService.getBorrowCashByDays(0);
        List<JsdBorrowCashDo> ystBorrowCashDoList = mgrBorrowCashService.getBorrowCashByDays(1);
        List<JsdBorrowCashDo> weekBorrowCashDoList = mgrBorrowCashService.getBorrowCashByDays(7);
        BigDecimal todayAmount = todayBorrowCashDoList.stream().map(JsdBorrowCashDo::getAmount).reduce(BigDecimal.ZERO,BigDecimal::add);
        BigDecimal ystAmount = ystBorrowCashDoList.stream().map(JsdBorrowCashDo::getAmount).reduce(BigDecimal.ZERO,BigDecimal::add);
        BigDecimal weekAmount = weekBorrowCashDoList.stream().map(JsdBorrowCashDo::getAmount).reduce(BigDecimal.ZERO,BigDecimal::add);
        int borrowMans = todayBorrowCashDoList.stream().map(JsdBorrowCashDo::getUserId).collect(Collectors.toSet()).size();//去重放贷人数
        BigDecimal avgAmountPer = BigDecimal.ZERO;
        if (null != todayBorrowCashDoList && 0 != todayBorrowCashDoList.size() ){
            avgAmountPer = todayAmount.divide(new BigDecimal(todayBorrowCashDoList.size()),2,BigDecimal.ROUND_HALF_UP);
        }

        BigDecimal totalLoanAmtRateByWeek = BigDecimal.ZERO;
        if (weekAmount != BigDecimal.ZERO ){
            totalLoanAmtRateByWeek = todayAmount.divide(weekAmount,4,BigDecimal.ROUND_HALF_UP).subtract(BigDecimal.ONE);
        }
        BigDecimal totalLoanAmtRateByDay = BigDecimal.ZERO;
        if (ystAmount != BigDecimal.ZERO ){
            totalLoanAmtRateByDay = todayAmount.divide(ystAmount,4,BigDecimal.ROUND_HALF_UP).subtract(BigDecimal.ONE);
        }

        int todayAllUserNum = mgrUserAuthService.getPassPersonNumByStatusEqualDays("", 0);
        int todayPaseUserNum = mgrUserAuthService.getPassPersonNumByStatusEqualDays("Y", 0);
        BigDecimal riskPassRate = BigDecimal.ZERO;
        if (todayAllUserNum  != 0){
            riskPassRate = new BigDecimal(todayPaseUserNum).divide(new BigDecimal(todayAllUserNum),4,BigDecimal.ROUND_HALF_UP).subtract(BigDecimal.ONE);
        }
        int ystAllUserNum = mgrUserAuthService.getPassPersonNumByStatusEqualDays("", 1);
        int ystPaseUserNum = mgrUserAuthService.getPassPersonNumByStatusEqualDays("Y", 1);
        BigDecimal riskPassRateByDay = BigDecimal.ZERO;
        if (ystAllUserNum  != 0){
            riskPassRateByDay = new BigDecimal(ystPaseUserNum).divide(new BigDecimal(ystAllUserNum),4,BigDecimal.ROUND_HALF_UP).subtract(BigDecimal.ONE);
        }
        int weekAllUserNum = mgrUserAuthService.getPassPersonNumByStatusEqualDays("", 7);
        int weekPaseUserNum = mgrUserAuthService.getPassPersonNumByStatusEqualDays("Y", 7);
        BigDecimal riskPassRateByWeek = BigDecimal.ZERO;
        if (weekAllUserNum  != 0){
            riskPassRateByWeek = new BigDecimal(weekPaseUserNum).divide(new BigDecimal(weekAllUserNum),4,BigDecimal.ROUND_HALF_UP).subtract(BigDecimal.ONE);
        }
        MgrDashboardInfoVo mgrDashboardInfoVo = new MgrDashboardInfoVo();
        mgrDashboardInfoVo.setTotalLoanAmt(todayAmount);
        mgrDashboardInfoVo.setTotalLoanAmtRateByWeek(totalLoanAmtRateByWeek);
        mgrDashboardInfoVo.setTotalLoanAmtRateByDay(totalLoanAmtRateByDay);
        mgrDashboardInfoVo.setBorrowMans(borrowMans);
        mgrDashboardInfoVo.setRiskPassRate(riskPassRate);
        mgrDashboardInfoVo.setRiskPassRateByDay(riskPassRateByDay);
        mgrDashboardInfoVo.setRiskPassRateByWeek(riskPassRateByWeek);
        mgrDashboardInfoVo.setAvgAmountPer(avgAmountPer);
        return mgrDashboardInfoVo;
    }

    @Override
    public MgrTrendTodayInfoVo getBorrowInfoTrendToday() {
        List<JsdBorrowCashDo> todayBorrowCashDoList = mgrBorrowCashService.getBorrowCashByDays(1);
        Map<Integer, List<JsdBorrowCashDo>> borrowCashInfo = todayBorrowCashDoList.stream().collect(Collectors.groupingBy(JsdBorrowCashDo::getGmyCreateHour));
        ArrayList<Map<Integer,Integer>> list = new ArrayList();
        borrowCashInfo.forEach((k,v) ->{
            Map map = new HashMap();
            map.put("hour",Integer.parseInt(String.valueOf(k)));
            map.put("num",v.size());
            list.add(map);
        });
        list.sort((o1, o2) -> o1.get("hour")-o2.get("hour"));
        MgrTrendTodayInfoVo mgrTrendTodayInfoVo = new MgrTrendTodayInfoVo();
        mgrTrendTodayInfoVo.setLoanNumPerHourToday(list);
        return mgrTrendTodayInfoVo;
    }

}
