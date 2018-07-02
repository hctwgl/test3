package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.AfBorrowCashPushDo;

/**
 * 菠萝觅订单详情Dao
 * 
 * @author gaojb
 * @version 1.0.0 初始化
 * @date 2018-03-01 18:59:28
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfBorrowCashPushDao extends BaseDao<AfBorrowCashPushDo, Long> {

	AfBorrowCashPushDo getByBorrowCashId(Long borrowCashId);

    

}
