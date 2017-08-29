package com.ald.fanbei.api.biz.rebate;

import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.*;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.dal.dao.AfGoodsDao;
import com.ald.fanbei.api.dal.dao.AfOrderDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountLogDao;
import com.ald.fanbei.api.dal.domain.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;

@Component(value = "rebate_SELFSUPPORT")
public class SelfSupportRebateService extends BaseRebateService {
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
        orderInfo.setGmtFinished(new Date());
        orderInfo.setGmtRebated(new Date());
        orderInfo.setGmtModified(new Date());
        orderInfo.setLogisticsInfo("已签收");
        afOrderDao.updateOrder(orderInfo);
        return super.addRebateAmount(rebateAmount,orderInfo);
    }

    @Override
    public boolean reverse(AfOrderDo afOrderDo) {
        
        return true;
    }
}
