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

    int getApplyBorrowCashByDays(Integer days);

    int getApplyBorrowCashNumByDays(Integer days);

    BigDecimal getPlanRepaymentCashAmountByDays(Integer days);

    int getApplyBorrowCashBetweenStartAndEnd(Date startDate,Date endDate);

    BigDecimal getPlanRepaymentCashAmountBetweenStartAndEnd(Date startDate,Date endDate);

    int getApplyBorrowCashNumBetweenStartAndEnd(Date startDate,Date endDate);

    int getUserNumByBorrowDays(Integer days);

    int getUserNumBetweenStartAndEnd(Date startDate,Date endDate);

    int getPlanRepaymentCashAmountByDays(Date startDate,Date endDate);

    BigDecimal getAmountByDays(Integer days);
}