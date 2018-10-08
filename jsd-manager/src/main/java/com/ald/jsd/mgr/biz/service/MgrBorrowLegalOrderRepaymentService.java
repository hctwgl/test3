package com.ald.jsd.mgr.biz.service;

import com.ald.fanbei.api.biz.service.ParentService;
import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderRepaymentDo;

import java.util.Date;
import java.util.List;

/**
 * 极速贷Service
 *
 * @author yanghailong
 * @version 1.0.0 初始化
 * @date 2018-08-22 16:18:08
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface MgrBorrowLegalOrderRepaymentService extends ParentService<JsdBorrowLegalOrderRepaymentDo, Long> {

    List<JsdBorrowLegalOrderRepaymentDo> getBorrowCashRepayByDays(Integer days);

    List<JsdBorrowLegalOrderRepaymentDo> getBorrowCashOrderRepayBetweenStartAndEnd(Date startDate,Date endDate);

}
