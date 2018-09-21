package com.ald.jsd.mgr.biz.service.impl;

import com.ald.fanbei.api.biz.vo.MgrBorrowInfoAnalysisVo;
import com.ald.fanbei.api.biz.vo.MgrDashboardCityInfoVo;
import com.ald.fanbei.api.biz.vo.MgrDashboardInfoVo;
import com.ald.fanbei.api.biz.vo.MgrTrendTodayInfoVo;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderInfoDo;
import com.ald.jsd.mgr.biz.service.MgrBorrowCashAnalysisService;
import com.ald.jsd.mgr.biz.service.MgrBorrowCashService;
import com.ald.jsd.mgr.biz.service.MgrBorrowLegalOrderInfoService;
import com.ald.jsd.mgr.web.dto.req.AnalysisReq;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

@Service("mgrBorrowCashAnalysisService")
public class MgrBorrowCashAnalysisServiceImpl implements MgrBorrowCashAnalysisService {

    private static final Logger logger = LoggerFactory.getLogger(MgrBorrowCashAnalysisServiceImpl.class);
    @Resource
    private MgrBorrowCashService mgrBorrowCashService;
    @Resource
    private MgrUserAuthService mgrUserAuthService;
    @Resource
    private MgrBorrowLegalOrderInfoService mgrBorrowLegalOrderInfoService;

    @Override
    public MgrBorrowInfoAnalysisVo getBorrowInfoAnalysis(AnalysisReq analysisReq) {
        List<JsdBorrowCashDo> jsdBorrowCashDoList = new ArrayList<>();
        int applyBorrowCashNum = 0;
        int haveBorrowCashNum = 0;
        int allUserNum = 0;
        int paseUserNum = 0;
        int days = 0;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (!NumberUtil.isNullOrZero(analysisReq.days)) {
            jsdBorrowCashDoList = mgrBorrowCashService.getBorrowCashLessThanDays(analysisReq.days);
            applyBorrowCashNum = mgrBorrowCashService.getApplyBorrowCashByDays(analysisReq.days);//申请借款人数
            haveBorrowCashNum = mgrBorrowCashService.getUserNumByBorrowDays(analysisReq.days);//当期复借人数
            allUserNum = mgrUserAuthService.getPassPersonNumByStatusAndDays("", analysisReq.days);
            paseUserNum = mgrUserAuthService.getPassPersonNumByStatusAndDays("Y", analysisReq.days);
            days = analysisReq.days;
        } else if (!StringUtils.isBlank(analysisReq.endDate) && !StringUtils.isBlank(analysisReq.startDate)) {
            Date startTime = null;
            Date endTime = null;
            try {
                startTime = dateFormat.parse(analysisReq.startDate);
                endTime = dateFormat.parse(analysisReq.endDate);
            } catch (ParseException e) {
                logger.error("mgrBorrowCashAnalysisService buildBorrowCash error =>{}", e);
                e.printStackTrace();
            }
            jsdBorrowCashDoList = mgrBorrowCashService.getBorrowCashBetweenStartAndEnd(startTime, endTime);
            applyBorrowCashNum = mgrBorrowCashService.getApplyBorrowCashBetweenStartAndEnd(startTime, endTime);//申请借款人数
            haveBorrowCashNum = mgrBorrowCashService.getUserNumBetweenStartAndEnd(startTime, endTime);//当期复借人数
            allUserNum = mgrUserAuthService.getPassPersonNumByStatusBetweenStartAndEnd("", startTime, endTime);
            paseUserNum = mgrUserAuthService.getPassPersonNumByStatusBetweenStartAndEnd("Y", startTime, endTime);
            Integer startDays = getDays(startTime);
            Integer endDays = getDays(endTime);
            days = endDays - startDays;
        }

        MgrBorrowInfoAnalysisVo mgrBorrowInfoAnalysisVo = new MgrBorrowInfoAnalysisVo();
        BigDecimal totalLoanAmount = BigDecimal.ZERO;
        BigDecimal returnedRate = BigDecimal.ZERO;//回款率
        BigDecimal returnAmount = BigDecimal.ZERO;//回款金额
        BigDecimal overdueAmount = BigDecimal.ZERO;//逾期金额
        BigDecimal dueAmount = BigDecimal.ZERO;//到期金额
        BigDecimal repeatBorrowRate = BigDecimal.ZERO;//复借率
        BigDecimal overdueRate = BigDecimal.ZERO;//逾期率
        BigDecimal profitRate = BigDecimal.ZERO;//收益率
        BigDecimal riskPassRate = BigDecimal.ZERO;//认证通过率
        BigDecimal borrowPassRate = BigDecimal.ZERO;//借款通过率
        BigDecimal borrowDayAmount = BigDecimal.ZERO; //日均放款额
        Integer borrowDayMans = 0; //日均借款人数
        Integer borrowMans = jsdBorrowCashDoList.stream().map(JsdBorrowCashDo::getUserId).collect(Collectors.toSet()).size();//去重放贷人数
        returnAmount = jsdBorrowCashDoList.stream().filter(jsdBorrowCashDo -> jsdBorrowCashDo.getStatus().equals("FINSHED")).map(jsdBorrowCashDo -> jsdBorrowCashDo.getRepayAmount()).reduce(BigDecimal.ZERO, BigDecimal::add);
        for (JsdBorrowCashDo borrow : jsdBorrowCashDoList) {
            totalLoanAmount = totalLoanAmount.add(borrow.getAmount());
            /*if (StringUtils.equals(borrow.getStatus(), "FINSHED")) {
                returnAmount.add(borrow.getRepayAmount());
            }*/
            if (StringUtils.equals(borrow.getOverdueStatus(), "Y")) {
                overdueAmount.add(borrow.getAmount().subtract(borrow.getRepayAmount().subtract(borrow.getSumRepaidInterest())
                        .subtract(borrow.getSumRepaidOverdue()).subtract(borrow.getSumRepaidPoundage())));
            }
            if (borrow.getGmtPlanRepayment().before(new Date())) {
                dueAmount.add(borrow.getAmount());
            }
        }
        if (!NumberUtil.isNullOrZero(days)) {
            borrowDayMans = new BigDecimal(borrowMans).divide(new BigDecimal(days), 0, BigDecimal.ROUND_HALF_UP).intValue();
            borrowDayAmount = totalLoanAmount.divide(new BigDecimal(days), 2, BigDecimal.ROUND_HALF_UP);
        }

        repeatBorrowRate = new BigDecimal(haveBorrowCashNum).divide(new BigDecimal(applyBorrowCashNum), 4, BigDecimal.ROUND_HALF_UP);
        if (dueAmount != BigDecimal.ZERO) {
            overdueRate = overdueAmount.divide(dueAmount, 2, BigDecimal.ROUND_HALF_UP);
            returnedRate = returnAmount.divide(dueAmount, 2, BigDecimal.ROUND_HALF_UP);//回款金额
        }
        if (applyBorrowCashNum != 0) {
            borrowPassRate = new BigDecimal(borrowMans).divide(new BigDecimal(applyBorrowCashNum), 4, BigDecimal.ROUND_HALF_UP);//借款通过人数
        }
        if (totalLoanAmount != BigDecimal.ZERO) {
            profitRate = (returnAmount.subtract(overdueAmount)).divide(totalLoanAmount, 4, BigDecimal.ROUND_HALF_UP);
        }

        if (allUserNum != 0) {
            riskPassRate = new BigDecimal(paseUserNum).divide(new BigDecimal(allUserNum), 4, BigDecimal.ROUND_HALF_UP);
        }
        mgrBorrowInfoAnalysisVo.setRiskPassRate(riskPassRate);
        mgrBorrowInfoAnalysisVo.setTotalLoanAmount(totalLoanAmount);
        mgrBorrowInfoAnalysisVo.setBorrowMans(borrowMans);
        mgrBorrowInfoAnalysisVo.setBorrowPassRate(borrowPassRate);
        mgrBorrowInfoAnalysisVo.setReturnedRate(returnedRate);
        mgrBorrowInfoAnalysisVo.setOverdueRate(overdueRate);
        mgrBorrowInfoAnalysisVo.setProfitRate(profitRate);
        mgrBorrowInfoAnalysisVo.setRepeatBorrowRate(repeatBorrowRate);
        mgrBorrowInfoAnalysisVo.setBorrowDayAmount(borrowDayAmount);
        mgrBorrowInfoAnalysisVo.setBorrowDayMans(borrowDayMans);
        return mgrBorrowInfoAnalysisVo;
    }

    private Integer getDays(Date time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    @Override
    public MgrDashboardInfoVo getBorrowInfoDashboard() {
        List<JsdBorrowCashDo> todayBorrowCashDoList = mgrBorrowCashService.getBorrowCashByDays(0);//今天的借款信息
        List<JsdBorrowCashDo> ystBorrowCashDoList = mgrBorrowCashService.getBorrowCashByDays(1);//昨天的借款信息
        List<JsdBorrowCashDo> weekBorrowCashDoList = mgrBorrowCashService.getBorrowCashByDays(7);//一周前的借款信息
        BigDecimal todayAmount = todayBorrowCashDoList.stream().map(JsdBorrowCashDo::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal ystAmount = ystBorrowCashDoList.stream().map(JsdBorrowCashDo::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal weekAmount = weekBorrowCashDoList.stream().map(JsdBorrowCashDo::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        int borrowMans = todayBorrowCashDoList.stream().map(JsdBorrowCashDo::getUserId).collect(Collectors.toSet()).size();//去重放贷人数
        BigDecimal avgAmountPer = BigDecimal.ZERO;
        if (null != todayBorrowCashDoList && 0 != todayBorrowCashDoList.size()) {
            avgAmountPer = todayAmount.divide(new BigDecimal(todayBorrowCashDoList.size()), 2, BigDecimal.ROUND_HALF_UP);
        }

        BigDecimal totalLoanAmtRateByWeek = BigDecimal.ZERO;//放款总额周同比
        if (weekAmount != BigDecimal.ZERO) {
            totalLoanAmtRateByWeek = todayAmount.divide(weekAmount, 4, BigDecimal.ROUND_HALF_UP).subtract(BigDecimal.ONE);
        }
        BigDecimal totalLoanAmtRateByDay = BigDecimal.ZERO;//放款总额日环比
        if (ystAmount != BigDecimal.ZERO) {
            totalLoanAmtRateByDay = todayAmount.divide(ystAmount, 4, BigDecimal.ROUND_HALF_UP).subtract(BigDecimal.ONE);
        }

        int todayAllUserNum = mgrUserAuthService.getPassPersonNumByStatusEqualDays("", 0);
        int todayPaseUserNum = mgrUserAuthService.getPassPersonNumByStatusEqualDays("Y", 0);
        BigDecimal riskPassRate = BigDecimal.ZERO;
        if (todayAllUserNum != 0) {
            riskPassRate = new BigDecimal(todayPaseUserNum).divide(new BigDecimal(todayAllUserNum), 4, BigDecimal.ROUND_HALF_UP).subtract(BigDecimal.ONE);
        }
        int ystAllUserNum = mgrUserAuthService.getPassPersonNumByStatusEqualDays("", 1);
        int ystPaseUserNum = mgrUserAuthService.getPassPersonNumByStatusEqualDays("Y", 1);
        BigDecimal riskPassRateByDay = BigDecimal.ZERO;
        if (ystAllUserNum != 0) {
            riskPassRateByDay = new BigDecimal(ystPaseUserNum).divide(new BigDecimal(ystAllUserNum), 4, BigDecimal.ROUND_HALF_UP).subtract(BigDecimal.ONE);
        }
        int weekAllUserNum = mgrUserAuthService.getPassPersonNumByStatusEqualDays("", 7);
        int weekPaseUserNum = mgrUserAuthService.getPassPersonNumByStatusEqualDays("Y", 7);
        BigDecimal riskPassRateByWeek = BigDecimal.ZERO;
        if (weekAllUserNum != 0) {
            riskPassRateByWeek = new BigDecimal(weekPaseUserNum).divide(new BigDecimal(weekAllUserNum), 4, BigDecimal.ROUND_HALF_UP).subtract(BigDecimal.ONE);
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
        List<JsdBorrowCashDo> todayBorrowCashDoList = mgrBorrowCashService.getBorrowCashByDays(0);
        Map<Integer, List<JsdBorrowCashDo>> borrowCashInfo = todayBorrowCashDoList.stream().collect(Collectors.groupingBy(JsdBorrowCashDo::getGmtCreateHour));
        ArrayList<Map<Integer, Integer>> list = new ArrayList();
        borrowCashInfo.forEach((k, v) -> {
            Map map = new HashMap();
            map.put("hour", Integer.parseInt(String.valueOf(k)));
            map.put("num", v.size());
            list.add(map);
        });
        list.sort((o1, o2) -> o1.get("hour") - o2.get("hour"));

        MgrTrendTodayInfoVo mgrTrendTodayInfoVo = new MgrTrendTodayInfoVo();
        mgrTrendTodayInfoVo.setLoanNumPerHourToday(list);
        return mgrTrendTodayInfoVo;
    }

    @Override
    public MgrDashboardCityInfoVo getdashboardCityInfo() {
        List<JsdBorrowLegalOrderInfoDo> orderInfoDoList = mgrBorrowLegalOrderInfoService.getInfoByDays(0);
        Map<String, List<JsdBorrowLegalOrderInfoDo>> borrowCashInfo = orderInfoDoList.stream().collect(Collectors.groupingBy(JsdBorrowLegalOrderInfoDo::getBorrowCity));
        ArrayList<Map<String, Integer>> list = new ArrayList();
        borrowCashInfo.forEach((k, v) -> {
            Map map = new HashMap();
            map.put("city", String.valueOf(k));
            map.put("num", v.size());
            list.add(map);
        });
        list.sort((o1, o2) -> o1.get("hour") - o2.get("hour"));
        MgrDashboardCityInfoVo mgrDashboardCityInfoVo = new MgrDashboardCityInfoVo();
        mgrDashboardCityInfoVo.setLoanCityNumToday(list);
        return mgrDashboardCityInfoVo;
    }

}
