package com.ald.jsd.mgr.biz.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.ald.fanbei.api.dal.domain.JsdBorrowCashDo;

public interface MgrBorrowCashService {

    /**
     * 获取一定期限内的借款数据
     */
    List<JsdBorrowCashDo> getBorrowCashByDays(Integer days);

    List<JsdBorrowCashDo> getPlanRepayBorrowCashByDays(Integer days);

    List<JsdBorrowCashDo> getBorrowCashLessThanDays(Integer days);

    List<JsdBorrowCashDo> getBorrowCashBetweenStartAndEnd(Date startDate,Date endDate);

    List<JsdBorrowCashDo> getPlanRepaymentBorrowCashBetweenStartAndEnd(Date startDate,Date endDate);

    /**
     * 当期到期人数
     * @param days
     * @return
     */
    int getArrivalBorrowCashPerByDays(Integer days);

    /**
     * 申请借款成功人数
     * @param days
     * @return
     */
    int getApplyBorrowCashSuPerByDays(Integer days);

    /**
     * 申请借款人数
     * @param days
     * @return
     */
    int getApplyBorrowCashPerByDays(Integer days);

    /**
     * 当期到期金额
     * @param days
     * @return
     */
    BigDecimal getPlanRepaymentCashAmountByDays(Integer days);

    /**
     * 当期到期人数
     * @param startDate
     * @param endDate
     * @return
     */
    int getArrivalBorrowCashBetweenStartAndEnd(Date startDate,Date endDate);

    BigDecimal getPlanRepaymentCashAmountBetweenStartAndEnd(Date startDate,Date endDate);

    /**
     * 申请借款成功人数
     * @param startDate
     * @param endDate
     * @return
     */
    int getApplyBorrowCashSuPerBetweenStartAndEnd(Date startDate,Date endDate);

    /**
     * 申请借款人数
     * @param startDate
     * @param endDate
     * @return
     */
    int getApplyBorrowCashPerBetweenStartAndEnd(Date startDate,Date endDate);

    /**
     * 当期复借人数
     * @param days
     * @return
     */
    int getUserNumByBorrowDays(Integer days);

    int getUserNumBetweenStartAndEnd(Date startDate,Date endDate);

    int getPlanRepaymentCashAmountByDays(Date startDate,Date endDate);

    BigDecimal getAmountByDays(Integer days);

    /**
     * 复借借款
     * @param days
     * @return
     */
    BigDecimal getRepeatBorrowCashByBorrowDays(Integer days);


    BigDecimal getRepayBorrowCashAmountBorrowDays(Integer days);

    /**
     * 获取逾期30天借款本金
     * @return
     */
    BigDecimal getOverdueBorrowAmountTo30Day();

    BigDecimal getAllBorrowAmount();


}