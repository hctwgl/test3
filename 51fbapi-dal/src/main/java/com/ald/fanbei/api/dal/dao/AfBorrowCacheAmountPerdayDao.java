package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.AfBorrowCacheAmountPerdayDo;

public interface AfBorrowCacheAmountPerdayDao {
	/**
	 * 增加记录
	 * @param borrowCacheAmountPerdayDo
	 * @return
	 */
	int addBorrowCacheAmountPerday(AfBorrowCacheAmountPerdayDo borrowCacheAmountPerdayDo);
	
	/**
	 * 更新借款金额
	 * @param borrowCacheAmountPerdayDo
	 * @return
	 */
	int updateBorrowCacheAmount(AfBorrowCacheAmountPerdayDo borrowCacheAmountPerdayDo);
	
	/**
	 * 获取当天的总金额
	 * @param borrowCacheAmountPerdayDo
	 * @return
	 */
	AfBorrowCacheAmountPerdayDo getSigninByDay(AfBorrowCacheAmountPerdayDo borrowCacheAmountPerdayDo);
}
