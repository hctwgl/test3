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
        //自营商品在确认收货时，直接返利
        //成单时,自营商品会自动返利金额写入 Order表中。而且必须使用成单时的金额，方式商品表中的有变动
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
