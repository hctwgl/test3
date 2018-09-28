package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderCashDo;

import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 极速贷订单借款Dao
 * 
 * @author yanghailong
 * @version 1.0.0 初始化
 * @date 2018-08-22 16:18:07
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface JsdBorrowLegalOrderCashDao extends BaseDao<JsdBorrowLegalOrderCashDo, Long> {
	/**
	 * 本次商品
	 */
	JsdBorrowLegalOrderCashDo getLastOrderCashByBorrowId(Long borrowId);



    JsdBorrowLegalOrderCashDo getBorrowLegalOrderCashByBorrowId(@Param("borrowId")Long borrowId);

    JsdBorrowLegalOrderCashDo getOverdueBorrowLegalOrderCashByBorrowId(@Param("borrowId")Long borrowId, @Param("nowTime") Date nowTime);

	/**
	 * 上一笔商品
	 */
	JsdBorrowLegalOrderCashDo getPreviousOrderCashByBorrowId(Long rid);

	List<JsdBorrowLegalOrderCashDo> getBorrowOrderCashsByBorrowId(@Param("borrowId")Long borrowId);

	JsdBorrowLegalOrderCashDo getBorrowLegalOrderCashByOrderId(@Param("orderId")Long orderId);

	JsdBorrowLegalOrderCashDo getBorrowOrderCashsByOrderCashId(@Param("rid")Long rid);
	/**
	 * 赊销模式借款下的订单
	 * @param borrowId
	 * @return
	 */
	JsdBorrowLegalOrderCashDo getBorrowLegalOrderCashDateBeforeToday(Long borrowId);

}
