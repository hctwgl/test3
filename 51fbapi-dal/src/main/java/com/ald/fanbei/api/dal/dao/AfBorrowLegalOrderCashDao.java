package com.ald.fanbei.api.dal.dao;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfBorrowLegalOrderCashDo;

/**
 * Dao
 * 
 * @author Jiang Rongbo
 * @version 1.0.0 初始化
 * @date 2017-12-10 10:26:01
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfBorrowLegalOrderCashDao extends BaseDao<AfBorrowLegalOrderCashDo, Long> {
	
	AfBorrowLegalOrderCashDo getBorrowLegalOrderCashByBorrowLegalOrderId(
			Long rid);

	AfBorrowLegalOrderCashDo getBorrowLegalOrderCashByBorrowId(@Param("borrowId")Long borrowId);

	AfBorrowLegalOrderCashDo getByOrderId(Long orderId);
	
	AfBorrowCashDo getRefBorrowCashByOrderId(Long orderId);
}
