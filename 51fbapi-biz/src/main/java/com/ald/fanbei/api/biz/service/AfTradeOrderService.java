package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.AfTradeOrderDo;
import com.ald.fanbei.api.dal.domain.dto.AfTradeOrderDto;
import com.ald.fanbei.api.dal.domain.dto.AfTradeOrderStatisticsDto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 商圈订单扩展表Service
 * 
 * @author huyang
 * @version 1.0.0 初始化
 * @date 2017-07-14 16:46:31
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfTradeOrderService extends ParentService<AfTradeOrderDo, Long>{

    /**
     * 获取可提现金额
     *
     * @param businessId
     * @return
     */
    BigDecimal getCanWithDrawMoney(Long businessId);

    /**
     * 不可提现金额
     *
     * @param businessId
     * @return
     */
    BigDecimal getCannotWithDrawMoney(Long businessId);

    /**
     * 统计一段时间内的支付订单
     *
     * @param businessId
     * @param startDate
     * @param endDate
     * @return
     */
    AfTradeOrderStatisticsDto payOrderInfo(Long businessId, Date startDate, Date endDate);

    /**
     * 收款明细时间列表
     * @param businessId
     * @param startOfDate
     * @param endOfDate
     * @param orderStatus
     * @return
     */
    List<String> orderGridDate(Long businessId, Date startOfDate, Date endOfDate, String orderStatus);

    /**
     * 分页查询商圈订单
     *
     * @param businessId
     * @param startOfDate
     * @param endOfDate
     * @param orderStatus
     * @return
     */
    List<AfTradeOrderDto> orderGrid(Long businessId, Date startOfDate, Date endOfDate, String orderStatus);

    /**
     * 分页查询商圈退款明细
     *
     * @param businessId
     * @param offset
     * @param limit
     * @param startDate
     * @param endDate
     * @return
     */
    List<AfTradeOrderDto> refundGrid(Long businessId, Integer offset, Integer limit, Date startDate, Date endDate, String refundStatus);

    /**
     * 分页查询商圈退款明细总条数
     *
     * @param businessId
     * @param startDate
     * @param endDate
     * @return
     */
    Long refundGridTotal(Long businessId, Date startDate, Date endDate, String refundStatus);

    /**
     * 商户提现
     *
     * @param businessId
     * @return
     */
    boolean withdraw(Long businessId);

    /**
     * 商户退款
     * @param businessId
     * @param orderId
     * @return
     */
    boolean refund(Long businessId, Long orderId);
}
