package com.ald.jsd.mgr.biz.service.impl;

import com.ald.fanbei.api.biz.vo.MgrBorrowInfoAnalysisVo;
import com.ald.fanbei.api.biz.vo.MgrDashboardCityInfoVo;
import com.ald.fanbei.api.biz.vo.MgrDashboardInfoVo;
import com.ald.fanbei.api.biz.vo.MgrTrendTodayInfoVo;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashRepaymentDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderInfoDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderRepaymentDo;
import com.ald.jsd.mgr.biz.service.*;
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
 * @author GSQ
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
    @Resource
    private MgrBorrowCashRepaymentService mgrBorrowCashRepaymentService;
    @Resource
    private MgrBorrowLegalOrderRepaymentService mgrBorrowLegalOrderRepaymentService;
    @Resource
    private MgrBorrowCashRenewalService mgrBorrowCashRenewalService;

    @Override
    public MgrBorrowInfoAnalysisVo getBorrowInfoAnalysis(AnalysisReq analysisReq) {
        Date startTime = null;
        Date endTime = null;
        if (!NumberUtil.isNullOrZero(analysisReq.days)) {
            startTime = DateUtil.initStartDateByDay(DateUtil.addDays(new Date(), -analysisReq.days + 1));
            endTime = DateUtil.initEndDateByDay(new Date());
        } else if (!StringUtils.isBlank(analysisReq.endDate) && !StringUtils.isBlank(analysisReq.startDate)) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                startTime = dateFormat.parse(analysisReq.startDate);
                endTime = dateFormat.parse(analysisReq.endDate);
            } catch (ParseException e) {
                logger.error("mgrBorrowCashAnalysisService buildBorrowCash error =>{}", e);
                e.printStackTrace();
            }
        }

        List<JsdBorrowCashDo> jsdBorrowCashDoList = mgrBorrowCashService.getBorrowCashBetweenStartAndEnd(startTime, endTime);//借款成功笔数

        BigDecimal totalLoanAmount = jsdBorrowCashDoList.stream().map(JsdBorrowCashDo::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        Integer borrowMans = jsdBorrowCashDoList.stream().map(JsdBorrowCashDo::getUserId).collect(Collectors.toSet()).size();//去重放贷人数
        Integer borrowDayMans = 0; //日均借款人数
        BigDecimal borrowDayAmount = BigDecimal.ZERO; //日均放款额
        long days = DateUtil.getNumberOfDayBetween(DateUtil.addDays(startTime, -1), endTime);
        if (!NumberUtil.isNullOrZeroOrNegative(days)) {
            borrowDayMans = new BigDecimal(borrowMans).divide(new BigDecimal(days), 0, BigDecimal.ROUND_UP).intValue();
            borrowDayAmount = totalLoanAmount.divide(new BigDecimal(days), 2, BigDecimal.ROUND_HALF_UP);
        }

        BigDecimal repeatBorrowRate = BigDecimal.ZERO;//复借率
        int arrivalBorrowCashPerNum = mgrBorrowCashService.getArrivalBorrowCashBetweenStartAndEnd(startTime, endTime);//当期到期人数
        int haveBorrowCashPerNum = mgrBorrowCashService.getUserNumBetweenStartAndEnd(startTime, endTime);//当期复借人数
        if (arrivalBorrowCashPerNum != 0) {
            repeatBorrowRate = new BigDecimal(haveBorrowCashPerNum).divide(new BigDecimal(arrivalBorrowCashPerNum), 4, BigDecimal.ROUND_HALF_UP);
        }

        BigDecimal overdueRate = BigDecimal.ZERO;//逾期率
        List<JsdBorrowCashDo> arrivalBorrowCashList = mgrBorrowCashService.getPlanRepaymentBorrowCashBetweenStartAndEnd(DateUtil.addDays(startTime, -1), DateUtil.addDays(endTime, -1));//到期借款笔数
        BigDecimal arrivalAmount = mgrBorrowCashService.getPlanRepaymentCashAmountBetweenStartAndEnd(DateUtil.addDays(startTime, -1), DateUtil.addDays(endTime, -1));//当期到期金额
        BigDecimal overDueAmount = arrivalBorrowCashList.stream().filter(jsdBorrowCashDo -> jsdBorrowCashDo.getOverdueStatus().equals("Y")).map(JsdBorrowCashDo::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (arrivalAmount != null && arrivalAmount.compareTo(BigDecimal.ZERO) != 0) {
            overdueRate = overDueAmount.divide(arrivalAmount, 4, BigDecimal.ROUND_HALF_UP);
        }

        BigDecimal returnedRate = BigDecimal.ZERO;//回款率
        BigDecimal returnAmount = buildTotalRepayAmtByDate(startTime, endTime);//当期还款金额
        BigDecimal dueAmount = mgrBorrowCashService.getPlanRepaymentCashAmountBetweenStartAndEnd(startTime, endTime);//当期到期金额
        if (dueAmount != null && dueAmount.compareTo(BigDecimal.ZERO) != 0) {
            returnedRate = returnAmount.divide(dueAmount, 4, BigDecimal.ROUND_HALF_UP);//回款金额
        }

        BigDecimal borrowPassRate = BigDecimal.ZERO;//借款通过率
        int applyBorrowCashSuPerNum = mgrBorrowCashService.getApplyBorrowCashSuPerBetweenStartAndEnd(startTime, endTime);//申请借款成功人数
        int applyBorrowCashPerNum = mgrBorrowCashService.getApplyBorrowCashPerBetweenStartAndEnd(startTime, endTime);//申请借款人数
        if (applyBorrowCashPerNum != 0) {
            borrowPassRate = new BigDecimal(applyBorrowCashSuPerNum).divide(new BigDecimal(applyBorrowCashPerNum), 4, BigDecimal.ROUND_HALF_UP);//借款通过人数
        }

        BigDecimal profitRate = BigDecimal.ZERO;//收益率
        if (totalLoanAmount != null && totalLoanAmount.compareTo(BigDecimal.ZERO) != 0) {
            profitRate = (returnAmount.subtract(overDueAmount)).divide(totalLoanAmount, 4, BigDecimal.ROUND_HALF_UP);
        }

        BigDecimal riskPassRate = BigDecimal.ZERO;//认证通过率
        int allUserNum = mgrUserAuthService.getPassPersonNumByStatusBetweenStartAndEnd("", startTime, endTime);
        int paseUserNum = mgrUserAuthService.getPassPersonNumByStatusBetweenStartAndEnd("Y", startTime, endTime);
        if (allUserNum != 0) {
            riskPassRate = new BigDecimal(paseUserNum).divide(new BigDecimal(allUserNum), 4, BigDecimal.ROUND_HALF_UP);
        }
        MgrBorrowInfoAnalysisVo mgrBorrowInfoAnalysisVo = new MgrBorrowInfoAnalysisVo();
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

    @Override
    public MgrDashboardInfoVo getBorrowInfoDashboard() {

        BigDecimal todayAmount = buildAmountBorrowCashByDays(0);//今天的借款金额

        BigDecimal ystAmount = buildAmountBorrowCashByDays(1);//昨天的借款金额

        BigDecimal weekAmount = buildAmountBorrowCashByDays(7);//上周的借款金额

        List<JsdBorrowCashDo> todayBorrowCashDoList = mgrBorrowCashService.getBorrowCashByDays(0);//今天的借款信息
        int borrowMans = todayBorrowCashDoList.stream().map(JsdBorrowCashDo::getUserId).collect(Collectors.toSet()).size();//去重放贷人数
        BigDecimal avgAmountPer = BigDecimal.ZERO;
        if (null != todayBorrowCashDoList && 0 != todayBorrowCashDoList.size()) {
            avgAmountPer = todayAmount.divide(new BigDecimal(borrowMans), 2, BigDecimal.ROUND_HALF_UP);
        }

        BigDecimal totalLoanAmtRateByWeek = BigDecimal.ZERO;//借款总额周同比
        if (weekAmount != BigDecimal.ZERO) {
            totalLoanAmtRateByWeek = todayAmount.divide(weekAmount, 4, BigDecimal.ROUND_HALF_UP).subtract(BigDecimal.ONE);
        }
        BigDecimal totalLoanAmtRateByDay = BigDecimal.ZERO;//借款总额日环比
        if (ystAmount != BigDecimal.ZERO) {
            totalLoanAmtRateByDay = todayAmount.divide(ystAmount, 4, BigDecimal.ROUND_HALF_UP).subtract(BigDecimal.ONE);
        }

        BigDecimal riskPassRate = buildPassPersonNum(0);//今日认证通过率

        BigDecimal ystRiskPassRate = buildPassPersonNum(1);//昨日认证通过率

        BigDecimal weekRiskPassRate = buildPassPersonNum(7);//上周认证通过率

        BigDecimal riskPassRateByDay = BigDecimal.ZERO;//认证通过率日环比

        if (ystRiskPassRate.compareTo(BigDecimal.ZERO) != 0) {
            riskPassRateByDay = riskPassRate.divide(ystRiskPassRate, 4, BigDecimal.ROUND_HALF_UP).subtract(BigDecimal.ONE);
        }

        BigDecimal riskPassRateByWeek = BigDecimal.ZERO;//认证通过率周同比

        if (weekRiskPassRate.compareTo(BigDecimal.ZERO) != 0) {
            riskPassRateByWeek = riskPassRate.divide(weekRiskPassRate, 4, BigDecimal.ROUND_HALF_UP).subtract(BigDecimal.ONE);
        }


        BigDecimal totalRepayAmtRateByDay = BigDecimal.ZERO;//今日还款额日环比

        BigDecimal avgRepayAmtPer = BigDecimal.ZERO;//人均还款额

        BigDecimal totalRepayAmtRateByWeek = BigDecimal.ZERO;//今日还款额周同比

        BigDecimal totalRepayAmt = buildTotalRepayAmtByOneDays(0);//今日还款额

        BigDecimal ystRepayAmt = buildTotalRepayAmtByOneDays(1);//昨日还款额

        BigDecimal lastWeekRepayAmt = buildTotalRepayAmtByOneDays(7);//上周还款额

        if (ystRepayAmt.compareTo(BigDecimal.ZERO) != 0) {
            totalRepayAmtRateByDay = totalRepayAmt.divide(ystRepayAmt, 4, BigDecimal.ROUND_HALF_UP).subtract(BigDecimal.ONE);
        }
        if (lastWeekRepayAmt.compareTo(BigDecimal.ZERO) != 0) {
            totalRepayAmtRateByWeek = totalRepayAmt.divide(lastWeekRepayAmt, 4, BigDecimal.ROUND_HALF_UP).subtract(BigDecimal.ONE);
        }
        int personNum = buildTotalRepayPerson(0);

        if (!NumberUtil.isNullOrZero(personNum)) {
            avgRepayAmtPer = totalRepayAmt.divide(new BigDecimal(personNum), 2, BigDecimal.ROUND_HALF_UP);
        }
        BigDecimal todayArrive = buildArriveAmountBorrowCashByDays(0);//今天的放款金额

        BigDecimal totalArriveLoanAmtRateByWeek = BigDecimal.ZERO;//放款总额周同比
        BigDecimal weekArriveAmount = buildArriveAmountBorrowCashByDays(7);//上周的放款金额
        BigDecimal totalArriveLoanAmtRateByDay = BigDecimal.ZERO;//放款总额周同比
        BigDecimal dayArriveAmount = buildArriveAmountBorrowCashByDays(1);//昨天的放款金额
        BigDecimal avgAmountArrive = BigDecimal.ZERO;
        if (null != todayBorrowCashDoList && 0 != todayBorrowCashDoList.size()) {
            avgAmountArrive = todayArrive.divide(new BigDecimal(borrowMans), 2, BigDecimal.ROUND_HALF_UP);
        }
        if (weekArriveAmount.compareTo(BigDecimal.ZERO) != 0) {
            totalArriveLoanAmtRateByWeek = todayArrive.divide(weekArriveAmount, 4, BigDecimal.ROUND_HALF_UP).subtract(BigDecimal.ONE);
        }
        if(dayArriveAmount.compareTo(BigDecimal.ZERO) != 0){
            totalArriveLoanAmtRateByDay=todayArrive.divide(dayArriveAmount, 4, BigDecimal.ROUND_HALF_UP).subtract(BigDecimal.ONE);
        }
        BigDecimal renewalAmount=buildRenewalAmount(0);
        BigDecimal renewalAmountWeek=buildRenewalAmount(7);
        BigDecimal renewalAmountDay=buildRenewalAmount(1);

        BigDecimal totalRenewalAmountByWeek=BigDecimal.ZERO;
        if(renewalAmountWeek.compareTo(BigDecimal.ZERO) != 0){
            totalRenewalAmountByWeek=renewalAmount.divide(renewalAmountWeek, 4, BigDecimal.ROUND_HALF_UP).subtract(BigDecimal.ONE);
        }
        BigDecimal totalRenewalAmountByDay=BigDecimal.ZERO;
        if(renewalAmountDay.compareTo(BigDecimal.ZERO) != 0){
            totalRenewalAmountByDay=renewalAmount.divide(renewalAmountDay, 4, BigDecimal.ROUND_HALF_UP).subtract(BigDecimal.ONE);
        }
        BigDecimal avgRenewalAmount=BigDecimal.ZERO;
        BigDecimal renewalPersons=new BigDecimal(mgrBorrowCashRenewalService.getRenewalPersonsByDays(0));
        if(renewalPersons.compareTo(BigDecimal.ZERO)!=0){
            avgRenewalAmount=renewalAmount.divide(renewalPersons, 2, BigDecimal.ROUND_HALF_UP);
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
        mgrDashboardInfoVo.setTotalRepayAmt(totalRepayAmt);
        mgrDashboardInfoVo.setTotalRepayAmtRateByDay(totalRepayAmtRateByDay);
        mgrDashboardInfoVo.setTotalRepayAmtRateByWeek(totalRepayAmtRateByWeek);
        mgrDashboardInfoVo.setAvgRepayAmtPer(avgRepayAmtPer);
        mgrDashboardInfoVo.setTotalArriveLoanAmt(todayArrive);
        mgrDashboardInfoVo.setTotalLoanAmtRateByWeek(totalArriveLoanAmtRateByWeek);
        mgrDashboardInfoVo.setTotalArriveLoanAmtRateByDay(totalArriveLoanAmtRateByDay);
        mgrDashboardInfoVo.setAvgAmountArrive(avgAmountArrive);
        mgrDashboardInfoVo.setRepeatBorrowRate(repeatBorrowRate());
        mgrDashboardInfoVo.setRepeatBorrowRateByWeek(repeatBorrowRateByWeek());
        mgrDashboardInfoVo.setRepeatBorrowRateByDay(repeatBorrowRateByDay());
        mgrDashboardInfoVo.setBadDebtRate(badDebtRate());
        mgrDashboardInfoVo.setTotalRenewalAmount(renewalAmount);
        mgrDashboardInfoVo.setTotalRenewalAmountByWeek(totalRenewalAmountByWeek);
        mgrDashboardInfoVo.setTotalRenewalAmountByDay(totalRenewalAmountByDay);
        mgrDashboardInfoVo.setAvgRenewalAmount(avgRenewalAmount);
        return mgrDashboardInfoVo;
    }

    public BigDecimal buildRenewalAmount(Integer days){
        Date startTime = DateUtil.initStartDateByDay(DateUtil.addDays(new Date(), -days));
        Date endTime = DateUtil.initEndDateByDay(DateUtil.addDays(new Date(), -days));
        BigDecimal renewalAmount=mgrBorrowCashRenewalService.getRenewalAmountByDays(startTime,endTime);
        return renewalAmount==null?BigDecimal.ZERO:renewalAmount;
    }

    public BigDecimal badDebtRate(){
        BigDecimal badDebtRate=BigDecimal.ZERO;
        BigDecimal badDebtAmount =mgrBorrowCashService.getOverdueBorrowAmountTo30Day();
        if(badDebtAmount==null){
            badDebtAmount=BigDecimal.ZERO;
        }
        BigDecimal sumAmount =mgrBorrowCashService.getAllBorrowAmount();
        if(sumAmount!=null){
            badDebtRate= badDebtAmount.divide(sumAmount,4, BigDecimal.ROUND_HALF_UP);
        }
        return badDebtRate==null?BigDecimal.ZERO:badDebtRate;
    }

    //复借率
    public BigDecimal repeatBorrowRate(){
        Date startTime = DateUtil.initStartDateByDay(DateUtil.addDays(new Date(), 0));
        Date endTime = DateUtil.initEndDateByDay(DateUtil.addDays(new Date(), 0));
        BigDecimal repeatBorrowRate = BigDecimal.ZERO;//复借率
        int arrivalBorrowCashPerNum = mgrBorrowCashService.getArrivalBorrowCashBetweenStartAndEnd(startTime, endTime);//当期到期人数
        int haveBorrowCashPerNum=repeatPersons(0);
        if (haveBorrowCashPerNum != 0) {
            repeatBorrowRate = new BigDecimal(haveBorrowCashPerNum).divide(new BigDecimal(arrivalBorrowCashPerNum), 4, BigDecimal.ROUND_HALF_UP);
        }
        return repeatBorrowRate;
    }
    //复借人数
    public int repeatPersons(Integer days){
        Date startTime = DateUtil.initStartDateByDay(DateUtil.addDays(new Date(), -days));
        Date endTime = DateUtil.initEndDateByDay(DateUtil.addDays(new Date(), -days));
        int haveBorrowCashPerNum = mgrBorrowCashService.getUserNumBetweenStartAndEnd(startTime, endTime);//当期复借人数
        return haveBorrowCashPerNum;
    }
    public BigDecimal repeatBorrowRateByWeek(){
        BigDecimal repeatBorrowWeek=BigDecimal.ZERO;
        int toDayRepeat=repeatPersons(0);
        int weekRepeat=repeatPersons(7);
        if(weekRepeat!=0){
            repeatBorrowWeek=new BigDecimal(toDayRepeat).divide(new BigDecimal(weekRepeat), 4, BigDecimal.ROUND_HALF_UP).subtract(BigDecimal.ONE);
        }
        return repeatBorrowWeek;

    }
    public BigDecimal repeatBorrowRateByDay(){
        BigDecimal repeatBorrowDay=BigDecimal.ZERO;
        int toDayRepeat=repeatPersons(0);
        int dayRepeat=repeatPersons(1);
        if(dayRepeat!=0){
            repeatBorrowDay=new BigDecimal(toDayRepeat).divide(new BigDecimal(dayRepeat), 4, BigDecimal.ROUND_HALF_UP).subtract(BigDecimal.ONE);
        }
        return repeatBorrowDay;
    }
    public BigDecimal buildAmountBorrowCashByDays(Integer days) {
        BigDecimal amount = getBorrowCashList(days).stream().map(JsdBorrowCashDo::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        return amount;
    }



    public BigDecimal buildArriveAmountBorrowCashByDays(Integer days) {
        BigDecimal arriveAmount = getBorrowCashList(days).stream().map(JsdBorrowCashDo::getArrivalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        return arriveAmount;
    }

    private List<JsdBorrowCashDo> getBorrowCashList(Integer days){
        Date startTime = DateUtil.initStartDateByDay(DateUtil.addDays(new Date(), -days));
        Date endTime = DateUtil.initEndDateByDay(DateUtil.addDays(new Date(), -days));
        List<JsdBorrowCashDo> jsdBorrowCashDoList = mgrBorrowCashService.getBorrowCashBetweenStartAndEnd(startTime, endTime);//借款成功笔数
        return jsdBorrowCashDoList;
    }

    public BigDecimal buildPassPersonNum(Integer days) {
        int AllUserNum = mgrUserAuthService.getPassPersonNumByStatusEqualDays("", days);
        int PaseUserNum = mgrUserAuthService.getPassPersonNumByStatusEqualDays("Y", days);
        BigDecimal riskPassRate = BigDecimal.ZERO;
        if (AllUserNum != 0) {
            riskPassRate = new BigDecimal(PaseUserNum).divide(new BigDecimal(AllUserNum), 4, BigDecimal.ROUND_HALF_UP);
        }
        return riskPassRate;
    }

    public int buildTotalRepayPerson(int days) {
        List<JsdBorrowCashRepaymentDo> jsdBorrowCashRepaymentDoList = mgrBorrowCashRepaymentService.getBorrowCashRepayByOneDays(days);
        //借款还款
        Set borrowCashList = jsdBorrowCashRepaymentDoList.stream().map(JsdBorrowCashRepaymentDo::getUserId).collect(Collectors.toSet());

        List<JsdBorrowLegalOrderRepaymentDo> jsdBorrowLegalOrderRepaymentDoList = mgrBorrowLegalOrderRepaymentService.getBorrowCashRepayByDays(days);
        //商品还款
        Set borrowOrderList = jsdBorrowLegalOrderRepaymentDoList.stream().map(JsdBorrowLegalOrderRepaymentDo::getUserId).collect(Collectors.toSet());
        borrowCashList.addAll(borrowOrderList);

        return borrowCashList.size();
    }

    public BigDecimal buildTotalRepayAmtByOneDays(int days) {
        List<JsdBorrowCashRepaymentDo> jsdBorrowCashRepaymentDoList = mgrBorrowCashRepaymentService.getBorrowCashRepayByOneDays(days);
        //借款还款
        BigDecimal borrowCashReapymoney = jsdBorrowCashRepaymentDoList.stream().map(JsdBorrowCashRepaymentDo::getRepaymentAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        List<JsdBorrowLegalOrderRepaymentDo> jsdBorrowLegalOrderRepaymentDoList = mgrBorrowLegalOrderRepaymentService.getBorrowCashRepayByDays(days);
        //商品还款
        BigDecimal borrowOrderReapymoney = jsdBorrowLegalOrderRepaymentDoList.stream().map(JsdBorrowLegalOrderRepaymentDo::getRepayAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        return borrowCashReapymoney.add(borrowOrderReapymoney);
    }

    public BigDecimal buildTotalRepayAmtByDate(Date startDate, Date endDate) {
        List<JsdBorrowCashRepaymentDo> jsdBorrowCashRepaymentDoList = mgrBorrowCashRepaymentService.getBorrowCashRepayBetweenStartAndEnd(startDate, endDate);
        //借款还款
        BigDecimal borrowCashReapymoney = jsdBorrowCashRepaymentDoList.stream().map(JsdBorrowCashRepaymentDo::getRepaymentAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        List<JsdBorrowLegalOrderRepaymentDo> jsdBorrowLegalOrderRepaymentDoList = mgrBorrowLegalOrderRepaymentService.getBorrowCashOrderRepayBetweenStartAndEnd(startDate, endDate);
        //商品还款
        BigDecimal borrowOrderReapymoney = jsdBorrowLegalOrderRepaymentDoList.stream().map(JsdBorrowLegalOrderRepaymentDo::getRepayAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        return borrowCashReapymoney.add(borrowOrderReapymoney);
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
        MgrDashboardCityInfoVo mgrDashboardCityInfoVo = new MgrDashboardCityInfoVo();
        mgrDashboardCityInfoVo.setLoanCityNumToday(list);
        return mgrDashboardCityInfoVo;
    }

}
