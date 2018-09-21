package com.ald.jsd.mgr.biz.service;

import com.ald.fanbei.api.biz.service.ParentService;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashDo;

import java.math.BigDecimal;
import java.util.List;

public interface MgrBorrowCashService extends ParentService<JsdBorrowCashDo, Long> {

    /**
     * 获取一定期限内的借款数据
     */
    List<JsdBorrowCashDo> getBorrowCashByDays(Integer days);

    List<JsdBorrowCashDo> getBorrowCashLessThanDays(Integer days);

    int getApplyBorrowCashByDays(Integer days);

    int getUserNumByBorrowDays(Integer days);

    BigDecimal getAmountByDays(Integer days);
}