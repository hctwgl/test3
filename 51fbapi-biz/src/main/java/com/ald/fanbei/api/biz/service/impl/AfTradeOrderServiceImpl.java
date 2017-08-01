package com.ald.fanbei.api.biz.service.impl;

import com.ald.fanbei.api.biz.bo.UpsDelegatePayRespBo;
import com.ald.fanbei.api.biz.service.AfTradeBusinessInfoService;
import com.ald.fanbei.api.biz.service.AfTradeOrderService;
import com.ald.fanbei.api.biz.third.util.UpsUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.TradeOrderStatus;
import com.ald.fanbei.api.common.enums.TradeWithdrawRecordStatus;
import com.ald.fanbei.api.common.enums.UserAccountLogType;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.dal.dao.*;
import com.ald.fanbei.api.dal.domain.AfTradeBusinessInfoDo;
import com.ald.fanbei.api.dal.domain.AfTradeOrderDo;
import com.ald.fanbei.api.dal.domain.AfTradeWithdrawDetailDo;
import com.ald.fanbei.api.dal.domain.AfTradeWithdrawRecordDo;
import com.ald.fanbei.api.dal.domain.dto.AfTradeOrderDto;
import com.ald.fanbei.api.dal.domain.dto.AfTradeOrderStatisticsDto;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * 商圈订单扩展表ServiceImpl
 *
 * @author huyang
 * @version 1.0.0 初始化
 * @date 2017-07-14 16:46:31
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */

@Service("afTradeOrderService")
public class AfTradeOrderServiceImpl extends ParentServiceImpl<AfTradeOrderDo, Long> implements AfTradeOrderService {

    private static final Logger logger = LoggerFactory.getLogger(AfTradeOrderServiceImpl.class);

    @Resource
    private AfTradeOrderDao afTradeOrderDao;
    @Resource
    private AfTradeBusinessInfoDao afTradeBusinessInfoDao;
    @Resource
    private AfTradeWithdrawRecordDao afTradeWithdrawRecordDao;
    @Resource
    private AfTradeWithdrawDetailDao afTradeWithdrawDetailDao;
    @Resource
    private UpsUtil upsUtil;
    @Resource
    private AfTradeBusinessInfoService afTradeBusinessInfoService;

    @Override
    public BaseDao<AfTradeOrderDo, Long> getDao() {
        return afTradeOrderDao;
    }

    @Override
    public BigDecimal getCanWithDrawMoney(Long businessId) {
        AfTradeBusinessInfoDo infoDo = afTradeBusinessInfoDao.getByBusinessId(businessId);
        Date canWithDrawDate = DateUtil.addDays(DateUtil.getEndOfDate(new Date()), 0 - infoDo.getWithdrawCycle());
        return afTradeOrderDao.getCanWithDrawMoney(businessId, canWithDrawDate);
    }

    @Override
    public BigDecimal getCannotWithDrawMoney(Long businessId) {
        AfTradeBusinessInfoDo infoDo = afTradeBusinessInfoDao.getByBusinessId(businessId);
        Date now = new Date();
        Date cannotWithDrawDate;
        if (now.compareTo(DateUtil.getWithDrawOfDate(now)) > 0) {
            cannotWithDrawDate = DateUtil.addDays(DateUtil.getEndOfDate(now), 0 - infoDo.getWithdrawCycle());
        } else {
            cannotWithDrawDate = DateUtil.addDays(DateUtil.getEndOfDate(now), 0 - infoDo.getWithdrawCycle() - 1);
        }

        return afTradeOrderDao.getCannotWithDrawMoney(businessId, cannotWithDrawDate);
    }

    @Override
    public AfTradeOrderStatisticsDto payOrderInfo(Long businessId, Date startDate, Date endDate) {
        return afTradeOrderDao.payOrderInfo(businessId, startDate, endDate);
    }

    @Override
    public List<AfTradeOrderDto> orderGrid(Long businessId, Integer offset, Integer limit, Date startOfDate, Date endOfDate, String orderStatus, String withDrawStatus) {
        AfTradeBusinessInfoDo afTradeBusinessInfoDo = afTradeBusinessInfoService.getByBusinessId(businessId);
        List<AfTradeOrderDto> list = afTradeOrderDao.orderGrid(businessId, offset, limit, startOfDate, endOfDate, orderStatus, withDrawStatus);
        for(AfTradeOrderDto dto : list) {
            dto.setIcon(afTradeBusinessInfoDo.getImageUrl());
        }
        return list;
    }

    @Override
    public Long orderGridTotal(Long businessId, Date startOfDate, Date endOfDate, String orderStatus, String withDrawStatus) {
        return afTradeOrderDao.orderGridTotal(businessId, startOfDate, endOfDate, orderStatus, withDrawStatus);
    }

    @Override
    public List<AfTradeOrderDto> refundGrid(Long businessId, Integer offset, Integer limit, Date startDate, Date endDate) {
        return afTradeOrderDao.refundGrid(businessId, offset, limit, startDate, endDate);
    }

    @Override
    public Long refundGridTotal(Long businessId, Date startDate, Date endDate) {
        return afTradeOrderDao.refundGridTotal(businessId, startDate, endDate);
    }

    @Override
    public boolean withdraw(Long businessId) {
        try {
            AfTradeBusinessInfoDo infoDo = afTradeBusinessInfoDao.getByBusinessId(businessId);
            Date canWithDrawDate = DateUtil.addDays(DateUtil.getEndOfDate(new Date()), 0 - infoDo.getWithdrawCycle());
            List<AfTradeOrderDto> orderList = afTradeOrderDao.getCanWithDrawList(businessId, canWithDrawDate);
            List<Long> ids = new ArrayList<>();
            BigDecimal withDrawMoney = BigDecimal.ZERO;
            for (AfTradeOrderDto order : orderList) {
                ids.add(order.getId());
                withDrawMoney = withDrawMoney.add(order.getActualAmount());
            }
            afTradeOrderDao.updateStatusByIds(ids, TradeOrderStatus.EXTRACTING.getCode());

            AfTradeWithdrawRecordDo recordDo = new AfTradeWithdrawRecordDo();
            recordDo.setBusinessId(businessId);
            recordDo.setAmount(withDrawMoney);
            recordDo.setCardCode(infoDo.getCardCode());
            recordDo.setCardName(infoDo.getCardBank());
            recordDo.setCardNumber(infoDo.getCardNo());
            recordDo.setStatus(TradeWithdrawRecordStatus.TRANSEDING.getCode());
            recordDo.setType("CASH");
            afTradeWithdrawRecordDao.saveRecord(recordDo);

            for (AfTradeOrderDto order : orderList) {
                AfTradeWithdrawDetailDo detailDo = new AfTradeWithdrawDetailDo();
                detailDo.setOrderId(order.getId());
                detailDo.setRecordId(recordDo.getId());
                afTradeWithdrawDetailDao.saveRecord(detailDo);
            }

            UpsDelegatePayRespBo tempUpsResult = upsUtil.delegatePay(withDrawMoney, infoDo.getCardName(), infoDo.getCardNo(), businessId.toString(), infoDo.getCardPhone(),
                    infoDo.getCardBank(), infoDo.getCardCode(), Constants.DEFAULT_REFUND_PURPOSE, "02", UserAccountLogType.TRADE_WITHDRAW.getCode(),
                    recordDo.getId() + StringUtils.EMPTY, infoDo.getCardIdnumber());
            logger.info("trade withdraw upsResult = {}", tempUpsResult);

            if (!tempUpsResult.isSuccess()) {
                recordDo.setStatus(TradeWithdrawRecordStatus.CLOSED.getCode());
                afTradeWithdrawRecordDao.updateById(recordDo);
                afTradeOrderDao.updateStatusByIds(ids, TradeOrderStatus.NEW.getCode());
            }

            return true;
        } catch (Exception e) {
            logger.error("trade withdraw error, exception={}", e);
            return false;
        }

    }
}