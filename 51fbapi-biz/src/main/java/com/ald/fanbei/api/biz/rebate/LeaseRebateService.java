package com.ald.fanbei.api.biz.rebate;


import com.ald.fanbei.api.dal.dao.AfOrderDao;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author zhourui on 2018年03月12日 16:44
 * @类描述：
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component(value = "rebate_LEASE")
public class LeaseRebateService extends BaseRebateService {
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
        //orderInfo.setStatus(OrderStatus.REBATED.getCode());
        orderInfo.setGmtFinished(new Date());
        orderInfo.setGmtRebated(new Date());
        orderInfo.setGmtModified(new Date());
        orderInfo.setLogisticsInfo("已签收");
        afOrderDao.updateOrder(orderInfo);
        afOrderDao.rebateOrderLease(orderInfo.getRid());
        return super.addRebateAmount(rebateAmount, orderInfo);
    }

    @Override
    public boolean reverse(AfOrderDo afOrderDo) {
        return true;
    }
}
