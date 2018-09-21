package com.ald.jsd.mgr.biz.service;

import com.ald.fanbei.api.biz.service.ParentService;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashDo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface MgrBorrowCashService extends ParentService<JsdBorrowCashDo, Long> {

    /**
     * 获取一定期限内的借款数据
     */
    List<JsdBorrowCashDo> getBorrowCashByDays(Integer days);

    List<JsdBorrowCashDo> getBorrowCashLessThanDays(Integer days);

    List<JsdBorrowCashDo> getBorrowCashBetweenStartAndEnd(Date startDate,Date endDate);

    int getApplyBorrowCashByDays(Integer days);

    int getApplyBorrowCashBetweenStartAndEnd(Date startDate,Date endDate);

    int getUserNumByBorrowDays(Integer days);

    int getUserNumBetweenStartAndEnd(Date startDate,Date endDate);

    BigDecimal getAmountByDays(Integer days);
}