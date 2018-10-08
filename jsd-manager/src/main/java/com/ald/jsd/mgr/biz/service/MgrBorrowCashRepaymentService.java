package com.ald.jsd.mgr.biz.service;

import com.ald.fanbei.api.dal.domain.JsdBorrowCashRepaymentDo;

import java.util.Date;
import java.util.List;

/**
 * 极速贷Service
 *
 * @author yanghailong
 * @version 1.0.0 初始化
 * @date 2018-08-22 16:18:06
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface MgrBorrowCashRepaymentService {

    List<JsdBorrowCashRepaymentDo> getByBorrowTradeNoXgxy(String tradeNoXgxy);

    /**
     * 某一天的还款金额
     * @param days
     * @return
     */
    List<JsdBorrowCashRepaymentDo> getBorrowCashRepayByOneDays(Integer days);

    /**
     * 某一段时间的还款金额
     * @param startDate
     * @param endDate
     * @return
     */
    List<JsdBorrowCashRepaymentDo> getBorrowCashRepayBetweenStartAndEnd(Date startDate,Date endDate);

}
