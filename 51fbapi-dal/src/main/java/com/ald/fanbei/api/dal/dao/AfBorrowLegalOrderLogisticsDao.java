package com.ald.fanbei.api.dal.dao;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfBorrowLegalOrderLogisticsDo;

/**
 * 借钱合规改造Dao
 * 
 * @author Jiang Rongbo
 * @version 1.0.0 初始化
 * @date 2017-12-20 18:49:14
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfBorrowLegalOrderLogisticsDao extends BaseDao<AfBorrowLegalOrderLogisticsDo, Long> {

	AfBorrowLegalOrderLogisticsDo getByOrderId(@Param("orderId")Long orderId);

    

}
