package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderDo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

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

    /**
     * 根据借款id获取最新一笔订单
     * @param borrowId
     * @return
     */
    JsdBorrowLegalOrderDo getLastOrderByBorrowId(Long borrowId);

    /**
     * 根据借款id获取第一笔订单
     * @param borrowId
     * @return
     */
    JsdBorrowLegalOrderDo getFirstOrderByBorrowId(Long borrowId);
    
    /**
     * 获取订单列表
     * @param borrowId
     * @return
     */
    List<JsdBorrowLegalOrderDo> getBorrowOrdersByBorrowId(Long borrowId);
    
    /**
     * 根据借款id获取最新一笔有效订单（状态不为 关闭和待支付的）
     * @param borrowId
     * @return
     */
    JsdBorrowLegalOrderDo getLastValidOrderByBorrowId(Long borrowId);


    /**
     * 商品搭售金额
     */
    BigDecimal getTyingAmount(String nper, String date);
}
