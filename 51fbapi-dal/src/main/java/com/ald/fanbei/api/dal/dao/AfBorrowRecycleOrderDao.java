package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.AfBorrowRecycleOrderDo;
import org.apache.ibatis.annotations.Param;

/**
 * 回收商品记录表Dao
 * 
 * @author guoshuaiqiang
 * @version 1.0.0 初始化
 * @date 2018-05-01 11:29:38
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfBorrowRecycleOrderDao extends BaseDao<AfBorrowRecycleOrderDo, Long> {

    AfBorrowRecycleOrderDo getBorrowRecycleOrderByBorrowId(@Param("id")Long id);

    Long tuchByBorrowId(@Param("borrowId") Long borrowId);

}
