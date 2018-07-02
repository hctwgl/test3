package com.ald.fanbei.api.biz.service;

import java.math.BigDecimal;
import java.util.Map;

import org.dom4j.Document;

import com.ald.fanbei.api.dal.domain.AfSupOrderDo;
import com.ald.fanbei.api.dal.domain.dto.GameOrderInfoDto;

/**
 * 新人专享Service
 * 
 * @author gaojibin_temple
 * @version 1.0.0 初始化
 * @date 2017-11-22 13:57:29 Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfSupOrderService extends ParentService<AfSupOrderDo, Long> {

    String processCallbackResult(String userOrderId, String status, String mes, String kminfo, String payoffPriceTotal, String signs);

    Map<String, Object> addSupOrder(Long userId, Long goodsId, BigDecimal actualAmount, Long couponId, String acctType, String gameName, String userName, Integer goodsNum, Integer priceTimes, Integer goodsCount, String gameType, String gameAcct, String gameArea, String gameSrv, String userIp) throws Exception;

    String sendOrderToSup(String orderNo, String goodsId, String userName, String gameName, String gameAcct, String gameArea, String gameType, String acctType, Integer goodsNum, String gameSrv, String orderIp);

    AfSupOrderDo getByOrderNo(String orderNo);

    Integer updateMsgByOrder(String orderNo, String msg);

    GameOrderInfoDto getOrderInfoByOrderNo(String orderNo);

    String getGameCodeValue(String gameName, String gameType, String gameAcct, String gameSrv, String gameArea, String xmlContent) throws Exception;
}
