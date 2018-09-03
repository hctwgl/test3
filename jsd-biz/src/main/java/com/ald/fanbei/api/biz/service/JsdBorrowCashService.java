package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.DsedLoanDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashDo;

import java.util.Date;

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


}
