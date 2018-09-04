package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.DsedLoanDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashDo;

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
public interface JsdBorrowCashService extends ParentService<JsdBorrowCashDo, Long>{

    JsdBorrowCashDo getByBorrowNo(String borrowNo);

    /**
     * 是否可借款
     *
     * @param openId
     * @return
     */
    boolean isCanBorrowCash(Long userId);

    String getCurrentLastBorrowNo(String orderNoPre);


    /**
     * 获取逾期数据的数量
     *
     * @return
     */
    int getBorrowCashOverdueCount();


    /**
     * 获取当前的逾期借款
     *
     */
    List<JsdBorrowCashDo> getBorrowCashOverdue(int nowPage, int pageSize);


    /**
     * 获取当前的测试逾期借款
     *
     */
    List<JsdBorrowCashDo> getBorrowCashOverdueByUserIds(String userIds);
}
