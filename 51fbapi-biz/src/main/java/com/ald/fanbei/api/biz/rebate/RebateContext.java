package com.ald.fanbei.api.biz.rebate;

import java.util.concurrent.TimeUnit;

import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.OrderType;
import com.ald.fanbei.api.common.util.SpringBeanContextUtil;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.dal.domain.AfRecommendUserDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;

@Component("rebateContext")
public class RebateContext {
    @Resource
    AfOrderService afOrderService;
    @Resource
    TransactionTemplate transactionTemplate;
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String prefix = "rebate_";

    private BaseRebateService processor(String orderType) {
        return (BaseRebateService) SpringBeanContextUtil.getBean(prefix + orderType);
    }

    public boolean rebate(long orderId) {
        AfOrderDo afOrderDo = afOrderService.getOrderById(orderId);
        return rebate(afOrderDo);
    }
   
    public boolean rebate(final AfOrderDo afOrderDo) {
        transactionTemplate.execute(new TransactionCallback<Boolean>() {
            @Override
            public Boolean doInTransaction(TransactionStatus status) {
                try {
                    BaseRebateService rebateService = processor(afOrderDo.getOrderType());
                    if (rebateService.valid(afOrderDo)) {
                        //数据库记录返利日志
                        logger.error("rebate process：orderId" + afOrderDo.getRid());
                        return rebateService.rebate(afOrderDo);
                    }
                    return false;
                } catch (Exception e) {
                    status.setRollbackOnly();
                    logger.info("addUser error:", e);
                    return false;
                }
            }
        });
        
        return true;
    }
}
