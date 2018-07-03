package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.biz.bo.AfOrderLogisticsBo;
import com.ald.fanbei.api.dal.domain.AfOrderLogisticsDo;

/**
 * '第三方-上树请求记录Service
 * 
 * @author renchunlei
 * @version 1.0.0 初始化
 * @date 2017-08-21 09:28:02
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfOrderLogisticsService extends ParentService<AfOrderLogisticsDo, Long>{
    /**
     * 获取单个物流订单信息
     * @param orderId
     * @return
     */
    AfOrderLogisticsDo getByOrderId(Long orderId);

    AfOrderLogisticsBo getOrderLogisticsBo(long orderId, long isOutTraces);


}