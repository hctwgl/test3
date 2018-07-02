package com.ald.fanbei.api.biz.rebate;

import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.common.enums.OrderStatus;
import com.ald.fanbei.api.common.enums.OrderType;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

public class AgentBuyRebateService extends BaseRebateService {
    @Autowired
    AfResourceService afResourceService;

    @Override
    public boolean valid(AfOrderDo afOrderDo) {
        boolean baseValid = super.valid(afOrderDo);
        return baseValid && true;
    }

    @Override
    public boolean rebate(AfOrderDo orderInfo) {
        return true;
    }

    @Override
    public boolean reverse(AfOrderDo afOrderDo) {
        return true;
    }
}
