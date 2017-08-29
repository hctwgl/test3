package com.ald.fanbei.api.biz.rebate;

import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.common.enums.OrderType;
import com.ald.fanbei.api.common.util.SpringBeanContextUtil;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component("rebateContext")
public class RebateContext {
    @Resource
    AfOrderService afOrderService;
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String prefix = "rebate_";

    private BaseRebateService processor(String orderType) {
        return (BaseRebateService) SpringBeanContextUtil.getBean(prefix + orderType);
    }

    public boolean rebate(long orderId) {
        AfOrderDo afOrderDo = afOrderService.getOrderById(orderId);
        return rebate(afOrderDo);
    }

    public boolean rebate(AfOrderDo afOrderDo) {
        try{
            BaseRebateService rebateService = processor(afOrderDo.getOrderType());
            if(rebateService.valid(afOrderDo)){
                //数据库记录返利日志
                logger.error("rebate process：orderId"+afOrderDo.getRid());
                return rebateService.rebate(afOrderDo);
            }
            return false;

        }catch (Exception e){
            logger.error("rebate exception： orderId|"+afOrderDo.getRid()+",details:"+e);
            throw e;
        }

    }
}
