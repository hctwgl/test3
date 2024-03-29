package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderRepaymentDo;

import java.util.List;

/**
 * 极速贷Service
 * 
 * @author yanghailong
 * @version 1.0.0 初始化
 * @date 2018-08-22 16:18:08
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface JsdBorrowLegalOrderRepaymentService extends ParentService<JsdBorrowLegalOrderRepaymentDo, Long>{
    JsdBorrowLegalOrderRepaymentDo getLastByBorrowId(Long borrowId);

    JsdBorrowLegalOrderRepaymentDo getByTradeNoXgxy(String tradeNoXgxy);

    List<JsdBorrowLegalOrderRepaymentDo> getRepayByBorrowId(Long borrowId);
}
