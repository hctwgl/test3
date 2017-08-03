package com.ald.fanbei.api.dal.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfTradeOrderDo;

/**
 * 商圈订单扩展表Dao
 * 
 * @author huyang
 * @version 1.0.0 初始化
 * @date 2017-07-14 16:46:31 Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfTradeOrderDao extends BaseDao<AfTradeOrderDo, Long> {

	int updateStatusByIds(@Param("items") List<Long> ids, @Param("status") String status);

}
