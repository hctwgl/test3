package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.AfBorrowRecycleGoodsDo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 回收商品表Dao
 * 
 * @author guoshuaiqiang
 * @version 1.0.0 初始化
 * @date 2018-04-28 14:08:11
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfBorrowRecycleGoodsDao extends BaseDao<AfBorrowRecycleGoodsDo, Long> {

    /**
     * 获取商品信息
     * @return
     */
    List<AfBorrowRecycleGoodsDo> getAllRecycleGoods();

    /**
     * 根据id获取商品信息
     * @return
     */
     AfBorrowRecycleGoodsDo getRecycleGoodsById(@Param("id")Long id);

}
