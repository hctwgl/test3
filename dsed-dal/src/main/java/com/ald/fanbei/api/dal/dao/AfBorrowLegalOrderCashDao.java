package com.ald.fanbei.api.dal.dao;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfBorrowLegalOrderCashDo;

import java.math.BigDecimal;

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
	
	AfBorrowLegalOrderCashDo getPrimaryOrderCashByBorrowId(@Param("borrowId")Long borrowId);

	AfBorrowLegalOrderCashDo getLastOrderCashByBorrowId(@Param("borrowId")Long borrowId);

	AfBorrowLegalOrderCashDo getByOrderId(Long orderId);
	
	AfBorrowCashDo getRefBorrowCashByOrderId(Long orderId);

	AfBorrowLegalOrderCashDo getNewOrderCash(@Param("borrowId")Long borrowId);

	AfBorrowLegalOrderCashDo getBorrowLegalOrderCashByBorrowIdNoStatus(@Param("borrowId")Long borrowId);

	AfBorrowLegalOrderCashDo getOrderCashByCashNo(String cashNo);

	BigDecimal calculateRestAmount(@Param("borrowId")Long borrowId);
	
	/**
	 * 查询borrowId 下是否有订单记录
	 * @param borrowId
	 * @return
	 */
	Long tuchByBorrowId(Long borrowId);

	int updateLegalOrderCashBalanced(AfBorrowLegalOrderCashDo legalOrderCashDo);

	AfBorrowLegalOrderCashDo getBorrowLegalOrderCashByBorrowIdNoClosed(@Param("borrowId")Long borrowId);
}
