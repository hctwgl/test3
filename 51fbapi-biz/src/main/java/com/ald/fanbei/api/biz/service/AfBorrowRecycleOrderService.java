package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.AfBorrowRecycleOrderDo;

/**
 * 回收商品记录表Service
 * 
 * @author guoshuaiqiang
 * @version 1.0.0 初始化
 * @date 2018-05-01 11:29:38
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfBorrowRecycleOrderService extends ParentService<AfBorrowRecycleOrderDo, Long>{

    AfBorrowRecycleOrderDo getBorrowRecycleOrderById(Long id);

}
