package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderDo;

import java.util.Date;

/**
 * 极速贷Service
 * 
 * @author yanghailong
 * @version 1.0.0 初始化
 * @date 2018-08-22 16:18:07
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface JsdBorrowLegalOrderService extends ParentService<JsdBorrowLegalOrderDo, Long>{

    /**
     * 获取当天最近的订单
     *
     * @param currentDate
     * @param
     * @return
     */
    String getCurrentLastOrderNo(Date currentDate);
}
