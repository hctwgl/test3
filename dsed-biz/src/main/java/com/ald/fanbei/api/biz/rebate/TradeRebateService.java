package com.ald.fanbei.api.biz.rebate;

import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.common.enums.OrderStatus;
import com.ald.fanbei.api.dal.dao.AfOrderDao;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;

@Component(value = "rebate_TRADE")
public class TradeRebateService extends BaseRebateService {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    AfResourceService afResourceService;
    @Autowired
    AfOrderDao afOrderDao;
    @Override
    public boolean valid(AfOrderDo afOrderDo) {
        boolean baseValid = super.valid(afOrderDo);
        return baseValid && true;
    }

    @Override
    public boolean rebate(AfOrderDo orderInfo) {
        //扫码支付后，自动结算商户返利信息
        //金额以成单时计算的的金额为准
        BigDecimal rebateAmount = orderInfo.getRebateAmount();
        orderInfo.setStatus(OrderStatus.REBATED.getCode());
        orderInfo.setGmtRebated(new Date());
        orderInfo.setGmtModified(new Date());
        afOrderDao.updateOrder(orderInfo);
        logger.error("rebate process :",rebateAmount);
        return super.addRebateAmount(rebateAmount,orderInfo);
    }

    @Override
    public boolean reverse(AfOrderDo afOrderDo) {
        return true;
    }
}
