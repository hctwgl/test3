package com.ald.fanbei.api.biz.service.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import com.ald.jsd.mgr.dal.domain.FinaneceDataDo;
import com.ald.fanbei.api.dal.dao.*;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.dal.domain.dto.JsdCashDto;
import com.ald.fanbei.api.biz.service.JsdLegalContractPdfCreateService;
import com.ald.fanbei.api.biz.third.util.JobThreadPoolUtils;
import com.ald.fanbei.api.common.enums.*;
import com.itextpdf.text.DocumentException;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.ald.fanbei.api.biz.bo.JsdProctocolBo;
import com.ald.fanbei.api.biz.bo.jsd.TrialBeforeBorrowBo;
import com.ald.fanbei.api.biz.bo.jsd.TrialBeforeBorrowBo.TrialBeforeBorrowReq;
import com.ald.fanbei.api.biz.bo.jsd.TrialBeforeBorrowBo.TrialBeforeBorrowResp;
import com.ald.fanbei.api.biz.bo.xgxy.XgxyBorrowNoticeBo;
import com.ald.fanbei.api.biz.service.JsdBorrowCashService;
import com.ald.fanbei.api.biz.service.JsdNoticeRecordService;
import com.ald.fanbei.api.biz.service.JsdResourceService;
import com.ald.fanbei.api.biz.service.impl.JsdResourceServiceImpl.ResourceRateInfoBo;
import com.ald.fanbei.api.biz.third.enums.XgxyBorrowNotifyStatus;
import com.ald.fanbei.api.biz.third.util.UpsUtil;
import com.ald.fanbei.api.biz.third.util.XgxyUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.ConfigProperties;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.exception.BizException;
import com.ald.fanbei.api.common.exception.BizExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.dal.domain.dto.LoanDto;
import com.ald.fanbei.api.dal.query.LoanQuery;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 极速贷ServiceImpl
 *
 * @author yanghailong
 * @version 1.0.0 初始化
 * @date 2018-08-22 16:18:06
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */

@Service("jsdBorrowCashService")
public class JsdBorrowCashServiceImpl extends ParentServiceImpl<JsdBorrowCashDo, Long> implements JsdBorrowCashService {

    private static String notifyHost = null;

    @Resource
    JsdBorrowCashDao jsdBorrowCashDao;
    @Resource
    JsdResourceService jsdResourceService;
    @Resource
    JsdBorrowLegalOrderDao jsdBorrowLegalOrderDao;
    @Resource
    JsdBorrowLegalOrderCashDao jsdBorrowLegalOrderCashDao;
    @Resource
    JsdNoticeRecordService jsdNoticeRecordService;

    @Resource
    JsdLegalContractPdfCreateService jsdLegalContractPdfCreateService;
    @Resource
    XgxyUtil xgxyUtil;
    @Resource
    BizCacheUtil bizCacheUtil;
    @Resource
    TransactionTemplate transactionTemplate;
    @Resource
    UpsUtil upsUtil;
    @Resource
    JobThreadPoolUtils jobThreadPoolUtils;


    @Override
    public BaseDao<JsdBorrowCashDo, Long> getDao() {
        return jsdBorrowCashDao;
    }

    @Override
    public JsdBorrowCashDo getByBorrowNo(String borrowNo) {
        return jsdBorrowCashDao.getByBorrowNo(borrowNo);
    }

    @Override
    public JsdBorrowCashDo getByRenewalNo(String borrowNo) {
        return jsdBorrowCashDao.getByRenewalNo(borrowNo);
    }

    @Override
    public JsdBorrowCashDo getByTradeNoXgxy(String tradeNoXgxy) {
        return jsdBorrowCashDao.getByTradeNoXgxy(tradeNoXgxy);
    }

    @Override
    public void checkCanBorrow(Long userId, BigDecimal amount) {
        // 未完成借款校验
        List<JsdBorrowCashDo> notFinishBorrowList = jsdBorrowCashDao.getBorrowCashByStatusNotInFinshAndClosed(userId);
        if (!notFinishBorrowList.isEmpty()) {
            throw new BizException(BizExceptionCode.JSD_BORROW_CASH_STATUS_ERROR);
        }

        // 借款金额区间校验
        JsdResourceDo rateInfoDo = jsdResourceService.getByTypeAngSecType(Constants.JSD_CONFIG, Constants.JSD_RATE_INFO);
        if (rateInfoDo != null) {
            String[] split = rateInfoDo.getValue2().split(",");
            if (amount.compareTo(new BigDecimal(split[0])) < 0 || amount.compareTo(new BigDecimal(split[1])) > 0) {
                throw new BizException(BizExceptionCode.BORROW_AMOUNT_NOT_IN_INTERVAL);
            }
        }
    }

    @Override
    public String getCurrentLastBorrowNo(String orderNoPre) {
        return jsdBorrowCashDao.getCurrentLastBorrowNo(orderNoPre);
    }

    @Override
    public int getBorrowCashOverdueCount() {
        Date date = new Date(System.currentTimeMillis());
        Date bengin = DateUtil.getStartOfDate(date);
        return jsdBorrowCashDao.getBorrowCashOverdueCount(bengin);
    }

    @Override
    public int getBorrowCashByBeforeTodayCount(Date todayLast) {
        return jsdBorrowCashDao.getBorrowCashByBeforeTodayCount(todayLast);
    }

    @Override
    public List<JsdBorrowCashDo> getBorrowCashOverdue(int nowPage, int pageSize) {
        Date date = new Date(System.currentTimeMillis());
        Date bengin = DateUtil.getStartOfDate(date);
        return jsdBorrowCashDao.getBorrowCashOverduePaging(bengin, nowPage, pageSize);
    }

    @Override
    public List<JsdBorrowCashDo> getBorrowCashByBeforeToday(int nowPage, int pageSize, Date todayLast) {
        return jsdBorrowCashDao.getBorrowCashByBeforeToday(nowPage, pageSize, todayLast);
    }

    @Override
    public List<JsdBorrowCashDo> getBorrowCashRepayByUserIds(String userIds, Date todayLast) {
        return jsdBorrowCashDao.getBorrowCashRepayByUserIds(userIds,todayLast);
    }

    @Override
    public List<JsdBorrowCashDo> getBorrowCashOverdueByUserIds(String userIds) {
        Date date = new Date(System.currentTimeMillis());
        Date bengin = DateUtil.getStartOfDate(date);
        return jsdBorrowCashDao.getBorrowCashOverdueByUserIds(bengin, userIds);
    }

    public  List<JsdBorrowCashDo> getTodayBorrowCashRepayByUserIds(String userIds, Date todayLast){
        return jsdBorrowCashDao.getTodayBorrowCashRepayByUserIds(userIds,DateUtil.formatDate(todayLast,"yyyy-MM-dd"));

    }
    @Override
    public List<LoanDto> getReviewLoanList(LoanQuery query) {
        return jsdBorrowCashDao.getReviewLoanList(query);
    }

    @Override
    public HashMap<String, BigDecimal> getReviewLoanStatistics() {
        HashMap<String, BigDecimal> result = new HashMap<>();
        HashMap<String, Long> hashMap = jsdBorrowCashDao.getReviewLoanStatistics();
        result.put("wait", BigDecimal.valueOf(hashMap.get("wait")));
        result.put("pass", BigDecimal.valueOf(hashMap.get("pass")));
        result.put("review", BigDecimal.valueOf(hashMap.get("review")));
        if (result.get("review").equals(BigDecimal.ZERO)) {
            result.put("passingRate", BigDecimal.ZERO);
        } else {
            result.put("passingRate", BigDecimalUtil.divide(result.get("pass"), result.get("review")).multiply(new BigDecimal(100)));
        }
        return result;
    }

    @Override
    public HashMap<String, BigDecimal> getLoanStatistics() {
        HashMap<String, BigDecimal> result = new HashMap<>();
        HashMap<String, Object> hashMap = jsdBorrowCashDao.getLoanStatistics();
        result.put("apply", new BigDecimal((Long) hashMap.get("apply")));
        result.put("amount", (BigDecimal) hashMap.get("amount"));
        if (result.get("apply").equals(BigDecimal.ZERO)) {
            result.put("perAmount", BigDecimal.ZERO);
        } else {
            result.put("perAmount", BigDecimalUtil.divide(result.get("amount"), result.get("apply")));
        }
        return result;
    }

    @Override
    public HashMap<String, BigDecimal> getRepayStatistics() {
        HashMap<String, BigDecimal> result = new HashMap<>();
        HashMap<String, Object> hashMap = jsdBorrowCashDao.getRepayStatistics();
        result.put("awaitRepay", new BigDecimal((Long) hashMap.get("awaitRepay")));
        result.put("allAwaitRepay", new BigDecimal((Long) hashMap.get("allAwaitRepay")));
        result.put("overdueAwaitRepay", new BigDecimal((Long) hashMap.get("overdueAwaitRepay")));
        result.put("repay", new BigDecimal((Long) hashMap.get("repay")));
        result.put("advanceRepay", new BigDecimal((Long) hashMap.get("advanceRepay")));
        result.put("overdueRepay", new BigDecimal((Long) hashMap.get("overdueRepay")));
        if (result.get("awaitRepay").equals(BigDecimal.ZERO)) {
            result.put("repayRate", BigDecimal.ZERO);
        } else {
            result.put("repayRate", BigDecimalUtil.divide(result.get("repay"), result.get("awaitRepay").add(result.get("repay"))).multiply(new BigDecimal(100)));
        }
        return result;
    }

    @Override
    public Boolean updateReviewStatusByXgNo(JSONArray jsonArray) {
        if (jsonArray != null && jsonArray.size() > 0) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String reviewStatus = jsonObject.getString("reviewStatus");
                String reviewRemark = jsonObject.getString("reviewRemark") == null ? "" : jsonObject.getString("reviewRemark");
                String tradeNoXgxy = jsonObject.getString("tradeNoXgxy");
                JsdBorrowCashDo jsdBorrowCashDo = jsdBorrowCashDao.getByTradeNoXgxy(tradeNoXgxy);
                if (reviewStatus.equals(JsdBorrowCashReviewStatus.REFUSE.name())) {
                    jsdBorrowCashDao.refuseByXgNo(reviewRemark, tradeNoXgxy);
                    jsdNoticeRecordService.dealBorrowNoticed(jsdBorrowCashDo, this.buildXgxyPay(jsdBorrowCashDo, "商户审核不通过", XgxyBorrowNotifyStatus.CANCEL.name()));
                }
                if (reviewStatus.equals(JsdBorrowCashReviewStatus.PASS.name())) {
                    JsdBorrowLegalOrderDo jsdBorrowLegalOrderDo = jsdBorrowLegalOrderDao.getLastOrderByBorrowId(jsdBorrowCashDo.getRid());
                    upsUtil.manualJsdDelegatePay(jsdBorrowCashDo, jsdBorrowLegalOrderDo);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public List<LoanDto> getLoanList(LoanQuery query) {
        return jsdBorrowCashDao.getLoanList(query);
    }

    @Override
    public List<LoanDto> getRepayList(LoanQuery query) {
        return jsdBorrowCashDao.getRepayList(query);
    }


    public String transUpdate(final JsdBorrowCashDo cashDo, final JsdBorrowLegalOrderDo orderDo, final JsdBorrowLegalOrderCashDo orderCashDo) {
        String result = transactionTemplate.execute(new TransactionCallback<String>() {
            @Override
            public String doInTransaction(TransactionStatus ts) {
                jsdBorrowCashDao.updateById(cashDo);
                jsdBorrowLegalOrderCashDao.updateById(orderCashDo);
                jsdBorrowLegalOrderDao.updateById(orderDo);
                return "success";
            }
        });
        return result;
    }


    /**
     * 获取风控分层利率
     *
     * @param openId
     * @return
     */
    @Override
    public BigDecimal getRiskDailyRate(String openId,String days,String unit) {
        BigDecimal riskRateDaily = BigDecimal.valueOf(0.02); // 0913产品与风控分析确定值

        // 默认利润率 取后台配置
        JsdResourceDo jsdResourceDo = jsdResourceService.getByTypeAngSecType(Constants.JSD_CONFIG, Constants.JSD_RATE_INFO);
        if (jsdResourceDo == null) {
            logger.error("getRiskDailyRate, openId=" + openId + ", riskRate from jsdResource is null !");
        } else {
            riskRateDaily = new BigDecimal(jsdResourceDo.getValue1()).divide(new BigDecimal(Constants.ONE_YEAY_DAYS), 6, RoundingMode.HALF_UP);
        }

        try {
            jsdResourceDo = jsdResourceService.getByTypeAngSecType(Constants.JSD_CONFIG, ResourceSecType.RISK_DAILY_RATE_SWITCH.getCode());
            String riskDailyRateSwitch = "open";
            if(jsdResourceDo != null){
                riskDailyRateSwitch = jsdResourceDo.getValue();
            }
            String riskRateDailyFromCache = bizCacheUtil.hget(Constants.CACHEKEY_RISK_LAYER_RATE, openId+"_"+days);
            if (StringUtils.isNotBlank(riskRateDailyFromCache) && riskDailyRateSwitch.equals("open")) {
                logger.info("getRiskDailyRate, openId=" + openId + ", risk from cache is " + riskRateDailyFromCache);
                riskRateDaily = new BigDecimal(riskRateDailyFromCache);
            } else {
                String riskRate = xgxyUtil.getOriRateNoticeRequest(openId,days,unit); //风控返回的数据为日利率，并除以1000
                if (StringUtils.isNotBlank(riskRate)) {
                    if (BigDecimal.ZERO.compareTo(new BigDecimal(riskRate)) == 0) {
                        logger.error("getRiskDailyRate, openId=" + openId + ", riskRate from xgxy is 0.00 !");
                    } else {
                        riskRateDaily = new BigDecimal(riskRate).divide(BigDecimal.valueOf(1000), 6, RoundingMode.HALF_UP);
                        bizCacheUtil.hset(Constants.CACHEKEY_RISK_LAYER_RATE, openId+"_"+days, riskRateDaily.toPlainString(), DateUtil.getTodayLast());
                    }
                } else {
                    logger.error("getRiskDailyRate, openId=" + openId + ", riskRate from xgxy is null!");
                }
            }
        } catch (Exception e) {
            logger.error("getRiskDailyRate, openId=" + openId + ", occur exception when getRiskDailyRate, msg=" + e.getMessage(), e);
        }
        return riskRateDaily;
    }

    @Override
    public BigDecimal calcuTotalAmount(JsdBorrowCashDo cashDo, JsdBorrowLegalOrderCashDo orderCashDo) {
        BigDecimal cashTotalAmount = BigDecimalUtil.add(cashDo.getAmount(), cashDo.getInterestAmount(), cashDo.getPoundageAmount(), cashDo.getOverdueAmount(),
                cashDo.getSumRepaidInterest(), cashDo.getSumRepaidPoundage(), cashDo.getSumRepaidOverdue());
        BigDecimal orderTotalAmount = BigDecimal.ZERO;
        if (orderCashDo != null) {
            orderTotalAmount = BigDecimalUtil.add(orderCashDo.getAmount(), orderCashDo.getInterestAmount(), orderCashDo.getPoundageAmount(), orderCashDo.getOverdueAmount(),
                    orderCashDo.getSumRepaidInterest(), orderCashDo.getSumRepaidPoundage(), orderCashDo.getSumRepaidOverdue());
        }
        return cashTotalAmount.add(orderTotalAmount);
    }

    @Override
    public BigDecimal calcuUnrepayAmount(JsdBorrowCashDo cashDo, JsdBorrowLegalOrderCashDo orderCashDo) {
        BigDecimal totalAmount = this.calcuTotalAmount(cashDo, orderCashDo);
        if (orderCashDo != null) {
            return totalAmount.subtract(cashDo.getRepayAmount()).subtract(orderCashDo.getRepaidAmount());
        }
        return totalAmount.subtract(cashDo.getRepayAmount());
    }

    /**
     * 解析各项利息费用
     *
     * @param bo
     * @return
     */
    public void resolve(TrialBeforeBorrowBo bo) {
        TrialBeforeBorrowReq req = bo.req;

        BigDecimal titularBorrowAmount = req.amount; // 并非真实借款额
        BigDecimal borrowDay = new BigDecimal(req.term);

        ResourceRateInfoBo borrowRateInfo = jsdResourceService.getRateInfo(req.term);

        BigDecimal borrowISRate = borrowRateInfo.interestRate.add(borrowRateInfo.serviceRate);
        BigDecimal borrowInterestRate = borrowRateInfo.interestRate;
        BigDecimal borrowOverdueRate = borrowRateInfo.overdueRate;

        // 借款 利息 与 服务费 乘法表达式可复用左侧
        BigDecimal borrowinterestLeft = borrowInterestRate.divide(BigDecimal.valueOf(Constants.ONE_YEAY_DAYS), 12, RoundingMode.HALF_UP).multiply(borrowDay);
        BigDecimal borrowISLeft = borrowISRate.divide(BigDecimal.valueOf(Constants.ONE_YEAY_DAYS), 12, RoundingMode.HALF_UP).multiply(borrowDay);

        BigDecimal titularInterestAmount = borrowinterestLeft.multiply(titularBorrowAmount);
        BigDecimal titularServiceAmount = borrowISLeft.multiply(titularBorrowAmount).subtract(titularInterestAmount);
        BigDecimal totalProfit = bo.riskDailyRate.multiply(titularBorrowAmount).multiply(borrowDay);

        BigDecimal finalDiffProfit = totalProfit.subtract(titularInterestAmount).subtract(titularServiceAmount);
        logger.info("resolve borrow amount, openId=" + req.openId + ", actualDiffProfit=" + finalDiffProfit);
        if (finalDiffProfit.compareTo(BigDecimal.ZERO) <= 0) {
            finalDiffProfit = BigDecimal.ZERO;
        }

        BigDecimal actualOrderAmount = finalDiffProfit.setScale(-1, RoundingMode.UP);    //最终商品价格,以10取整
        BigDecimal actualBorrowAmount = titularBorrowAmount.subtract(actualOrderAmount);//真实借款额
        TrialBeforeBorrowResp resp = new TrialBeforeBorrowResp();

        //处理借款相关利息
        BigDecimal actualBorrowInterestAmount = borrowinterestLeft.multiply(actualBorrowAmount).setScale(2, RoundingMode.HALF_UP);
        BigDecimal actualBorrowServiceAmount = borrowISLeft.multiply(actualBorrowAmount).setScale(2, RoundingMode.DOWN).subtract(actualBorrowInterestAmount);
        resp.borrowAmount = actualBorrowAmount.setScale(2, RoundingMode.HALF_UP).toString();
        resp.arrivalAmount = actualBorrowAmount.setScale(2, RoundingMode.HALF_UP).toString();
        resp.interestRate = borrowInterestRate.setScale(4, RoundingMode.HALF_UP).toString();
        resp.interestAmount = actualBorrowInterestAmount.toString();
        resp.serviceRate = borrowRateInfo.serviceRate.setScale(4, RoundingMode.HALF_UP).toString();
        resp.serviceAmount = actualBorrowServiceAmount.toString();
        resp.overdueRate = borrowOverdueRate.divide(new BigDecimal(360),4, RoundingMode.HALF_UP).setScale(4, RoundingMode.HALF_UP).toString();
        resp.overdueYearRate = borrowOverdueRate.toString();
        //处理搭售商品相关利息
        ResourceRateInfoBo orderRateInfo = jsdResourceService.getOrderRateInfo(req.term);
        BigDecimal orderInterestRate = orderRateInfo.interestRate;
        BigDecimal orderISRate = orderRateInfo.interestRate.add(orderRateInfo.serviceRate);
        BigDecimal orderOverdueRate = orderRateInfo.overdueRate;
        BigDecimal actualOrderInterestAmount = orderInterestRate.divide(BigDecimal.valueOf(Constants.ONE_YEAY_DAYS), 12, RoundingMode.HALF_UP)
                .multiply(borrowDay).multiply(actualOrderAmount).setScale(2, RoundingMode.HALF_UP);
        BigDecimal actualOrderServiceAmount = orderISRate.divide(BigDecimal.valueOf(Constants.ONE_YEAY_DAYS), 12, RoundingMode.HALF_UP)
                .multiply(borrowDay).multiply(actualOrderAmount).setScale(2, RoundingMode.DOWN).subtract(actualOrderInterestAmount);
        resp.totalDiffFee = actualOrderAmount.toPlainString();
        resp.sellInterestFee = actualOrderInterestAmount.toString();
        resp.sellInterestRate = orderInterestRate.setScale(4, RoundingMode.HALF_UP);
        resp.sellServiceFee = actualOrderServiceAmount.toString();
        resp.sellServiceRate = orderRateInfo.serviceRate.setScale(4, RoundingMode.HALF_UP);
        resp.sellOverdueRate = orderOverdueRate.divide(new BigDecimal(360)).setScale(4, RoundingMode.HALF_UP);
        resp.riskDailyRate= bo.riskDailyRate;
        BigDecimal totalAmount = BigDecimalUtil.add(actualBorrowAmount, actualBorrowInterestAmount, actualBorrowServiceAmount,
                actualOrderAmount, actualOrderInterestAmount, actualOrderServiceAmount);
        resp.totalAmount = totalAmount.toString();
        resp.billAmount = new BigDecimal[]{totalAmount.setScale(2, RoundingMode.HALF_UP)};

        // 1、借款费用【借款利息金额+借款服务费金额】元，其中借款利息【借款利息金额】元，借款服务费【借款服务费金额】元。2、商品费用【分期利息金额+分期服务费金额】元，其中分期利息【分期利息金额】元，分期服务费【分期服务费金额】元。
        resp.remark = "1、借款费用" + BigDecimalUtil.add(actualBorrowInterestAmount, actualBorrowServiceAmount) + "元，"
                + "其中借款利息" + actualBorrowInterestAmount + "元，借款服务费" + actualBorrowServiceAmount + "元。"
                + "2、商品费用" + BigDecimalUtil.add(actualOrderInterestAmount, actualOrderServiceAmount) + "元，"
                + "其中分期利息" + actualOrderInterestAmount + "元，分期服务费" + actualOrderServiceAmount + "元。";

        bo.resp = resp;
    }


    @Override
    public void dealBorrowSucc(Long cashId, String outTradeNo,String tradeDate) throws IOException, DocumentException {
        JsdBorrowCashDo cashDo = jsdBorrowCashDao.getById(cashId);
        if (cashDo == null) {
            throw new BizException("dealBorrowSucc, can't find refer borrowCash by id=" + cashId);
        }

        logger.info("dealBorrowSucc, borrowCashId=" + cashId + ", borrowNo=" + cashDo.getBorrowNo()
                + ", tradeNoXgxy=" + cashDo.getTradeNoXgxy() + ", tradeNoUps=" + cashDo.getTradeNoUps());

        if (JsdBorrowCashStatus.FINISHED.name().equals(cashDo.getStatus())) {
            logger.warn("cur borrowNo " + cashDo.getBorrowNo() + "have FINISHED! repeat UPS callback!");
            return;
        }

        JsdBorrowLegalOrderDo orderDo = jsdBorrowLegalOrderDao.getLastOrderByBorrowId(cashId);
        JsdBorrowLegalOrderCashDo orderCashDo = jsdBorrowLegalOrderCashDao.getLastOrderCashByBorrowId(cashId);
        Date currDate;
        if(StringUtils.isBlank(tradeDate)){
            currDate = new Date();
        }else {
            currDate = DateUtil.parseDate(tradeDate,"yyyy-MM-dd HH:mm:ss");
            currDate = DateUtil.afterDay(currDate,new Date())?new Date():currDate;
            currDate = DateUtil.beforeDay(currDate,cashDo.getGmtCreate())?new Date():currDate;
        }
        Date arrivalEnd = DateUtil.getEndOfDatePrecisionSecond(currDate);
        Date repaymentDate = DateUtil.addDays(arrivalEnd, Integer.valueOf(cashDo.getType()) - 1);
        cashDo.setGmtArrival(currDate);
        cashDo.setGmtPlanRepayment(repaymentDate);
        cashDo.setStatus(JsdBorrowCashStatus.TRANSFERRED.name());
        cashDo.setTradeNoUps(outTradeNo);

        orderDo.setStatus(JsdBorrowLegalOrderStatus.AWAIT_DELIVER.name());
        orderCashDo.setStatus(JsdBorrowLegalOrderCashStatus.AWAIT_REPAY.name());
        orderCashDo.setGmtPlanRepay(repaymentDate);
        String result = this.transUpdate(cashDo, orderDo, orderCashDo);
        if(StringUtils.equals(result,"success")){
            jobThreadPoolUtils.platformServiceSellProtocol(cashDo.getTradeNoXgxy());
        }
        jsdNoticeRecordService.dealBorrowNoticed(cashDo, this.buildXgxyPay(cashDo, "放款成功", XgxyBorrowNotifyStatus.SUCCESS.name()));
    }

    @Override
    public void dealBorrowFail(Long cashId, String outTradeNo, String failMsg) {
        JsdBorrowCashDo cashDo = jsdBorrowCashDao.getById(cashId);
        if (cashDo == null) {
            throw new BizException("dealBorrowFail, can't find refer borrowCash by id=" + cashId);
        }
        logger.info("dealBorrowFail, borrowCashId=" + cashId + ", borrowNo=" + cashDo.getBorrowNo()
                + ", tradeNoXgxy=" + cashDo.getTradeNoXgxy() + ", tradeNoUps=" + cashDo.getTradeNoUps());

        JsdBorrowLegalOrderDo orderDo = jsdBorrowLegalOrderDao.getLastOrderByBorrowId(cashId);
        JsdBorrowLegalOrderCashDo orderCashDo = jsdBorrowLegalOrderCashDao.getLastOrderCashByBorrowId(cashId);
        cashDo.setTradeNoUps(outTradeNo);

        dealBorrowFail(cashDo, orderDo, orderCashDo, failMsg);
    }

    @Override
    public void dealBorrowFail(JsdBorrowCashDo cashDo, JsdBorrowLegalOrderDo orderDo, JsdBorrowLegalOrderCashDo orderCashDo, String failMsg) {
        cashDo.setStatus(JsdBorrowCashStatus.CLOSED.name());
        cashDo.setRemark(failMsg);
        cashDo.setGmtClose(new Date());

        orderDo.setStatus(JsdBorrowLegalOrderStatus.CLOSED.name());
        orderCashDo.setStatus(JsdBorrowLegalOrderCashStatus.CLOSED.name());
        this.transUpdate(cashDo, orderDo, orderCashDo);

        jsdNoticeRecordService.dealBorrowNoticed(cashDo, this.buildXgxyPay(cashDo, failMsg, XgxyBorrowNotifyStatus.FAILED.name()));
    }

    /**
     * 获取借款相关协议
     *
     * @param openId
     * @param tradeNoXgxy
     * @param previewJsonStr
     * @return
     */
    @Override
    public List<JsdProctocolBo> getBorrowProtocols(String openId, String tradeNoXgxy, String previewJsonStr) {
        List<JsdResourceDo> ress = jsdResourceService.listByType(ResourceType.PROTOCOL_BORROW.getCode());
        List<JsdProctocolBo> protocolVos = new ArrayList<>();
        for (JsdResourceDo resdo : ress) {
            JsdProctocolBo protocolVo = new JsdProctocolBo();
            protocolVo.setProtocolName(resdo.getName());
            String urlPrefix = getNotifyHost() + resdo.getValue();
            try {
                String urlParams = "?openId=" + openId + "&tradeNoXgxy=" + (tradeNoXgxy == null ? "" : tradeNoXgxy) + "&preview=" + URLEncoder.encode(previewJsonStr, "UTF-8");
                protocolVo.setProtocolUrl(urlPrefix + urlParams);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            protocolVos.add(protocolVo);
        }
        return protocolVos;
    }

    /**
     * 获取搭售代买协议
     *
     * @param openId
     * @param tradeNoXgxy
     * @param previewJsonStr
     * @return
     */
    public List<JsdProctocolBo> getAgencyProtocols(String openId, String tradeNoXgxy, String previewJsonStr) {
        JsdResourceDo resdo = jsdResourceService.getByTypeAngSecType(ResourceType.PROTOCOL_AGENCY.name(), ResourceSecType.PROTOCOL_AGENCY.name());
        List<JsdProctocolBo> protocolVos = new ArrayList<>();

        JsdProctocolBo protocolVo = new JsdProctocolBo();
        protocolVo.setProtocolName(resdo.getName());
        String urlPrefix = getNotifyHost() + resdo.getValue();
        try {
            String urlParams = "?openId=" + openId + "&tradeNoXgxy=" + (tradeNoXgxy == null ? "" : tradeNoXgxy) + "&preview=" + URLEncoder.encode(previewJsonStr, "UTF-8");
            protocolVo.setProtocolUrl(urlPrefix + urlParams);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        protocolVos.add(protocolVo);
        return protocolVos;
    }

    /**
     * 获取续期协议
     *
     * @param openId
     * @param tradeNoXgxy
     * @param previewJsonStr
     * @return
     */
    public List<JsdProctocolBo> getRenewalProtocols(String openId, String tradeNoXgxy, String previewJsonStr) {
        JsdResourceDo resdo = jsdResourceService.getByTypeAngSecType(ResourceType.PROTOCOL_RENEWAL.name(), ResourceSecType.PROTOCOL_RENEWAL.name());
        List<JsdProctocolBo> protocolVos = new ArrayList<>();

        JsdProctocolBo protocolVo = new JsdProctocolBo();
        protocolVo.setProtocolName(resdo.getName());
        String urlPrefix = getNotifyHost() + resdo.getValue();
        try {
            // TODO
            String urlParams = "?openId=" + openId + "&tradeNoXgxy=" + (tradeNoXgxy == null ? "" : tradeNoXgxy) + "&preview=" + URLEncoder.encode(previewJsonStr, "UTF-8");
            protocolVo.setProtocolUrl(urlPrefix + urlParams);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        protocolVos.add(protocolVo);
        return protocolVos;
    }

    /**
     * 获取续期协议(plus)
     *
     * @param openId
     * @param tradeNoXgxy
     * @param previewJsonStr
     * @return
     */
    public List<JsdProctocolBo> getRenewalPlusProtocols(String openId, String tradeNoXgxy, String previewJsonStr) {
        JsdResourceDo resdo = jsdResourceService.getByTypeAngSecType(ResourceType.PLUS_PROTOCOL_RENEWAL.name(), ResourceSecType.PLUS_PROTOCOL_RENEWAL.name());
        List<JsdProctocolBo> protocolVos = new ArrayList<>();

        JsdProctocolBo protocolVo = new JsdProctocolBo();
        protocolVo.setProtocolName(resdo.getName());
        String urlPrefix = getNotifyHost() + resdo.getValue();
        try {
            // TODO
            String urlParams = "?openId=" + openId + "&tradeNoXgxy=" + (tradeNoXgxy == null ? "" : tradeNoXgxy) + "&preview=" + URLEncoder.encode(previewJsonStr, "UTF-8");
            protocolVo.setProtocolUrl(urlPrefix + urlParams);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        protocolVos.add(protocolVo);
        return protocolVos;
    }

    @Override
    public List<FinaneceDataDo> getPaymentDetail() {
        return jsdBorrowCashDao.getPaymentDetail();
    }

    @Override
    public List<FinaneceDataDo> getPromiseIncomeDetail() {
        return jsdBorrowCashDao.getPromiseIncomeDetail();
    }

    /**
     * 获取借款相关协议(plus)
     *
     * @param openId
     * @param tradeNoXgxy
     * @param previewJsonStr
     * @return
     */
    @Override
    public List<JsdProctocolBo> getBorrowPlusProtocols(String openId, String tradeNoXgxy, String previewJsonStr) {
        List<JsdResourceDo> ress = jsdResourceService.listByType(ResourceType.PLUS_PROTOCOL_BORROW.getCode());
        List<JsdProctocolBo> protocolVos = new ArrayList<>();
        for (JsdResourceDo resdo : ress) {
            JsdProctocolBo protocolVo = new JsdProctocolBo();
            protocolVo.setProtocolName(resdo.getName());
            String urlPrefix = getNotifyHost() + resdo.getValue();
            try {
                String urlParams = "?openId=" + openId + "&tradeNoXgxy=" + (tradeNoXgxy == null ? "" : tradeNoXgxy) + "&preview=" + URLEncoder.encode(previewJsonStr, "UTF-8");
                protocolVo.setProtocolUrl(urlPrefix + urlParams);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            protocolVos.add(protocolVo);
        }
        return protocolVos;
    }


    private String getNotifyHost() {
        if (notifyHost == null) {
            notifyHost = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST);
            return notifyHost;
        }
        return notifyHost;
    }

    private XgxyBorrowNoticeBo buildXgxyPay(JsdBorrowCashDo cashDo, String msg, String status) {
        XgxyBorrowNoticeBo xgxyPayBo = new XgxyBorrowNoticeBo();
        xgxyPayBo.setTradeNo(cashDo.getTradeNoUps());
        xgxyPayBo.setBorrowNo(cashDo.getTradeNoXgxy());
        xgxyPayBo.setReason(msg);
        xgxyPayBo.setStatus(status);
        xgxyPayBo.setGmtArrival(cashDo.getGmtArrival());
        xgxyPayBo.setTimestamp(System.currentTimeMillis());
        return xgxyPayBo;
    }

    @Override
    public List<JsdBorrowCashDo> getBorrowCashsInfos(Long userId) {
        List<JsdBorrowCashDo> cashs = new ArrayList<JsdBorrowCashDo>(4);

        List<JsdBorrowCashDo> transedCashDtos = jsdBorrowCashDao.getTransedCashDtosByUserId(userId);
        cashs.addAll(transedCashDtos);

        JsdBorrowCashDo finshCash = jsdBorrowCashDao.getLastFinishCashByUserId(userId);
        if (finshCash != null) {
            cashs.add(finshCash);
        }
        return cashs;
    }

    @Override
    public List<JsdBorrowCashDo> getBorrowCashsTransedForCrawler(String topThree,String laterTwo,String realName) {
        return jsdBorrowCashDao.getBorrowCashsTransedForCrawler(topThree,laterTwo,realName);
    }

    @Override
    public JsdCashDto getGoodsInfoByBorrowId(Long borrowId) {
        return jsdBorrowCashDao.getGoodsInfoByBorrowId(borrowId);
    }


    @Override
    public JsdBorrowCashDo getBorrowByRid(Long id){
        return jsdBorrowCashDao.getBorrowByRid(id);
    }
    @Override
    public int getBorrowCashByOverdueCountBySection(Date startOverdue, Date endOverdue) {
        return jsdBorrowCashDao.getBorrowCashByOverdueCountBySection(startOverdue,endOverdue);
    }

    @Override
    public List<JsdBorrowCashDo> getBorrowCashOverdueBySection(Date startTime, Date endTime) {
        return jsdBorrowCashDao.getBorrowCashOverdueBySection(startTime,  endTime);
    }

    @Override
    public int getBorrowCashByTodayCount(Date todayLast) {
        return jsdBorrowCashDao.getBorrowCashByTodayCount(DateUtil.formatDate(todayLast,"yyyy-MM-dd"));
    }

    @Override
    public List<JsdBorrowCashDo> getBorrowCashByToday(Date todayLast) {
        return jsdBorrowCashDao.getBorrowCashByToday( DateUtil.formatDate(todayLast,"yyyy-MM-dd"));
    }

    public List<JsdBorrowCashDo> getOverSectionBorrowCashRepayByUserIds(String userIds,Date startTime,Date endTime){
        return jsdBorrowCashDao.getOverSectionBorrowCashRepayByUserIds(userIds,startTime,endTime);
    }

    @Override
    public Integer getLoanNum(String nper,String date){
        return jsdBorrowCashDao.getLoanNum(nper,date);
    }

    @Override
    public BigDecimal getLoanAmount(String nper,String date){
        return jsdBorrowCashDao.getLoanAmount(nper,date);
    }

    @Override
    public BigDecimal getAppleAmount(String nper,String date){
        return jsdBorrowCashDao.getAppleAmount(nper,date);
    }



    @Override
    public BigDecimal getRepaymentAmount(String nper,String date){
        return jsdBorrowCashDao.getRepaymentAmount(nper,date);
    }

    @Override
    public BigDecimal getNormalAmount(String nper,String date){
        return jsdBorrowCashDao.getNormalAmount(nper,date);
    }

    @Override
    public Integer getRepaymentNum(String nper,String date){
        return jsdBorrowCashDao.getRepaymentNum(nper,date);
    }

    @Override
    public Integer getSumRepaymentNum(String nper,String date){
        return jsdBorrowCashDao.getSumRepaymentNum(nper,date);
    }

    @Override
    public Integer getNormalNum(String nper,String date){
        return jsdBorrowCashDao.getNormalNum(nper,date);
    }
}
